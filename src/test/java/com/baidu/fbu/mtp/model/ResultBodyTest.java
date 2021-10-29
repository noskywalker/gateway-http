package com.baidu.fbu.mtp.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.baidu.fbu.mtp.common.type.ResultCode;

public class ResultBodyTest {
    
    private ResultBody resultBody;
    
    @Before
    public void setUp() {
        resultBody = new ResultBody(-2, "ResultBody test");
    }
    
    @Test
    public void testResultBodyWithStatusAndObject() {
        ResultBody resultBody = new ResultBody(0, "testResultBodyWithStatusAndObject");
        System.out.println(resultBody.getStatus());
    }
    
    @Test
    public void testResultBodyWithStatusAndMsg() {
        ResultBody resultBody = new ResultBody(-2, "ResultBody test");
        System.out.println(resultBody.getMsg());
    }
    
    @Test
    public void testResultBodyWithResultCodeAndMsg() {
        ResultBody resultBody = new ResultBody(ResultCode.AUTH_ERROR, "ResultBody test", "ResultBody test");
        System.out.println(resultBody.getMsg());
    }
    
    @Test
    public void testResultBodyWithResultCodeAndObject() {
        ResultBody resultBody = new ResultBody(ResultCode.AUTH_ERROR, "ResultBody test Object");
        System.out.println(resultBody.getMsg());
    }
    
    @Test
    public void testSetAndGetStatus() {
        resultBody.setStatus(-3);
        int status = resultBody.getStatus();
        Assert.assertEquals(-3, status);
    }
    
    @Test
    public void testSetAndGetResult() {
        resultBody.setResult("Object result");
        Object object = resultBody.getResult();
        Assert.assertEquals("Object result", object);
    }
    
    @Test
    public void testSetAndGetMsg() {
        resultBody.setMsg("set msg");
        String msg = resultBody.getMsg();
        Assert.assertEquals("set msg", msg);
    }
}
