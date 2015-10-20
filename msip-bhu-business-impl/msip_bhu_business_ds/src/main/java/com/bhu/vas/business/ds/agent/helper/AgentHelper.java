package com.bhu.vas.business.ds.agent.helper;

import java.util.Date;

import com.bhu.vas.business.ds.agent.mdto.WifiDeviceWholeDayMDTO;
import com.smartwork.msip.cores.helper.DateTimeHelper;

public class AgentHelper {
	/**
	 * 判定某台设备的mac是否能返现
	 * 目前判定规则为：在线超过两小时并且 挂接终端>=3 终端总在线时长为4小时
	 * @param dto
	 * @return
	 */
	public static boolean validateCashback(WifiDeviceWholeDayMDTO dto){
		if(dto.getDod()>=2*60*60*1000 && dto.getHandsets()>=3 && dto.getHod() >=4*60*60*1000)
			return true;
		return false;
	}
	
	public static boolean sameday(Date device_reg_date,Date currentDate){
		return DateTimeHelper.isSameDay(device_reg_date, currentDate);
	}
	
	public static void main(String[] argv){
		
		System.out.println(DateTimeHelper.getTimeDiff(2*60*60*1000));
	}
}
