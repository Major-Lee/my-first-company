package com.bhu.vas.api.dto.baidumap;
/**
 * 用于百度地图麻点图的扩展字段数据
 * @author tangzichao
 *
 */
public class GeoPoiExtensionDTO {
	private String mac;//wifi设备mac
	private int online;//wifi设备是否在线
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public int getOnline() {
		return online;
	}
	public void setOnline(int online) {
		this.online = online;
	}
}
