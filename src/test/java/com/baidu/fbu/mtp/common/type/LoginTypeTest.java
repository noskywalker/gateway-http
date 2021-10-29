package com.baidu.fbu.mtp.common.type;

import org.junit.Assert;
import org.junit.Test;

public class LoginTypeTest {

    @Test
    public void testGetCode() {
        int code = LoginType.APP001.getCode();
        Assert.assertEquals(1, code);
    }
    
    @Test
    public void testGetDesc() {
        String desc = LoginType.APP001.getDesc();
        Assert.assertEquals("", desc);
    }
}
