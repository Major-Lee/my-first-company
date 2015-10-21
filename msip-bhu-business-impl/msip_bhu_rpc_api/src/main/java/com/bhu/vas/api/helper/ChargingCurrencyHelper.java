package com.bhu.vas.api.helper;

import com.smartwork.msip.cores.helper.ArithHelper;

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
		return ArithHelper.div(onlineduration*2, (10*3600*1000), 2);//onlineduration/(10*3600*1000)*2;
	}
}
