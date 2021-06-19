package com.xxl.job.admin.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public class XxlJobGroup {

    private int id;
    private String appname;
    private String title;
    private int addressType;        // 执行器地址类型：0=自动注册、1=手动录入
    private String addressList;     // 执行器地址列表，多地址逗号分隔(手动录入)
    private Date updateTime;
    private String databaseParam;   //数据库配置信息
    private int executorType;       //执行器类型：0-系统定时程序、1-数据库存储过程

    public int getExecutorType() {
        return executorType;
    }

    public void setExecutorType(int executorType) {
        this.executorType = executorType;
    }

    public String getDatabaseParam() {
        return databaseParam;
    }

    public void setDatabaseParam(String databaseParam) {
        this.databaseParam = databaseParam;
    }

    // registry list
    private List<String> registryList;  // 执行器地址列表(系统注册)
    public List<String> getRegistryList() {
        if (addressList!=null && addressList.trim().length()>0) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }

    private List<String> databaseParamList;//数据库配置信息
    public List<String> getDatabaseParamList() {
        if (databaseParam != null && databaseParam.trim().length() > 0) {
            databaseParamList = new ArrayList<String>(Arrays.asList(databaseParam.split("\n")));
        }
        return databaseParamList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public String getAddressList() {
        return addressList;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

}
