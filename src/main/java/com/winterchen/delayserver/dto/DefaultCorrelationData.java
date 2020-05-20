package com.winterchen.delayserver.dto;

import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 * @author Donghua.Chen 2020/5/20
 */
public class DefaultCorrelationData extends CorrelationData {

    private volatile DefaultDelayMessageDTO message;

    private String exchange;

    private String routingKey;

    private int retryCount = -1;

    public DefaultCorrelationData() {
    }

    public DefaultCorrelationData(String id) {
        super(id);
    }

    public DefaultCorrelationData(DefaultDelayMessageDTO message) {
        super(message.getId());
        this.message = message;
    }

    public DefaultCorrelationData(String id, DefaultDelayMessageDTO message) {
        super(id);
        this.message = message;
    }

    public DefaultDelayMessageDTO getMessage() {
        return message;
    }

    public void setMessage(DefaultDelayMessageDTO message) {
        this.message = message;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultCorrelationData{");
        sb.append("message=").append(message);
        sb.append(", exchange='").append(exchange).append('\'');
        sb.append(", routingKey='").append(routingKey).append('\'');
        sb.append(", retryCount=").append(retryCount);
        sb.append('}');
        return sb.toString();
    }
}
