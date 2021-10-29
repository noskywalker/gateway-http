package com.baidu.fbu.mtp.common;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.baidu.fbu.mtp.common.Config.ChangeListener;

public class ConfigTest {
    
    private Config config;
    
    @Test
    public void testGetXML() {
        Config config = Config.get("spring-dao.xml");
        System.out.println(config.toString());
    }
    
    @Test(expected = RuntimeException.class)
    public void testGetXMLPathNull() {
        Config config = Config.get("spring-dao1.xml");
        System.out.println(config.toString());
    }
    
    @Test
    public void testGetProperties() {
        Config config = Config.get("config.properties");
        System.out.println(config.toString());
    }
    
    @Test
    public void testGetOrNullXML() {
        Config config = Config.getOrNull("spring-dao.xml");
        System.out.println(config.toString());
    }
    
    @Test
    public void testGetOrNullProperties() {
        Config config = Config.getOrNull("config.properties");
        System.out.println(config.toString());
    }
    
    @Test
    public void testGetOrNullNameNull() {
        Config config = Config.getOrNull(null);
    }
    
    @Before
    public void setUp() {
        URL path = Thread.currentThread().getContextClassLoader().getResource("config.properties");
        File file = new File(path.getPath());
        config = new Config(file, "UTF-8");
    }
    
    @Test
    public void testCheckUpdate() {
        config.checkUpdate(true);
    }
    
    @Test
    public void testGetProperty() {
        String pro = config.getProperty("PassportLogin_BASE_RUL");
        System.out.println(pro);
    }
    
    @Test
    public void testGetString() {
        String pro = config.getString("PassportLogin_BASE_RUL", "");
        System.out.println(pro);
    }
    
    @Test
    public void testGetStringNotExist() {
        String pro = config.getString("PassportLogin_BASE", "");
        System.out.println(pro);
    }
    
    @Test
    public void testGetInt() {
        int pro = config.getInt("testInt", 0);
        System.out.println(pro);
    }
    
    @Test
    public void testGetIntNotExist() {
        int pro = config.getInt("test", 0);
        System.out.println(pro);
    }
    
    @Test
    public void testGetLong() {
        Long pro = config.getLong("testInt", 0);
        System.out.println(pro);
    }
    
    @Test
    public void testGetLongNotExist() {
        Long pro = config.getLong("test", 0);
        System.out.println(pro);
    }
    
    @Test
    public void testGetFloat() {
        Float pro = config.getFloat("testInt", 0);
        System.out.println(pro);
    }
    
    @Test
    public void testGetFloatNotExist() {
        Float pro = config.getFloat("test", 0);
        System.out.println(pro);
    }
    
    @Test
    public void testGetDouble() {
        Double pro = config.getDouble("testInt", 0);
        System.out.println(pro);
    }
    
    @Test
    public void testGetDoubleNotExist() {
        Double pro = config.getDouble("test", 0);
        System.out.println(pro);
    }
    
    @Test
    public void testGetBoolean() {
        Boolean pro = config.getBoolean("testInt", false);
        System.out.println(pro);
    }
    
    @Test
    public void testGetBooleanNotExist() {
        Boolean pro = config.getBoolean("test", false);
        System.out.println(pro);
    }
    
    @Test
    public void testGetAll() {
        Map<String, String> map = config.getAll();
        System.out.println(map.toString());
    }
    
    @Test
    public void testAddChangeListener() {
        ChangeListener listener = EasyMock.createMock(ChangeListener.class);
        config.addChangeListener(listener);
    }
}
