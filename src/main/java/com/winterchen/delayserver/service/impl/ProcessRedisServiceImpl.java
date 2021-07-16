package com.winterchen.delayserver.service.impl;

import com.winterchen.delayserver.constants.RedisConstants;
import com.winterchen.delayserver.dto.DefaultDelayMessageDTO;
import com.winterchen.delayserver.service.ProcessStrategyService;
import com.winterchen.delayserver.strategy.ProcessStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Service
public class ProcessRedisServiceImpl implements ProcessStrategyService, InitializingBean {

    private static final String REDIS = "REDIS";


    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessRedisServiceImpl.class);


    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;


    @Override
    public void saveProcessWaitMessage(DefaultDelayMessageDTO messageDTO) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("保存等待处理的消息到redis");
        }
        redisTemplate.opsForSet().add(RedisConstants.DEFAULT_SAVE_DELAY_MESSAGG_BY_WAIT_PROCESS_SET, messageDTO);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("保存等待处理的消息到redis成功");
        }
    }

    @Override
    public void deleteProcessWaitMessage(DefaultDelayMessageDTO messageDTO) {
        redisTemplate.opsForSet().remove(RedisConstants.DEFAULT_SAVE_DELAY_MESSAGG_BY_WAIT_PROCESS_SET, messageDTO);
    }

    @Override
    public Set<DefaultDelayMessageDTO> listAllProcessWaitMessage() {
        Set<Serializable> members = redisTemplate.opsForSet().members(RedisConstants.DEFAULT_SAVE_DELAY_MESSAGG_BY_WAIT_PROCESS_SET);
        Set<DefaultDelayMessageDTO> result = new HashSet<>(members != null ? members.size() : 0);
        if (members != null && 0 < members.size()) {
            Iterator<Serializable> iterator = members.iterator();
            while (iterator.hasNext()) {
                result.add((DefaultDelayMessageDTO) iterator.next());
            }
        }
        return result;
    }


    @Override
    public void saveProcessFailedMessage(DefaultDelayMessageDTO messageDTO) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("保存处理失败的消息到redis");
        }
        redisTemplate.opsForSet().add(RedisConstants.DEFAULT_SAVE_DELAY_MESSAGG_BY_PROCESS_FAILED_SET, messageDTO);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("保存处理失败的消息到redis成功");
        }
    }

    @Override
    public void savePushFailedMessage(Message messageDTO) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("保存处理失败的消息到redis");
        }
        redisTemplate.opsForSet().add(RedisConstants.DEFAULT_SAVE_DELAY_MESSAGG_BY_PUSH_FAILED_SET, messageDTO);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("保存处理失败的消息到redis成功");
        }
    }

    @Override
    public void saveOutRetryCountMessage(DefaultDelayMessageDTO messageDTO) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("保存超过重试次数被删除的消息到redis");
        }
        redisTemplate.opsForSet().add(RedisConstants.DEFAULT_SAVE_DELAY_MESSAGG_BY_OUT_RETRY_SET, messageDTO);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("保存超过重试次数被删除的消息到redis成功");
        }
    }

    @Override
    public Set<DefaultDelayMessageDTO> listAllProcessFailedMessage() {
        Set<Serializable> members = redisTemplate.opsForSet().members(RedisConstants.DEFAULT_SAVE_DELAY_MESSAGG_BY_PROCESS_FAILED_SET);
        Set<DefaultDelayMessageDTO> result = new HashSet<>(members != null ? members.size() : 0);
        if (members != null && 0 < members.size()) {
            Iterator<Serializable> iterator = members.iterator();
            while (iterator.hasNext()) {
                result.add((DefaultDelayMessageDTO) iterator.next());
            }
        }
        return result;
    }

    @Override
    public Set<Message> listAllPushFailedMessage() {
        Set<Serializable> members = redisTemplate.opsForSet().members(RedisConstants.DEFAULT_SAVE_DELAY_MESSAGG_BY_PUSH_FAILED_SET);
        Set<Message> result = new HashSet<>(members != null ? members.size() : 0);
        if (members != null && 0 < members.size()) {
            Iterator<Serializable> iterator = members.iterator();
            while (iterator.hasNext()) {
                result.add((Message) iterator.next());
            }
        }
        return result;
    }

    @Override
    public Set<DefaultDelayMessageDTO> listAllOutRetryCountMessage() {
        Set<Serializable> members = redisTemplate.opsForSet().members(RedisConstants.DEFAULT_SAVE_DELAY_MESSAGG_BY_OUT_RETRY_SET);
        Set<DefaultDelayMessageDTO> result = new HashSet<>(members != null ? members.size() : 0);
        if (members != null && 0 < members.size()) {
            Iterator<Serializable> iterator = members.iterator();
            while (iterator.hasNext()) {
                result.add((DefaultDelayMessageDTO) iterator.next());
            }
        }
        return result;
    }



    @Override
    public void afterPropertiesSet() throws Exception {
        ProcessStrategyFactory.register(REDIS, this);
    }
}