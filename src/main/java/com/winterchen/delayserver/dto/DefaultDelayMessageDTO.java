package com.winterchen.delayserver.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author winterchen 2020/5/19
 */
public class DefaultDelayMessageDTO implements Serializable {

    private static final long serialVersionUID = 8446324418945575338L;
    /**
     * 消息id，保证唯一性
     */
    @NotBlank(message = "消息Id不能为空")
    private String id;
    /**
     * 超时时间
     */
    @NotNull(message = "超时时间不能为空")
    @Min(value = 0, message = "超时时间必须大于0")
    private Long expireTime;

    /**
     * 消息体
     */
    @NotBlank(message = "消息不能为空")
    private String message;

    /**
     * 回调地址
     */
    @NotBlank(message = "回调地址不能为空")
    @Pattern(regexp = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]", message = "请输入正确的url")
    private String callbackPath;

    /**
     * 调用方当前服务器时间
     */
    @NotNull(message = "当前系统时间不能为空")
    private Long currentTime;

    /**
     * 重试次数： -1 无限重试（默认）； 0 不重试
     */
    private int retryCount = -1;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCallbackPath() {
        return callbackPath;
    }

    public void setCallbackPath(String callbackPath) {
        this.callbackPath = callbackPath;
    }

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultDelayMessageDTO{");
        sb.append("id='").append(id).append('\'');
        sb.append(", expireTime=").append(expireTime);
        sb.append(", message='").append(message).append('\'');
        sb.append(", callbackPath='").append(callbackPath).append('\'');
        sb.append(", currentTime=").append(currentTime);
        sb.append(", retryCount=").append(retryCount);
        sb.append('}');
        return sb.toString();
    }
}
