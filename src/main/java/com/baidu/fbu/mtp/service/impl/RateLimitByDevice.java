package com.baidu.fbu.mtp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.baidu.fbu.mtp.common.exception.MTPException;
import com.baidu.fbu.mtp.common.type.ResultCode;
import com.baidu.fbu.mtp.common.util.Filter;
import com.baidu.fbu.mtp.common.util.RedisKeyDomain;
import com.baidu.fbu.mtp.model.RequestMsg;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * 同一设备（CUID相同则为同一设备）12h内出现仅限10个申请，拒绝第11个请求，
 * 拒绝之后12个小时内所有申请，拒绝时间进行顺延（拒绝也计数）拒绝时间进行顺延
 */
@Service
@Filter(order = 40, channel = {"ZCP001", "TB001"},
        loginType = {"WEB001", "AND001", "IOS001", "PC001", "WEB003", "H5001"})
public class RateLimitByDevice extends AbstractRateLimitFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitByDevice.class);
    @Resource
    private StringRedisTemplate template;

    @Value("${FlushLimitHourPerDevice}")
    private long flushLimitHour; // 统计时间，默认是从当前起12小时内
    @Value("${FlushLimitHourForbiddenPerDevice}")
    private long flushLimitHourForbidden; // 禁止时间，默认是12小时
    @Value("${FlushLimitMaxPerDevice}")
    private long flushLimitMax; // 统计时间内最大允许次数，默认是10
    @Value("${WhiteDevicelist}")
    private List<String> whiteDevicelist = new ArrayList<String>(); // 用户白名单，默认为空

    @Override
    public boolean filter(HttpServletRequest request, RequestMsg requestMsg) {
        if (skip(requestMsg)) {
            return true;
        }
        boolean rel = flushLimit(requestMsg);
        if (rel) {
            throw new MTPException(ResultCode.REQUEST_LIMIT);
        }
        return true;
    }

    public boolean flushLimit(RequestMsg requestMsg) {
        JSONObject dataJson = requestMsg.getDataJson();
        String device = dataJson.getJSONObject("channeldata").getString("uuid");
        if (!whiteDevicelist.contains(device) && "applycredit".equals(requestMsg.getMethod())) {
            String pid = getProduct(dataJson);
            if (pid != null && pid.startsWith("DXJ")) {
                return false;
            }
            boolean limit = needLimit(device);
            if (limit) {
                log.info("DEVICE_FLUSH_LIMIT_WARN: IP[{}], DATA[{}]", device, requestMsg.getData());
            }
            return limit;
        }
        return false;
    }

    private String getProduct(JSONObject dataJson) {
        JSONObject methodData = dataJson.getJSONObject("methoddata");
        JSONObject basicData = methodData.getJSONObject("basic");
        return basicData.getString("pid");
    }

    public boolean needLimit(String key) {
        String keyH = RedisKeyDomain.buildKey(RedisKeyDomain.FLUSH_H_RECORD_PREFIX, key);
        String keyLimitH = RedisKeyDomain.buildKey(RedisKeyDomain.FLUSH_LIMIT_H_PREFIX, key);
        boolean result = false;
        String start = Long.toString(System.currentTimeMillis());
        template.opsForList().leftPush(keyH, start); // 入buffer
        long hcount = template.opsForList().size(keyH);
        String time1 = null;
        while (hcount > flushLimitMax) {
            time1 = template.opsForList().rightPop(keyH); // 最早的记录出buffer
            hcount--;
        }
        if (time1 != null) {
            if (Long.valueOf(start) - Long.valueOf(time1) <= flushLimitHour) {
                template.opsForValue().set(keyLimitH, keyLimitH, flushLimitHourForbidden, TimeUnit.SECONDS); // 重新计时
                result = true;
            }
        }
        if (StringUtils.isNotEmpty(template.opsForValue().get(keyLimitH))) {
            result = true; // 在惩罚期内
        }
        log.info("DEVICE_FLUSH_LIMIT_PROCESS[{}], RESULT[{}], COST[{}]MS", key, result,
                (System.currentTimeMillis() - Long.valueOf(start)));
        return result;
    }

}
