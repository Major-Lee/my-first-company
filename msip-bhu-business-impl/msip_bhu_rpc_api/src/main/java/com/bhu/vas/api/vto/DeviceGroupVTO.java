package com.bhu.vas.api.vto;

import com.bhu.vas.api.dto.WifiDeviceDTO;

import java.util.List;

@SuppressWarnings("serial")
public class DeviceGroupVTO implements java.io.Serializable{
	private int gid;
	private int pid;
	
	private String name;
	private String pname;
	
	private String path;
	private int children;
	private List<String> devices;

	private List<WifiDeviceVTO> detail_devices;

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<String> getDevices() {
		return devices;
	}

	public void setDevices(List<String> devices) {
		this.devices = devices;
	}

	public List<WifiDeviceVTO> getDetail_devices() {
		return detail_devices;
	}

	public void setDetail_devices(List<WifiDeviceVTO> detail_devices) {
		this.detail_devices = detail_devices;
	}

	public int getChildren() {
		return children;
	}

	public void setChildren(int children) {
		this.children = children;
	}

}
