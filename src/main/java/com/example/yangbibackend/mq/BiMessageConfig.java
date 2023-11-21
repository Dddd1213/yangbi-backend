package com.example.yangbibackend.mq;
import com.example.yangbibackend.common.constant.BiMqConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BiMessageConfig {

	/**
	 * 声明死信队列和交换机
	 */
	@Bean
	Queue BiDeadQueue(){
		return QueueBuilder.durable(BiMqConstant.BI_DEAD_QUEUE_NAME).build();
	}

	@Bean
	DirectExchange BiDeadExchange() {
		return new DirectExchange(BiMqConstant.BI_DEAD_EXCHANGE_NAME);
	}


	@Bean
	Binding BiDeadBinding(Queue BiDeadQueue, DirectExchange BiDeadExchange) {
		return BindingBuilder.bind(BiDeadQueue).to(BiDeadExchange).with(BiMqConstant.BI_DEAD_ROUTING_KEY);
	}


	/**
	 * 声明bi队列和交换机
	 * @return
	 */
	@Bean
	Queue BiQueue(){
		//信息参数 设置TTL为1ms
		Map<String,Object> arg = new HashMap<>();
		arg.put("x-message-ttl",1);
		//绑定死信交换机
		arg.put("x-dead-letter-exchange",BiMqConstant.BI_DEAD_EXCHANGE_NAME);
		arg.put("x-dead-letter-routing-key",BiMqConstant.BI_DEAD_ROUTING_KEY);
		return QueueBuilder.durable(BiMqConstant.BI_QUEUE_NAME).withArguments(arg).build();
	}

	@Bean
	DirectExchange BiExchange() {
		return new DirectExchange(BiMqConstant.BI_EXCHANGE_NAME);
	}

	@Bean
	Binding BiBinding(Queue BiQueue, DirectExchange BiExchange) {
		return BindingBuilder.bind(BiQueue).to(BiExchange).with(BiMqConstant.BI_ROUTING_KEY);
	}

}
