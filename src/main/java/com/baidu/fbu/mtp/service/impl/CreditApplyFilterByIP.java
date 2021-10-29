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
import com.baidu.fbu.mtp.service.IPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * IP防刷控制（同一IP下，M 秒内同一IP不同设备的申请请求≥N个，拒绝申请。）
 *
 */
@Service
@Filter(order = 30, channel = {"ZCP001", "TB001"},
        loginType = {"WEB001", "AND001", "IOS001", "PC001", "WEB003", "H5001"})
public class CreditApplyFilterByIP extends AbstractRateLimitFilter {

    private static final Logger log = LoggerFactory.getLogger(CreditApplyFilterByIP.class);

    @Resource
    private StringRedisTemplate template;

    @Value("${CreditApplyciationObserveIntervalPerIP}")
    private long m;
    @Value("${CreditApplyciationFromNPerIP}")
    private long n;

    @Value("${WhiteIPlist}")
    private List<String> whitelist = new ArrayList<String>(); // 用户白名单，默认为空
    @Resource
    private IPService ipService;

    @Override
    public boolean filter(HttpServletRequest request, RequestMsg requestMsg) {
        if (skip(requestMsg)) {
            return true;
        }
        JSONObject dataJson = requestMsg.getDataJson();
        String device = dataJson.getJSONObject("channeldata").getString("uuid");
        if ("applycredit".equals(requestMsg.getMethod())) {
            String ip = ipService.getRemoteIp(request, requestMsg);
            String pid = getProduct(dataJson);
            if (pid != null && pid.startsWith("DXJ")) {
                return true;
            }
            if (! whitelist.contains(ip) && needRefuse(ip, device)) {
                throw new MTPException(ResultCode.REQUEST_LIMIT);
            }
        }
        return true;
    }

    private String getProduct(JSONObject dataJson) {
        JSONObject methodData = dataJson.getJSONObject("methoddata");
        JSONObject basicData = methodData.getJSONObject("basic");
        return basicData.getString("pid");
    }

    private boolean needRefuse(String key, String device) {
        boolean result = false;
        String keyH = RedisKeyDomain.buildKey(RedisKeyDomain.FLUSH_S_RECORD_PREFIX, key, "CA_IP");
        String start = Long.toString(System.currentTimeMillis());
        String value = generateValue(device);
        removeSameDevice(keyH, device);
        template.opsForList().leftPush(keyH, value); // 入buffer
        long hcount = template.opsForList().size(keyH);
        String time1 = null;
        while (hcount >= n) {
            time1 = template.opsForList().rightPop(keyH);// 最早的记录出buffer
            hcount--;
        }
        if (time1 != null) {
            if (getTime(value) - getTime(time1) <= m) {
                result = true;
            }
        }
        log.info("CREDIT_APPLICATION_LIMIT_PROCESS[{}], RESULT[{}], COST[{}]MS", key, result,
                (System.currentTimeMillis() - Long.valueOf(start)));
        return result;
    }

    private long getTime(String v) {
        long rel = 0;
        if (v != null && v.contains("_")) {
            String[] temp = v.split("_");
            rel = Long.parseLong(temp[0]);
        }
        return rel;
    }

    private void removeSameDevice(String key, String device) {
        long size = template.opsForList().size(key);
        if (size == 0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            String val = template.opsForList().leftPop(key);
            if (getDevice(val).equalsIgnoreCase(device)) {
                continue;
            }
            template.opsForList().rightPush(key, val);
        }
    }

    private String getDevice(String v) {
        String rel = "";
        if (v != null && v.contains("_")) {
            String[] temp = v.split("_");
            int len =  temp.length;
            rel = temp[len - 1];
        }
        return rel;
    }

    private String generateValue(String device) {
        return Long.toString(System.currentTimeMillis()) + "_" + device;
    }
}
