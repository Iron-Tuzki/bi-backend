package com.yupi.springbootinit.manager;

import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lanshu
 * @date 2023-07-07
 */
@Service
public class AiManager {

    @Resource
    public YuCongMingClient yuCongMingClient;


    /**
     * Ai对话
     * @param message
     * @return
     */
    public String doChat(Long modelId, String message) {
        DevChatRequest devChatRequest = new DevChatRequest();

        devChatRequest.setModelId(modelId);

        devChatRequest.setMessage(message);

        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);

        return response.getData().getContent();
    }

}
