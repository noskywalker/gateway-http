package com.baidu.fbu.mtp.util.http;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.baidu.fbu.mtp.common.ConfigUtil;
import com.baidu.fbu.mtp.common.exception.MTPException;
import com.baidu.fbu.mtp.common.type.ConfigFileType;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RemoteUtil.class)
@PowerMockIgnore("javax.net.ssl.*")
public class LoginPassportUtilTest {
    
    @Before
    public void setUp() {
        mockStatic(RemoteUtil.class);
    }
    
    @Test
    public void testSsnLoginStatusNot0() throws Exception {
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", "FJLKSJFKLDSJFLKDSJFLKDSJLKFJDSLKJFLKDS--");
        whenNew(HashMap.class).withNoArguments().thenReturn(map);
        when(RemoteUtil.get(ConfigUtil.getProperty(ConfigFileType.CONFIG_PROPERTY, "PassportLogin_BASE_RUL"), 
                map)).thenReturn("uid=5678&status=1");
        
        LoginPassportUtil.ssnLogin("FJLKSJFKLDSJFLKDSJFLKDSJLKFJDSLKJFLKDS--");
    }
    
    @Test
    public void testSsnLoginBdussEmpty() throws Exception {
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", "FJLKSJFKLDSJFLKDSJFLKDSJLKFJDSLKJFLKDS--");
        whenNew(HashMap.class).withNoArguments().thenReturn(map);
        when(RemoteUtil.get(ConfigUtil.getProperty(ConfigFileType.CONFIG_PROPERTY, "PassportLogin_BASE_RUL"), 
                map)).thenReturn("uid=5678&status=1");
        
        LoginPassportUtil.ssnLogin("");
    }
    
    @Test
    public void testSsnLoginError() throws Exception {
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", "FJLKSJFKLDSJFLKDSJFLKDSJLKFJDSLKJFLKDS--");
        whenNew(HashMap.class).withNoArguments().thenReturn(map);
        when(RemoteUtil.get(ConfigUtil.getProperty(ConfigFileType.CONFIG_PROPERTY, "PassportLogin_BASE_RUL"), 
                map)).thenThrow(new MTPException());
        
        LoginPassportUtil.ssnLogin("FJLKSJFKLDSJFLKDSJFLKDSJLKFJDSLKJFLKDS--");
    }
    
    @Test
    public void testSsnLoginUidAndStatusEmpty() throws Exception {
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", "FJLKSJFKLDSJFLKDSJFLKDSJLKFJDSLKJFLKDS--");
        whenNew(HashMap.class).withNoArguments().thenReturn(map);
        when(RemoteUtil.get(ConfigUtil.getProperty(ConfigFileType.CONFIG_PROPERTY, "PassportLogin_BASE_RUL"), 
                map)).thenReturn("");
        
        LoginPassportUtil.ssnLogin("FJLKSJFKLDSJFLKDSJFLKDSJLKFJDSLKJFLKDS--");
    }
    
    @Test
    public void testSsnLoginUidAndStatus() throws Exception {
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", "FJLKSJFKLDSJFLKDSJFLKDSJLKFJDSLKJFLKDS--");
        whenNew(HashMap.class).withNoArguments().thenReturn(map);
        when(RemoteUtil.get(ConfigUtil.getProperty(ConfigFileType.CONFIG_PROPERTY, "PassportLogin_BASE_RUL"), 
                map)).thenReturn("uid=5678&status=0");
        
        LoginPassportUtil.ssnLogin("FJLKSJFKLDSJFLKDSJFLKDSJLKFJDSLKJFLKDS--");
    }
    
    @Test
    public void testSsnLoginStatusAndUid() throws Exception {
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", "FJLKSJFKLDSJFLKDSJFLKDSJLKFJDSLKJFLKDS--");
        whenNew(HashMap.class).withNoArguments().thenReturn(map);
        when(RemoteUtil.get(ConfigUtil.getProperty(ConfigFileType.CONFIG_PROPERTY, "PassportLogin_BASE_RUL"), 
                map)).thenReturn("status=0&uid=5678");
        
        LoginPassportUtil.ssnLogin("FJLKSJFKLDSJFLKDSJFLKDSJLKFJDSLKJFLKDS--");
    }
}
