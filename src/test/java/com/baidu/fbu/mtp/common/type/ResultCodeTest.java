package com.baidu.fbu.mtp.common.type;

import org.junit.Assert;
import org.junit.Test;

public class ResultCodeTest {
    
    @Test
    public void testGetCode() {
        int code = ResultCode.SUCCESS.getCode();
        Assert.assertEquals(0, code);
    }
    
    @Test
    public void testGetDesc() {
        String desc = ResultCode.SUCCESS.getDesc();
        Assert.assertEquals("成功", desc);
    }
}
