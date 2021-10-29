package com.baidu.fbu.mtp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.baidu.fbu.mtp.common.exception.MTPException;
import com.baidu.fbu.mtp.common.type.ResultCode;
import com.baidu.fbu.mtp.common.util.Filter;
import com.baidu.fbu.mtp.common.util.RedisKeyDomain;
import com.baidu.fbu.mtp.model.RequestMsg;
import com.baidu.fbu.mtp.model.VerificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 图片验证码弹出逻辑1（同一设备在M分钟内出现第N个请求，则输出图片验证码；
 * 图片验证filter必须在CreditapplyFilter之前进行
 */
@Service
@Filter(order = 10, channel = {"ZCP001", "TB001"},
        loginType = {"WEB001", "AND001", "IOS001", "PC001", "WEB003", "H5001"})
public class ImageValidateCodeByDevice extends AbstractRateLimitFilter {

    private static final Logger log = LoggerFactory.getLogger(ImageValidateCodeByDevice.class);

    @Resource
    private StringRedisTemplate template;

    @Value("${ImageValidateObserveIntervalPerDevice}")
    private long m;
    @Value("${ImageValidateShowFromNPerDevice}")
    private long n;

    @Value("${WhiteDevicelist}")
    private List<String> whitelist = new ArrayList<String>(); // 用户白名单，默认为空

    @Override
    public boolean filter(HttpServletRequest request, RequestMsg requestMsg) {
        if (skip(requestMsg)) {
            return true;
        }
        if (needImgValidateCode(requestMsg)) {
            throw new MTPException(ResultCode.NEED_CODE_CHECK);
        }
        return true;
    }

    private boolean needImgValidateCode(RequestMsg requestMsg) {
        JSONObject dataJson = requestMsg.getDataJson();
        String device = dataJson.getJSONObject("channeldata").getString("uuid");
        if (!whitelist.contains(device) && "applycredit".equals(requestMsg.getMethod())) {
            String pid = getProduct(dataJson);
            if (pid != null && pid.startsWith("DXJ")) {
                return false;
            }
            if (deviceLimit(device, requestMsg)) {
                return true;
            }
        }
        return false;
    }

    private String getProduct(JSONObject dataJson) {
        JSONObject methodData = dataJson.getJSONObject("methoddata");
        JSONObject basicData = methodData.getJSONObject("basic");
        return basicData.getString("pid");
    }

    private boolean deviceLimit(String key, RequestMsg requestMsg) {
        String keyH = RedisKeyDomain.buildKey(RedisKeyDomain.FLUSH_S_RECORD_PREFIX, key, "ImgCode_Device");
        boolean result = false;
        String start = Long.toString(System.currentTimeMillis());
        template.opsForList().leftPush(keyH, start); // 入buffer
        long hcount = template.opsForList().size(keyH);
        String time1 = null;
        while (hcount >= n) {
            time1 = template.opsForList().rightPop(keyH); // 最早的记录出buffer
            hcount--;
        }
        if (time1 != null) {
            if (Long.valueOf(start) - Long.valueOf(time1) <= m) {
                result = true;
            }
        }
        if (result && hasImgVerification(requestMsg)) {
            result = false;
        }
        log.info("IMG_VALIDATE_PROCESS[{}], RESULT[{}], COST[{}]MS", key, result,
                (System.currentTimeMillis() - Long.valueOf(start)));
        return result;
    }

    private boolean hasImgVerification(RequestMsg requestMsg) {
        JSONObject jsonObject = requestMsg.getDataJson();
        JSONObject methodData = jsonObject.getJSONObject("methoddata");
        JSONArray verificationArray = methodData.getJSONArray("verifications");
        if (verificationArray == null || verificationArray.size() <= 0) {
            return false;
        }
        for (int i = 0; i < verificationArray.size(); i++) {
            JSONObject verificationJson = verificationArray.getJSONObject(i);
            int type = verificationJson.getIntValue("type");
            if (type == VerificationType.IMGVERIFY.value()) {
                return true;
            }
        }
        return false;
    }

}
