package com.baidu.fbu.mtp.common.type;

import com.baidu.fbu.mtp.common.EnumTrait;
import com.baidu.fbu.mtp.model.RequestMsg;

/**
 * Created on 18:45 12/31/2015.
 *
 * 调用 {@link RequestMsg#isFlag(FlagType)} 进行一些标记的设置
 *
 * @author skywalker
 */
public enum FlagType implements EnumTrait {
    CONCURRENT_REQ  (1, "并发请求设置标记");

    private int code;
    private String desc;

    FlagType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
