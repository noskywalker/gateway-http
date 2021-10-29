package com.baidu.fbu.mtp.common.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {
    
    int order() default 0;
    
    String[] channel() default {};
    
    String[] loginType() default {};
}
