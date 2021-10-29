package com.baidu.fbu.mtp.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.expectLastCall;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.baidu.fbu.mtp.common.type.ConfigType;
import com.baidu.fbu.mtp.dao.redis.StringRedisDao;
import com.baidu.fbu.mtp.listoperations.ListOperationsImpl;
import com.baidu.fbu.mtp.listoperations.ValueOperationsImpl;
import com.baidu.fbu.mtp.model.RequestMsg;
import com.baidu.fbu.mtp.service.impl.MTPServiceImpl;

public class MTPServiceTest {
    
    private MTPService mtpService;
    private StringRedisDao redisDao;
    private IPService ipService;
    private MtpConfigService configService;
    private StringRedisTemplate template;
    
    @Before
    public void setUp() {
        mtpService = new MTPServiceImpl();
        redisDao = createMock(StringRedisDao.class);
        ipService = createMock(IPService.class);
        configService = createMock(MtpConfigService.class);
        template = createMock(StringRedisTemplate.class);
        
        ReflectionTestUtils.setField(mtpService, "redisDao", redisDao);
        ReflectionTestUtils.setField(mtpService, "ipService", ipService);
        ReflectionTestUtils.setField(mtpService, "configService", configService);
        ReflectionTestUtils.setField(mtpService, "template", template);
        
        ReflectionTestUtils.setField(mtpService, "flushLimitSecond", 1000L);
        ReflectionTestUtils.setField(mtpService, "flushLimitSecondForbidden", 60L);
        ReflectionTestUtils.setField(mtpService, "flushLimitSecondMax", 3);
        ReflectionTestUtils.setField(mtpService, "flushLimitHour", 86400000L);
        ReflectionTestUtils.setField(mtpService, "flushLimitHourForbidden", 60L);
        ReflectionTestUtils.setField(mtpService, "flushLimitHourMax", 3L);
        ReflectionTestUtils.setField(mtpService, "whiteiplist", Arrays.asList("180.149.143.26", "180.149.143.27"));
        
    }
    
    @Test
    public void testSkipRequest() {
        RequestMsg msg = new RequestMsg();
        msg.setRequestURI("127.0.0.1");
        expect(configService.getConfigValue("127.0.0.1", ConfigType.REQUEST_NOT_AUTH)).andReturn(null);
        replay(configService);
        
        boolean result = mtpService.skipRequest(msg);
        System.out.println(result);
    }
    
    @Test
    public void testRemoveRedisData() {
        redisDao.remove("testRemoveRedisData");
        expectLastCall().anyTimes();
        replay(redisDao);
        
        mtpService.removeRedisData("testRemoveRedisData");
    }
    
    @Test
    public void testNotLogin() {
        expect(redisDao.setIfAbsent("bduss", "-1")).andReturn(true);
        replay(redisDao);
        
        boolean result = mtpService.notLogin("bduss");
        System.out.println(result);
    }
    
    @Test
    public void testIsUniqueRequest() {
        expect(redisDao.setIfAbsent("loginInfo", "loginInfo", 300)).andReturn(true);
        replay(redisDao);
        
        boolean result = mtpService.isUniqueRequest("loginInfo");
        System.out.println(result);
    }
    
    @Test
    public void testGetUserBid() {
        expect(redisDao.get("bduss")).andReturn("userBid");
        replay(redisDao);
        
        String userBid = mtpService.getUserBid("bduss");
        System.out.println(userBid);
    }
    
    @Test
    public void testFlushLimit() {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        RequestMsg msg = new RequestMsg();
        msg.setData("requestData");
        msg.setMethod("applycredit");
        
        expect(ipService.getRemoteIp(request, msg)).andReturn("127.0.0.1");
        replay(ipService);
        
        ListOperationsImpl listOperations = new ListOperationsImpl();
        ValueOperationsImpl valueOperations = new ValueOperationsImpl();
        expect(template.opsForList()).andReturn(listOperations).times(10);
        expect(template.opsForValue()).andReturn(valueOperations).times(10);
        replay(template);
        
        mtpService.flushLimit(request, msg);
    }
    
    @Test
    public void testAllowCurrentRequest() {
        RequestMsg msg = new RequestMsg();
        msg.setBduss("UserBid");
        msg.setRequestURI("127.0.0.1");
        expect(configService.getConfigValue("127.0.0.1", ConfigType.REQUEST_CONCURRENT))
                .andReturn(Arrays.asList("method"));
        replay(configService);
        expect(redisDao.setIfAbsent("UNIQUE_REQUEST", "UNIQUE_REQUEST", 300)).andReturn(true);
        replay(redisDao);
        
        boolean result = mtpService.allowCurrentRequest(msg);
        System.out.println(result);
    }
}
