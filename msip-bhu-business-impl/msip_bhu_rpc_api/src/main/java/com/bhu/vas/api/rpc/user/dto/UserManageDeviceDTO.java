package com.bhu.vas.api.rpc.user.dto;

import java.util.List;

/**
 * 用户管理---设备信息
 * @author Jason
 *
 */
public class UserManageDeviceDTO {
	
	//设备数量
	private String deviceNum;
	//在线设备数量
	private String deviceOnlineNum;
	//开启打赏设备数量
	private String rewardDeviceNum;
	
	//设备详细信息
	private List<UserDeviceInfoDTO> userDeviceInfoList;

	public String getDeviceNum() {
		return deviceNum;
	}

	public void setDeviceNum(String deviceNum) {
		this.deviceNum = deviceNum;
	}

	public String getDeviceOnlineNum() {
		return deviceOnlineNum;
	}

	public void setDeviceOnlineNum(String deviceOnlineNum) {
		this.deviceOnlineNum = deviceOnlineNum;
	}

	public String getRewardDeviceNum() {
		return rewardDeviceNum;
	}

	public void setRewardDeviceNum(String rewardDeviceNum) {
		this.rewardDeviceNum = rewardDeviceNum;
	}

	public List<UserDeviceInfoDTO> getUserDeviceInfoList() {
		return userDeviceInfoList;
	}

	public void setUserDeviceInfoList(List<UserDeviceInfoDTO> userDeviceInfoList) {
		this.userDeviceInfoList = userDeviceInfoList;
	}

	
	
}
