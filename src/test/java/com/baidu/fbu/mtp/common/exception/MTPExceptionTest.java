package com.baidu.fbu.mtp.common.exception;

import org.junit.Test;

import com.baidu.fbu.mtp.common.type.ResultCode;

public class MTPExceptionTest {
    
    @Test
    public void testMtpException() {
        MTPException ex = new MTPException();
    }
    
    @Test
    public void testMtpExceptionCode() {
        MTPException ex = new MTPException(ResultCode.AUTH_ERROR);
        System.out.println(ex.getCode().getDesc());
    }
    
    @Test
    public void testMtpExceptionErrMsg() {
        MTPException ex = new MTPException("请求失败");
        System.out.println(ex.getMsg());
    }
    
    @Test
    public void testMtpExceptionThrowable() {
        MTPException ex = new MTPException(new Exception());
        System.out.println(ex.getCode().getDesc());
    }
    
    @Test
    public void testMtpExceptionThrowableAndMsg() {
        MTPException ex = new MTPException("请求失败", new Exception());
        System.out.println(ex.getMsg());
    }
    
    @Test
    public void testFillInStackTrace() {
        MTPException ex = new MTPException("请求失败", new Exception());
        ex.fillInStackTrace();
    }
    
    @Test
    public void testSetCode() {
        MTPException ex = new MTPException();
        ex.setCode(ResultCode.AUTH_ERROR);
    }
    
    @Test
    public void testSetMsg() {
        MTPException ex = new MTPException();
        ex.setMsg("SetMsg");
    }
    
}
