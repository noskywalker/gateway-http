package com.baidu.fbu.mtp.common;

import org.junit.Test;

import com.baidu.fbu.mtp.common.type.ConfigFileType;

public class ConfigUtilTest {
    
    @Test
    public void testGetProperty() {
        String pro = ConfigUtil.getProperty(ConfigFileType.CONFIG_PROPERTY, "PassportLogin_BASE_RUL");
        System.out.println(pro);
    }
    
    @Test
    public void testGetString() {
        String pro = ConfigUtil.getString(ConfigFileType.CONFIG_PROPERTY, "PassportLogin_BASE_RUL", "为找到对应属性");
        System.out.println(pro);
    }
    
    @Test
    public void testGetLong() {
        Long pro = ConfigUtil.getLong(ConfigFileType.CONFIG_PROPERTY, "testInt", 0);
        System.out.println(pro);
    }
    
    @Test
    public void testGetInt() {
        int pro = ConfigUtil.getInt(ConfigFileType.CONFIG_PROPERTY, "testInt", 0);
        System.out.println(pro);
    }
}
