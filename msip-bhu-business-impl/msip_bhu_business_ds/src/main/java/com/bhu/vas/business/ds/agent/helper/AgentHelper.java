package com.bhu.vas.business.ds.agent.helper;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeDayMDTO;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class AgentHelper {
	
	public static Set<String> CashbackSupported_HdTypes = new HashSet<String>();
	static{
		//Mass AP H103 H110
		CashbackSupported_HdTypes.add("H103");
		CashbackSupported_HdTypes.add("H110");
		//Mass AP Pro H201 H303
		CashbackSupported_HdTypes.add("H201");
		CashbackSupported_HdTypes.add("H303");
	}
	
	/**
	 * 判定某台设备的mac是否能返现
	 * 目前判定规则为：在线超过两小时并且 挂接终端>=3 终端总在线时长为4小时
	 * @param dto
	 * @return
	 */
	/*public static boolean validateCashback(WifiDeviceWholeDayMDTO dto){
		//if(dto.getDod()>=2*60*60*1000 && dto.getHandsets()>=3 && dto.getHod() >=4*60*60*1000)
		if(dto.getDod()>=2*60 && dto.getHandsets()>=3 && dto.getHod() >=4*60)
			return true;
		return false;
	}*/
	//TODO:内网测试 此阀值不设置
	public static boolean validateCashback(WifiDeviceWholeDayMDTO dto){
		//if(dto.getDod()>=2*60 && dto.getHandsets()>=3 && dto.getHod() >=4*60)
		//	return true;
		return true;
	}
	
	public static boolean validateDeviceCashbackSupported(String hd_type){
		if(StringUtils.isEmpty(hd_type)) return false;
		return CashbackSupported_HdTypes.contains(hd_type);
	}
	
	/**
	 * 在线时长24小时1块钱
	 * @param onlineduration 分钟
	 * @return
	 */
	/*public static double currency(double onlineduration){
		//return ArithHelper.div(onlineduration*2, (10*60*60*1000), 2);//onlineduration/(10*3600*1000)*2;
		return ArithHelper.div(onlineduration*2, (10*60), 2);//onlineduration/(10*3600*1000)*2;
	}*/
	private static double Cash4FirstEach = 30.00d;
	//内网测试上线4分钟0.01元 
	public static double currency(double onlineduration,int firstcbs){
		double extra = ArithHelper.mul(Cash4FirstEach, firstcbs);
		double normal = ArithHelper.div(onlineduration, 4*100, 2);
		return ArithHelper.round(ArithHelper.add(extra, normal),2);
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
	 * 获取时间区间时长(秒的数字不显示)
	 * @param diff 单位分钟
	 * @return
	 */
	public static String getTimeDiff(double diff_double){
		long diff = (long)(diff_double*1000*60);
		long day = 0;
        long hour = 0;
        long min = 0;
        //long sec = 0;
        day = 	diff / Day;
        hour = 	(diff / Hour - day * 24);
        min = 	((diff / Min) - day * 24 * 60 - hour * 60);
        //sec = 	(diff/Sec-day*24*60*60-hour*60*60-min*60);
        StringBuilder sb = new StringBuilder();
        if(day>0) sb.append(day).append("天");
        if(hour>0) sb.append(hour).append("小时");
        if(min>0) sb.append(min).append("分");
        //if(sec>0) sb.append(sec).append("秒");
        return sb.toString();
		//return ConvertHelper.timeFormat((long)(diff*1000*60));
	}
	private static long Day = 	(24 * 60 * 60 * 1000);
	private static long Hour = (60 * 60 * 1000);
	private static long Min = 	(60 * 1000);
	private static long Sec = 	1000;
	
	
	public static void main(String[] argv){
		
		System.out.println(DateTimeHelper.getTimeDiff(2*60*60*1000));
		
		System.out.println(AgentHelper.flowByte2Megabyte(188322));
		
		System.out.println(AgentHelper.millisecond2Minute(188322));
	}
}
