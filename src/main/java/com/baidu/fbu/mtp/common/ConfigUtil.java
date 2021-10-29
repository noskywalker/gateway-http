package com.baidu.fbu.mtp.common;

import com.baidu.fbu.mtp.common.type.ConfigFileType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created on 10:39 11/04/2015.
 *
 * @author skywalker
 */
public class ConfigUtil {

    private final static Map<String, Config> CONFIG_MAP;

    static {
        Map<String, Config> map = Maps.newHashMap();
        for (ConfigFileType file : ConfigFileType.values()) {
            map.put(file.getName(), Config.get(file.getName()));
        }
        CONFIG_MAP = ImmutableMap.copyOf(map);
    }

    public static String getProperty(ConfigFileType fileType, String key) {
        return CONFIG_MAP.get(fileType.getName()).getProperty(key);
    }

    public static String getString(ConfigFileType fileType, String key, String def) {
        return CONFIG_MAP.get(fileType.getName()).getString(key, def);
    }

    public static long getLong(ConfigFileType fileType, String key, long def) {
        return CONFIG_MAP.get(fileType.getName()).getLong(key, def);
    }

    public static int getInt(ConfigFileType fileType, String key, int def) {
        return CONFIG_MAP.get(fileType.getName()).getInt(key, def);
    }

}
