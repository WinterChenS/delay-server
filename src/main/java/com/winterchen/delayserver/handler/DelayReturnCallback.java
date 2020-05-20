package com.winterchen.delayserver.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


/**
 * @author Donghua.Chen 2020/5/20
 */
public class DelayReturnCallback implements RabbitTemplate.ReturnCallback {


    private static final Logger LOGGER = LoggerFactory.getLogger(DelayReturnCallback.class);




    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        LOGGER.error("消息丢失: message({}),replyCode({}),replytext({}),exchange({}),routingKey({})",message,replyCode,replyText,exchange,routingKey);
    }
}
