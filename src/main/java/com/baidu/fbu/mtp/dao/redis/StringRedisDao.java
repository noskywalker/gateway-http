/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.fbu.mtp.dao.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * redis String DAO class
 * 
 * @author limingjian
 *
 */
@Repository
public class StringRedisDao {

    @Resource
    private StringRedisTemplate template;

    public void set(String key, String value) {
        template.opsForValue().set(key, value);
    }

    /**
     * 带超时时间的set key value, 秒为单位
     */
    public void setWithTimeout(String key, String value, long timeout) {
        template.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public boolean setIfAbsent(String key, String value) {
        return template.opsForValue().setIfAbsent(key, value);
    }
    
    public boolean setIfAbsent(String key, String value, long timeout) {
        boolean b = template.opsForValue().setIfAbsent(key, value);
        template.expire(key, timeout, TimeUnit.SECONDS);
        return b;
    }

    public String get(Object key) {
        return template.opsForValue().get(key);
    }

    public Long size(String key) {
        return template.opsForValue().size(key);
    }

    public void remove(String key) {
        template.delete(key);
    }

}
