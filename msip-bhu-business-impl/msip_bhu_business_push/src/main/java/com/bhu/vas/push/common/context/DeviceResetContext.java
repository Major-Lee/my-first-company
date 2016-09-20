package com.bhu.vas.push.common.context;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 设备reset解绑通知 push上下文
 * @author tangzichao
 *
 */
public class DeviceResetContext {
	//设备显示信息
	private String deviceInfo = StringHelper.EMPTY_STRING_GAP;

	public String getDeviceInfo() {
		return deviceInfo;
	}
	public String getDeviceInfoChop() {
		if(StringUtils.isEmpty(deviceInfo)) return deviceInfo;
		return StringHelper.chopMiddleString(deviceInfo, 10, StringHelper.ELLIPSIS_STRING_GAP);
	}
	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
}
