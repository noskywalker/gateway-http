package com.baidu.fbu.mtp.common.type;

import com.baidu.fbu.mtp.common.EnumTrait;

/**
 * Created on 11:05 11/04/2015.
 *
 * @author skywalker
 */
public enum  ConfigFileType implements EnumTrait {
    CONFIG_PROPERTY (1, "config.properties");

    private String name;
    private int code;

    ConfigFileType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getCode() {
        return code;
    }
}
