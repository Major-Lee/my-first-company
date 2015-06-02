package com.bhu.vas.api.rpc.user.dto;



/**
 * Wifi 设置定时开关
 * 存储的数据
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class UserWifiTimerSettingDTO extends UserSettingDTO{
	public static final String Setting_Key = "uwt";
	//开启模式
	//public static final int Timeslot_Mode_Normal = 1;
	//关闭模式
	//public static final int Timeslot_Mode_Silent = 2;
	//Wifi Timer开关
	private boolean on = true;
	//开始时间段
	private String start;
	//结束
	private String end;
	//时间段通知模式
	//private int timeslot_mode = Timeslot_Mode_Normal;
	
	public boolean isOn() {
		return on;
	}
	public void setOn(boolean on) {
		this.on = on;
	}
	public String getStart() {
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
	}
	@Override
	public String getSettingKey() {
		return Setting_Key;
	}
}
