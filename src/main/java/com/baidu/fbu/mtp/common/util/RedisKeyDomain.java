/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.fbu.mtp.common.util;

import com.google.common.base.Joiner;

/**
 * Redis String的key统一从此取
 * 
 * @author limingjian
 *
 */
public class RedisKeyDomain {
    /** 登录信息前缀 */
    public static final String ALREADY_LOGIN_PREFIX = "ALREADY_LOGIN";
    /** 唯一前缀 */
    public static final String UNIQUE_REQUEST_PREFIX = "UNIQUE_REQUEST";
    /** 秒级刷机次数记录 */
    public static final String FLUSH_S_RECORD_PREFIX = "FLUSH_S_RECORD_PREFIX";
    /** 秒级刷机限制 */
    public static final String FLUSH_LIMIT_S_PREFIX = "FLUSH_LIMIT_S_PREFIX";
    /** 小时刷机次数记录 */
    public static final String FLUSH_H_RECORD_PREFIX = "FLUSH_H_RECORD_PREFIX";
    /** 小时刷机次数记录 */
    public static final String FLUSH_LIMIT_H_PREFIX = "FLUSH_LIMIT_H_PREFIX";
    /** MTP 配置 */
    public static final String MTP_CONFIG = "MTP_CONFIG";

    public static String buildKey(String prefix, String... subkey) {
        if (subkey == null || subkey.length == 0) {
            throw new RuntimeException("subkey can not be null");
        }
        String[] keys = new String[subkey.length + 1];
        keys[0] = prefix;
        System.arraycopy(subkey, 0, keys, 1, subkey.length);
        return Joiner.on("_").join(keys);
    }
}
