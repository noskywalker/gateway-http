package com.baidu.fbu.mtp.dao.redis;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.baidu.fbu.mtp.listoperations.ValueOperationsImpl;

public class StringRedisDaoTest {
    
    private StringRedisTemplate template;
    private StringRedisDao redisDao;
    
    @Before
    public void setUp() {
        redisDao = new StringRedisDao();
        template = createMock(StringRedisTemplate.class);
        ReflectionTestUtils.setField(redisDao, "template", template);
    }
    
    @Test
    public void testSet() {
        expect(template.opsForValue()).andReturn(new ValueOperationsImpl());
        replay(template);
        redisDao.set("key", "value");
    }
    
    @Test
    public void testSetWithTimeout() {
        expect(template.opsForValue()).andReturn(new ValueOperationsImpl());
        replay(template);
        redisDao.setWithTimeout("key", "value", 300);
    }
    
    @Test
    public void testSetIfAbsent() {
        expect(template.opsForValue()).andReturn(new ValueOperationsImpl());
        replay(template);
        redisDao.setIfAbsent("key", "value");
    }
    
    @Test
    public void testSetIfAbsentWithTimeOut() {
        expect(template.opsForValue()).andReturn(new ValueOperationsImpl());
        expect(template.expire("key", 300, TimeUnit.SECONDS)).andReturn(true);
        replay(template);
        redisDao.setIfAbsent("key", "value", 300);
    }
    
    @Test
    public void testGet() {
        expect(template.opsForValue()).andReturn(new ValueOperationsImpl());
        replay(template);
        redisDao.get("key");
    }
    
    @Test
    public void testSize() {
        expect(template.opsForValue()).andReturn(new ValueOperationsImpl());
        replay(template);
        redisDao.size("key");
    }
    
    @Test
    public void testRemove() {
        template.delete("key");
        expectLastCall().anyTimes();
        replay(template);
        redisDao.remove("key");
    }
}
