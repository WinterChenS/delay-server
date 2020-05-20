package com.winterchen.delayserver.controller;

import com.winterchen.delayserver.dto.APIResponse;
import com.winterchen.delayserver.dto.DefaultDelayMessageDTO;
import com.winterchen.delayserver.service.DefaultDelayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author Donghua.Chen 2020/5/20
 */
@RestController
@RequestMapping("/default/delay")
public class DefaultDelayController {

    @Autowired
    private DefaultDelayService defaultDelayService;

    @PostMapping("")
    public APIResponse pushDelayMessage(
            @RequestBody
            @Validated
            DefaultDelayMessageDTO message
    ) {
        defaultDelayService.pushMessage(message);
        return APIResponse.success();
    }
}
