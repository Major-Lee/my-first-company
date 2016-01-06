package com.bhu.vas.push.common.context;

import com.smartwork.msip.cores.plugins.filterhelper.StringHelper;
/**
 * 终端上线/访客上线 push上下文
 * @author tangzichao
 *
 */
public class HandsetOnlineContext {
	//如果终端主机名显示为"android-", 则终端显示名为 安卓终端
	public static final String Android_Host_Name_Match = "android-";
	public static final String Android_Terminal = "安卓终端";
	//第一次接入设备的终端额外显示名
	public static final String Android_Stranger = "陌生";
	//如果用户管理多个设备,需要显示设备信息, 设备信息模板
	public static final String Device_Info_Template = "在 %s";
	//本次push是否进行推送
	private boolean vaild;
	//终端厂家名称
	private String manufactor = StringHelper.EMPTY_STRING;
	//终端显示名
	private String handsetName;
	//陌生显示
	private String strange = StringHelper.EMPTY_STRING;
	//设备显示信息
	private String deviceInfo = StringHelper.EMPTY_STRING;
	
	public boolean isVaild() {
		return vaild;
	}
	public void setVaild(boolean vaild) {
		this.vaild = vaild;
	}
	public String getManufactor() {
		return manufactor;
	}
	public void setManufactor(String manufactor) {
		this.manufactor = manufactor;
	}
	public String getHandsetName() {
		return handsetName;
	}
	public void setHandsetName(String handsetName) {
		this.handsetName = handsetName;
	}
	public String getStrange() {
		return strange;
	}
	public void setStrange(String strange) {
		this.strange = strange;
	}
	public String getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	
}
