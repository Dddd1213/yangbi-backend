package com.example.yangbibackend.manager;

import com.example.yangbibackend.common.enumeration.ErrorCode;
import com.example.yangbibackend.common.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AiManager {

    @Resource
    private YuCongMingClient yuCongMingClient;

    /**
     * ai对话
     * @param message
     * @return
     */
    public String doChart(String message){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(1659171950288818178L);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        if(response==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"ai响应错误");
        }
        System.out.println("111"+response.getData());
        return response.getData().getContent();
    }

}
