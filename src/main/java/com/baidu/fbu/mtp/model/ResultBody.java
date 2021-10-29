package com.baidu.fbu.mtp.model;

import com.baidu.fbu.mtp.common.type.ResultCode;

public class ResultBody {
    private int status;
    private Object result;
    private String msg;

    public ResultBody(int status, Object result) {
        this.status = status;
        this.result = result;
        this.msg = "";
    }

    public ResultBody(ResultCode resultCode, Object result) {
        this(resultCode.getCode(), result);
    }

    public ResultBody(ResultCode resultCode, String ...msg) {
        this.status = resultCode.getCode();
        this.result = "";
        this.msg = resultCode.getDesc() + (msg != null && msg.length > 0 ? msg[0] : "");
    }


    public ResultBody(int status, String msg) {
        this.status = status;
        this.result = new Object();
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
