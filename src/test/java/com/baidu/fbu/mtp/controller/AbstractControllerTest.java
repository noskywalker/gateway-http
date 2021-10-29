package com.baidu.fbu.mtp.controller;

import static org.powermock.api.mockito.PowerMockito.mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.baidu.fbu.mtp.common.exception.MTPException;

public class AbstractControllerTest {
    
    private AbstractController controller;
    
    @Before
    public void setUp() {
        controller = new AbstractController() {
        };
        
    }
    
    @Test
    public void testHandleException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        MTPException e = new MTPException("AbstractController test");
        controller.handleException(request, response, e);
    }
    
    @Test
    public void testInitConnectionManager() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        // controller.initConnectionManager(cm);
    }
    
    @Test
    public void testRequestConfig() {
        // controller.requestConfig();
    }
    
    @Test
    public void testRegistry() {
        ReflectionTestUtils.invokeMethod(controller, "registry");
    }
}
