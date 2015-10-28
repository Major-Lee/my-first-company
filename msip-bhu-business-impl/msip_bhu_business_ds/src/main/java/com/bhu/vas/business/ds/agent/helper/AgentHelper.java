package com.bhu.vas.business.ds.agent.helper;

import java.util.Date;

import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeDayMDTO;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.ConvertHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class AgentHelper {
	/**
	 * 判定某台设备的mac是否能返现
	 * 目前判定规则为：在线超过两小时并且 挂接终端>=3 终端总在线时长为4小时
	 * @param dto
	 * @return
	 */
	public static boolean validateCashback(WifiDeviceWholeDayMDTO dto){
		//if(dto.getDod()>=2*60*60*1000 && dto.getHandsets()>=3 && dto.getHod() >=4*60*60*1000)
		if(dto.getDod()>=2*60 && dto.getHandsets()>=3 && dto.getHod() >=4*60)
			return true;
		return false;
	}
	
	public static boolean sameday(Date device_reg_date,Date currentDate){
		return DateTimeHelper.isSameDay(device_reg_date, currentDate);
	}
	
	private static long KB = 1024*8;
	private static long MB = 1024*KB;
	private static long GB = 1024*MB;
	private static long TB = 1024*GB;
	static java.text.DecimalFormat format = new java.text.DecimalFormat("0.00");
	public static double flowByte2Megabyte(long x_byte){
		return ArithHelper.round(ArithHelper.div(x_byte*8, MB),2);///format.format(x_byte*8/MB);
	}
	
	public static double millisecond2Minute(long ms){
		return ArithHelper.round(ArithHelper.div(ms, 1000*60),2);
	}
	
	/**
	 * 获取时间区间时长
	 * @param diff 单位分钟
	 * @return
	 */
	public static String getTimeDiff(double diff){
		return ConvertHelper.timeFormat((long)(diff*1000*60));
	}
	
	public static void main(String[] argv){
		
		System.out.println(DateTimeHelper.getTimeDiff(2*60*60*1000));
		
		System.out.println(AgentHelper.flowByte2Megabyte(188322));
		
		System.out.println(AgentHelper.millisecond2Minute(188322));
	}
}
