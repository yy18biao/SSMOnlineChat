package com.chat.sms;

import com.alibaba.fastjson2.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SMSService {
    @Resource
    private Client client;

    @Value("${sms.aliyun.templateCode:}")
    private String templateCode;

    @Value("${sms.aliyun.singName:}")
    private String singName;

    // 发送验证码
    public boolean sendPhoneCode(String phone, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);

        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setPhoneNumbers(phone);
        sendSmsRequest.setSignName(singName);
        sendSmsRequest.setTemplateCode(templateCode);
        sendSmsRequest.setTemplateParam(JSON.toJSONString(params));
        try {
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            SendSmsResponseBody responseBody = sendSmsResponse.getBody();
            if (!"OK".equalsIgnoreCase(responseBody.getCode())) {
                log.error("短信{} 发送失败，失败原因: {}", JSON.toJSONString(sendSmsRequest), responseBody.getMessage());
                return false;
            }
            return true;
        }  catch (Exception e) {
            log.error("短信{} 发送失败，失败原因: {}",  JSON.toJSONString(sendSmsRequest), e.getMessage());
            return false;
        }
    }
}
