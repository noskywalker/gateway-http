package com.baidu.fbu.mtp.service;

import com.baidu.fbu.mtp.common.type.ConfigType;
import com.baidu.fbu.mtp.dao.ConfigDao;
import static com.google.common.base.Preconditions.checkNotNull;

import com.baidu.fbu.mtp.model.ConfigKey;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created on 14:07 11/04/2015.
 *
 * @author skywalker
 */
@Service
public class MtpConfigService {
    private static final Logger logger = LoggerFactory.getLogger(MtpConfigService.class);

    @Resource
    private ConfigDao configDao;

    private final LoadingCache<ConfigKey, List<String>> cache = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .expireAfterAccess(30, TimeUnit.MINUTES).build(new CacheLoader<ConfigKey, List<String>>() {
                @Override
                public List<String> load(ConfigKey configKey) throws Exception {
                    List<String> results = getConfigValue(configKey.getType(), configKey.getKey());
                    logger.info("load config, key: {}, results: {}", configKey, results);
                    return results;
                }
            });

    private List<String> getConfigValue(ConfigType configType, String key) {
        checkNotNull(key, "key");
        checkNotNull(configType, "configType");
        return configDao.selectValue(key, configType);
    }

    public List<String> getConfigValue(String key, ConfigType configType) {
        try {
            return cache.get(new ConfigKey(key, configType));
        } catch (Exception e) {
            logger.error("LoadingCache error.", e);
            return getConfigValue(configType, key);
        }
    }

    public List<String> getConfigValue(ConfigType configType) {
        return getConfigValue(configType.lowerCase(), configType);
    }
}
