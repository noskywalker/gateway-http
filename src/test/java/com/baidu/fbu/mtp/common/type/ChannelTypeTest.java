package com.baidu.fbu.mtp.common.type;

import org.junit.Assert;
import org.junit.Test;

public class ChannelTypeTest {
    
    @Test
    public void testGetCode() {
        int code = ChannelType.ZCP001.getCode();
        Assert.assertEquals(1, code);
    }
    
    @Test
    public void testGetDesc() {
        String desc = ChannelType.ZCP001.getDesc();
        Assert.assertEquals("主产品", desc);
    }
    
    @Test
    public void testEquals() {
        boolean b = ChannelType.ZCP001.equals(ChannelType.ZCP001, "ZCP001");
        Assert.assertEquals(true, b);
    }
}
