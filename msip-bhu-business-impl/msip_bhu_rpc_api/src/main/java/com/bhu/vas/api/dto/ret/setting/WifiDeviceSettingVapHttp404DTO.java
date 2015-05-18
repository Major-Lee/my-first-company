package com.bhu.vas.api.dto.ret.setting;

import com.smartwork.msip.cores.helper.JsonHelper;


/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author tangzichao
 *
 */
public class WifiDeviceSettingVapHttp404DTO implements DeviceSettingBuilderDTO{
private String enable;
	
	private String url;
	
	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[2];
		properties[0] = enable;
		properties[1] = url;
		return properties;
	}
	
	@Override
	public Object[] builderProperties(int type) {
		return builderProperties();
	}
	
	@Override
	public boolean beRemoved() {
		return false;
	}
	
	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
//{"enable":"enable","url":"http://auth.wi2o.cn/ad/ad.js"}
	public static void main(String[] argv){
		WifiDeviceSettingVapHttp404DTO dto = new WifiDeviceSettingVapHttp404DTO();
		//dto.setAd_interface(ad_interface);
		//dto.setAd_url(ad_url);
		dto.setUrl("http://auth.wi2o.cn/ad/ad.js");
		dto.setEnable("enable");
		System.out.println(JsonHelper.getJSONString(dto));
	}
}
