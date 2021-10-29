package com.baidu.fbu.mtp.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;

import com.baidu.fbu.mtp.service.MTPFilter;
import com.baidu.fbu.mtp.service.impl.CreditApplyFilterByDevice;
import com.baidu.fbu.mtp.service.impl.CreditApplyFilterByIP;
import com.baidu.fbu.mtp.service.impl.ImageValidateCodeByDevice;

public class ComponentUntilsTest {
    
    private List<Object> filters;
    private ProxyFactory pf;
    private MTPFilter ipFilter;
    
    @Before
    public void setUp() {
        filters = new ArrayList<Object>();
        MTPFilter deviceFilter = new CreditApplyFilterByDevice();
        MTPFilter imgFilter = new ImageValidateCodeByDevice();
        ipFilter = new CreditApplyFilterByIP();
        pf = createMock(ProxyFactory.class);
        
        filters.add(new Object());
        filters.add(deviceFilter);
        filters.add(imgFilter);
        filters.add(ipFilter);
        filters.add(pf);
    }
    
    @Test
    public void testSortFiltersNull() {
        ComponentUtils.sortFilters(null);
    }
    
    @Test
    public void testSortFiltersException() {
        ComponentUtils.sortFilters(filters);
    }
    
    @Test
    public void testSortFilters() throws Exception {
        TargetSource targetSource = createMock(TargetSource.class);
        expect(pf.getTargetSource()).andReturn(targetSource).times(5);
        replay(pf);
        expect(targetSource.getTarget()).andReturn(ipFilter).times(5);
        replay(targetSource);
        ComponentUtils.sortFilters(filters);
    }
    
}
