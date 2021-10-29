package com.baidu.fbu.mtp.util.http;

import static com.baidu.fbu.mtp.common.Timing.timing;

import static com.baidu.fbu.mtp.common.ConfigUtil.getInt;
import com.baidu.fbu.mtp.common.HttpClientFactory;
import com.baidu.fbu.mtp.common.exception.MTPException;
import com.baidu.fbu.mtp.common.retry.Retryer;
import com.baidu.fbu.mtp.common.retry.RetryerBuilder;
import static com.baidu.fbu.mtp.common.type.ConfigFileType.CONFIG_PROPERTY;
import com.google.common.base.Stopwatch;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class RemoteUtil {

    public static final String CHARSET = "UTF-8";

    private static final Logger logger = LoggerFactory.getLogger(RemoteUtil.class);

    private static final CloseableHttpClient HTTP_CLIENT = HttpClientFactory.getInstance();

    private RemoteUtil() {
    }

    private static Retryer retryer() {
        return new RetryerBuilder()
                .retryTimes(getInt(CONFIG_PROPERTY, "http.retry.times", 0))
                .retryInterval(getInt(CONFIG_PROPERTY, "http.retry.interval", 0), TimeUnit.MILLISECONDS)
                .retryIfExceptionOfType(new Class[]{NoHttpResponseException.class})
                .build();
    }

    public static CloseableHttpResponse executeWithRetry(
            final HttpRequest request, final HttpHost httpHost) throws Exception {
        return retryer().callWithRetry(() -> HTTP_CLIENT.execute(httpHost, request));
    }

    public static CloseableHttpResponse executeWithRetry(
            final HttpRequest request, final HttpHost httpHost, final RequestConfig config) throws Exception {
        return retryer().callWithRetry(() -> {
            HttpClientContext clientContext = HttpClientContext.create();
            clientContext.setRequestConfig(config);
            return HTTP_CLIENT.execute(httpHost, request, clientContext);
        });
    }

    public static CloseableHttpResponse executeWithRetry(final HttpUriRequest request) throws Exception {
        return retryer().callWithRetry(() -> HTTP_CLIENT.execute(request));
    }

    private static ResponseResult requestAndResponse(HttpUriRequest request, boolean retry) {
        HttpEntity entity;
        CloseableHttpResponse response = null;
        try {
            response = retry ? executeWithRetry(request) : HTTP_CLIENT.execute(request);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                entity = response.getEntity();
                if (entity != null) {
                    return new ResponseResult(true, status, EntityUtils.toString(entity, CHARSET));
                }
                logger.error("remote call abnormal, entity is null, url: {}", request.getURI());
            }
            logger.error("remote call error, url: {}, status: {}", request.getURI(), status);
            return new ResponseResult(false, status, "");
        } catch (Exception e) {
            logger.error("HTTP Exception, url: " + request.getURI(), e);
            return new ResponseResult(false, -1, "");
        } finally {
            consumeEntityAndCloseResponse(response);
        }
    }

    private static class ResponseResult {
        boolean success;
        int code;
        String result;

        public ResponseResult(boolean success, int code, String result) {
            this.success = success;
            this.code = code;
            this.result = result;
        }
    }

    public static String get(String url, Map<String, String> params) {
        return get(url, params, null, false);
    }

    public static String get(String url, Map<String, String> params, RequestConfig config) {
        return get(url, params, config, false);
    }

    public static String get(String url, Map<String, String> params, boolean filterLog) {
        return get(url, params, null, filterLog);
    }

    public static String get(String url, Map<String, String> params, RequestConfig config, boolean filterLog) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        String newUrl = setParam(url, params);
        HttpGet httpget = new HttpGet(newUrl);
        if (config != null) {
            httpget.setConfig(config);
        }

        logger.info("remote call: {}, params: {}", url, filterLog ? "***" : params);
        ResponseResult result = requestAndResponse(httpget, true);
        logger.info("remote call: {}, response time: {}, status: {}, result: {}",
                url, timing(stopwatch.stop()), result.code, filterLog ? "***" : result.result);

        if (result.success) {
            return result.result;
        }

        throw new MTPException();
    }


    public static String post(String url, Map<String, String> params) {
        return post(url, params, false);
    }

    public static String post(String url, Map<String, String> params, RequestConfig config) {
        return post(url, params, config, false);
    }

    public static String post(String url, Map<String, String> params, boolean filterLog) {
        return post(url, params, null, filterLog);
    }

    public static String post(String url, Map<String, String> params, RequestConfig config, boolean filterLog) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        HttpPost httppost = new HttpPost(url);
        if (config != null) {
            httppost.setConfig(config);
        }
        setParam(httppost, params);

        logger.info("remote call: {}, params: {}", url, filterLog ? "***" : params);
        ResponseResult result = requestAndResponse(httppost, true);
        logger.info("remote call: {}, response time: {}, status: {}, result: {}",
                url, timing(stopwatch.stop()), result.code, filterLog ? "***" : result.result);

        if (result.success) {
            return result.result;
        }

        throw new MTPException();
    }

    public static String postJson(String url, String jsonBody) {
        return postJson(url, jsonBody, null);
    }

    public static String postJson(String url, String jsonBody, boolean filterLog) {
        return postJson(url, jsonBody, null, null, filterLog);
    }

    public static String postJson(String url, String jsonBody, RequestConfig config) {
        return postJson(url, jsonBody, null, config, false);
    }

    public static String postJson(String url, String jsonBody,
                                  Map<String, String> headers, RequestConfig config, boolean filterLog) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        HttpPost httppost = new HttpPost(url);
        if (config != null) {
            httppost.setConfig(config);
        }
        if (headers != null && ! headers.isEmpty()) {
            headers.forEach(httppost::addHeader);
        }
        StringEntity s = new StringEntity(jsonBody, CHARSET);
        s.setContentType("application/json");
        httppost.setEntity(s);

        logger.info("remote call: {}, params: {}", url, filterLog ? "***" : jsonBody);
        ResponseResult result = requestAndResponse(httppost, true);
        logger.info("remote call: {}, response time: {}, status: {}, result: {}",
                url, timing(stopwatch.stop()), result.code, filterLog ? "***" : result.result);

        if (result.success) {
            return result.result;
        }

        throw new MTPException();
    }

    private static void setParam(HttpPost httppost, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        List<NameValuePair> formParams = Lists.newArrayList();
        params.forEach((k, v) -> formParams.add(new BasicNameValuePair(k, v)));
        try {
            httppost.setEntity(new UrlEncodedFormEntity(formParams, CHARSET));
        } catch (IOException e) {
            logger.error("setParam error: ", e);
        }
    }

    private static String setParam(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return Strings.nullToEmpty(url);
        }

        StringBuilder stbURL = new StringBuilder(url);
        stbURL.append(url.contains("?") ? "&" : "?");

        params.entrySet().stream()
                .filter((e) -> StringUtils.isNotBlank(e.getValue()))
                .forEach((entry) -> {
                    try {
                        stbURL.append(entry.getKey())
                                .append("=")
                                .append(URLEncoder.encode(entry.getValue(), CHARSET))
                                .append("&");
                    } catch (UnsupportedEncodingException e) {
                        logger.error("Error when prepared for remote http params", e);
                    }
                });
        // 删除末尾多余的 & 或 ?
        stbURL.delete(stbURL.length() - 1, stbURL.length());
        return stbURL.toString();
    }

    public static void consumeEntityAndCloseResponse(CloseableHttpResponse response) {
        if (response == null) {
            return;
        }
        try {
            EntityUtils.consumeQuietly(response.getEntity());
            response.close();
        } catch (Exception e) {
            logger.error("close response error", e);
        }
    }

}
