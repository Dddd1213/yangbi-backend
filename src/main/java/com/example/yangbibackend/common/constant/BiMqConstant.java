package com.example.yangbibackend.common.constant;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

public interface BiMqConstant {

    String BI_EXCHANGE_NAME = "bi_exchange";

    String BI_QUEUE_NAME = "bi_queue";

    String BI_ROUTING_KEY = "bi_routingKey";

    String BI_DEAD_QUEUE_NAME = "bi_dead_queue";

    String BI_DEAD_EXCHANGE_NAME = "bi_dead_exchange";

    String  BI_DEAD_ROUTING_KEY = "bi_dead_routingKey";

}
