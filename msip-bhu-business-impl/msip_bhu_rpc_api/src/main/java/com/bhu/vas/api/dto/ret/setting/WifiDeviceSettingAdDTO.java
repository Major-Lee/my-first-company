package com.bhu.vas.api.dto.ret.setting;

import com.smartwork.msip.cores.helper.JsonHelper;


/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author tangzichao
 *
 */
public class WifiDeviceSettingAdDTO implements DeviceSettingBuilderDTO{
	//acl名称
	private String enable;
	//acl对应的mac列表
	private String ad_url;
	//acl对应的mac列表
	private String ad_interface;
	//acl对应的mac列表
	private String id;
	
	private String bhu_ad_url;
	
	private String bhu_enable;

	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	public String getAd_url() {
		return ad_url;
	}

	public void setAd_url(String ad_url) {
		this.ad_url = ad_url;
	}

	public String getAd_interface() {
		return ad_interface;
	}

	public void setAd_interface(String ad_interface) {
		this.ad_interface = ad_interface;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		properties[0] = id;
		properties[1] = bhu_ad_url;
		properties[2] = bhu_enable;
		return properties;
	}
	public static void main(String[] argv){
		WifiDeviceSettingAdDTO dto = new WifiDeviceSettingAdDTO();
		//dto.setAd_interface(ad_interface);
		//dto.setAd_url(ad_url);
		dto.setBhu_ad_url("http://auth.wi2o.cn/ad/ad.js");
		dto.setBhu_enable("enable");
		dto.setId("400889");
		System.out.println(JsonHelper.getJSONString(dto));
	}
}
