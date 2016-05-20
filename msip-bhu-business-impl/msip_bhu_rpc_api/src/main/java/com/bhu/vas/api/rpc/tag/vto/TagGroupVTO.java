package com.bhu.vas.api.rpc.tag.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TagGroupVTO implements Serializable {

	private int gid;
	private int pid;
	private String name;
	private String pname;
	private String path;
	private int creator;
	private boolean parent;
	private int device_count;

//	 private int onLine;
//	 private int offLine;
//	private double earnings;
//


	public int getDevice_count() {
		return device_count;
	}

	public void setDevice_count(int device_count) {
		this.device_count = device_count;
	}

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

	public int getCreator() {
		return creator;
	}

	public void setCreator(int i) {
		this.creator = i;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}
	
//	public int getOnLine() {
//		return onLine;
//	}
//
//	public void setOnLine(int onLine) {
//		this.onLine = onLine;
//	}
//
//	public int getOffLine() {
//		return offLine;
//	}
//
//	public void setOffLine(int offLine) {
//		this.offLine = offLine;
//	}
//	
//	public double getEarnings() {
//		return earnings;
//	}
//
//	public void setEarnings(double earnings) {
//		this.earnings = earnings;
//	}
}
