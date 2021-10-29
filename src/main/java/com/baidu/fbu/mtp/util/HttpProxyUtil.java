package com.baidu.fbu.mtp.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.HeaderGroup;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baidu.fbu.mtp.common.Constants;
import com.baidu.fbu.mtp.common.context.Context;
import com.baidu.fbu.mtp.common.type.LoginSource;
import com.baidu.fbu.mtp.model.RequestMsg;
import com.baidu.fbu.mtp.util.json.JsonUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Http协议代理转发工具类.
 *
 * @author skywalker
 *
 */
public final class HttpProxyUtil {

    private HttpProxyUtil() {
    }

    /** hop-by-hop属性标记位. */
    private static final HeaderGroup HOP_BY_HOP_HEADERS;

    /** 默认端口. */
    private static final int DEFAULT_PORT = 80;

    private static final String CHARSET = "UTF-8";

    /**
     * 根据<link>www.w3.org/Protocols/rfc2616/rfc2616-sec13.html</link>
     * 如下字段做代理转发时，不应当复制.
     * - Connection
     * - Keep-Alive
     * - Proxy-Authenticate
     * - Proxy-Authorization
     * - TE
     * - Trailers
     * - Transfer-Encoding
     * - Upgrade
     * 使用HeaderGroup可免去考虑字符串大小写问题.
     */
    static {
        HOP_BY_HOP_HEADERS = new HeaderGroup();
        String[] headers = new String[] { "Connection", "Keep-Alive",
                "Proxy-Authenticate", "Proxy-Authorization", "TE", "Trailers",
                "Transfer-Encoding", "Upgrade" };
        for (String header : headers) {
            HOP_BY_HOP_HEADERS.addHeader(new BasicHeader(header, null));
        }
    }

    /**
     * 拷贝请求消息头部信息.
     * <p>
     * @param fromRequest 原生请求.
     * @param toRequest 转发请求.
     */
    public static void copyRequestHeaders(HttpServletRequest fromRequest, HttpRequest toRequest) {
        Enumeration<String> enumerationOfHeaderNames = fromRequest.getHeaderNames();
        while (enumerationOfHeaderNames.hasMoreElements()) {
            String headerName = enumerationOfHeaderNames.nextElement();
            if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)
                    || headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
                continue;
            }
            if (HOP_BY_HOP_HEADERS.containsHeader(headerName)) {
                continue;
            }
            
