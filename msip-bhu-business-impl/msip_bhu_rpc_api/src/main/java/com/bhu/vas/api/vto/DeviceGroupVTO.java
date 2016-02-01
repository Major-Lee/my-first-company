package com.bhu.vas.api.vto;


@SuppressWarnings("serial")
public class DeviceGroupVTO implements java.io.Serializable{
	private long gid;

	private long pid;
	
	private String name;

	private String pname;
	
	private String path;
	private boolean parent;
	//private int children;

	//private boolean isParent;
	//private int device_count;

	//private TailPage<WifiDeviceVTO> page_devices;

	public long getGid() {
		return gid;
	}

	public void setGid(long gid) {
		this.gid = gid;
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

	/*public int getDevice_count() {
		return device_count;
	}

	public void setDevice_count(int device_count) {
		this.device_count = device_count;
	}*/

/*	public int getChildren() {
		return children;
	}

	public void setChildren(int children) {
		this.children = children;
	}*/

	/*public long getpId() {
		return pId;
	}

	public void setpId(long pId) {
		this.pId = pId;
	}*/

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	/*public boolean isParent() {
		return children>0;
	}*/

	/*public void setParent(boolean isParent) {
		this.isParent = isParent;
	}*/

	/*public boolean isIsparent() {
		return children>0;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}*/
	

	/*public TailPage<WifiDeviceVTO> getPage_devices() {
		return page_devices;
	}

	public void setPage_devices(TailPage<WifiDeviceVTO> page_devices) {
		this.page_devices = page_devices;
	}*/
}
