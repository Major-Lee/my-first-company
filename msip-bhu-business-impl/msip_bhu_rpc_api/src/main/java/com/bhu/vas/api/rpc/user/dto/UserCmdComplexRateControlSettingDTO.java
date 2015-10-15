package com.bhu.vas.api.rpc.user.dto;

import java.util.List;

import com.bhu.vas.api.dto.ret.setting.param.RateControlParamDTO;


/**
 * 终端限速功能
 * enable = true 开启并下发限速指令
 * enable = false 关闭并下发关闭限速指令
 * 
 * 备注：取消限速 可以删除指定的mac地址也可以设置mac地址的限速值为0
 * 备注：此功能实现 以删除指定的mac地址来实现取消限速
 * @author edmond
 *
 */
@SuppressWarnings("serial")
public class UserCmdComplexRateControlSettingDTO extends UserSettingDTO{
	public static final String Setting_Key = "urc";
	//Wifi Timer开关
	private boolean on = false;
	//uniform_tx_threshold 统一上行限速阀值
	private String utt;
	//uniform_rx_threshold 统一下行限速阀值
	private String urt;
	//取消限速的终端mac
	//private List<String> umacs;
	// control macs 需要进行限速的终端mac
	private List<RateControlParamDTO> cmacs;
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
	
	public String getUtt() {
		return utt;
	}
	public void setUtt(String utt) {
		this.utt = utt;
	}
	public String getUrt() {
		return urt;
	}
	public void setUrt(String urt) {
		this.urt = urt;
	}
	public List<RateControlParamDTO> getCmacs() {
		return cmacs;
	}
	public void setCmacs(List<RateControlParamDTO> cmacs) {
		this.cmacs = cmacs;
	}
	@Override
	public String getSettingKey() {
		return Setting_Key;
	}
	
	
}
