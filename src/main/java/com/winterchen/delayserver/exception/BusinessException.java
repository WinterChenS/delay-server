package com.winterchen.delayserver.exception;

import com.winterchen.delayserver.util.CodeConvertUtil;

/**
 * @author Donghua.Chen 2020/5/20
 */
public class BusinessException extends RuntimeException{

    private String errorCode;

    public BusinessException() {
    }

    public BusinessException(String errorCode) {
        super(CodeConvertUtil.convertCodeToMsg(errorCode));
        String msg = CodeConvertUtil.convertCodeToMsg(errorCode);
        this.errorCode = errorCode;
    }

    public static BusinessException withErrorCode(String errorCode) {
        return new BusinessException(errorCode);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
