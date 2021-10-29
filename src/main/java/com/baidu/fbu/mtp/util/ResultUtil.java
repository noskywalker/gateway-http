package com.baidu.fbu.mtp.util;

import com.baidu.fbu.mtp.common.type.ResultCode;
import com.baidu.fbu.mtp.model.ResultBody;
import com.baidu.fbu.mtp.util.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created on 12:48 11/05/2015.
 *
 * @author skywalker
 */
public class ResultUtil {
    private static final Logger log = LoggerFactory.getLogger(ResultUtil.class);

    public static Object success(Object result) {
        ResultBody resultBody = new ResultBody(ResultCode.SUCCESS, result);
        log.info("SUCCESS RESULT:{}", JsonUtil.toJson(result));
        return resultBody;
    }

    public static Object error(ResultCode resultCode, String ...msg) {
        ResultBody resultBody = new ResultBody(resultCode, msg);
        log.error("ERROR RESULT: {}", JsonUtil.toJson(resultBody));
        return resultBody;
    }

    public static Object error(int status, String msg) {
        if (msg == null) {
            msg = "";
        }
        log.info("ERROR RESULT:status={},msg={}", status, msg);
        return new ResultBody(status, msg);
    }


    public static void printErrorMsg(HttpServletResponse response, ResultCode errorCode, String message) {
        String result = "{\"status\":" + errorCode.getCode() + ",\"msg\":\""
                + errorCode.getDesc() + (StringUtils.isNotBlank(message) ? "[" + message + "]" : "")
                + "\",\"result\":{}}";
        log.error("ERROR RESULT:{}", result);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter out = response.getWriter();
            out.print(result);
            out.close();
        } catch (Exception e) {
            log.error("response.getWriter() error", e);
        }

    }
}
