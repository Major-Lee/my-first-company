package com.bhu.vas.api.rpc.user.dto;

/**
 * 用户管理---设备详细信息
 * @author Jason
 *
 */
@SuppressWarnings("serial")
public class UserDeviceInfoDTO implements java.io.Serializable{
	
	//设备型号
	private String deviceType;
	//MAC
	private String deviceMac;
	//SN
	private String deviceSN;
	//认证网络类型
	private String accNetType;
	//用户分成比例
	private String userGainsharing;
	//收益
	private double income;
	//状态
	private String deviceStatus;
	//所属分组
	private String subordinateGroup;
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getDeviceMac() {
		return deviceMac;
	}
	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}
	public String getDeviceSN() {
		return deviceSN;
	}
	public void setDeviceSN(String deviceSN) {
		this.deviceSN = deviceSN;
	}
	public String getAccNetType() {
		return accNetType;
	}
	public void setAccNetType(String accNetType) {
		this.accNetType = accNetType;
	}
	public String getUserGainsharing() {
		return userGainsharing;
	}
	public void setUserGainsharing(String userGainsharing) {
		this.userGainsharing = userGainsharing;
	}
	
	public double getIncome() {
		return income;
	}
	public void setIncome(double income) {
		this.income = income;
	}
	public String getDeviceStatus() {
		return deviceStatus;
	}
	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	public String getSubordinateGroup() {
		return subordinateGroup;
	}
	public void setSubordinateGroup(String subordinateGroup) {
		this.subordinateGroup = subordinateGroup;
	}
	
	
}
