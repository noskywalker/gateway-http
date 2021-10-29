package com.baidu.fbu.mtp.common.type;

import org.junit.Assert;
import org.junit.Test;

public class ConfigTypeTest {
    
    @Test
    public void testGetCode() {
        int code = ConfigType.CHANNEL.getCode();
        Assert.assertEquals(11, code);
    }
    
    @Test
    public void testGetDesc() {
        String desc = ConfigType.CHANNEL.getDesc();
        Assert.assertEquals("channel", desc);
    }
}
