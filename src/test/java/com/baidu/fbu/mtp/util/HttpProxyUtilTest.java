package com.baidu.fbu.mtp.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

public class HttpProxyUtilTest {

    @Test
    public void testHttpProxyUtil() throws SecurityException, NoSuchMethodException,
        IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<HttpProxyUtil> c = HttpProxyUtil.class.getDeclaredConstructor();
        c.setAccessible(true);
        HttpProxyUtil u = c.newInstance();
    }

    @Test
    public void testCopyRequestHeadersContentLength() throws IOException {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader(HttpHeaders.CONTENT_LENGTH, "123");
        HttpRequest httpRequest = new BasicHttpRequest("POST", "www.baidu.com");
        HttpProxyUtil.copyRequestHeaders(servletRequest, httpRequest);
        Assert.assertNull(httpRequest.getFirstHeader(HttpHeaders.CONTENT_LENGTH));
    }

    @Test
    public void testCopyRequestHeadersHost() throws IOException {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader(HttpHeaders.HOST, "www.baidu.com");
        HttpRequest httpRequest = new BasicHttpRequest("POST", "www.baidu.com");
        HttpProxyUtil.copyRequestHeaders(servletRequest, httpRequest);
        Assert.assertNull(httpRequest.getFirstHeader(HttpHeaders.HOST));
    }

    @Test
    public void testCopyRequestHeadersHopByHopHeader() throws IOException {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader(HttpHeaders.CONNECTION, "keep-alive");
        HttpRequest httpRequest = new BasicHttpRequest("POST", "www.baidu.com");
        HttpProxyUtil.copyRequestHeaders(servletRequest, httpRequest);
        Assert.assertNull(httpRequest.getFirstHeader(HttpHeaders.CONNECTION));
    }

    @Test
    public void testCopyRequestOneHeader() throws IOException {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader(HttpHeaders.CONTENT_TYPE, "text/html");
        HttpRequest httpRequest = new BasicHttpRequest("POST", "www.baidu.com");
        HttpProxyUtil.copyRequestHeaders(servletRequest, httpRequest);
        Assert.assertArrayEquals(new String[] {"Content-Type: text/html"},
                new String[] {httpRequest.getFirstHeader(HttpHeaders.CONTENT_TYPE).toString()});
    }

    @Test
    public void testCopyRequestHeaders() throws IOException {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader(HttpHeaders.CONTENT_TYPE, "text/html");
        servletRequest.addHeader(HttpHeaders.CONTENT_LANGUAGE, "mi,en");
        HttpRequest httpRequest = new BasicHttpRequest("POST", "www.baidu.com");
        HttpProxyUtil.copyRequestHeaders(servletRequest, httpRequest);
        Assert.assertArrayEquals(new String[] {"Content-Type: text/html", "Content-Language: mi,en"},
                new String[] {httpRequest.getFirstHeader(HttpHeaders.CONTENT_TYPE).toString(),
                httpRequest.getFirstHeader(HttpHeaders.CONTENT_LANGUAGE).toString()});
    }

    @Test
    public void testCreateHttpRequestEmptyMessage() throws URISyntaxException, IOException {
        URI uri = new URI("http://www.baidu.com");
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        HttpMethod httpMethod = HttpMethod.valueOf("POST");
        HttpRequest httpRequest = HttpProxyUtil.createHttpRequest(servletRequest, uri, httpMethod);
        Assert.assertArrayEquals(new String[]{"http://www.baidu.com"},
                new String[] {httpRequest.getRequestLine().getUri()});
    }
    
    @Test
    public void testCreateHttpRequestEmptyMessageWithQueryString() throws URISyntaxException, IOException {
        URI uri = new URI("http://www.baidu.com");
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setQueryString("data={}");
        HttpMethod httpMethod = HttpMethod.valueOf("POST");
        HttpRequest httpRequest = HttpProxyUtil.createHttpRequest(servletRequest, uri, httpMethod);
        Assert.assertArrayEquals(new String[]{"http://www.baidu.com?data=%7B%7D"},
                new String[] {httpRequest.getRequestLine().getUri()});
    }

    @Test
    public void testCreateHttpRequestMessageContentLength() throws URISyntaxException, UnsupportedOperationException,
        IOException {
        URI uri = new URI("http://www.baidu.com");
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader(HttpHeaders.CONTENT_LENGTH, "100");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("data", "data");
        servletRequest.addParameters(parameters);
        HttpMethod httpMethod = HttpMethod.valueOf("POST");
        HttpEntityEnclosingRequest httpRequest = (HttpEntityEnclosingRequest)
                HttpProxyUtil.createHttpRequest(servletRequest, uri, httpMethod);
        StringWriter writer = new StringWriter();
        IOUtils.copy(httpRequest.getEntity().getContent(), writer);
        Assert.assertArrayEquals(new String[]{"http://www.baidu.com"},
                new String[] {httpRequest.getRequestLine().getUri()});
        Assert.assertArrayEquals(new String[]{"data=data"},
                new String[] {writer.toString()});
    }

    @Test
    public void testCreateHttpRequestMessageTransferEncoding() throws URISyntaxException,
        UnsupportedOperationException, IOException {
        URI uri = new URI("http://www.baidu.com");
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader(HttpHeaders.TRANSFER_ENCODING, "abc");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("data", "data");
        servletRequest.addParameters(parameters);
        HttpMethod httpMethod = HttpMethod.valueOf("POST");
        HttpEntityEnclosingRequest httpRequest = (HttpEntityEnclosingRequest)
                HttpProxyUtil.createHttpRequest(servletRequest, uri, httpMethod);
        StringWriter writer = new StringWriter();
        IOUtils.copy(httpRequest.getEntity().getContent(), writer);
        Assert.assertArrayEquals(new String[]{"http://www.baidu.com"},
                new String[] {httpRequest.getRequestLine().getUri()});
        Assert.assertArrayEquals(new String[]{"data=data"},
                new String[] {writer.toString()});
    }
    
    @Test
    public void testCreateHttpRequestMultiRequest() throws Exception {
        URI uri = new URI("http://www.baidu.com");
        MockMultipartHttpServletRequest servletRequest = new MockMultipartHttpServletRequest();
        servletRequest.addHeader(HttpHeaders.CONTENT_LENGTH, "100");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("data", "data");
        servletRequest.addParameters(parameters);
        HttpMethod httpMethod = HttpMethod.valueOf("POST");
        HttpEntityEnclosingRequest httpRequest = (HttpEntityEnclosingRequest)
                HttpProxyUtil.createHttpRequest(servletRequest, uri, httpMethod);
    }

    @Test
    public void testCreateHttpHostDefaultPort() throws URISyntaxException {
        URI uri = new URI("http://www.baidu.com");
        HttpHost httpHost = HttpProxyUtil.createHttpHost(uri);
        Assert.assertArrayEquals(new int[] {80}, new int[] {httpHost.getPort()});
    }

    @Test
    public void testCopyResponseHeadersHopByHopHeaders() {
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        HttpResponse proxyResponse = createMock(HttpResponse.class);
        expect(proxyResponse.getAllHeaders()).andReturn(
                new Header[]{new BasicHeader("Connection", "keep-alive")});
        replay(proxyResponse);
        HttpProxyUtil.copyResponseHeaders(proxyResponse, httpServletResponse);
        Assert.assertArrayEquals(new int[]{0}, new int[] {httpServletResponse.getHeaderNames().size()});
    }

    @Test
    public void testCopyResponseHeaders() {
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        HttpResponse proxyResponse = createMock(HttpResponse.class);
        expect(proxyResponse.getAllHeaders()).andReturn(
                new Header[]{new BasicHeader("Content-type", "application/x-www-form-urlencoded")});
        replay(proxyResponse);
        HttpProxyUtil.copyResponseHeaders(proxyResponse, httpServletResponse);
        Assert.assertArrayEquals(new int[]{1}, new int[] {httpServletResponse.getHeaderNames().size()});
    }

    @Test
    public void testCopyResponseNullEntity() throws IOException {
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        HttpResponse proxyResponse = createMock(HttpResponse.class);
        HttpProxyUtil.copyResponseEntity(proxyResponse, httpServletResponse);
        Assert.assertArrayEquals(new String[]{""},
                new String[]{httpServletResponse.getContentAsString()});
    }

    @Test
    public void testCopyResponseEntity() throws IOException {
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        HttpResponse proxyResponse = createMock(HttpResponse.class);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("data", "data"));
        expect(proxyResponse.getEntity()).andReturn(new UrlEncodedFormEntity(nameValuePairs));
        replay(proxyResponse);
        HttpProxyUtil.copyResponseEntity(proxyResponse, httpServletResponse);
        Assert.assertArrayEquals(new String[]{"data=data"},
                new String[]{httpServletResponse.getContentAsString()});
    }

    @Test
    public void testConsumeQuietly() throws IOException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("data", "data"));
        HttpEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs);
        // HttpProxyUtil.consumeQuietly(httpEntity);
        Assert.assertFalse(httpEntity.isStreaming());
    }

    @Test
    public void testCreateHttpHost() throws URISyntaxException {
        URI uri = new URI("http://www.baidu.com:9090/umoney");
        HttpHost httpHost = HttpProxyUtil.createHttpHost(uri);
        Assert.assertArrayEquals(new int[] {9090}, new int[] {httpHost.getPort()});
    }
    
}
