package com.bhu.vas.web.model;

public class MidasRespone {

	private int ret;
	private String msg;
	public MidasRespone(int ret,String msg){
		this.ret = ret;
		this.msg = msg;
	}
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
