package com.winterchen.delayserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


/**
 * 分布式锁
 * @author winterchen 2020/5/20
 */
@Component
public class RedisLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLock.class);

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String UNLOCK_LUA;

    /**
     * 释放锁脚本，原子操作
     */
    static {
        UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
                "then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end ";
    }

    /**
     * 获取分布式锁，原子操作
     *
     * @param lockKey   加锁的key
     * @param requestId 唯一ID, 可以使用UUID.randomUUID().toString();
     * @param expire    过期时间
     * @param timeUnit  时间单位
     * @return
     */
    public Boolean tryLock(String lockKey, String requestId, long expire, TimeUnit timeUnit) {
        try {
            RedisCallback<Boolean> callback = (connection) -> connection.set(lockKey.getBytes(StandardCharsets.UTF_8),
                    requestId.getBytes(StandardCharsets.UTF_8),
                    Expiration.seconds(timeUnit.toSeconds(expire)),
                    RedisStringCommands.SetOption.SET_IF_ABSENT);
            return (Boolean) redisTemplate.execute(callback);
        } catch (Exception e) {
            LOGGER.error("redis lock error.", e);
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param lockKey   加锁的key
     * @param requestId 唯一ID
     * @return
     */
    public Boolean releaseLock(String lockKey, String requestId) {
        RedisCallback<Boolean> callback = (connection) -> connection.eval(UNLOCK_LUA.getBytes(),
                ReturnType.BOOLEAN, 1,
                lockKey.getBytes(StandardCharsets.UTF_8),
                requestId.getBytes(StandardCharsets.UTF_8));
        return (Boolean) redisTemplate.execute(callback);
    }
}
