package com.baidu.fbu.mtp.listoperations;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;

public class ListOperationsImpl implements ListOperations<String, String> {

    @Override
    public List<String> range(String key, long start, long end) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void trim(String key, long start, long end) {
        // TODO Auto-generated method stub
        
    }
    
    private Long size = 4L;
    
    @Override
    public Long size(String key) {
        // TODO Auto-generated method stub
        return size;
    }

    @Override
    public Long leftPush(String key, String value) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Long leftPush(String key, String pivot, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long leftPushAll(String key, String... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long leftPushAll(String key, Collection<String> values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long leftPushIfPresent(String key, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long rightPush(String key, String pivot, String value) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Long rightPush(String key, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long rightPushAll(String key, String... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long rightPushAll(String key, Collection<String> values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long rightPushIfPresent(String key, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void set(String key, long index, String value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Long remove(String key, long i, Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String index(String key, long index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String leftPop(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String leftPop(String key, long timeout, TimeUnit unit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String rightPop(String key) {
        if ("FLUSH_S_RECORD_PREFIX_127.0.0.1".equals(key)) {
            size = size - 1L;
        }
        return String.valueOf(System.currentTimeMillis());
    }

    @Override
    public String rightPop(String key, long timeout, TimeUnit unit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String rightPopAndLeftPush(String sourceKey, String destinationKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String rightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RedisOperations<String, String> getOperations() {
        // TODO Auto-generated method stub
        return null;
    }

   

}
