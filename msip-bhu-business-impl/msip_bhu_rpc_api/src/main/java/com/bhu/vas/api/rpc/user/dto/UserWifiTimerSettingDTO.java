package com.bhu.vas.api.rpc.user.dto;

import java.util.Date;

import com.bhu.vas.api.dto.ret.param.ParamCmdWifiTimerStartDTO;
import com.smartwork.msip.cores.helper.DateTimeHelper;



/**
 * Wifi 设置定时开关
 * 存储的数据
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class UserWifiTimerSettingDTO extends UserSettingDTO{
	public static final String Setting_Key = "uwt";
	//Wifi Timer开关
	private boolean on = false;
	//device response ok 时 为true
	private boolean ds = false;
	private String timeslot = ParamCmdWifiTimerStartDTO.Default_Timeslot;
	
	//根据timeslot换算出当前时间是否wifi开启或关闭，此字段只用于展示，存储的值无效，取出后需重设定
	private boolean enable = true;
	/*//开始时间段
	private String start;
	//结束
	private String end;*/
	//时间段通知模式
	//private int timeslot_mode = Timeslot_Mode_Normal;
	
	public boolean isOn() {
		return on;
	}
	public void setOn(boolean on) {
		this.on = on;
	}
	
	public String getTimeslot() {
		return timeslot;
	}
	public void setTimeslot(String timeslot) {
		this.timeslot = timeslot;
	}
	/*public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}*/
	
	@Override
	public String getSettingKey() {
		return Setting_Key;
	}
	public boolean isDs() {
		return ds;
	}
	public void setDs(boolean ds) {
		this.ds = ds;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	//{"on":false,"ds":false,"timeslot":"02:00:00-23:00:00"}
	/*public static void main(String[] argv){
		UserWifiTimerSettingDTO dto = new UserWifiTimerSettingDTO();
		dto.setStart_time("20:00");
		dto.setEnd_time("21:00");
		//dto.setUrls("http://www.src1.com,http://www.dst1.com,http://src2.com,http://dst2.com");
		System.out.println(JsonHelper.getJSONString(dto));
	}*/
	
	public boolean wifiCurrentEnable(){
		if(on){
			return DateTimeHelper.rangeInDefined(new Date(), timeslot, DateTimeHelper.longDateFormat);
		}else{
			return true;
		}
	}
}
