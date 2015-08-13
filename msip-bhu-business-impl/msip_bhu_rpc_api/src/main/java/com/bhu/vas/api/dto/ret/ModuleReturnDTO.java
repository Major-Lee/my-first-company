package com.bhu.vas.api.dto.ret;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ModuleReturnDTO implements Serializable{
	//修改结果
	private String result;
	//错误原因
	private String reason;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
