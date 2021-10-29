package com.baidu.fbu.mtp.util.http;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.baidu.fbu.mtp.common.exception.MTPException;
import com.baidu.fbu.mtp.util.json.JsonUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RemoteUtil.class})
@PowerMockIgnore("javax.net.ssl.*")
public class RemoteUtilTest {
    
    private CloseableHttpClient client;
    
    @Before
    public void setUp() throws Exception {
        client = mock(CloseableHttpClient.class);
        Field field = RemoteUtil.class.getDeclaredField("HTTPCLIENT");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers"); 
        modifiersField.setAccessible(true);
        modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, client);
    }
    
    @Test
    public void testGet() throws Exception {
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        HttpGet get = new HttpGet("http://168.8.8.8");
        whenNew(HttpGet.class).withAnyArguments().thenReturn(get);
        
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getEntity()).thenReturn(mock(HttpEntity.class));
        
        when(client.execute(get)).thenReturn(httpResponse);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", "value");
        params.put("mehtod", "GET");
        RemoteUtil.get("http://127.0.0.1", params);
    }
    
    @Test(expected = MTPException.class)
    public void testGetThrowException() throws Exception {
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        HttpGet get = new HttpGet("http://168.8.8.8");
        whenNew(HttpGet.class).withAnyArguments().thenReturn(get);
        
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getEntity()).thenReturn(mock(HttpEntity.class));
        
        when(client.execute(get)).thenReturn(httpResponse);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", "value");
        params.put("mehtod", "GET");
        RemoteUtil.get("http://127.0.0.1", params);
    }
    
    @Test
    public void testPost() throws Exception {
        HttpPost post = new HttpPost("http://168.8.8.8");
        whenNew(HttpPost.class).withAnyArguments().thenReturn(post);
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getEntity()).thenReturn(mock(HttpEntity.class));
        
        when(client.execute(post)).thenReturn(httpResponse);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", "value");
        params.put("mehtod", "GET");
        RemoteUtil.post("http://127.0.0.1", params);
    }
    
    @Test(expected = MTPException.class)
    public void testPostThrowException() throws Exception {
        HttpPost post = new HttpPost("http://168.8.8.8");
        whenNew(HttpPost.class).withAnyArguments().thenReturn(post);
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getEntity()).thenReturn(mock(HttpEntity.class));
        
        when(client.execute(post)).thenReturn(httpResponse);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", "value");
        params.put("mehtod", "GET");
        RemoteUtil.post("http://127.0.0.1", params);
    }
    
    @Test
    public void testPostJson() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", "value");
        
        HttpPost post = new HttpPost("http://127.0.0.1");
        whenNew(HttpPost.class).withAnyArguments().thenReturn(post);
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getEntity()).thenReturn(mock(HttpEntity.class));
        
        when(client.execute(post)).thenReturn(httpResponse);
        RemoteUtil.postJson("http://127.0.0.1", JsonUtil.toJson(params) , params);
    }
    
    @Test(expected  = MTPException.class)
    public void testPostJsonThrowException() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", "value");
        
        HttpPost post = new HttpPost("http://127.0.0.1");
        whenNew(HttpPost.class).withAnyArguments().thenReturn(post);
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getEntity()).thenReturn(mock(HttpEntity.class));
        
        when(client.execute(post)).thenReturn(httpResponse);
        RemoteUtil.postJson("http://127.0.0.1", JsonUtil.toJson(params) , params);
    }
    
    @Test
    public void testRequestAndResponseException() throws Exception {
        Method mehtod = RemoteUtil.class.getDeclaredMethod("requestAndResponse", HttpUriRequest.class);
        mehtod.setAccessible(true);
        HttpGet get = new HttpGet();
        mehtod.invoke(RemoteUtil.class, get);
    }
    
    @Test
    public void testRequestAndResponseEntityNull() throws Exception {
        Method mehtod = RemoteUtil.class.getDeclaredMethod("requestAndResponse", HttpUriRequest.class);
        mehtod.setAccessible(true);
        HttpGet get = new HttpGet();
        CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpResponse.getEntity()).thenReturn(null);
        
        when(client.execute(get)).thenReturn(httpResponse);
        mehtod.invoke(RemoteUtil.class, get);
    }
    
}
