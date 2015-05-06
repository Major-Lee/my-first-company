package com.bhu.vas.api.dto.ret.setting;

import com.smartwork.msip.cores.helper.JsonHelper;


/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author tangzichao
 *
 */
public class WifiDeviceSettingAdDTO implements DeviceSettingBuilderDTO{
	
	private String bhu_id;
	
	private String bhu_ad_url;
	
	private String bhu_enable;

	public String getBhu_id() {
		return bhu_id;
	}
	public void setBhu_id(String bhu_id) {
		this.bhu_id = bhu_id;
	}
	public String getBhu_ad_url() {
		return bhu_ad_url;
	}
	public void setBhu_ad_url(String bhu_ad_url) {
		this.bhu_ad_url = bhu_ad_url;
	}
	public String getBhu_enable() {
		return bhu_enable;
	}
	public void setBhu_enable(String bhu_enable) {
		this.bhu_enable = bhu_enable;
	}
	//bhu_enable=\"%s\"  bhu_ad_url=\"%s\" id=\"%s\"
	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[3];
		properties[0] = bhu_id;
		properties[1] = bhu_ad_url;
		properties[2] = bhu_enable;
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
	
	public static void main(String[] argv){
		WifiDeviceSettingAdDTO dto = new WifiDeviceSettingAdDTO();
		//dto.setAd_interface(ad_interface);
		//dto.setAd_url(ad_url);
		dto.setBhu_ad_url("http://auth.wi2o.cn/ad/ad.js");
		dto.setBhu_enable("enable");
		dto.setBhu_id("400889");
		System.out.println(JsonHelper.getJSONString(dto));
	}
}
