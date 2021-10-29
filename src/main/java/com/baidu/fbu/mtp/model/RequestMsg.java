package com.baidu.fbu.mtp.model;

import com.alibaba.fastjson.JSONObject;
import com.baidu.fbu.mtp.common.type.FlagType;

import java.net.URI;
import java.util.BitSet;

/**
 * Created on 14:53 11/03/2015.
 *
 * @author skywalker
 */
public class RequestMsg {
    private JSONObject dataJson;
    private String data;
    private String requestURI;
    private String channel;
    private String method;
    private String logintype;
    private String methoddata;
    private String bduss;
    private Long bid;
    private String version;
    private URI destination;
    private long forwardTime;
    private BitSet flag;

    public JSONObject getDataJson() {
        return dataJson;
    }

    public void setDataJson(JSONObject dataJson) {
        this.dataJson = dataJson;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getLogintype() {
        return logintype;
    }

    public void setLogintype(String logintype) {
        this.logintype = logintype;
    }

    public String getMethoddata() {
        return methoddata;
    }

    public void setMethoddata(String methoddata) {
        this.methoddata = methoddata;
    }

    public String getBduss() {
        return bduss;
    }

    public void setBduss(String bduss) {
        this.bduss = bduss;
    }

    public Long getBid() {
        return bid;
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public URI getDestination() {
        return destination;
    }

    public void setDestination(URI destination) {
        this.destination = destination;
    }

    public long getForwardTime() {
        return forwardTime;
    }

    public void setForwardTime(long forwardTime) {
        this.forwardTime = forwardTime;
    }

    public void setFlag(FlagType flagType) {
        if (flag == null) {
            flag = new BitSet(32);
        }
        flag.set(flagType.getCode());
    }

    public boolean isFlag(FlagType flagType) {
        return flag != null && flag.get(flagType.getCode());
    }
}
