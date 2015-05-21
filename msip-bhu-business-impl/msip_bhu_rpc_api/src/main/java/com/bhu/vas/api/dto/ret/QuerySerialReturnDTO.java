package com.bhu.vas.api.dto.ret;

import java.io.Serializable;

@SuppressWarnings("serial")
public class QuerySerialReturnDTO implements Serializable{
	private String cmd;//="sysdebug" serial="4" status="doing"
	private String serial;
	private String status;
	private String index;
	//设备网速
	private String rate;
	
	//资源包版本
	private String resource_ver;
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getResource_ver() {
		return resource_ver;
	}
	public void setResource_ver(String resource_ver) {
		this.resource_ver = resource_ver;
	}
	
//	public boolean isDone(){
//		if(Done_Status.equals(status)) return true;
//		return false;
//	}
//	
//	public boolean isNone(){
//		if(None_Status.equals(status)) return true;
//		return false;
//	}
//	
//	public boolean isDoing(){
//		if(Doing_Status.equals(status)) return true;
//		return false;
//	}
}
