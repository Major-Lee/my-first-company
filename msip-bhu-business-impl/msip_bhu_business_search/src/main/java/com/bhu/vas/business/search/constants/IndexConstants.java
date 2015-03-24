package com.bhu.vas.business.search.constants;
/**
 * 索引常量类
 * 记录各种业务索引的存放库名和type名
 * @author lawliet
 *
 */
public class IndexConstants {
	public static final String CommonShards = "5";
	public static final String CommonReplicas = "1";
	public static final String WifiDeviceIndex = "wifi_device_index";//wifi设备数据索引库名字
	
	public static interface Types {
		public static final String WifiDeviceType = "wifiDevice";//wifi设备数据索引库类别
	}
}
