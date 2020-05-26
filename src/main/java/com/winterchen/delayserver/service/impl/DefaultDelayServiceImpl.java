package com.winterchen.delayserver.service.impl;

import com.google.gson.Gson;
import com.winterchen.delayserver.constants.RabbitConstants;
import com.winterchen.delayserver.dto.APIResponse;
import com.winterchen.delayserver.dto.DefaultDelayMessageDTO;
import com.winterchen.delayserver.handler.DefaultMessageSender;
import com.winterchen.delayserver.service.DefaultDelayService;
import com.winterchen.delayserver.util.HttpRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author winterchen 2020/5/19
 */
@Service
public class DefaultDelayServiceImpl implements DefaultDelayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDelayServiceImpl.class);

    @Autowired
    private DefaultMessageSender defaultMessageSender;

    @Override
    public void pushMessage(DefaultDelayMessageDTO messageDTO) {

        pushMessage(messageDTO, messageDTO.getExpireTime());

    }

    @Override
    public void pushMessage(DefaultDelayMessageDTO messageDTO, Long expireTime) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("延迟消息投递开始");
        }

        // 添加延时队列
        defaultMessageSender.sendMessage(RabbitConstants.DEFAULT_DELAY_EXCHANGE, RabbitConstants.DEFAULT_DELAY_KEY, messageDTO, expireTime);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("延迟消息投递成功");
        }
    }


    @Async
    @Override
    public void processAndCallback(DefaultDelayMessageDTO messageDTO, DefaultDelayService defaultDelayService){
        /**
         * 此方法为异步，防止同步调用阻塞队列的消费，如果回调失败将重新回到死信队列，重试次数的判断在消费方法中，只要id不变就会增加重试次数
         */
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("开始处理请求回调");
        }
        String responseStr = HttpRequestUtil.get(messageDTO.getCallbackPath());
        boolean success = true;
        if (StringUtils.isEmpty(responseStr)) {
            LOGGER.error("请求回调地址失败");
            success = false;
        } else {
            Gson gson = new Gson();
            APIResponse apiResponse = gson.fromJson(responseStr, APIResponse.class);
            if (!apiResponse.isSuccess()) {
                LOGGER.error("请求回调地址失败 {}", responseStr);
                success = false;
            }
        }

        //回调失败，将消息重新放回队列
        if (!success) {
            LOGGER.error("回调失败，将消息重新push到死信队列");
            defaultDelayService.pushMessage(messageDTO, 2L);
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("回调成功");
        }
    }
}
