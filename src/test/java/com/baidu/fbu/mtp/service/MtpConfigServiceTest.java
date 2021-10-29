package com.baidu.fbu.mtp.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.baidu.fbu.mtp.common.type.ConfigType;
import com.baidu.fbu.mtp.dao.ConfigDao;

public class MtpConfigServiceTest {
    
    private MtpConfigService configService;
    private ConfigDao configDao;
    
    @Before
    public void setUp() {
        configService = new MtpConfigService();
        configDao = createMock(ConfigDao.class);
        expect(configDao.selectValue("key", ConfigType.LOGINTYPE)).andReturn(Arrays.asList("configValue"));
        replay(configDao);
        
        ReflectionTestUtils.setField(configService, "configDao", configDao);
    }
    
    @Test
    public void testGetConfigValue() {
        List<String> list = configService.getConfigValue("key", ConfigType.LOGINTYPE);
        System.out.println(list.size());
    }
}
