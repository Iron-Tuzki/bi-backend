package com.yupi.springbootinit.manager;

import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lanshu
 * @date 2023-07-07
 */
@Service
@Slf4j
public class AiManager {

    @Resource
    public YuCongMingClient yuCongMingClient;


    /**
     * Ai对话
     *
     * @param message
     * @return
     */
    public synchronized String doChat(Long modelId, String message, String queueName) {
        if (StringUtils.isNotBlank(queueName)) {
            log.info("当前线程：" + Thread.currentThread().getName() + "\n"
                    + "当前消费者" + queueName + "正在调用AI");
        }

        DevChatRequest devChatRequest = new DevChatRequest();

        devChatRequest.setModelId(modelId);

        devChatRequest.setMessage(message);

        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);

        return response.getData().getContent();
    }

}
