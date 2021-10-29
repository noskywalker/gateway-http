package com.baidu.fbu.mtp.common.util;

import org.junit.Test;

public class RedisKeyDomainTest {
    
    @Test
    public void testBuildRedisKey() {
        String value = RedisKeyDomain.buildKey("test:", "test-value");
        System.out.println(value);
    }
}
