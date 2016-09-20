package com.bhu.vas.api.vto.device;

@SuppressWarnings("serial")
public class VersionVTO implements java.io.Serializable{
	
	public static final String VersionType_FW = "Firmware";
	public static final String VersionType_OM = "Operation Module";
	
	//版本id 也就是具体的版本 eg.AP306P07V1.3.3Build8694_TS
	private String id;
	//版本定义的名称
	private String n;
	//VersionType VersionType_FW or VersionType_OM
	private String t;
	//device unit type
	private String dut;
	private String minid;
	//上传日期 yyyy-MM-dd hh:mm:ss
	private String d;
	//related
	private boolean r;
	
	//版本描述
	private String context;
	//上传者
	private String creator;
	
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
	}
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	public String getDut() {
		return dut;
	}
	public void setDut(String dut) {
		this.dut = dut;
	}
	public boolean isR() {
		return r;
	}
	public void setR(boolean r) {
		this.r = r;
	}
	public String getMinid() {
		return minid;
	}
	public void setMinid(String minid) {
		this.minid = minid;
	}
}
