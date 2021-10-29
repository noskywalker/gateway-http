/* Copyright 2014-2015 baidu.com. All rights reserved. */
package com.baidu.fbu.mtp.common.exception;

import com.baidu.fbu.mtp.common.type.ResultCode;

public class MTPException extends RuntimeException {

    private boolean stackTraceEnabled = true;

    private ResultCode code = ResultCode.NOP;
    private String msg = "";

    public MTPException() {
    }

    public MTPException(ResultCode code) {
        this.stackTraceEnabled = false;
        this.code = code;
        this.msg = "";
    }

    public MTPException(ResultCode code, String msg) {
        this(msg);
        this.code = code;

    }

    public MTPException(String errMsg) {
        this.stackTraceEnabled = false;
        this.msg = errMsg;
    }

    public MTPException(Throwable cause) {
        super(cause);
        code = ResultCode.INTERNAL_ERROR;
    }

    public MTPException(String errMsg, Throwable cause) {
        super(errMsg, cause);
        code = ResultCode.INTERNAL_ERROR;
        this.msg = errMsg;
    }

    public ResultCode getCode() {
        return code;
    }

    public void setCode(ResultCode code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        if (! stackTraceEnabled) {
            return null;
        }
        return super.fillInStackTrace();
    }
}
