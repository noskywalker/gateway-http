package com.baidu.fbu.mtp.controller;

import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.PrintWriter;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.baidu.fbu.mtp.service.SystemRouterService;
import com.baidu.fbu.mtp.util.HttpProxyUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpProxyUtil.class)
@PowerMockIgnore("javax.net.ssl.*")
public class MTPControllerTest {
    
    private MTPController controller;
    private SystemRouterService systemRouterService;
    private CloseableHttpClient client;
    
    @Before
    public void setUp() {
        controller = new MTPController();
        mockStatic(HttpProxyUtil.class);
        systemRouterService = mock(SystemRouterService.class);
        client = spy(HttpClients.custom().build());
        
        ReflectionTestUtils.setField(controller, "systemRouterService", systemRouterService);
        ReflectionTestUtils.setField(controller, "httpClient", client);
    }
    
    @Test
    public void testForward() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("version")).thenReturn("1.0");
        when(request.getRequestURI()).thenReturn("/umoney");
        
        URI uri = new URI("http://localhost:8080");
        
        MTPController mtp = spy(controller);
        doReturn(uri).when(mtp).getSystemURI("1.0", "1.0", "/umoney");
        
        HttpHost host = new HttpHost("http://localhost", 8080);
        when(HttpProxyUtil.createHttpHost(uri)).thenReturn(host);
        
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        doReturn(httpResponse).when(client).execute(host, null);
        
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(systemRouterService.getSystemURI("1.0", "1.0")).thenReturn("http://127.0.0.1");
        
        mtp.forward(request, response);
    }
    
    @Test
    public void testForwardNot200() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("version")).thenReturn("1.0");
        when(request.getRequestURI()).thenReturn("/umoney");
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        
        URI uri = new URI("http://localhost:8080");
        
        MTPController mtp = spy(controller);
        doReturn(uri).when(mtp).getSystemURI("1.0", "1.0", "/umoney");
        
        HttpHost host = new HttpHost("http://localhost", 8080);
        when(HttpProxyUtil.createHttpHost(uri)).thenReturn(host);
        
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        doReturn(httpResponse).when(client).execute(host, null);
        
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(systemRouterService.getSystemURI("1.0", "1.0")).thenReturn("http://127.0.0.1");
        
        mtp.forward(request, response);
    }
    
    @Test
    public void testGetSystemURI() throws Exception {
        when(systemRouterService.getSystemURI("1.0", "1.0")).thenReturn("http://127.0.0.1");
        URI uri = controller.getSystemURI("1.0", "1.0", "/umoney");
        System.out.println(uri.toString());
    }
    
}
