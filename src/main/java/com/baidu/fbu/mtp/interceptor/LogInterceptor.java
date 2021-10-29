/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.fbu.mtp.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baidu.fbu.mtp.common.Constants;
import com.baidu.fbu.mtp.common.context.Context;
import com.baidu.fbu.mtp.common.type.ResultCode;
import com.baidu.fbu.mtp.model.RequestMsg;
import com.baidu.fbu.mtp.util.MTPUtil;
import com.baidu.fbu.mtp.util.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.Random;

public class LogInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(LogInterceptor.class);
    private static final NamedThreadLocal<Long> TIME_THREAD_LOCAL = new NamedThreadLocal<>("StopWatch-StartTime");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        MDC.put(Constants.TRACE_KEY, random() + "");
        TIME_THREAD_LOCAL.set(System.currentTimeMillis());
        try {
            Context.getCurrent(true).set(RequestMsg.class, getRequestMsg(request, response));
        } catch (Exception e) {
            logger.error("parse request message error", e);
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        try {
            long takes = System.currentTimeMillis() - TIME_THREAD_LOCAL.get();
            RequestMsg r = Context.getCurrent().get(RequestMsg.class);
            logger.info("request uri: {}, method: {}, channel: {}, bid: {}, loginType: {}, "
                      + "version: {}, dst: {}, forward takes: {}, delta: {}, total takes: {}",
                    r.getRequestURI(), r.getMethod(), r.getChannel(), r.getBid(), r.getLogintype(),
                    r.getVersion(), r.getDestination(), r.getForwardTime(), takes - r.getForwardTime(), takes);
        } finally {
            Context.removeCurrent();
            TIME_THREAD_LOCAL.remove();
            MDC.remove(Constants.TRACE_KEY);
        }
    }

    private RequestMsg getRequestMsg(HttpServletRequest request, HttpServletResponse response) {
        RequestMsg requestMsg = new RequestMsg();
        requestMsg.setVersion(MTPUtil.getVersion(request));
        requestMsg.setRequestURI(request.getRequestURI());

        String data = request.getParameter("data");
        requestMsg.setData(data);

        if (StringUtils.isBlank(data)) {
            return requestMsg;
        }

        try {
            JSONObject dataJson = JSONObject.parseObject(data);
            requestMsg.setDataJson(dataJson);

            // method
            String method = dataJson.getString("method");
            requestMsg.setMethod(method);

            // channel
            String channel = dataJson.getString("channel");
            requestMsg.setChannel(channel);

            // logintype
            String logintype = dataJson.getString("logintype");
            requestMsg.setLogintype(logintype);

            // bduss
            JSONObject channelData = dataJson.getJSONObject("channeldata");
            if (channelData != null) {
                String bduss = channelData.getString("bduss");
                requestMsg.setBduss(bduss);
            }
            tryParsePassUid(requestMsg);
        } catch (JSONException e) {
            logger.error("parse data error", e);
            ResultUtil.printErrorMsg(response, ResultCode.PARAM_FORMAT_ERROR, "JSON 解析错误");
            throw e;
        } catch (NullPointerException e) {
            return requestMsg;
        }
        return requestMsg;
    }

    private void tryParsePassUid(RequestMsg requestMsg) {
        if (StringUtils.isNotBlank(requestMsg.getBduss())) {
            return;
        }
        try {
            JSONObject methodData = requestMsg.getDataJson().getJSONObject("methoddata");
            if (methodData == null) {
                return;
            }
            String passUidStr = methodData.getString("passid");
            passUidStr = StringUtils.isBlank(passUidStr) ? methodData.getString("bid") : passUidStr;
            if (StringUtils.isBlank(passUidStr)) {
                return;
            }
            Long passUid = Long.parseLong(passUidStr);
            requestMsg.setBid(passUid);
        } catch (Exception e) {
            logger.warn("tryParsePassUid error");
        }
    }

    private int random() {
        Random r = new Random();
        long r1 = r.nextLong();
        long hilo = r1 ^ r.nextLong();
        return ((int) (hilo >> 32)) ^ (int) hilo;
    }

}
