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
	//上传日期 yyyy-MM-dd hh:mm:ss
	private String d;
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
	public static String getVersiontypeFw() {
		return VersionType_FW;
	}
	public static String getVersiontypeOm() {
		return VersionType_OM;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	
	
}
