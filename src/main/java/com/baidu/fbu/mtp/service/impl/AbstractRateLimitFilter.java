package com.baidu.fbu.mtp.service.impl;

import com.baidu.fbu.mtp.common.util.Filter;
import com.baidu.fbu.mtp.model.RequestMsg;
import com.baidu.fbu.mtp.service.MTPFilter;


public abstract class AbstractRateLimitFilter implements MTPFilter {

    public boolean skip(RequestMsg requestMsg) {
        Filter annotation = getClass().getAnnotation(Filter.class);
        if (annotation.channel() == null || annotation.channel().length == 0) {
            return false;
        }
        boolean flag = true;
        for (String ch : annotation.channel()) {
            if (ch.equalsIgnoreCase(requestMsg.getChannel())) {
                flag = false;
            }
        }
        if (! flag) {
            flag = true;
            if (annotation.loginType() == null || annotation.loginType().length == 0) {
                return false;
            }
            for (String lo : annotation.loginType()) {
                if (requestMsg.getLogintype().equalsIgnoreCase(lo)) {
                    flag = false;
                }
            }
        }
        return flag;
    }
}
