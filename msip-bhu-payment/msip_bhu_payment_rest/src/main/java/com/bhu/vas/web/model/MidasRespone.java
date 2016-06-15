package com.bhu.vas.web.model;

public class MidasRespone {

	private String id;
	private String third_payinfo;
	public MidasRespone(String id,String third_payinfo){
		this.id = id;
		this.third_payinfo = third_payinfo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getThird_payinfo() {
		return third_payinfo;
	}
	public void setThird_payinfo(String third_payinfo) {
		this.third_payinfo = third_payinfo;
	}
}
