package com.baidu.fbu.mtp.util.http;

import org.apache.http.impl.client.DefaultRedirectStrategy;

/**
 * Created on 12:58 12/01/2015.
 *
 * @author skywalker
 */
public class NoRedirectStrategy extends DefaultRedirectStrategy {
    @Override
    protected boolean isRedirectable(final String method) {
        return false;
    }
}
