package com.baidu.fbu.mtp.common.context;

import org.junit.Before;
import org.junit.Test;

public class ContextTest {
    
    @Before
    public void setUp() {
        Context.getCurrent(true).set(String.class, "Just Context test");
    }
    
    @Test
    public void testGetCurrent() {
        Context context = Context.getCurrent();
        System.out.println(context.get(String.class));
    }
    
    @Test
    public void testGetCurrentCreateIfAbsent() {
        Context context = Context.getCurrent(true);
        System.out.println(context.get(String.class));
    }
    
    @Test
    public void testGetFromCurrentType() {
        String result = Context.getFromCurrent(String.class);
        System.out.println(result);
    }
    
    @Test
    public void testGetFromCurrentBoolean() {
        String result = Context.getFromCurrent(String.class, true);
        System.out.println(result);
    }
    
    @Test
    public void testGetFromCurrentClassName() {
        String result = (String) Context.getFromCurrent(String.class.getName());
        System.out.println(result);
    }
    
    @Test
    public void testGetFromCurrentClassType() {
        String result = (String) Context.getFromCurrent(String.class);
        System.out.println(result);
    }
    
    @Test
    public void testCacheGetClassName() {
        String result = (String) Context.getCurrent().get(String.class.getName());
        System.out.println(result);
    }
    
    @Test
    public void testSetClassName() {
        Context.getCurrent().set(String.class.getName(), "Context Set By ClassName");
        String result = (String) Context.getCurrent().get(String.class.getName());
        System.out.println(result);
    }
    
    @Test
    public void testSetClassType() {
        Context.getCurrent().set(String.class, "Context Set By ClassType");
        String result = (String) Context.getCurrent().get(String.class.getName());
        System.out.println(result);
    }
    
    @Test
    public void testCacheRemoveClassName() {
        Context.getCurrent().remove(String.class.getName());
    }
    
    @Test
    public void testCacheRemoveClassType() {
        Context.getCurrent().remove(String.class);
    }
    
    @Test
    public void testClear() {
        Context.getCurrent().clear();
    }
    
    @Test
    public void testClose() {
        Context.getCurrent().close();
    }
    
    @Test
    public void testRemoveCurrent() {
        Context.removeCurrent();
    }
    
}
