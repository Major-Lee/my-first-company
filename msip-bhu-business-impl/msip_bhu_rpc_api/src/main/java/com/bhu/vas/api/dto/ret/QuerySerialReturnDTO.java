package com.bhu.vas.api.dto.ret;

import java.io.Serializable;

@SuppressWarnings("serial")
public class QuerySerialReturnDTO implements Serializable{
	 private String cmd;//="sysdebug" serial="4" status="doing"
	 private String serial;
	 private String status;
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
	 
	 
}
