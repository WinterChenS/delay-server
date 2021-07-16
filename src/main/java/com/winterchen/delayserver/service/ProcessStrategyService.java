package com.winterchen.delayserver.service;

import com.winterchen.delayserver.dto.DefaultDelayMessageDTO;
import org.springframework.amqp.core.Message;

import java.util.Set;

/**
 * 处理失败的消息存储服务
 * @author winterchen 2020/5/21
 */
public interface ProcessStrategyService {


    /**
     * 保存等待处理的消息
     * @param messageDTO
     */
    void saveProcessWaitMessage(DefaultDelayMessageDTO messageDTO);

    /**
     * 移除等待处理的消息
     * @param messageDTO
     */
    void deleteProcessWaitMessage(DefaultDelayMessageDTO messageDTO);

    /**
     * 查询所有等待处理的消息
     * @return
     */
    Set<DefaultDelayMessageDTO> listAllProcessWaitMessage();


    /**
     * 查询所有处理失败的消息
     * @return
     */
    Set<DefaultDelayMessageDTO> listAllProcessFailedMessage();

    /**
     * 保存处理失败的消息
     * @param messageDTO
     */
    void saveProcessFailedMessage(DefaultDelayMessageDTO messageDTO);


    /**
     * 查询所有投递失败的消息
     * @return
     */
    Set<Message> listAllPushFailedMessage();

    /**
     * 保存投递失败的消息
     * @param messageDTO
     */
    void savePushFailedMessage(Message messageDTO);

    /**
     * 查询超过重试次数的消息
     * @return
     */
    Set<DefaultDelayMessageDTO> listAllOutRetryCountMessage();


    /**
     * 保存超过重试次数的消息
     * @param messageDTO
     */
    void saveOutRetryCountMessage(DefaultDelayMessageDTO messageDTO);

}
