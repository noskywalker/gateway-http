package com.baidu.fbu.mtp.common.handler;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.baidu.fbu.mtp.common.type.ConfigType;
import com.mysql.jdbc.CallableStatement;

public class GenericEnumTypeHandlerTest {
    
    private GenericEnumTypeHandler<ConfigType> handler;
    
    @Before
    public void setUp() {
        
        handler = new GenericEnumTypeHandler<ConfigType>(ConfigType.class);
    }
    
    @Test
    public void testSetNonNullParameter() throws SQLException {
        PreparedStatement ps = createMock(PreparedStatement.class);
        ps.setInt(0, 11);
        expectLastCall().anyTimes();
        replay(ps);
        handler.setNonNullParameter(ps, 0, ConfigType.CHANNEL, null);
    }
    
    @Test
    public void testGetNullableResultWithName() throws SQLException {
        ResultSet rs = createMock(ResultSet.class);
        expect(rs.getInt("CHANNEL")).andReturn(11);
        replay(rs);
        ConfigType type = handler.getNullableResult(rs, "CHANNEL");
        System.out.println(type.getCode());
    }
    
    @Test
    public void testGetNullableResultRSAndIndex() throws SQLException {
        ResultSet rs = createMock(ResultSet.class);
        expect(rs.getInt(11)).andReturn(11);
        replay(rs);
        
        ConfigType type = handler.getNullableResult(rs, 11);
        System.out.println(type.getCode());
    }
    
    @Test
    public void testGetNullableResultCSAndIndex() throws SQLException {
        CallableStatement cs = createMock(CallableStatement.class);
        expect(cs.getInt(11)).andReturn(11);
        replay(cs);
        ConfigType type = handler.getNullableResult(cs, 11);
        System.out.println(type.getDesc());
    }
}
