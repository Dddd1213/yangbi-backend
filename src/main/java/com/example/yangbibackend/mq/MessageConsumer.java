package com.example.yangbibackend.mq;

import com.example.yangbibackend.common.constant.BiMqConstant;
import com.example.yangbibackend.common.enumeration.ErrorCode;
import com.example.yangbibackend.common.exception.BusinessException;
import com.example.yangbibackend.common.utils.ExcelUtils;
import com.example.yangbibackend.controller.ChartController;
import com.example.yangbibackend.manager.AiManager;
import com.example.yangbibackend.pojo.VO.chart.BiVO;
import com.example.yangbibackend.pojo.entity.Chart;
import com.example.yangbibackend.service.ChartService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class MessageConsumer {

    @Autowired
    ChartController chartController;

    @Autowired
    AiManager aiManager;

    @Autowired
    ChartService chartService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = BiMqConstant.BI_QUEUE_NAME),
            exchange = @Exchange(name = BiMqConstant.BI_EXCHANGE_NAME, type = ExchangeTypes.DIRECT),
            key = {BiMqConstant.BI_ROUTING_KEY}),
            ackMode = "MANUAL")
    public void receiveMessage(String msg, Channel channel,@Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        log.info("消费者接收到消息：【" + msg + "】");

        if(StringUtils.isBlank(msg)){
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }

        Long chartId = Long.valueOf(msg);
        Chart chart = chartService.getById(chartId);
        if(chart==null){
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表为空");
        }

        Chart chart1 = new Chart();
        chart1.setId(chartId);
        chart1.setStatus("ai分析中");
        boolean c = chartService.updateById(chart1);
        if(!c){
            channel.basicNack(deliveryTag, false, false);
            chartController.handleChartUpdateError(chartId, "更新图表执行中状态失败");
        }

        //拼接分析需求和原始数据输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append(chart.getGoal()).append("\n");
        if(StringUtils.isNotBlank(chart.getChartType())){
            userInput.append("，请使用"+chart.getChartType()+"进行代码生成");
        }
        userInput.append("原始数据").append(chart.getChartData()).append("\n");

        //调用ai
        String userOutput = aiManager.doChart(userInput.toString());
        String[] split = userOutput.split("【【【【【");
        if(split.length<3){
            channel.basicNack(deliveryTag, false, false);
            chartController.handleChartUpdateError(chartId, "AI 生成错误");
        }

        Chart chart2 = new Chart();
        chart2.setId(chartId);
        chart2.setGenChart(split[1].trim());
        chart2.setGenResult(split[2].trim());
        chart2.setStatus("生成完毕");
        c = chartService.updateById(chart2);
        if(!c){
            channel.basicNack(deliveryTag, false, false);
            chartController.handleChartUpdateError(chartId, "更新图表成功状态失败");
        }

        // 消息确认
        channel.basicAck(deliveryTag, false);
    }
}
