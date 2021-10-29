package com.baidu.fbu.mtp.service.impl;

import com.baidu.fbu.mtp.common.ConfigUtil;
import com.baidu.fbu.mtp.common.Constants;
import com.baidu.fbu.mtp.common.context.Context;
import com.baidu.fbu.mtp.common.type.ConfigFileType;
import com.baidu.fbu.mtp.common.type.ConfigType;
import com.baidu.fbu.mtp.common.type.FlagType;
import com.baidu.fbu.mtp.common.util.RedisKeyDomain;
import com.baidu.fbu.mtp.dao.redis.StringRedisDao;
import com.baidu.fbu.mtp.model.RequestMsg;
import com.baidu.fbu.mtp.service.IPService;
import com.baidu.fbu.mtp.service.MTPFilter;
import com.baidu.fbu.mtp.service.MTPService;
import com.baidu.fbu.mtp.service.MtpConfigService;
import com.baidu.fbu.mtp.util.ComponentUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MTPServiceImpl implements MTPService {
    private static final Logger log = LoggerFactory.getLogger(MTPServiceImpl.class);

    @Resource
    private StringRedisDao redisDao;
    @Resource
    private StringRedisTemplate template;
    @Resource
    private IPService ipService;
    @Resource
    private MtpConfigService configService;

    @Value("${FlushLimitSecond}")
    private long flushLimitSecond;
    @Value("${FlushLimitSecondForbidden}")
    private long flushLimitSecondForbidden;
    @Value("${FlushLimitSecondMax}")
    private long flushLimitSecondMax;
    @Value("${FlushLimitHour}")
    private long flushLimitHour;
    @Value("${FlushLimitHourForbidden}")
    private long flushLimitHourForbidden;
    @Value("${FlushLimitHourMax}")
    private long flushLimitHourMax;
    @Value("${WhiteIPlist}")
    private List<String> whiteiplist;
    @Resource
    private List<MTPFilter> filters;

    @PostConstruct
    public void init() {
        ComponentUtils.sortFilters(filters);
    }

    public boolean skipRequest(RequestMsg rMsg) {
        List<String> requestPrefix = configService.getConfigValue(ConfigType.REQUEST_PREFIX);
        for (String prefix : requestPrefix) {
            if (rMsg.getRequestURI().startsWith(prefix)) {
                return true;
            }
        }

        List<String> methods = configService.getConfigValue(rMsg.getRequestURI(), ConfigType.REQUEST_NOT_AUTH);
        return CollectionUtils.isNotEmpty(methods)
                && (methods.contains(Constants.ALL) || methods.contains(rMsg.getMethod()));
    }

    @Override
    public void removeRedisData(String key) {
        try {
            redisDao.remove(key);
        } catch (Exception e) {
            log.error("Redis remove error, key: " + key, e);
        }
    }

    @Override
    public boolean notLogin(String bduss) {
        try {
            return redisDao.setIfAbsent(bduss, "-1");
        } catch (Exception e) {
            log.error("Redis setIfAbsent error, bduss: " + bduss, e);
            return true;
        }
    }

    @Override
    public void saveLoginInfo(String key, String value) {
        try {
            redisDao.setWithTimeout(key, value, ConfigUtil.getInt(
                    ConfigFileType.CONFIG_PROPERTY, "redis.bduss.timeout", 10));
        } catch (Exception e) {
            log.error("MTPService#saveLoginInfo error", e);
        }
    }

    @Override
    public boolean isUniqueRequest(String loginInfo) {
        try {
            return redisDao.setIfAbsent(loginInfo, loginInfo, 60);
        } catch (Exception e) {
            log.error("MTPService#isUniqueRequest error", e);
            return true;
        }
    }

    @Override
    public String getUserBid(String bduss) {
        try {
            return redisDao.get(bduss);
        } catch (Exception e) {
            log.error("MTPService#getUserBid error", e);
            return null;
        }
    }

    @Override
    public boolean flushLimit(HttpServletRequest request, RequestMsg requestMsg) {
        if (! "applycredit".equals(requestMsg.getMethod())) {
            return false;
        }
        try {
            boolean limit = false;
            String ip = ipService.getRemoteIp(request, requestMsg);
            if (! whiteiplist.contains(ip)) {
                limit = flushLimitProcess(ip);
            }
            log.info("IP_FLUSH_LIMIT_WARN: IP[{}], limit[{}], DATA[{}]", ip, limit, requestMsg.getData());
            return limit;
        } catch (Exception e) {
            log.error("MTPService#flushLimit error", e);
            return false;
        }
    }

    public boolean flushLimitProcess(String ip) {
        String keyS = RedisKeyDomain.buildKey(RedisKeyDomain.FLUSH_S_RECORD_PREFIX, ip);
        String keyH = RedisKeyDomain.buildKey(RedisKeyDomain.FLUSH_H_RECORD_PREFIX, ip);
        String keyLimitS = RedisKeyDomain.buildKey(RedisKeyDomain.FLUSH_LIMIT_S_PREFIX, ip);
        String keyLimitH = RedisKeyDomain.buildKey(RedisKeyDomain.FLUSH_LIMIT_H_PREFIX, ip);
        boolean result = false;
        String start = System.currentTimeMillis() + "";

        template.opsForList().leftPush(keyS, start);
        template.opsForList().leftPush(keyH, start);

        long scount = template.opsForList().size(keyS);
        long hcount = template.opsForList().size(keyH);

        while (scount >= flushLimitSecondMax) {
            String time1 = template.opsForList().rightPop(keyS);
            scount = template.opsForList().size(keyS);
            if (scount < flushLimitSecondMax) { // 取第flushLimitSecondMax个
                if (Long.valueOf(start) - Long.valueOf(time1) <= flushLimitSecond) {
                    template.opsForValue().set(keyLimitS, keyLimitS, flushLimitSecondForbidden, TimeUnit.SECONDS);
                    result = true;
                }
            }
        }
        if (StringUtils.isNotEmpty(template.opsForValue().get(keyLimitS))) {
            result = true;
        }

        while (hcount > flushLimitHourMax) {
            String time1 = template.opsForList().rightPop(keyH);
            hcount = template.opsForList().size(keyH);
            if (hcount <= flushLimitHourMax) { // 取第flushLimitHourMax个
                if (Long.valueOf(start) - Long.valueOf(time1) <= flushLimitHour
                        && StringUtils.isEmpty(template.opsForValue().get(keyLimitH))) {
                    template.opsForValue().set(keyLimitH, keyLimitH, flushLimitHourForbidden, TimeUnit.SECONDS);
                    result = true;
                }
            }
        }
        if (StringUtils.isNotEmpty(template.opsForValue().get(keyLimitH))) {
            result = true;
        }

        log.info("IP_FLUSH_LIMIT_PROCESS[{}] COST[{}]MS", ip, (System.currentTimeMillis() - Long.valueOf(start)));
        return result;
    }

    @Override
    public boolean allowCurrentRequest(RequestMsg rMsg) {
        if (rMsg.getBid() == null || rMsg.getBid() <= 0) {
            return true;
        }
        List<String> methods = configService.getConfigValue(rMsg.getRequestURI(), ConfigType.REQUEST_CONCURRENT);
        if (CollectionUtils.isNotEmpty(methods)
                && (methods.contains(Constants.ALL) || methods.contains(rMsg.getMethod()))) {
            return true;
        }
        rMsg.setFlag(FlagType.CONCURRENT_REQ);
        String loginInfo = RedisKeyDomain.buildKey(RedisKeyDomain.UNIQUE_REQUEST_PREFIX, rMsg.getBid() + "");
        return isUniqueRequest(loginInfo);
    }

    @Override
    public void triggerAfterCompletion() {
        RequestMsg msg = Context.getFromCurrent(RequestMsg.class, false);
        if (msg != null && msg.isFlag(FlagType.CONCURRENT_REQ)) {
            removeRedisData(RedisKeyDomain.buildKey(RedisKeyDomain.UNIQUE_REQUEST_PREFIX, msg.getBid() + ""));
        }
    }


    @Override
    public boolean filter(HttpServletRequest request, RequestMsg requestMsg) {
        if (CollectionUtils.isEmpty(filters)) {
            return true;
        }
        try {
            for (MTPFilter filter : filters) {
                if (! filter.filter(request, requestMsg)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            log.error("MTPService#filter error", e);
            return true;
        }
    }

}
