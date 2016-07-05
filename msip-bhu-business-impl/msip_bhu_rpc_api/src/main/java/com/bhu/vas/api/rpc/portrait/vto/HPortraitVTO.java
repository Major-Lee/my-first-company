package com.bhu.vas.api.rpc.portrait.vto;


@SuppressWarnings("serial")
public class HPortraitVTO implements java.io.Serializable{
	private String id;
	private String mobileno;
	private String os;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	
}
