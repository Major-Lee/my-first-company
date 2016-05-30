package com.bhu.vas.api.rpc.user.dto;



/**
 * 终端上线通知设置
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class UserTerminalOnlineSettingDTO extends UserSettingDTO{
	public static final String Setting_Key = "uto";
	public static final String Default_Timeslot = "00:00-24:00";
	
	//正常通知模式 按时间段通知
	public static final int Timeslot_Mode_Normal = 1;
	//静默通知模式 按时间段外通知
	public static final int Timeslot_Mode_Silent = 2;
	//终端上线通知开关
	private boolean on = true;
	//陌生终端通知开关
	private boolean stranger_on;
	//修改昵称终端通知开关
	private boolean alias_on;
	//时间段
	private String timeslot = Default_Timeslot;
	//时间段通知模式
	private int timeslot_mode = Timeslot_Mode_Normal;
	
	public boolean isOn() {
		return on;
	}
	public void setOn(boolean on) {
		this.on = on;
	}
	public boolean isStranger_on() {
		return stranger_on;
	}
	public void setStranger_on(boolean stranger_on) {
		this.stranger_on = stranger_on;
	}

	public boolean isAlias_on() {
		return alias_on;
	}

	public void setAlias_on(boolean alias_on) {
		this.alias_on = alias_on;
	}

	public String getTimeslot() {
		return timeslot;
	}
	public void setTimeslot(String timeslot) {
		this.timeslot = timeslot;
	}
	public int getTimeslot_mode() {
		return timeslot_mode;
	}
	public void setTimeslot_mode(int timeslot_mode) {
		this.timeslot_mode = timeslot_mode;
	}
	@Override
	public String getSettingKey() {
		return Setting_Key;
	}
	
//	public static void main(String[] args){
//		UserSettingDTO dto = new UserTerminalOnlineSettingDTO();
//		String json = JsonHelper.getJSONString(dto);
//		UserTerminalOnlineSettingDTO dto1 = JsonHelper.getDTO(json, UserTerminalOnlineSettingDTO.class);
//	}
}
