package com.baidu.fbu.mtp.model;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SystemRouterTest {
    
    private SystemRouter systemRouter;
    
    @Before
    public void setUp() {
        systemRouter = new SystemRouter();
    }
    
    @Test
    public void testSetAndGetGateway() {
        systemRouter.setGateway("127.0.0.1");
        String gateway = systemRouter.getGateway();
        Assert.assertEquals("127.0.0.1", gateway);
    }
    
    @Test
    public void testSetAndGetProtocol() {
        systemRouter.setProtocol("http");
        String protocol = systemRouter.getProtocol();
        Assert.assertEquals("http", protocol);
    }
    
    @Test
    public void testSetAndSetDstVersion() {
        systemRouter.setDstVersion("1.0");
        String dstVersion = systemRouter.getDstVersion();
        Assert.assertEquals("1.0", dstVersion);
    }
    
    @Test
    public void testSetAndGetMtpVersion() {
        systemRouter.setMtpVersion("IOS");
        String mtpVersion = systemRouter.getMtpVersion();
        Assert.assertEquals("IOS", mtpVersion);
    }
    
    @Test
    public void testSetAndGetDeletedFlag() {
        systemRouter.setDeletedFlag(0);
        int deletedFlag = systemRouter.getDeletedFlag();
        Assert.assertEquals(0, deletedFlag);
    }
    
    @Test
    public void testSetAndGetCreateBy() {
        systemRouter.setCreateBy("admin");
        String createBy = systemRouter.getCreateBy();
        Assert.assertEquals("admin", createBy);
    }
    
    @Test
    public void testSetAndGetUpdateBy() {
        systemRouter.setUpdateBy("admin");
        String updateBy = systemRouter.getUpdateBy();
        Assert.assertEquals("admin", updateBy);
    }
    
    @Test
    public void testSetAndGetCreateTime() {
        Date date = new Date(System.currentTimeMillis());
        systemRouter.setCreateTime(date);
        Date createTime = systemRouter.getCreateTime();
        Assert.assertEquals(date, createTime);
    }
    
    @Test
    public void testSetAndGetUpdateTime() {
        Date date = new Date(System.currentTimeMillis());
        systemRouter.setUpdateTime(date);
        Date updateTime = systemRouter.getUpdateTime();
        Assert.assertEquals(date, updateTime);
    }
    
    @Test
    public void testGenerateKey() {
        systemRouter.setDstVersion("1.0");
        systemRouter.setMtpVersion("IOS");
        String key = systemRouter.generateKey();
        Assert.assertEquals("IOS`1.0", key);
    }
    
    @Test
    public void testGetRoute() {
        systemRouter.setProtocol("http");
        systemRouter.setGateway("127.0.0.1");
        String route = systemRouter.getRoute();
        Assert.assertEquals("http://127.0.0.1", route);
    }
}
