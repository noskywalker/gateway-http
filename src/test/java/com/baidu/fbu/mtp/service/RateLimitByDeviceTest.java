package com.baidu.fbu.mtp.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import com.alibaba.fastjson.JSONObject;
import com.baidu.fbu.mtp.common.exception.MTPException;
import com.baidu.fbu.mtp.listoperations.ListOperationsImpl;
import com.baidu.fbu.mtp.listoperations.ValueOperationsImpl;
import com.baidu.fbu.mtp.model.RequestMsg;
import com.baidu.fbu.mtp.service.impl.RateLimitByDevice;

public class RateLimitByDeviceTest {
    
    private RateLimitByDevice rateLimitByDevice;
    private StringRedisTemplate template;
    private String data = "{"
            + "\"channeldata\":{\"uuid\":\"E5A5B4928ACA9ABC1984C540E8EDA431|387058620192568\"},"
            + "\"logintype\":\"AND001\","
            + "\"channel\":\"ZCP001\","
            + "\"method\":\"applycredit\","
            + "\"methoddata\":{\"basic\":{\"pid\":\"DLQ001\"},"
            + "\"verifications\":[{\"type\":54}]}"
            + "}";
    
    @Before
    public void setUp() {
        rateLimitByDevice = new RateLimitByDevice();
        ReflectionTestUtils.setField(rateLimitByDevice, "flushLimitHour", 300000);
        ReflectionTestUtils.setField(rateLimitByDevice, "flushLimitHourForbidden", 43200);
        
        ReflectionTestUtils.setField(rateLimitByDevice, "whiteDevicelist", 
                Arrays.asList("526D05CE48490C88ACE8F50663C9BF73|260932220315468"));
        
        template = createMock(StringRedisTemplate.class);
        ReflectionTestUtils.setField(rateLimitByDevice, "template", template);
    }
    
    @Test(expected = MTPException.class)
    public void testSkip() {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getParameter("data")).andReturn(data).times(3);
        replay(request);
        
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setChannel("ZCP001");
        requestMsg.setDataJson(JSONObject.parseObject(data));
        requestMsg.setLogintype("AND001");
        requestMsg.setMethod("applycredit");
        
        ValueOperations<String, String> valueOperations = new ValueOperationsImpl();
        expect(template.opsForList()).andReturn(new ListOperationsImpl()).times(3);
        expect(template.opsForValue()).andReturn(valueOperations).times(2);
        replay(template);
        
        ReflectionTestUtils.setField(rateLimitByDevice, "flushLimitMax", 3);
        rateLimitByDevice.filter(request, requestMsg);
    }
}
