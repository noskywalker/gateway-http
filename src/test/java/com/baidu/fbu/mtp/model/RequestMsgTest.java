package com.baidu.fbu.mtp.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

public class RequestMsgTest {
    
    private RequestMsg requestMsg;
    
    @Before
    public void setUp() {
        requestMsg = new RequestMsg();
    }
    
    @Test
    public void testGetDataJson() {
        
    }
    
    @Test
    public void testSetAndGetDataJson() {
        String data = "{\"key\":\"value\"}";
        JSONObject json = JSONObject.parseObject(data);
        requestMsg.setDataJson(json);
        JSONObject dataJson = requestMsg.getDataJson();
        
        Assert.assertEquals(json, dataJson);
    }
    
    @Test
    public void testSetAndGetData() {
        requestMsg.setData("{\"key\":\"value\"}");
        String data = requestMsg.getData();
        Assert.assertEquals("{\"key\":\"value\"}", data);
    }
    
    @Test
    public void testSetAndGetRequestURI() {
        requestMsg.setRequestURI("127.0.0.1");
        String uri = requestMsg.getRequestURI();
        Assert.assertEquals("127.0.0.1", uri);
    }
    
    @Test
    public void testSetAndGetChannel() {
        requestMsg.setChannel("WEB001");
        String channel = requestMsg.getChannel();
        Assert.assertEquals("WEB001", channel);
    }
    
    @Test
    public void testSetAndGetMethod() {
        requestMsg.setMethod("POST");
        String method = requestMsg.getMethod();
        Assert.assertEquals("POST", method);
    }
    
    @Test
    public void testSetAndGetLogintype() {
        requestMsg.setLogintype("AND001");
        String loginType = requestMsg.getLogintype();
        Assert.assertEquals("AND001", loginType);
        
    }
    
    @Test
    public void testSetAndGetMethoddata() {
        requestMsg.setMethoddata("methodddata");
        String methodData = requestMsg.getMethoddata();
        Assert.assertEquals("methodddata", methodData);
    }
    
    @Test
    public void testSetAndGetBduss() {
        requestMsg.setBduss("JFLKDSJFLKDSJFLKJDSLKF--");
        String bduss = requestMsg.getBduss();
        Assert.assertEquals("JFLKDSJFLKDSJFLKJDSLKF--", bduss);
    }
    
    @Test
    public void testSetAndGetBid() {
        Long id = 123456789L;
        requestMsg.setBid(id);
        Long bid = requestMsg.getBid();
        Assert.assertEquals(id, bid);
    }
}
