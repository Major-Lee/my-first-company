package com.bhu.vas.api.rpc.devices.dto.sharednetwork;


/**
 * 设备状态数据
 * @author edmond
 *
 */
@SuppressWarnings("serial")
public class DeviceStatusExchangeDTO implements java.io.Serializable{
	//设备工作模式
	private String workmode;
	//设备固件版本号
	private String orig_swver;
	public String getWorkmode() {
		return workmode;
	}
	public void setWorkmode(String workmode) {
		this.workmode = workmode;
	}
	public String getOrig_swver() {
		return orig_swver;
	}
	public void setOrig_swver(String orig_swver) {
		this.orig_swver = orig_swver;
	}
	
	public static DeviceStatusExchangeDTO build(String workmode,String orig_swver){
		DeviceStatusExchangeDTO dto = new DeviceStatusExchangeDTO();
		dto.setWorkmode(workmode);
		dto.setOrig_swver(orig_swver);
		return dto;
	}
}
