package com.baidu.fbu.mtp.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.fbu.mtp.model.RequestMsg;
import com.baidu.fbu.mtp.model.VerificationType;

@Service
public class IPService {

    public String getRemoteIp(HttpServletRequest request, RequestMsg requestMsg) {
        String ip = getWebIp(requestMsg);
        if (ip != null) {
            return ip;
        }
        if (request.getHeader("x-forwarded-for") == null) {
            return request.getRemoteAddr();
        }
        return request.getHeader("x-forwarded-for");
    }

    private String getWebIp(RequestMsg requestMsg) {
        JSONObject dataJson = requestMsg.getDataJson();
        JSONObject methodData = dataJson.getJSONObject("methoddata");
        if (methodData == null) {
            return null;
        }
        JSONArray verificationArray = methodData.getJSONArray("verifications");
        if (verificationArray != null && verificationArray.size() > 0) {
            for (int i = 0; i < verificationArray.size(); i++) {
                JSONObject verificationJson = verificationArray
                        .getJSONObject(i);
                int type = verificationJson.getIntValue("type");
                String content = verificationJson.getString("content");
                JSONObject contentJson = JSONObject.parseObject(content);
                if (type == VerificationType.BROWSER.value()) {
                    return contentJson.getString("ip");
                }
                if (type == VerificationType.TIEBA.value()) {
                    return contentJson.getString("ip");
                }
            }
        }
        return null;
    }
}
