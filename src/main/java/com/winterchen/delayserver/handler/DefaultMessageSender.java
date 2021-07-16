package com.winterchen.delayserver.handler;

import com.winterchen.delayserver.dto.DefaultCorrelationData;
import com.winterchen.delayserver.dto.DefaultDelayMessageDTO;
import com.winterchen.delayserver.service.ProcessFailStrategyService;
import com.winterchen.delayserver.strategy.ProcessFailStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author winterchen 2020/5/20
 */
@Service
public class DefaultMessageSender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Value("${com.winterchen.process.fail.store.strategy.code:REDIS}")
    private String code;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (!ack) {
            LOGGER.error("消息发送失败:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
            if (correlationData instanceof DefaultCorrelationData) {
                LOGGER.error("消息发送失败, 将重新发送: correlationData({}),ack({}),cause({})",correlationData,ack,cause);
                DefaultCorrelationData defaultCorrelationData = (DefaultCorrelationData) correlationData;
                sendMessage(defaultCorrelationData.getExchange(),
                        defaultCorrelationData.getRoutingKey(), defaultCorrelationData.getMessage(),
                        defaultCorrelationData.getMessage().getExpireTime());
            }
        } else {
            LOGGER.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        LOGGER.error("消息丢失: message({}),replyCode({}),replytext({}),exchange({}),routingKey({})",message,replyCode,replyText,exchange,routingKey);
        ProcessFailStrategyService processFailStrategyService = ProcessFailStrategyFactory.getByCode(code);
        processFailStrategyService.savePushFailedMessage(message);
    }


    public void sendMessage(String exchangeName, String routingKey, DefaultDelayMessageDTO message, Long expireTime) {
        DefaultCorrelationData correlationData = new DefaultCorrelationData(message);
        correlationData.setExchange(exchangeName);
        correlationData.setRoutingKey(routingKey);
        this.convertAndSend(correlationData, expireTime);
    }

    private void convertAndSend(DefaultCorrelationData correlationData, Long expireTime) {
        rabbitTemplate.convertAndSend(correlationData.getExchange(), correlationData.getRoutingKey(), correlationData.getMessage(), message -> {
            if (expireTime != null) {
                long expire = expireTime * 1000;
                message.getMessageProperties().setExpiration(String.valueOf(expire));
            }
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        }, correlationData);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rabbitTemplate.setReturnCallback(this);
        rabbitTemplate.setConfirmCallback(this);
    }
}
