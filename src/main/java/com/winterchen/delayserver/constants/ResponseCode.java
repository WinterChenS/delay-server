package com.winterchen.delayserver.constants;

/**
 * 响应码
 * @author Donghua.Chen 2020/5/19
 */
public enum ResponseCode {

    CODE200(200, "success"),
    CODE400(400, "error");

    private Integer code;

    private String desc;

    ResponseCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
