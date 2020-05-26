package com.winterchen.delayserver.controller;

import com.winterchen.delayserver.constants.ErrorCode;
import com.winterchen.delayserver.constants.RedisConstants;
import com.winterchen.delayserver.dto.APIResponse;
import com.winterchen.delayserver.dto.DefaultDelayMessageDTO;
import com.winterchen.delayserver.exception.BusinessException;
import com.winterchen.delayserver.service.DefaultDelayService;
import com.winterchen.delayserver.service.ProcessFailStrategyService;
import com.winterchen.delayserver.util.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Donghua.Chen 2020/5/20
 */
@RestController
@RequestMapping("/api/v1/default/delay")
public class DefaultDelayController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDelayController.class);

    @Autowired
    private DefaultDelayService defaultDelayService;

    @Autowired
    private ProcessFailStrategyService processFailStrategyService;

    @Autowired
    private RedisLock redisLock;

    @PostMapping("")
    public APIResponse pushDelayMessage(
            @RequestBody
            @Validated
            DefaultDelayMessageDTO message
    ) {
        String lockKey = RedisConstants.DEFAULT_DELAY_PUSH_MSG_LOCK + message.getId();
        if (!redisLock.tryLock(lockKey, message.getId(), 5, TimeUnit.SECONDS)) {
            LOGGER.error("重复提交");
            throw BusinessException.withErrorCode(ErrorCode.Delay.REPEATED_REQUESTS);
        }
        defaultDelayService.pushMessage(message);
        return APIResponse.success();
    }

    @GetMapping("/list/process-failed-message")
    public APIResponse listAllProcessFailedMessage() {
        return APIResponse.success(processFailStrategyService.listAllProcessFailedMessage());
    }

    @GetMapping("/list/push-failed-message")
    public APIResponse listAllPushFailedMessage(){
        return APIResponse.success(processFailStrategyService.listAllPushFailedMessage());
    }

    @GetMapping("/list/out-retry-message")
    public APIResponse listAllOutRetryCountMessage(){
        return APIResponse.success(processFailStrategyService.listAllOutRetryCountMessage());
    }
}
