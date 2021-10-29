package com.baidu.fbu.mtp.controller;

import com.baidu.fbu.mtp.common.exception.MTPException;
import com.baidu.fbu.mtp.common.type.ResultCode;
import com.baidu.fbu.mtp.util.ResultUtil;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.SocketTimeoutException;
import java.util.Set;

/**
 * Created on 12:43 11/05/2015.
 *
 * @author skywalker
 */
public abstract class AbstractController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(Exception.class)
    protected void handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        if (e instanceof MTPException) {
            MTPException mtpException = (MTPException) e;
            ResultUtil.printErrorMsg(response, mtpException.getCode(), "");
            return;
        }
        if (! FILTER_EXCEPTION.contains(e.getClass())) {
            logger.error("mtp unexpected exception", e);
        }
        ResultUtil.printErrorMsg(response, ResultCode.INTERNAL_ERROR, "");
    }


    private static final Set<Class<?>> FILTER_EXCEPTION;

    static {
        Set<Class<?>> set = Sets.newHashSet();
        set.add(HttpHostConnectException.class);
        set.add(ConnectTimeoutException.class);
        set.add(ConnectionPoolTimeoutException.class);
        set.add(SocketTimeoutException.class);

        FILTER_EXCEPTION = ImmutableSet.copyOf(set);
    }

}
