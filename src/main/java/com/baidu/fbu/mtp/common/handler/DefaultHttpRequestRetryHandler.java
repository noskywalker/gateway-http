package com.baidu.fbu.mtp.common.handler;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created on 10:38 01/05/2016.
 *
 * @author skywalker
 */
public class DefaultHttpRequestRetryHandler implements HttpRequestRetryHandler {

    @Override
    public boolean retryRequest(IOException e, int executionCount, HttpContext context) {
        if (executionCount >= 3) {
            // Do not retry if over max retry count
            return false;
        }
        if (e instanceof NoHttpResponseException) {
            return true;
        }
        if (e instanceof ConnectTimeoutException) {
            return false;
        }
        if (e instanceof SocketTimeoutException) {
            return false;
        }
        if (e instanceof HttpHostConnectException) {
            return false;
        }

        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final HttpRequest request = clientContext.getRequest();

        if (handleAsIdempotent(request)) {
            // Retry if the request is considered idempotent
            return true;
        }
        if (! clientContext.isRequestSent()) {
            // Retry if the request has not been sent fully or
            // if it's OK to retry methods that have been sent
            return true;
        }
        // otherwise do not retry
        return false;
    }

    protected boolean handleAsIdempotent(final HttpRequest request) {
        return ! (request instanceof HttpEntityEnclosingRequest);
    }
}
