package com.example.yangbibackend.mq;

import com.example.yangbibackend.common.constant.BiMqConstant;
import io.netty.channel.ChannelOutboundBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @author 31067
 */
@Component
@Slf4j
public class MessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param message
     */
    public void sendMessage(String message) throws InterruptedException {

//        Message message1 = MessageBuilder.withBody(message.getBytes(StandardCharsets.UTF_8))
//                .setExpiration("1")
//                .build();
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY, message);
        log.info("生产者发送消息成功：{}",message);
    }


}
