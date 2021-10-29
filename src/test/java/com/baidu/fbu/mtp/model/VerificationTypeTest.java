package com.baidu.fbu.mtp.model;

import org.junit.Assert;
import org.junit.Test;

public class VerificationTypeTest {
    
    @Test
    public void testValue() {
        int value = VerificationType.WEIBO.value();
        Assert.assertEquals(1, value);
    }
    
    @Test
    public void testValueOf() {
        VerificationType type = VerificationType.valueOf(1);
        Assert.assertEquals(1, type.value());
    }
}
