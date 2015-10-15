package com.bhu.vas.api.dto.ret.param;

import java.util.List;

import com.bhu.vas.api.dto.ret.setting.param.RateControlParamDTO;



/**
 * 
 * 复杂的流量控制插件开关 参数类
 * 支持整体lan口，wan口限速
 * 支持具体某个mac地址限速终端限速功能
 * enable = true 开启并下发限速指令
 * enable = false 关闭并下发关闭限速指令
 * 
 * 备注：取消限速 可以删除指定的mac地址也可以设置mac地址的限速值为0
 * 备注：此功能实现 以删除指定的mac地址来实现取消限速
 * @author edmond
 *
 */
public class ParamCmdComplexRateControlDTO{
	private String enable;
	//uniform_tx_threshold 统一上行限速阀值
	private String utt;
	//uniform_rx_threshold 统一下行限速阀值
	private String urt;
	//取消限速的终端mac
	private List<String> umacs;
	// control macs 需要进行限速的终端mac
	private List<RateControlParamDTO> cmacs;
	
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
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
	public List<String> getUmacs() {
		return umacs;
	}
	public void setUmacs(List<String> umacs) {
		this.umacs = umacs;
	}
	public List<RateControlParamDTO> getCmacs() {
		return cmacs;
	}
	public void setCmacs(List<RateControlParamDTO> cmacs) {
		this.cmacs = cmacs;
	}
}
