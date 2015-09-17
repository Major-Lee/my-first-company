package com.bhu.vas.api.dto.ret.param;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.smartwork.msip.cores.helper.StringHelper;


/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class ParamCmdWifiTimerStartDTO{
	private String enable;
	private String timeslot = WifiDeviceHelper.WifiTimer_Default_Timeslot;
	private String days = WifiDeviceHelper.WifiTimer_Default_Days;
	//开始时间 18:00:00
	//private String start_time;
	//结束时间 05:00:00
	//private String end_time;
	
	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}


	/*public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}*/

   public String getTimeslot() {
		return timeslot;
	}
	public void setTimeslot(String timeslot) {
		this.timeslot = timeslot;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public static String[] fetchSlot(String _timeslot){
		if(StringUtils.isEmpty(_timeslot)){
			return WifiDeviceHelper.WifiTimer_Default_Timeslot_Array;
		}
		String[] array = _timeslot.split(StringHelper.MINUS_STRING_GAP);
		if(array.length == 2) return array;
		return WifiDeviceHelper.WifiTimer_Default_Timeslot_Array;
	}
	
	//{"enable":"enable","timeslot":"02:00:00-23:00:00"}
	/*public static void main(String[] argv){
		ParamCmdWifiTimerStartDTO dto = new ParamCmdWifiTimerStartDTO();
		dto.setEnable("enable");
		dto.setStart_time("20:00");
		dto.setEnd_time("21:00");
		//dto.setUrls("http://www.src1.com,http://www.dst1.com,http://src2.com,http://dst2.com");
		System.out.println(JsonHelper.getJSONString(dto));
	}*/
}
