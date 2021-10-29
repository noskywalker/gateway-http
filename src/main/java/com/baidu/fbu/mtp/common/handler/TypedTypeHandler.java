package com.baidu.fbu.mtp.common.handler;

import org.apache.ibatis.type.BaseTypeHandler;

/**
 * Created on 13:42 11/04/2015.
 *
 * @author skywalker
 */
public abstract class TypedTypeHandler<T> extends BaseTypeHandler<T> {
    private Class<T> javaType;

    public TypedTypeHandler(Class<T> javaType) {
        super();
        this.javaType = javaType;
    }

    public Class<T> getJavaType() {
        return javaType;
    }
}
