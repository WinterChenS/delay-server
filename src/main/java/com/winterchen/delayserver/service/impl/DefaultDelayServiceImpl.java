package com.winterchen.delayserver.service.impl;

import com.google.gson.Gson;
import com.winterchen.delayserver.constants.ErrorCode;
import com.winterchen.delayserver.constants.RabbitConstants;
import com.winterchen.delayserver.constants.RedisConstants;
import com.winterchen.delayserver.dto.APIResponse;
import com.winterchen.delayserver.dto.DefaultDelayMessageDTO;
import com.winterchen.delayserver.exception.BusinessException;
import com.winterchen.delayserver.handler.DefaultMessageSender;
import com.winterchen.delayserver.service.DefaultDelayService;
import com.winterchen.delayserver.util.HttpRequestUtil;
import com.winterchen.delayserver.util.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author winterchen 2020/5/19
 */
@Service
public class DefaultDelayServiceImpl implements DefaultDelayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDelayServiceImpl.class);

    @Autowired
    private DefaultMessageSender defaultMessageSender;

    @Autowired
    private RedisLock redisLock;

    @Override
    public void pushMessage(DefaultDelayMessageDTO messageDTO) {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("延迟消息投递开始");
        }

        String lockKey = RedisConstants.DEFAULT_DELAY_PUSH_MSG_LOCK + messageDTO.getId();
        if (!redisLock.tryLock(lockKey, messageDTO.getId(), 30, TimeUnit.MINUTES)) {
            LOGGER.error("重复提交");
            throw BusinessException.withErrorCode(ErrorCode.Delay.REPEATED_REQUESTS);
        }

        // 添加延时队列
        defaultMessageSender.sendMessage(RabbitConstants.DEFAULT_DELAY_EXCHANGE, RabbitConstants.DEFAULT_DELAY_KEY, messageDTO);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("延迟消息投递成功");
        }

    }

    @Override
    public void processAndCallback(DefaultDelayMessageDTO messageDTO) throws Exception{
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("开始处理请求回调");
        }
        String responseStr = HttpRequestUtil.get(messageDTO.getCallbackPath());
        if (StringUtils.isEmpty(responseStr)) {
            LOGGER.error("请求回调地址失败");
            throw BusinessException.withErrorCode(ErrorCode.Delay.REMOTE_BAD_RESPONSE);
        }
        Gson gson = new Gson();
        APIResponse apiResponse = gson.fromJson(responseStr, APIResponse.class);
        if (!apiResponse.isSuccess()) {
            LOGGER.error("请求回调地址失败 {}", responseStr);
            throw BusinessException.withErrorCode(ErrorCode.Delay.REMOTE_RESPOSE_FAIL);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("回调成功");
        }
    }
}
