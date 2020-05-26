package com.winterchen.delayserver.dto;

import com.winterchen.delayserver.constants.ResponseCode;
import com.winterchen.delayserver.util.CodeConvertUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 调用方接收到延迟消息的回调
 * @author Donghua.Chen 2020/5/19
 */
public class APIResponse implements Serializable {

    private static final long serialVersionUID = 8446324418525575338L;


    private Integer code;

    private Object data;

    private String error;

    private String message;


    private Long timestamp;


    public APIResponse() {
        this(new Builder());
    }

    public APIResponse(Builder builder) {
        this.code = builder.code;
        this.data = builder.data;
        this.message = builder.message;
        this.timestamp = builder.timestamp;
        this.error = builder.error;
    }

    public boolean isSuccess() {
        boolean result = true;
        if (code != 200) {
            result = false;
        }
        return result;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static class Builder {

        private Integer code;

        private Object data;

        private String error;

        private String message;

        private Long timestamp;

        public Builder() {
        }

        public Builder(APIResponse APIResponse) {
            this.code = APIResponse.code;
            this.data = APIResponse.data;
            this.error = APIResponse.error;
            this.message = APIResponse.message;
            this.timestamp = APIResponse.timestamp;
        }



        public Builder setCode(Integer code) {
            this.code = code;
            return this;
        }


        public Builder setData(Object data) {
            this.data = data;
            return this;
        }

        public Builder setError(String error) {
            this.error = error;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public APIResponse build() {
            return new APIResponse(this);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Builder{");
            sb.append("code=").append(code);
            sb.append(", data=").append(data);
            sb.append(", error='").append(error).append('\'');
            sb.append(", message='").append(message).append('\'');
            sb.append(", timestamp=").append(timestamp);
            sb.append('}');
            return sb.toString();
        }
    }

    public static APIResponse success() {
        return new APIResponse.Builder()
                .setCode(ResponseCode.CODE200.getCode())
                .setMessage(ResponseCode.CODE200.getDesc())
                .setTimestamp(System.currentTimeMillis())
                .build();
    }

    public static APIResponse success(Object data) {
        return new APIResponse.Builder()
                .setCode(ResponseCode.CODE200.getCode())
                .setMessage(ResponseCode.CODE200.getDesc())
                .setTimestamp(System.currentTimeMillis())
                .setData(data)
                .build();
    }

    public static APIResponse fail(String error) {
        return new APIResponse.Builder()
                .setCode(ResponseCode.CODE400.getCode())
                .setError(error)
                .setMessage(CodeConvertUtil.convertCodeToMsg(error))
                .setTimestamp(new Date().getTime())
                .build();
    }

    public APIResponse withData(Object data) {
        this.data = data;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
