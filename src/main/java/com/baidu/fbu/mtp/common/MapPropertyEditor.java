package com.baidu.fbu.mtp.common;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class MapPropertyEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isEmpty(text)) {
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        String[] arrays = text.split(",");
        for (int i = 0; i < arrays.length; i++) {
            String[] keyValue = arrays[i].split("=");
            map.put(keyValue[0], keyValue[1]);
        }

        super.setValue(map);
    }
}
