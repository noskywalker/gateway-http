package com.baidu.fbu.mtp.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.baidu.fbu.mtp.dao.SysRouterDao;
import com.baidu.fbu.mtp.model.SystemRouter;
import com.baidu.fbu.mtp.service.impl.SystemRouterServiceImpl;

public class SystemRouterServiceTest {
    
    private SystemRouterServiceImpl systemRouterService;
    
    @Before
    public void setUp() throws Exception {
        systemRouterService = new SystemRouterServiceImpl();
        
        SystemRouter sysRouter = new SystemRouter();
        sysRouter.setDstVersion("IOS");
        sysRouter.setGateway("127.0.0.1");
        sysRouter.setMtpVersion("1.0");
        sysRouter.setProtocol("http");
        List<SystemRouter> list = Arrays.asList(sysRouter);
        SysRouterDao sysRouterDao = createMock(SysRouterDao.class);
        expect(sysRouterDao.selectAllRoute()).andReturn(list).times(2);
        replay(sysRouterDao);
        
        Map<String, SystemRouter> routerTable = new ConcurrentHashMap<String, SystemRouter>();
        routerTable.put(sysRouter.generateKey(), sysRouter);
        
        ReflectionTestUtils.setField(systemRouterService, "sysRouterDao", sysRouterDao);
        ReflectionTestUtils.setField(systemRouterService, "routerTable", routerTable);
    }
    
    @Test
    public void testInitRouterTable() {
        systemRouterService.initRouterTable();
    }
    
    @Test
    public void testSyncRouterTable() {
        ReflectionTestUtils.setField(systemRouterService, "isLoaded", true);
        systemRouterService.syncRouterTable();
    }
    
    @Test
    public void testGetSystemURI() {
        String uri = systemRouterService.getSystemURI("1.0", "IOS");
        System.out.println(uri);
    }
    
}
