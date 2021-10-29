package com.baidu.fbu.mtp.listoperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;

public class ValueOperationsImpl implements ValueOperations<String, String> {

    @Override
    public void set(String key, String value) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void set(String key, String value, long offset) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Boolean setIfAbsent(String key, String value) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void multiSet(Map<? extends String, ? extends String> m) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Boolean multiSetIfAbsent(Map<? extends String, ? extends String> m) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String get(Object key) {
        return "true";
    }
    
    @Override
    public String get(String key, long start, long end) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAndSet(String key, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> multiGet(Collection<String> keys) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long increment(String key, long delta) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double increment(String key, double delta) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer append(String key, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long size(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RedisOperations<String, String> getOperations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean setBit(String key, long offset, boolean value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean getBit(String key, long offset) {
        // TODO Auto-generated method stub
        return null;
    }

}
