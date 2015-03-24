package com.bhu.vas.api.dto.ret;

import java.io.Serializable;

@SuppressWarnings("serial")
public class QuerySerialReturnDTO implements Serializable{
	private String cmd;//="sysdebug" serial="4" status="doing"
	private String serial;
	private String status;
	private String index;
	
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
