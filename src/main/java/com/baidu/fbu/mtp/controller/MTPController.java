package com.baidu.fbu.mtp.controller;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.baidu.fbu.mtp.common.Timing.timing;
import com.baidu.fbu.mtp.common.context.Context;
import com.baidu.fbu.mtp.common.retry.RetryException;
import com.baidu.fbu.mtp.model.RequestMsg;
import com.baidu.fbu.mtp.service.SystemRouterService;
import com.baidu.fbu.mtp.util.MTPUtil;
import com.baidu.fbu.mtp.util.http.RemoteUtil;
import com.google.common.base.Stopwatch;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baidu.fbu.mtp.util.HttpProxyUtil;

/**
 * 转发器的基类.
 *
 * @author skywalker
 *
 */
@Controller
public class MTPController extends AbstractController {

    /** 系统路由服务 */
    @Resource
    private SystemRouterService systemRouterService;

    /**
     * 完成Http消息转发.
     */
    @RequestMapping(value = "/**")
    public void forward(HttpServletRequest request, HttpServletResponse response) throws Exception {
        URI forwardUri = getSystemURI(MTPUtil.getVersion(request), request.getRequestURI());

        CloseableHttpResponse remoteResponse = null;
        try {
            remoteResponse = execute(request, forwardUri);
            HttpProxyUtil.copyResponse(remoteResponse, response);
        } catch (Exception e) {
            logger.error("http execute error", e);
            throw e;
        } finally {
            RemoteUtil.consumeEntityAndCloseResponse(remoteResponse);
        }
    }

    private CloseableHttpResponse execute(HttpServletRequest request, URI forwardUri) throws Exception {
        try {
            return doExecute(request, HttpMethod.valueOf(request.getMethod()), forwardUri);
        } catch (HttpHostConnectException e) {
            logger.error("Remote Server Unavailable.", e);
            throw e;
        } catch (ConnectionPoolTimeoutException e) {
            logger.error("Connection Pool Timeout.", e);
            throw e;
        } catch (ConnectTimeoutException e) {
            logger.error("Connect Timeout.", e);
            throw e;
        } catch (SocketTimeoutException e) {
            logger.error("Remote Server Timeout.", e);
            throw e;
        }
    }

    private CloseableHttpResponse doExecute(HttpServletRequest originalReq, HttpMethod method, URI uri)
            throws Exception {
        Stopwatch w = Stopwatch.createStarted();
        HttpRequest newRequest = HttpProxyUtil.createHttpRequest(originalReq, uri, method);
        HttpHost httpHost = HttpProxyUtil.createHttpHost(uri);
        try {
            return RemoteUtil.executeWithRetry(newRequest, httpHost);
        } catch (RetryException e) {
            Exception actual = e.getLastFailedAttempt();
            throw actual != null ? actual : e;
        } catch (ExecutionException e) {
            Throwable actual = e.getCause();
            throw actual instanceof Exception ? (Exception) actual : e;
        } finally {
            long takes = timing(w.stop());
            logger.info("forward request: {}, takes: {}", uri, takes);
            try {
                Context.getFromCurrent(RequestMsg.class, false).setForwardTime(takes);
            } catch (Exception e) {
                logger.error("setForwardTime error", e);
            }
        }
    }

    protected URI getSystemURI(String dstVersion, String requestPath) throws Exception {
        String mtpVersion = MTPUtil.DEFAULT_MTP_VERSION;
        String systemAddress = systemRouterService.getSystemURI(mtpVersion, dstVersion);
        URI uri = new URI(systemAddress + requestPath);
        try {
            Context.getFromCurrent(RequestMsg.class, false).setDestination(uri);
        } catch (Exception e) {
            logger.error("setDestination error", e);
        }
        return uri;
    }
}
