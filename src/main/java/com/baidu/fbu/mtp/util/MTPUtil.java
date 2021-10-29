package com.baidu.fbu.mtp.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created on 14:56 12/16/2015.
 *
 * @author skywalker
 */
public class MTPUtil {

    public static final String DEFAULT_MTP_VERSION = "1.0";
    public static final String DEFAULT_DESTINATION_VERSION = "default";

    public static String getVersion(HttpServletRequest request) {
        String version = request.getParameter("version");
        return StringUtils.isBlank(version) ? DEFAULT_DESTINATION_VERSION : StringUtils.lowerCase(version);
    }
}
