package com.winterchen.delayserver.controller;

import com.winterchen.delayserver.constants.ErrorCode;
import com.winterchen.delayserver.dto.APIResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
/**
 * @author Donghua.Chen 2020/5/20
 */
@RestController
@RequestMapping("/test")
public class TestCallbackController {

    @GetMapping("/success")
    public APIResponse callbackSuccess(){
        return APIResponse.success();
    }

    @GetMapping("/fail")
    public APIResponse callbackFail(){
        return APIResponse.fail(ErrorCode.Common.SYSTEM_ERROR);
    }

    @GetMapping("/uuid")
    public APIResponse createUUID() {
        return APIResponse.success(UUID.randomUUID());
    }
}