            Enumeration<String> headers = fromRequest.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                String headerValue = headers.nextElement();
                toRequest.addHeader(headerName, headerValue);
            }
        }
    }

    /**
     * 拷贝请求消息的消息体.
     * 根据RFC 2616, sec 4.3: CONTENT_LENGTH或者TRANSFER_ENCODING其中之一存在,
     * 表明存在消息体.
     * @throws IOException 
     */
    public static HttpRequest createHttpRequest(HttpServletRequest request, URI uri, HttpMethod method)
            throws IOException {
        if (request.getHeader(HttpHeaders.CONTENT_LENGTH) != null
                || request.getHeader(HttpHeaders.TRANSFER_ENCODING) != null) {
            return ServletFileUpload.isMultipartContent(request)
                    ? buildMultipartFormRequest(request, uri, method)
                    : buildFormDataRequest(request, uri, method); // todo 处理 application/json 等的提交
        }
        return buildBasicRequest(request, uri, method);
    }

    private static HttpRequest buildFormDataRequest(HttpServletRequest request, URI uri, HttpMethod method)
            throws IOException {
        List<NameValuePair> nameValuePairs = Lists.newArrayList();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = request.getParameter(name);
            nameValuePairs.add(new BasicNameValuePair(name, value));
        }
        addExtra(nameValuePairs);
        HttpEntityEnclosingRequest eProxyRequest =
                new BasicHttpEntityEnclosingRequest(method.name(), uri.toString());
        eProxyRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, CHARSET));
        eProxyRequest.setHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED
                .withCharset(CHARSET).toString());
        return eProxyRequest;
    }

    private static HttpRequest buildMultipartFormRequest(HttpServletRequest request, URI uri, HttpMethod method)
            throws IOException {
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().setCharset(Charset.forName(CHARSET));
        Iterator<String> itf = multiRequest.getFileNames();
        while (itf.hasNext()) {
            String fileName = itf.next();
            MultipartFile mf = multiRequest.getFile(fileName);
            entityBuilder.addPart(fileName, new InputStreamBody(mf.getInputStream(), mf.getContentType()));
        }
        Enumeration<String> itp = multiRequest.getParameterNames();
        while (itp.hasMoreElements()) {
            String name = itp.nextElement();
            entityBuilder.addTextBody(name, multiRequest.getParameter(name),
                    ContentType.MULTIPART_FORM_DATA.withCharset(CHARSET));
        }
        addExtra(entityBuilder);
        HttpEntityEnclosingRequest newRequest =
                new BasicHttpEntityEnclosingRequest(method.name(), uri.toString());
        newRequest.setEntity(entityBuilder.build());
        return newRequest;
    }

    private static HttpRequest buildBasicRequest(HttpServletRequest request, URI uri, HttpMethod method) {
        String queryString = request.getQueryString();
        return new BasicHttpRequest(method.name(), StringUtils.isBlank(queryString)
                ? uri.toString() : uri.toString() + "?" + encodeUriQuery(queryString));
    }


    
    private static final BitSet ASCIIQUERYCHARS = new BitSet(128);

    static {
        initQueryChars();
    }

    /**
     * RFC1738
     * only alphanumerics, the special characters "$-_.+!*'(),", and reserved characters
     * used for their reserved purposes may be used unencoded within a URL.
     */
    private static void initQueryChars() {
        char[] specialCharacters = ",$+_-!.'()*".toCharArray();
        char[] reserved = "?/@=&;:".toCharArray();

        for (char c = 'a'; c <= 'z'; c++) {
            ASCIIQUERYCHARS.set((int) c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            ASCIIQUERYCHARS.set((int) c);
        }
        for (char c = '0'; c <= '9'; c++) {
            ASCIIQUERYCHARS.set((int) c);
        }
        for (char c : specialCharacters) {
            ASCIIQUERYCHARS.set((int) c);
        }
        for (char c : reserved) {
            ASCIIQUERYCHARS.set((int) c);
        }

        // leave existing percent escapes in place
        ASCIIQUERYCHARS.set((int) '%');
    }

    protected static CharSequence encodeUriQuery(CharSequence in) {
        // Note that I can't simply use URI.java to encode because it will escape pre-existing escaped things.
        StringBuilder outBuf = null;
        Formatter formatter = null;

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            boolean escape = true;
            if (c < 128) {
                if (ASCIIQUERYCHARS.get((int) c)) {
                    escape = false;
                }
            } else if (! Character.isISOControl(c) && ! Character.isSpaceChar(c)) { // not-ascii
                escape = false;
            }
            if (! escape) {
                if (outBuf != null) {
                    outBuf.append(c);
                }
            } else {
                if (outBuf == null) {
                    outBuf = new StringBuilder(in.length() + 5 * 3);
                    outBuf.append(in, 0, i);
                    formatter = new Formatter(outBuf);
                }
                // leading %, 0 padded, width 2, capital hex
                formatter.format("%%%02X", (int) c);
            }
        }
        return outBuf != null ? outBuf : in;
    }

    private static void addExtra(List<NameValuePair> pairList) {
        pairList.add(new BasicNameValuePair("_extra", JsonUtil.toJson(buildExtra())));
    }

    private static void addExtra(MultipartEntityBuilder builder) {
        builder.addTextBody("_extra", JsonUtil.toJson(buildExtra()));
    }

    private static Map<String, Object> buildExtra() {
        Map<String, Object> extra = Maps.newHashMap();
        RequestMsg requestMsg = Context.getFromCurrent(RequestMsg.class, false);
        if (requestMsg == null) {
            return extra;
        }

        extra.put("loginFrom", LoginSource.PASS_ID.getCode());
        extra.put("loginId", requestMsg.getBid());
        extra.put("logKey", MDC.get(Constants.TRACE_KEY));
        return extra;
    }

    /**
     * 拷贝响应消息的消息头.
     * hop-by-hop属性字段均不拷贝.
     * <p>
     * @param fromResponse 转发响应.
     * @param toResponse 原生响应.
     */
    public static void copyResponseHeaders(HttpResponse fromResponse, HttpServletResponse toResponse) {
        for (Header header : fromResponse.getAllHeaders()) {
            if (HOP_BY_HOP_HEADERS.containsHeader(header.getName())) {
                continue;
            }
            // TODO: handle cookies here!
            toResponse.addHeader(header.getName(), header.getValue());
        }
    }

    public static void copyResponse(HttpResponse fromResponse, HttpServletResponse toResponse) throws IOException {
        copyResponseHeaders(fromResponse, toResponse);
        toResponse.setStatus(fromResponse.getStatusLine().getStatusCode());
        copyResponseEntity(fromResponse, toResponse);
    }

    /**
     * 拷贝响应消息的消息体.
     * <p>
     * @param fromResponse 转发响应.
     * @param toResponse 原生响应.
     * @throws IOException 异常.
     */
    public static void copyResponseEntity(HttpResponse fromResponse,
            HttpServletResponse toResponse) throws IOException {
        HttpEntity entity = fromResponse.getEntity();
        if (entity != null) {
            OutputStream servletOutputStream = toResponse.getOutputStream();
            entity.writeTo(servletOutputStream);
        }
    }

    /**
     * 根据URI构造HttpHost.
     * <p>
     * @param uri URI.
     * @return HttpHost.
     */
    public static HttpHost createHttpHost(URI uri) {
        int uriPort = uri.getPort();
        int port = uriPort == -1 ? DEFAULT_PORT : uriPort;
        return new HttpHost(uri.getHost(), port);
    }
}
