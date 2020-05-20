package com.winterchen.delayserver.constants;

/**
 * redis 常量
 * @author Donghua.Chen 2020/5/19
 */
public interface RedisConstants {

    /**
     * 投递消息分布式锁
     */
    String DEFAULT_DELAY_PUSH_MSG_LOCK = "com.winterchen:delay:default:push:lock:";

    /**
     * 处理死信消息幂等性锁
     */
    String DEFAULT_DELAY_PROCESS_MSG_LOCK = "com.winterchen:delay:default:process:lock:";


    String DEFAULT_DELAY_PROCESS_RETRY_COUNT_PREFIX = "com.winterchen:delay:default:process:retrycount:";

}
