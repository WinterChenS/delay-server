package com.winterchen.delayserver.config;

import com.winterchen.delayserver.constants.RabbitConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author winterchen 2020/5/19
 */
@Configuration
public class RabbitmqConfiguration {


    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory){
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }



    /**
     * 死信队列
     * @return
     */
    @Bean
    public Queue defaultDelayQueue() {
        return QueueBuilder.durable(RabbitConstants.DELAY_QUEUE_PER_QUEUE_TTL_NAME)
                .withArgument("x-dead-letter-exchange", RabbitConstants.DEFAULT_DELAY_PROCESS_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", RabbitConstants.DEFAULT_DELAY_PROCESS_KEY)
                .build();
    }

    /**
     * 死信交换机
     * @return
     */
    @Bean
    public DirectExchange defaultDelayExchange() {
        return new DirectExchange(RabbitConstants.DEFAULT_DELAY_EXCHANGE,true,false);
    }

    /**
     * 绑定死信队列和死信交换机
     * @param defaultDelayQueue
     * @param defaultDelayExchange
     * @return
     */
    @Bean
    public Binding defaultDelayBinging(Queue defaultDelayQueue, DirectExchange defaultDelayExchange) {
        return BindingBuilder.bind(defaultDelayQueue).to(defaultDelayExchange).with(RabbitConstants.DEFAULT_DELAY_KEY);
    }


    /**
     * 处理死信的队列
     * @return
     */
    @Bean
    public Queue defaultDelayProcessQueue() {
        return new Queue(RabbitConstants.DEFAULT_DELAY_PROCESS_QUEUE_NAME, true);
    }


    /**
     * 处理死信的交换机
     * @return
     */
    @Bean
    public DirectExchange defaultDelayProcessExchange() {
        return new DirectExchange(RabbitConstants.DEFAULT_DELAY_PROCESS_EXCHANGE_NAME,true, false);
    }

    /**
     * 绑定处理死信的队列和交换机
     * @param defaultDelayProcessQueue
     * @param defaultDelayProcessExchange
     * @return
     */
    @Bean
    public Binding defaultDelayProcessBinding(Queue defaultDelayProcessQueue, DirectExchange defaultDelayProcessExchange) {
        return BindingBuilder.bind(defaultDelayProcessQueue).to(defaultDelayProcessExchange).with(RabbitConstants.DEFAULT_DELAY_PROCESS_KEY);
    }


}
