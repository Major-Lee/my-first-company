package com.bhu.vas.api.dto.ret.setting;

import java.util.List;
/**
 * 设备的配置dto
 * @author tangzichao
 *
 */
public class WifiDeviceSettingDTO {
	//获取统一的block黑名单列表的名称
	public static final String Default_AclName = "blackList";
	
	public static final String Mode_Static = "static";
	public static final String Mode_Dhcpc = "dhcpc";
	public static final String Mode_Pppoe = "pppoe";
	public static final String Mode_Pppol2tp = "pppol2tp";
	
	//信号强度
//	private String power;
	//信号强度 多频设备会有多个dto
	private List<WifiDeviceSettingRadioDTO> radios;
	//上网方式
	private String mode;
	//配置流水号
	private String sequence;
	//VAP列表
	private List<WifiDeviceSettingVapDTO> vaps;
	//黑白名单列表
	private List<WifiDeviceSettingAclDTO> acls;
	//接口速率控制列表
	private List<WifiDeviceSettingInterfaceDTO> interfaces;
	//终端速率控制列表
	private List<WifiDeviceSettingRateControlDTO> ratecontrols;
	//管理员用户列表
	private List<WifiDeviceSettingUserDTO> users;
	//广告
	private WifiDeviceSettingAdDTO ad;
	
//	public String getPower() {
//		return power;
//	}
//
//	public void setPower(String power) {
//		this.power = power;
//	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public List<WifiDeviceSettingVapDTO> getVaps() {
		return vaps;
	}

	public void setVaps(List<WifiDeviceSettingVapDTO> vaps) {
		this.vaps = vaps;
	}

	public List<WifiDeviceSettingAclDTO> getAcls() {
		return acls;
	}

	public void setAcls(List<WifiDeviceSettingAclDTO> acls) {
		this.acls = acls;
	}

	public List<WifiDeviceSettingInterfaceDTO> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<WifiDeviceSettingInterfaceDTO> interfaces) {
		this.interfaces = interfaces;
	}

	public List<WifiDeviceSettingRateControlDTO> getRatecontrols() {
		return ratecontrols;
	}

	public void setRatecontrols(List<WifiDeviceSettingRateControlDTO> ratecontrols) {
		this.ratecontrols = ratecontrols;
	}

	public List<WifiDeviceSettingUserDTO> getUsers() {
		return users;
	}

	public void setUsers(List<WifiDeviceSettingUserDTO> users) {
		this.users = users;
	}

	public List<WifiDeviceSettingRadioDTO> getRadios() {
		return radios;
	}

	public void setRadios(List<WifiDeviceSettingRadioDTO> radios) {
		this.radios = radios;
	}

	public WifiDeviceSettingAdDTO getAd() {
		return ad;
	}

	public void setAd(WifiDeviceSettingAdDTO ad) {
		this.ad = ad;
	}
}
