package com.baidu.fbu.mtp.util.http;

import java.util.Map;

import com.baidu.fbu.mtp.common.ConfigUtil;
import com.baidu.fbu.mtp.common.type.ConfigFileType;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPassportUtil {
    
    private static Logger log = LoggerFactory.getLogger(LoginPassportUtil.class);

    public static long ssnLogin(String bduss) {
        if (StringUtils.isBlank(bduss)) {
            return -1;
        }
        try {
            Map<String, String> paramMap = ImmutableMap.of("sid", bduss);
            String entityInfoStr = RemoteUtil.get(ConfigUtil.getProperty(
                    ConfigFileType.CONFIG_PROPERTY, "PassportLogin_BASE_RUL"), paramMap, true);
            if (StringUtils.isNoneBlank(entityInfoStr)) {
                long statusValue = parseValue(entityInfoStr, "status");
                return statusValue == 0 ? parseValue(entityInfoStr, "uid") : statusValue * (-1);
            }
            return -1;
        } catch (Exception e) {
            log.error("ssnLogin error", e);
            return -1;
        }
    }

    private static long parseValue(String str, String key) {
        String key2 = key + "=";
        int key2Pos = str.indexOf(key2);
        // 从 key2 的位置起找后边的第一个 & 符号
        int ampersandPos = str.indexOf("&", key2Pos);
        if (ampersandPos == -1) { // 未找到了&符号，返回当前位置到末尾的字符串
            return Long.parseLong(str.substring(key2Pos + key2.length()));
        } else {                  // 找到了 & 符号
            return Long.parseLong(str.substring(key2Pos + key2.length(), ampersandPos));
        }
    }
}
