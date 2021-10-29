package com.baidu.fbu.mtp.common.type;

import org.junit.Assert;
import org.junit.Test;

public class LoginSourceTest {
    
    @Test
    public void testGetCode() {
        int code = LoginSource.PASS_ID.getCode();
        Assert.assertEquals(1, code);
    }
    
    @Test
    public void testGetDesc() {
        String desc = LoginSource.PASS_ID.getDesc();
        Assert.assertEquals("百度账号", desc);
    }
}
