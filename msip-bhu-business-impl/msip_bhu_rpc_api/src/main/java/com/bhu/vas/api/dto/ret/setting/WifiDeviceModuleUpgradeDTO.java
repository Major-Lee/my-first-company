package com.bhu.vas.api.dto.ret.setting;

import java.io.Serializable;

import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * Created by bluesand on 5/14/15.
 */
@SuppressWarnings("serial")
public class WifiDeviceModuleUpgradeDTO implements Serializable {
    private String urlprefix;
    private int retry_count;
    private int retry_interval;
	public String getUrlprefix() {
		return urlprefix;
	}
	public void setUrlprefix(String urlprefix) {
		this.urlprefix = urlprefix;
	}
	public int getRetry_count() {
		return retry_count;
	}
	public void setRetry_count(int retry_count) {
		this.retry_count = retry_count;
	}
	public int getRetry_interval() {
		return retry_interval;
	}
	public void setRetry_interval(int retry_interval) {
		this.retry_interval = retry_interval;
	}
	public static void main(String[] argv){
		WifiDeviceModuleUpgradeDTO dto = new WifiDeviceModuleUpgradeDTO();
		dto.setUrlprefix("http://192.168.66.7/upgrade/urouter-module/");
		dto.setRetry_interval(300);
		dto.setRetry_count(5);
		System.out.println(JsonHelper.getJSONString(dto));
	}
}
