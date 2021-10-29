package com.baidu.fbu.mtp.dao.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created on 16:00 11/20/2015.
 *
 * @author skywalker
 */
@Repository
public class ListRedisDao<T> {
    private static final Logger logger = LoggerFactory.getLogger(ListRedisDao.class);

    @Resource
    private RedisTemplate<String, List<T>> template;

    public void set(String key, List<T> value, long expireMinutes) {
        try {
            template.opsForValue().set(key, value, expireMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("redis set error", e);
        }
    }

    public List<T> get(String key) {
        try {
            return template.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("redis get error", e);
            return null;
        }
    }
}
