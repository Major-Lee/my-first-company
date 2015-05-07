package com.bhu.vas.api.dto.ret.setting;

import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * Created by bluesand on 5/7/15.
 */
public class WifiDeviceSettingLinkModeDTO implements DeviceSettingBuilderDTO {

//    public static final String MODEL_PPPOE = "pppoe";
//    public static final String MODEL_STATIC = "static";
//    public static final String MODEL_DHCPC= "dhcpc";

    public static final int MODEL_PPPOE_TYPE = 0;
    public static final int MODEL_STATIC_TYPE = 1;
    public static final int MODEL_DHCPC_TYPE= 2;
    /**
     * 上网方式pppoe/static/dhcpc三种模式
     */
    private String model;

    /**
     * pppoe 用户名
     */
    private String username;

    /**
     * pppoe 用户名密码，rsa加密
     */
    private String password_rsa;

    private String link_mode;

    private String idle;

    /**
     * static ip地址
     */
    private String ip;

    /**
     * static 子网
     */
    private String netmask;

    /**
     * static 网关
     */
    private String gateway;

    /**
     * static dns
     */
    private String dns;


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword_rsa() {
        return password_rsa;
    }

    public void setPassword_rsa(String password_rsa) {
        this.password_rsa = password_rsa;
    }


    public String getLink_mode() {
        return link_mode;
    }

    public void setLink_mode(String link_mode) {
        this.link_mode = link_mode;
    }

    public String getIdle() {
        return idle;
    }

    public void setIdle(String idle) {
        this.idle = idle;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    @Override
    public Object[] builderProperties() {
        Object[] properties = new Object[7];
        properties[0] = model;
        properties[1] = username;
        properties[2] = password_rsa;
        properties[3] = ip;
        properties[4] = netmask;
        properties[5] = gateway;
        properties[6] = dns;
        return properties;
    }

    @Override
    public Object[] builderProperties(int type) {
        Object[] properties = null;
        if (MODEL_PPPOE_TYPE == type) {
            properties = new Object[5];
            properties[0] = model;
            properties[1] = username;
            properties[2] = password_rsa;
            properties[3] = link_mode;
            properties[4] = idle;
        } else if (MODEL_STATIC_TYPE == type) {
            properties = new Object[5];
            properties[0] = model;
            properties[1] = ip;
            properties[2] = netmask;
            properties[3] = gateway;
            properties[4] = dns;
        } else if (MODEL_DHCPC_TYPE == type) {
            properties = new Object[0];
            properties[0] = model;
        }
        return properties;
    }

    @Override
    public boolean beRemoved() {
        return false;
    }


    public static void main(String[] args) {
        WifiDeviceSettingLinkModeDTO wifiDeviceSettingLinkModelDTO = new WifiDeviceSettingLinkModeDTO();
        wifiDeviceSettingLinkModelDTO.setModel("pppoe");
        wifiDeviceSettingLinkModelDTO.setUsername("google");
        wifiDeviceSettingLinkModelDTO.setPassword_rsa("1234");
        System.out.println(JsonHelper.getJSONString(wifiDeviceSettingLinkModelDTO));
    }
}
