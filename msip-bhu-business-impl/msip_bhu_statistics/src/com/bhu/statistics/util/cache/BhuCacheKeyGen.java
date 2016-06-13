package com.bhu.statistics.util.cache;

/**
 * gappy缓存KEY管理类
 * 
 * @author zhangsongpu
 * @date:2015-9-7 下午10:10:25
 */
public class BhuCacheKeyGen {
	private static final String PREFIX = "BHU_";
	// 1分钟
	public static final int CACHE_TIME_ONE_MINITS = 60;
	// 5分钟
	public static final int CACHE_TIME_FIVE_MINITS = 5 * CACHE_TIME_ONE_MINITS;
	// 30分钟
	public static final int CACHE_TIME_HALF_HOUR = 30 * CACHE_TIME_ONE_MINITS;
	// 一小时
	public static final int CACHE_TIME_ONE_HOUR = 2 * CACHE_TIME_HALF_HOUR;
	// 一天
	public static final int CACHE_TIME_ONE_DAY = 24 * CACHE_TIME_ONE_HOUR;
	// 一个月
	public static final int CACHE_TIME_ONE_MONTH = 30 * CACHE_TIME_ONE_DAY;

	public static String getCaptcha(String mobile, int type) {
		return PREFIX + "CAPTCHA_" + type + "_" + mobile;
	}

	public static String getTokenKey(String userId) {
		return PREFIX + "TOKEN_" + userId;
	}

	public static String getSendMsgKey(String ip) {
		return PREFIX+"MSG_IP_"+ip;
	}

	public static String getLastSendTimeKey(String mobilePhone) {
		return  PREFIX+"MSG_MOBILE_LAST_TIME_"+mobilePhone;
	}

	public static String getUserKey(String userId) {
		return PREFIX+"USER_DOMAN_"+userId;
	}
	
	public static String getTotalPV(String typeName){
		return PREFIX+"TOTAL_PV_"+typeName;
	}
	
	public static String getTotalUV(String typeName){
		return PREFIX+"TOTAL_UV_"+typeName;
	}
	public static String getMac(String typeName){
		return PREFIX+"SINGLE_UV_"+typeName;
	}
	
	public static String getDayPV(String data){
		return PREFIX+"DAY_PV_"+data;
	}
	
	public static String getDayUV(String data){
		return PREFIX+"DAY_UV_"+data;
	}
	
	public static String getSSID(String date){
		return PREFIX+"SSID_"+date;
	}
	
	public static String getSSIDUV(String date){
		return PREFIX+"SSID_UV_"+date;
	}
	
	public static String getEquipment(String date){
		return PREFIX+"Equipment_"+date;
	}
	
	public static String getStOrder(String date){
		return PREFIX+"StOrder_"+date;
	}
	
	public static String getPCUV(String date){
		return PREFIX+"PCUV_"+date;
	}
	
	public static String getPcClickNum(String date){
		return PREFIX+"PcClickNum_"+date;
	}
	
	public static String getMobileUv(String date){
		return PREFIX+"MobileUv_"+date;
	}
	
	public static String getMobileClickNum(String date){
		return PREFIX+"MobileClickNum_"+date;
	}
	
	public static String getIosUv(String date){
		return PREFIX+"IosUv_"+date;
	}
	
	public static String getIosClickNum(String date){
		return PREFIX+"IosClickNum_"+date;
	}
	
	public static String getAndroidUv(String date){
		return PREFIX+"AndroidUv_"+date;
	}
	
	public static String getAndroidClickNum(String date){
		return PREFIX+"AndroidClickNum_"+date;
	}
}
