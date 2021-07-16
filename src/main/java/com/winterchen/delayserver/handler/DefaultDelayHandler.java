package com.winterchen.delayserver.handler;

import com.rabbitmq.client.Channel;
import com.winterchen.delayserver.constants.RabbitConstants;
import com.winterchen.delayserver.constants.RedisConstants;
import com.winterchen.delayserver.dto.DefaultDelayMessageDTO;
import com.winterchen.delayserver.service.DefaultDelayService;
import com.winterchen.delayserver.service.ProcessStrategyService;
import com.winterchen.delayserver.strategy.ProcessStrategyFactory;
import com.winterchen.delayserver.util.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class DefaultDelayHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDelayHandler.class);

    @Autowired
    private DefaultDelayService defaultDelayService;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private RedisLock redisLock;

    @Value("${com.winterchen.fail.store.strategy.code:REDIS}")
    private String stategryCode;

    @RabbitListener(queues = {RabbitConstants.DEFAULT_DELAY_PROCESS_QUEUE_NAME})
    public void listenDefaultDelayQueue(DefaultDelayMessageDTO messageDTO, Message message, Channel channel) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("[listen default delay queue 监听的消息] - [消费时间] - [{}] - [{}]", LocalDateTime.now(), messageDTO.toString());
        }

        //避免重复消费
        String lockKey = RedisConstants.DEFAULT_DELAY_PROCESS_MSG_LOCK + messageDTO.getId();
        if (!redisLock.tryLock(lockKey, messageDTO.getId(), 1L, TimeUnit.SECONDS)) {
            return;
        }

        try {
            boolean checkProcessRetryCount = checkProcessRetryCount(messageDTO, channel, message);
            if (checkProcessRetryCount) {
                defaultDelayService.processAndCallback(messageDTO, defaultDelayService);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (Exception e) {
            LOGGER.error("处理延迟消息失败", e);
            try {
                /**
                 * 如果出现任何异常，都是把消息重新发送到死信队列，防止回调服务问题导致队列一直在阻塞
                 */
                long retryExpireTime = 2L;
                defaultDelayService.pushMessage(messageDTO, retryExpireTime);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException ex) {
                LOGGER.error("将消息放回队列失败, 将消息存入到redis中", e);
                ProcessStrategyService processFailStrategyService = ProcessStrategyFactory.getByCode(stategryCode);
                processFailStrategyService.saveProcessFailedMessage(messageDTO);
                try {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                } catch (IOException exc) {
                    LOGGER.error("返回ACK失败", e);
                }
            }
        }
    }

    private boolean checkProcessRetryCount(DefaultDelayMessageDTO defaultDelayMessageDTO, Channel channel, Message message) throws IOException {
        // 小于0表示可以无限重试
        if (defaultDelayMessageDTO.getRetryCount() < 0) {
            return true;
        }
        String key = RedisConstants.DEFAULT_DELAY_PROCESS_RETRY_COUNT_PREFIX + defaultDelayMessageDTO.getId();
        Integer retryCount = (Integer) redisTemplate.opsForValue().get(key);
        long expireTime = 30 * 60;
        if (retryCount != null) {
            if (defaultDelayMessageDTO.getRetryCount() <= retryCount) {
                LOGGER.error("重试次数已达到上限，将从队列进行删除, 并将数据存入数据库");
                ProcessStrategyService processFailStrategyService = ProcessStrategyFactory.getByCode(stategryCode);
                processFailStrategyService.saveOutRetryCountMessage(defaultDelayMessageDTO);
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                redisTemplate.delete(key);
                return false;
            }
            retryCount ++;
            redisTemplate.opsForValue().set(key, retryCount, expireTime, TimeUnit.SECONDS);
            LOGGER.info("重试第[] 次", retryCount);
            return true;
        } else {
            redisTemplate.opsForValue().set(key, 0, expireTime, TimeUnit.SECONDS);
            return true;
        }
    }

}
