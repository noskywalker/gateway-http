package com.baidu.fbu.mtp.interceptor;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.NamedThreadLocal;

import com.baidu.fbu.mtp.common.context.Context;
import com.baidu.fbu.mtp.common.exception.MTPException;
import com.baidu.fbu.mtp.model.RequestMsg;

public class LogInterceptorTest {
    
    private LogInterceptor logInterceptor;
    private NamedThreadLocal<Long> timeThreadLocal;
    private String data = "{"
            + "\"channeldata\":{\"uuid\":\"E5A5B4928ACA9ABC1984C540E8EDA431|387058620192568\"},"
            + "\"logintype\":\"WEB001\","
            + "\"channel\":\"ZCP001\","
            + "\"method\":\"applycredit\","
            + "\"methoddata\":{\"basic\":{\"pid\":\"DLQ001\"},"
            + "\"verifications\":[{\"type\":54}]}"
            + "}";
    
    private String jsonExceptiondata = "{"
            + "\"channeldata\":{\"uuid\":\"E5A5B4928ACA9ABC1984C540E8EDA431|387058620192568\"},"
            + "\"logintype\":\"WEB001\","
            + "\"channel\":\"ZCP001\","
            + "\"method\":\"applycredit\","
            + "\"verifications\":[{\"type\":54}]}"
            + "}";
    
    private String nullPointerExceptiondata = "{"
            + "\"logintype\":\"WEB001\","
            + "\"channel\":\"ZCP001\","
            + "\"method\":\"applycredit\","
            + "\"methoddata\":{\"basic\":{\"pid\":\"DLQ001\"},"
            + "\"verifications\":[{\"type\":54}]}"
            + "}";
    
    @Before
    public void setUp() throws Exception {
        logInterceptor = new LogInterceptor();
        timeThreadLocal = new NamedThreadLocal<Long>("StopWatch-StartTime");
        
        Field field = LogInterceptor.class.getDeclaredField("TIME_THREAD_LOCAL");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers"); 
        modifiersField.setAccessible(true);
        modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, timeThreadLocal);
    }
    
    @Test
    public void testPreHandleThrowException() throws Exception {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        
        expect(request.getRequestURI()).andThrow(new MTPException());
        expect(request.getParameter("data")).andReturn(data);
        replay(request);
        
        logInterceptor.preHandle(request, response, null);
    }
    
    @Test
    public void testPreHandleThrowJSONException() throws Exception {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        
        expect(request.getRequestURI()).andReturn("127.0.0.1");
        expect(request.getParameter("data")).andReturn(jsonExceptiondata);
        replay(request);
        
        logInterceptor.preHandle(request, response, null);
    }
    
    @Test
    public void testPreHandleThrowNullPointerException() throws Exception {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        
        expect(request.getRequestURI()).andReturn("127.0.0.1");
        expect(request.getParameter("data")).andReturn(nullPointerExceptiondata);
        replay(request);
        
        logInterceptor.preHandle(request, response, null);
    }
    
    @Test
    public void testPreHandle() throws Exception {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        
        expect(request.getRequestURI()).andReturn("127.0.0.1");
        expect(request.getParameter("data")).andReturn(data);
        replay(request);
        
        logInterceptor.preHandle(request, response, null);
    }
    
    @Test
    public void testAfterCompletion() throws Exception {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        
        expect(request.getRequestURI()).andReturn("127.0.0.1");
        replay(request);
        Context.getCurrent(true).set(RequestMsg.class, new RequestMsg());
        timeThreadLocal.set(System.currentTimeMillis());
        
        logInterceptor.afterCompletion(request, response, null, null);
    }
    
}
