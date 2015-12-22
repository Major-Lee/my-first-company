package com.bhu.vas.api.dto.ret.setting;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * 升级指令DTO
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceUpgradeDTO implements Serializable {

    private String upgrade_begin;

    private String upgrade_end;

    private String url;
    private String url_groups;
    private boolean ctrl_version;

    public String getUpgrade_begin() {
        return upgrade_begin;
    }

    public void setUpgrade_begin(String upgrade_begin) {
        this.upgrade_begin = upgrade_begin;
    }

    public String getUpgrade_end() {
        return upgrade_end;
    }

    public void setUpgrade_end(String upgrade_end) {
        this.upgrade_end = upgrade_end;
    }

    public String getUrl() {
    	if(StringUtils.isEmpty(url)){
    		return StringUtils.EMPTY;
    	}else{
    		return url;
    	}
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl_groups() {
    	if(StringUtils.isEmpty(url_groups)){
    		return StringUtils.EMPTY;
    	}else{
    		return url_groups;
    	}
	}

	public void setUrl_groups(String url_groups) {
		this.url_groups = url_groups;
	}

	public boolean isCtrl_version() {
        return ctrl_version;
    }

    public void setCtrl_version(boolean ctrl_version) {
        this.ctrl_version = ctrl_version;
    }
}
