package com.baidu.fbu.mtp.model;

import com.baidu.fbu.mtp.common.type.ConfigType;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created on 14:03 12/31/2015.
 *
 * @author skywalker
 */
public class ConfigKey {
    private String key;
    private ConfigType type;

    public ConfigKey(String key, ConfigType type) {
        this.key = checkNotNull(key, "key");
        this.type = checkNotNull(type, "type");
    }

    public String getKey() {
        return key;
    }

    public ConfigType getType() {
        return type;
    }

    public String getConfigKey() {
        return key + type.getCode();
    }

    @Override
    public int hashCode() {
        return getConfigKey().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ConfigKey) {
            return getConfigKey().equals(((ConfigKey) obj).getConfigKey());
        }
        return false;
    }

    @Override
    public String toString() {
        return "[key=" + key + ", type=" + type + "]";
    }
}
