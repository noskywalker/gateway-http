package com.baidu.fbu.mtp.interceptor;


import com.baidu.fbu.mtp.common.context.Context;
import com.baidu.fbu.mtp.common.type.ConfigType;
import com.baidu.fbu.mtp.common.type.ResultCode;
import com.baidu.fbu.mtp.common.util.RedisKeyDomain;
import com.baidu.fbu.mtp.model.RequestMsg;
import com.baidu.fbu.mtp.service.MTPService;
import com.baidu.fbu.mtp.service.MtpConfigService;
import com.baidu.fbu.mtp.util.ResultUtil;
import com.baidu.fbu.mtp.util.http.LoginPassportUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UmoneyInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(UmoneyInterceptor.class);

    @Resource
    private MTPService mtpService;
    @Resource
    private MtpConfigService configService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String data = req.getParameter("data");
        log.info("request data: {}", data);

        RequestMsg requestMsg = getRequestMsg(req);

        if (mtpService.skipRequest(requestMsg)) {
            if (! mtpService.allowCurrentRequest(requestMsg)) {
                ResultUtil.printErrorMsg(res, ResultCode.REQUEST_LIMIT, "请求受限");
                return false;
            }
            return true;
        }

        if (! verifyParam(requestMsg, res)) {
            return false;
        }

        if (mtpService.flushLimit(req, requestMsg)) {
            ResultUtil.printErrorMsg(res, ResultCode.REQUEST_LIMIT, "");
            return false;
        }

        long bid = getBid(requestMsg);
        // long bid = 144697529L;
        if (bid <= 0) {
            ResultUtil.printErrorMsg(res, ResultCode.LOGIN_ERROR, "bduss 无效或 passport 接口异常");
            return false;
        }

        requestMsg.setBid(bid);

        if (! mtpService.allowCurrentRequest(requestMsg)) {
            ResultUtil.printErrorMsg(res, ResultCode.REQUEST_LIMIT, "请求受限");
            return false;
        }

        return mtpService.filter(req, requestMsg);
    }

    private boolean verifyParam(RequestMsg requestMsg, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(requestMsg.getData())) {
            ResultUtil.printErrorMsg(response, ResultCode.ERROR_PARAM, "data");
            return false;
        }

        List<String> channels = configService.getConfigValue(ConfigType.CHANNEL);
        if (! channels.contains(requestMsg.getChannel())) {
            ResultUtil.printErrorMsg(response, ResultCode.ERROR_PARAM, "channel");
            return false;
        }

        // check loginType
        List<String> loginTypes = configService.getConfigValue(ConfigType.LOGINTYPE);
        if (! loginTypes.contains(requestMsg.getLogintype())) {
            ResultUtil.printErrorMsg(response, ResultCode.ERROR_PARAM, "logintype");
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res,
                                Object handler, Exception ex) throws Exception {
        mtpService.triggerAfterCompletion();
    }

    private RequestMsg getRequestMsg(HttpServletRequest request) {
        return Context.getCurrent().get(RequestMsg.class);
    }

    private long getBid(RequestMsg requestMsg) {
        long bid;
        String bduss = requestMsg.getBduss();
        String loginKey = RedisKeyDomain.buildKey(RedisKeyDomain.ALREADY_LOGIN_PREFIX, bduss);
        if (mtpService.notLogin(loginKey)) {
            bid = LoginPassportUtil.ssnLogin(bduss);
            // bid = 144697529L;
            if (bid > 0) {
                mtpService.saveLoginInfo(loginKey, bid + "");
            }
            return bid;
        } else {
            String userid = mtpService.getUserBid(loginKey);
            bid = StringUtils.isNotBlank(userid) ? Long.valueOf(userid) : -1;
        }

        if (bid <= 0) { // 同一台服务器同时两个请求，处理后一个；不同不同服务器同时请求，处理后一个
            bid = LoginPassportUtil.ssnLogin(bduss);
            // bid = 144697529L;
        }
        return bid;
    }
}
