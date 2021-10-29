package com.baidu.fbu.mtp.common.type;

import com.baidu.fbu.mtp.common.EnumTrait;

/**
 * Created on 15:49 08/19/2015.
 *
 * @author skywalker
 */
public enum ChannelType implements EnumTrait {
    ZCP001          (1, "主产品"),
    TB001           (2, "贴吧"),
    qianbao         (3, "钱包"),
    NUOMI01         (4, "糯米"),
    QN001           (5, "去哪儿"),
    CC              (6, "客服系统"),
    qianbao_app     (7, "钱包NA端");

    private int code;
    private String desc;

    ChannelType(int code, String desc) {
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

    public static boolean equals(ChannelType channel1, String channelStr) {
        return channel1.name().equals(channelStr);
    }
}
