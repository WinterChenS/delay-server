package com.winterchen.delayserver.handler;

import com.winterchen.delayserver.constants.ErrorCode;
import com.winterchen.delayserver.dto.APIResponse;
import com.winterchen.delayserver.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Donghua.Chen 2020/5/20
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public APIResponse handleBusinessException(BusinessException ex, HttpServletRequest request) {
        LOGGER.error("throw business exception", ex);
        return APIResponse.fail(ex.getErrorCode());
    }

    @ExceptionHandler(value= {MethodArgumentNotValidException.class , BindException.class})
    public APIResponse handleVaildException(Exception e){
        LOGGER.error("request params error", e);
        BindingResult bindingResult = null;
        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException)e).getBindingResult();
        } else if (e instanceof BindException) {
            bindingResult = ((BindException)e).getBindingResult();
        }
        Map<String,String> errorMap = new HashMap<>(16);
        bindingResult.getFieldErrors().forEach((fieldError)->
                errorMap.put(fieldError.getField(),fieldError.getDefaultMessage())
        );
        return APIResponse.fail(ErrorCode.Common.ILLEGAL_DATA).withData(errorMap);
    }


    @ExceptionHandler(Throwable.class)
    public APIResponse handleBaseException(Throwable e, HttpServletRequest request) {
        LOGGER.error("throw error", e);
        return APIResponse.fail(ErrorCode.Common.SYSTEM_ERROR);
    }





}
