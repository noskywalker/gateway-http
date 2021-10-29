package com.baidu.fbu.mtp.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.alibaba.fastjson.JSONObject;
import com.baidu.fbu.mtp.listoperations.ListOperationsImpl;
import com.baidu.fbu.mtp.model.RequestMsg;
import com.baidu.fbu.mtp.service.impl.ImageValidateCodeByDevice;

public class ImageValidateCodeByDeviceTest {
    
    private ImageValidateCodeByDevice imageValidateCodeByDevice;
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
        imageValidateCodeByDevice = new ImageValidateCodeByDevice();
        ReflectionTestUtils.setField(imageValidateCodeByDevice, "m", 180000);
        ReflectionTestUtils.setField(imageValidateCodeByDevice, "n", 4);
        
        ReflectionTestUtils.setField(imageValidateCodeByDevice, "whitelist", 
                Arrays.asList("526D05CE48490C88ACE8F50663C9BF73|260932220315468"));
        
        template = createMock(StringRedisTemplate.class);
        ReflectionTestUtils.setField(imageValidateCodeByDevice, "template", template);
    }
    
    @Test
    public void testFilter() {
        
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getParameter("data")).andReturn(data).times(3);
        replay(request);
        expect(template.opsForList()).andReturn(new ListOperationsImpl()).times(3);
        replay(template);
        
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setChannel("ZCP001");
        requestMsg.setDataJson(JSONObject.parseObject(data));
        requestMsg.setLogintype("AND001");
        requestMsg.setMethod("applycredit");
        
        imageValidateCodeByDevice.filter(request, requestMsg);
    }
}
