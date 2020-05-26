package com.winterchen.delayserver.service;

import com.winterchen.delayserver.dto.DefaultDelayMessageDTO;

/**
 * @author winterchen 2020/5/19
 */
public interface DefaultDelayService {

    void pushMessage(DefaultDelayMessageDTO messageDTO);

    void pushMessage(DefaultDelayMessageDTO messageDTO, Long expireTime);

    void processAndCallback(DefaultDelayMessageDTO messageDTO, DefaultDelayService defaultDelayService);
}
