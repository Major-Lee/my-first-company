package com.bhu.vas.web.model;

public class ResponseModel {

	private String code;
	private String msg;
	private Object body;
	
	public void init(){
		setCode("0");
		setMsg("successful");
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	
	
}
