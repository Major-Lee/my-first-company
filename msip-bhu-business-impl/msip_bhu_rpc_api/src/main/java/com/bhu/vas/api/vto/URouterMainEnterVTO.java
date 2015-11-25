package com.bhu.vas.api.vto;

import java.io.Serializable;
import java.util.List;

import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;

/**
 * urouter主界面VTO
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class URouterMainEnterVTO implements Serializable{
	private URouterEnterVTO en;
	//终端在线列表
	private List<URouterHdVTO> hds;
	//绑定设备列表
	private List<UserDeviceDTO> devices;
	//当前路由器mac地址
	private String c_dmac;
	private String c_dver;
	//信号强度 采用string 避免信号强度初始化为零出现
	private String power;
	public URouterEnterVTO getEn() {
		return en;
	}
	public void setEn(URouterEnterVTO en) {
		this.en = en;
	}
	public List<URouterHdVTO> getHds() {
		return hds;
	}
	public void setHds(List<URouterHdVTO> hds) {
		this.hds = hds;
	}
	public List<UserDeviceDTO> getDevices() {
		return devices;
	}
	public void setDevices(List<UserDeviceDTO> devices) {
		this.devices = devices;
	}
	public String getC_dmac() {
		return c_dmac;
	}
	public void setC_dmac(String c_dmac) {
		this.c_dmac = c_dmac;
	}
	public String getC_dver() {
		return c_dver;
	}
	public void setC_dver(String c_dver) {
		this.c_dver = c_dver;
	}
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	
}
