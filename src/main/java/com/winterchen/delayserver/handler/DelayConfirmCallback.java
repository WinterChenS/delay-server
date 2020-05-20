package com.winterchen.delayserver.handler;

import com.winterchen.delayserver.dto.DefaultCorrelationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


/**
 * @author Donghua.Chen 2020/5/20
 */
public class DelayConfirmCallback implements RabbitTemplate.ConfirmCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayConfirmCallback.class);


    private RabbitTemplate rabbitTemplate;

    public DelayConfirmCallback() {
    }

    public DelayConfirmCallback(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            LOGGER.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
        } else {
            LOGGER.error("消息发送失败:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
            if (correlationData instanceof DefaultCorrelationData) {
            LOGGER.error("消息发送失败, 将重新发送: correlationData({}),ack({}),cause({})",correlationData,ack,cause);
                DefaultCorrelationData defaultCorrelationData = (DefaultCorrelationData) correlationData;
                rabbitTemplate.convertAndSend(defaultCorrelationData.getExchange(),
                        defaultCorrelationData.getRoutingKey(), defaultCorrelationData.getMessage(), defaultCorrelationData);
            }
        }
    }
}
