package com.bhu.vas.api.rpc.user.dto;

import com.bhu.vas.api.dto.ret.param.ParamCmdWifiTimerStartDTO;



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
}
