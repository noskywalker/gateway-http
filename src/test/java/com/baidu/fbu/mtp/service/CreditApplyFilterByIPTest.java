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
import com.baidu.fbu.mtp.service.impl.CreditApplyFilterByIP;

public class CreditApplyFilterByIPTest {
    
    private CreditApplyFilterByIP creditApplyFilterByIP;
    private StringRedisTemplate template;
    private IPService ipService;
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
        creditApplyFilterByIP = new CreditApplyFilterByIP();
        ReflectionTestUtils.setField(creditApplyFilterByIP, "m", 300000);
        ReflectionTestUtils.setField(creditApplyFilterByIP, "n", 4);
        
        ReflectionTestUtils.setField(creditApplyFilterByIP, "whitelist", 
                Arrays.asList("180.149.143.26", "180.149.143.27", "180.149.143.153"));
        
        template = createMock(StringRedisTemplate.class);
        ReflectionTestUtils.setField(creditApplyFilterByIP, "template", template);
        
        ipService = createMock(IPService.class);
        ReflectionTestUtils.setField(creditApplyFilterByIP, "ipService", ipService);
    }
    
    @Test
    public void testFilter() {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getParameter("data")).andReturn(data).times(3);
        replay(request);
        
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setChannel("ZCP001");
        requestMsg.setDataJson(JSONObject.parseObject(data));
        requestMsg.setLogintype("AND001");
        requestMsg.setMethod("applycredit");
        
        expect(ipService.getRemoteIp(request, requestMsg)).andReturn("127.0.0.1");
        replay(ipService);
        expect(template.opsForList()).andReturn(new ListOperationsImpl()).times(20);
        replay(template);
        creditApplyFilterByIP.filter(request, requestMsg);
    }
}
