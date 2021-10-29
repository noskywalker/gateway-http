package com.baidu.fbu.mtp.service;


import com.baidu.fbu.mtp.model.RequestMsg;

import javax.servlet.http.HttpServletRequest;

public interface MTPService {

    boolean skipRequest(RequestMsg requestMsg);

    boolean notLogin(String bduss);

    void saveLoginInfo(String key, String value);
    
    boolean isUniqueRequest(String loginInfo);

    void removeRedisData(String key);
    
    String getUserBid(String bduss);

    boolean flushLimit(HttpServletRequest request, RequestMsg requestMsg);

    boolean allowCurrentRequest(RequestMsg requestMsg);

    void triggerAfterCompletion();
    
    boolean filter(HttpServletRequest request, RequestMsg requestMsg);

}
