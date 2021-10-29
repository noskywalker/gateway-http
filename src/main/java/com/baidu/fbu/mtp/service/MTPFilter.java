package com.baidu.fbu.mtp.service;

import com.baidu.fbu.mtp.model.RequestMsg;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MTPFilter {
    
    boolean filter(HttpServletRequest req, RequestMsg requestMsg);
}
