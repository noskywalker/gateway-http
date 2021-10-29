package com.baidu.fbu.mtp.common.type;

import org.junit.Assert;
import org.junit.Test;

public class ConfigFileTypeTest {
    
    @Test
    public void testGetCode() {
        int code = ConfigFileType.CONFIG_PROPERTY.getCode();
        Assert.assertEquals(1, code);
    }
    
    @Test
    public void testGetName() {
        String name = ConfigFileType.CONFIG_PROPERTY.getName();
        Assert.assertEquals("config.properties", name);
    }
    
}
