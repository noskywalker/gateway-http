package com.baidu.fbu.mtp.service;

import javax.servlet.http.HttpServletRequest;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.baidu.fbu.mtp.model.RequestMsg;

public class IPServiceTest {
    
    private IPService ipService;
    private String data = "{"
            + "\"channeldata\":{\"uuid\":\"E5A5B4928ACA9ABC1984C540E8EDA431|387058620192568\"},"
            + "\"logintype\":\"WEB001\","
            + "\"channel\":\"ZCP001\","
            + "\"method\":\"applycredit\","
            + "\"methoddata\":{\"basic\":{\"pid\":\"DLQ001\"},"
            + "\"verifications\":[{\"type\":54}]}"
            + "}";
    
    @Before
    public void setUp() {
        ipService = new IPService();
    }
    
    @Test
    public void testGetRemoteIpLoginTypeWEB001() {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getHeader("x-forwarded-for")).andReturn("127.0.0.1").times(2);
        replay(request);
        RequestMsg msg = new RequestMsg();
        msg.setData(data);
        msg.setDataJson(JSONObject.parseObject(data));
        msg.setLogintype("WEB001");
        
        ipService.getRemoteIp(request, msg);
    }
    
    @Test
    public void testGetRemoteIpLoginTypeAND001() {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getHeader("x-forwarded-for")).andReturn("127.0.0.1").times(2);
        replay(request);
        RequestMsg msg = new RequestMsg();
        msg.setData(data);
        msg.setDataJson(JSONObject.parseObject(data));
        msg.setLogintype("AND001");
        
        ipService.getRemoteIp(request, msg);
    }
    
    @Test
    public void testGetRemoteIpRemoteAddr() {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getHeader("x-forwarded-for")).andReturn(null).times(2);
        expect(request.getRemoteAddr()).andReturn("127.0.0.1");
        replay(request);
        RequestMsg msg = new RequestMsg();
        msg.setData(data);
        msg.setDataJson(JSONObject.parseObject(data));
        msg.setLogintype("AND001");
        
        ipService.getRemoteIp(request, msg);
    }
    
}
