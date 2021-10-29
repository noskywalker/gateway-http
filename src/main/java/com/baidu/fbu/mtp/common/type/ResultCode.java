package com.baidu.fbu.mtp.common.type;

import com.baidu.fbu.mtp.common.EnumTrait;

/**
 * Created on 11:25 11/05/2015.
 *
 * @author skywalker
 */
public enum  ResultCode implements EnumTrait {
    SUCCESS                 (0, "成功"),

    NO_ROUTE                (-1, "无此路由"),

    // 为了保持与业务后台返回的错误码一致
    INTERNAL_ERROR          (7, "服务器异常, 请稍候再试"),
    PARAM_FORMAT_ERROR      (23, "参数格式错误"),
    ERROR_PARAM             (42, "参数输入有误"),
    LOGIN_ERROR             (49, "用户尚未登录或登录超时"),
    REQUEST_LIMIT           (50, "您的操作过于频繁, 请稍后再试"),
    NEED_CODE_CHECK         (1006, "需要验证码验证"),

    NOP                     (-9999, "");



    private int code;
    private String desc;

    ResultCode(int code, String desc) {
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
