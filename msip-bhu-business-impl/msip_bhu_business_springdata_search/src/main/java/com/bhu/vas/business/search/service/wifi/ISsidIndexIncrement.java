package com.bhu.vas.business.search.service.wifi;

public interface ISsidIndexIncrement {
	/**
	 * 设备信息更新
	 * @param bssid
	 * @param ssid 
	 * @param mode 
	 * @param pwd 
	 */
	public void updateIncrement(String id, String ssid, String mode, String pwd);
}
