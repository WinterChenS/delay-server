package com.winterchen.delayserver.handler;

import com.rabbitmq.client.Channel;
import com.winterchen.delayserver.constants.RabbitConstants;
import com.winterchen.delayserver.constants.RedisConstants;
import com.winterchen.delayserver.dto.DefaultDelayMessageDTO;
import com.winterchen.delayserver.service.DefaultDelayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RabbitListener(queues = {RabbitConstants.DEFAULT_DELAY_PROCESS_QUEUE_NAME})
    public void listenDefaultDelayQueue(DefaultDelayMessageDTO messageDTO, Message message, Channel channel) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("[listen default delay queue 监听的消息] - [消费时间] - [{}] - [{}]", LocalDateTime.now(), messageDTO.toString());
        }

        //避免重复消费
        String lockKey = RedisConstants.DEFAULT_DELAY_PROCESS_MSG_LOCK + messageDTO.getId();
        if (redisTemplate.opsForValue().get(lockKey) != null) {
            return;
        }
        redisTemplate.opsForValue().set(lockKey, messageDTO, 3, TimeUnit.SECONDS);

        try {
            checkProcessRetryCount(messageDTO.getId(), messageDTO.getRetryCount(), message, channel);
            defaultDelayService.processAndCallback(messageDTO);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            LOGGER.error("处理延迟消息失败", e);
            try {
                /**
                 * 如果出现任何异常，都是把消息重新发送到死信队列，防止回调服务问题导致队列一直在阻塞
                 */
                messageDTO.setExpireTime(1000L);
                defaultDelayService.pushMessage(messageDTO);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException ex) {
                LOGGER.error("将消息放回队列失败", e);
            }
        }
    }

    private void checkProcessRetryCount(String id, Integer maxRetryCount, Message message, Channel channel) throws IOException {
        // 小于0表示可以无限重试
        if (maxRetryCount < 0) {
            return;
        }
        String key = RedisConstants.DEFAULT_DELAY_PROCESS_RETRY_COUNT_PREFIX + id;
        Integer retryCount = (Integer) redisTemplate.opsForValue().get(key);
        long timeExpire = 30 * 60;
        if (retryCount != null) {
            if (maxRetryCount <= retryCount) {
                LOGGER.error("重试次数已达到上限，将从队列进行删除");
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            retryCount ++;
            redisTemplate.opsForValue().set(key, retryCount, timeExpire, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, 0, timeExpire, TimeUnit.SECONDS);
        }
    }

}
