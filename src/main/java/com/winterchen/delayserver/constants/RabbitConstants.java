package com.winterchen.delayserver.constants;

/**
 * @author winterchen 2020/5/19
 */
public interface RabbitConstants {

    String DELAY_QUEUE_PER_QUEUE_TTL_NAME = "default.delay.queue.per.queue.ttl";

    /**
     * 死信队列名称
     */
    String DELAY_QUEUE_NAME = "default.delay.exchange";


    /**
     * 死信交换机
     */
    String DEFAULT_DELAY_EXCHANGE = "default.delay.exchange";


    String DEFAULT_DELAY_KEY = "default.delay.key";

    /**
     * 处理死信队列
     */
    String DEFAULT_DELAY_PROCESS_QUEUE_NAME = "default.delay.process.queue";

    /**
     * 处理死信交换机
     */
    String DEFAULT_DELAY_PROCESS_EXCHANGE_NAME = "default.delay.process.exchange";

    String DEFAULT_DELAY_PROCESS_KEY = "defalut.delay.process.key";

}
