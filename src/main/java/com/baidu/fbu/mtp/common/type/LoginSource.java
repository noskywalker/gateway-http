package com.baidu.fbu.mtp.common.type;

import com.baidu.fbu.mtp.common.EnumTrait;

/**
 * Created on 14:45 11/05/2015.
 *
 * @author skywalker
 */
public enum LoginSource implements EnumTrait {

    PASS_ID    (1, "百度账号"),
    NUOMI      (2, "糯米"),
    QUNAR      (3, "去哪儿");

    private int code;
    private String desc;

    LoginSource(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public int getCode() {
        return code;
    }
}
