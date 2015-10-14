package com.bhu.vas.api.helper;

public class ChargingCurrencyHelper {
	/*public static double currency(long devices,long handsets){
		return devices*0.1d;
	}*/
	
	/**
	 * 在线时长24小时块钱
	 * @param onlineduration
	 * @return
	 */
	public static double currency(long onlineduration){
		return onlineduration/(10*3600*1000)*2;
	}
}
