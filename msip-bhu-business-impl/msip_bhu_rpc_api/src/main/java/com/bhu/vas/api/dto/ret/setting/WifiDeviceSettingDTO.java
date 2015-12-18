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
	
	public static final int Boot_On_Reset_Happen = 1;
	public static final int Boot_On_Reset_NotHappen = 0;
	
	//信号强度
//	private String power;
	//信号强度 多频设备会有多个dto
	private List<WifiDeviceSettingRadioDTO> radios;
	//上网方式
	//private String mode;
	private WifiDeviceSettingLinkModeDTO mode;
	//配置流水号
	private String sequence;
	//取值0和1， 1 表示设备做过reset后重启进行的上报数据。
	private int boot_on_reset;
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
	//终端别名列表
	private List<WifiDeviceSettingMMDTO> mms;
	
	//终端别名列表
	private List<WifiDeviceSettingPluginDTO> plugins;
	
	//广告
	private WifiDeviceSettingVapAdDTO ad;
	
//	public String getPower() {
//		return power;
//	}
//
//	public void setPower(String power) {
//		this.power = power;
//	}


	public String getSequence() {
		return sequence;
	}

	public WifiDeviceSettingLinkModeDTO getMode() {
		return mode;
	}

	public void setMode(WifiDeviceSettingLinkModeDTO mode) {
		this.mode = mode;
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

	public List<WifiDeviceSettingMMDTO> getMms() {
		return mms;
	}

	public void setMms(List<WifiDeviceSettingMMDTO> mms) {
		this.mms = mms;
	}

	public WifiDeviceSettingVapAdDTO getAd() {
		return ad;
	}

	public void setAd(WifiDeviceSettingVapAdDTO ad) {
		this.ad = ad;
	}

	public int getBoot_on_reset() {
		return boot_on_reset;
	}

	public void setBoot_on_reset(int boot_on_reset) {
		this.boot_on_reset = boot_on_reset;
	}

	public List<WifiDeviceSettingPluginDTO> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<WifiDeviceSettingPluginDTO> plugins) {
		this.plugins = plugins;
	}
}
