package com.baidu.fbu.mtp.interceptor;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.baidu.fbu.mtp.common.context.Context;
import com.baidu.fbu.mtp.common.type.ConfigType;
import com.baidu.fbu.mtp.model.RequestMsg;
import com.baidu.fbu.mtp.service.MTPService;
import com.baidu.fbu.mtp.service.MtpConfigService;

public class InterceptorTest {
    
    private UmoneyInterceptor interceptor;
    private MTPService mtpService;
    private MtpConfigService configService;
    private RequestMsg msg;
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
        interceptor = new UmoneyInterceptor();
        mtpService = createMock(MTPService.class);
        configService = createMock(MtpConfigService.class);
        
        ReflectionTestUtils.setField(interceptor, "mtpService", mtpService);
        ReflectionTestUtils.setField(interceptor, "configService", configService);
        
        msg = new RequestMsg();
        msg.setRequestURI("127.0.0.1");
        msg.setData(data);
        msg.setMethod("applycredit");
        msg.setLogintype("WEB001");
        msg.setChannel("ZCP001");
        
        Context.getCurrent(true).set(RequestMsg.class, msg);
    }
    
    @Test
    public void testPreHandleSkipRequest() throws Exception {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        expect(mtpService.skipRequest(msg)).andReturn(true);
        replay(mtpService);
        expect(request.getParameter("data")).andReturn(data);
        replay(request);
        boolean result = interceptor.preHandle(request, response, null);
        System.out.println(result);
    }
    
    @Test
    public void testPreHandleVerifyParamFail() throws Exception {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        PrintWriter writer = createMock(PrintWriter.class);
        expect(response.getWriter()).andReturn(writer);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        replay(response);
        RequestMsg requestMsg = new RequestMsg();
        Context.getCurrent(true).set(RequestMsg.class, requestMsg);
        expect(mtpService.skipRequest(requestMsg)).andReturn(false);
        replay(mtpService);
        expect(request.getParameter("data")).andReturn(data);
        replay(request);
        boolean result = interceptor.preHandle(request, response, null);
        System.out.println(result);
    }
    
    @Test
    public void testPreHandleFlushLimitTrue() throws Exception {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        
        PrintWriter writer = createMock(PrintWriter.class);
        expect(response.getWriter()).andReturn(writer);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        replay(response);
        
        expect(mtpService.skipRequest(msg)).andReturn(false);
        expect(mtpService.flushLimit(request, msg)).andReturn(true);
        replay(mtpService);
        expect(request.getParameter("data")).andReturn(data);
        replay(request);
        expect(configService.getConfigValue("channel", ConfigType.CHANNEL)).andReturn(Arrays.asList("ZCP001"));
        replay(configService);
        
        boolean result = interceptor.preHandle(request, response, null);
        System.out.println(result);
    }
    
    @Test
    public void testPreHandle() throws Exception {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        
        expect(mtpService.skipRequest(msg)).andReturn(false);
        expect(mtpService.flushLimit(request, msg)).andReturn(false);
        expect(mtpService.notLogin("ALREADY_LOGIN")).andReturn(false);
        expect(mtpService.getUserBid("ALREADY_LOGIN")).andReturn("123456");
        expect(mtpService.allowCurrentRequest(msg)).andReturn(true);
        expect(mtpService.filter(request, msg)).andReturn(true);
        replay(mtpService);
        expect(configService.getConfigValue("channel", ConfigType.CHANNEL)).andReturn(Arrays.asList("ZCP001"));
        replay(configService);
        expect(request.getParameter("data")).andReturn(data);
        replay(request);
        
        boolean result = interceptor.preHandle(request, response, null);
        System.out.println(result);
    }
    
    @Test
    public void testAfterCompletion() throws Exception {
        mtpService.triggerAfterCompletion();
        expectLastCall().anyTimes();
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        interceptor.afterCompletion(request, response, null, null);
    }
}