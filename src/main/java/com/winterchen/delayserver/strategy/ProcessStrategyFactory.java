package com.winterchen.delayserver.strategy;

import com.winterchen.delayserver.constants.ErrorCode;
import com.winterchen.delayserver.exception.BusinessException;
import com.winterchen.delayserver.service.ProcessStrategyService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 失败消息存储策略
 * @author winterchen 2020/5/21
 */
public class ProcessStrategyFactory {

    private static Map<String, ProcessStrategyService> processFailStrategyServiceMap = new ConcurrentHashMap<>();

    public static ProcessStrategyService getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            throw BusinessException.withErrorCode(ErrorCode.Common.ILLEGAL_DATA);
        }
        code = code.toUpperCase().trim();
        return processFailStrategyServiceMap.get(code);
    }

    public static void register(String code, ProcessStrategyService processFailStrategyService) {
        Assert.notNull(code, "code can`t not be null");
        processFailStrategyServiceMap.put(code, processFailStrategyService);
    }
}
