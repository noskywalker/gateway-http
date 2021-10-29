package com.baidu.fbu.mtp.common.type;

import com.baidu.fbu.mtp.common.EnumTrait;

/**
 * Created on 13:36 11/04/2015.
 *
 * @author skywalker
 */
public enum  ConfigType implements EnumTrait {
    REQUEST_CONCURRENT   (1, "允许并发请求"),
    REQUEST_NOT_AUTH     (2, "不做登陆限制"),
    REQUEST_PREFIX       (3, "前缀请求"),

    CHANNEL              (11, "channel"),
    LOGINTYPE            (12, "logintype");

    private int code;
    private String desc;

    ConfigType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public String lowerCase() {
        return this.name().toLowerCase();
    }

    @Override
    public int getCode() {
        return code;
    }
}
