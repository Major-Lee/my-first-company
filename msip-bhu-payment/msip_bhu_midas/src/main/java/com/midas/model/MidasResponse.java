package com.midas.model;

public class MidasResponse {
	private String ret;
	private String msg;
	private String token_id;
	private String url_params;
	public String getRet() {
		return ret;
	}
	public void setRet(String ret) {
		this.ret = ret;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getToken_id() {
		return token_id;
	}
	public void setToken_id(String token_id) {
		this.token_id = token_id;
	}
	public String getUrl_params() {
		return url_params;
	}
	public void setUrl_params(String url_params) {
		this.url_params = url_params;
	}
	
	
}
