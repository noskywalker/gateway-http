package com.baidu.fbu.mtp.model;

import com.google.common.base.Joiner;

import java.util.Date;

public class SystemRouter {

    /** 分隔符. */
    public static final String SEPARATOR = "`";

    private String gateway;
    private String protocol;
    private String dstVersion;
    private String mtpVersion;
    private int deletedFlag;
    private String createBy;
    private String updateBy;
    private Date createTime;
    private Date updateTime;


    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDstVersion() {
        return dstVersion;
    }

    public void setDstVersion(String dstVersion) {
        this.dstVersion = dstVersion;
    }

    public String getMtpVersion() {
        return mtpVersion;
    }

    public void setMtpVersion(String mtpVersion) {
        this.mtpVersion = mtpVersion;
    }

    public int getDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(int deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String generateKey() {
        return Joiner.on(SEPARATOR).join(mtpVersion, dstVersion);
    }

    public String getRoute() {
        return protocol + "://" + gateway;
    }
}
