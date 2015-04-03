package com.bhu.vas.api.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WifiDeviceVTO implements Serializable{
	private String wid;//wifi id
	private int ol;//online wifi设备是否在线 1表示在线 0标识离线
	private String adr;//所在地域的格式化地址
	private String n;//设备型号
	private String osv;//原始设备软件版本号
	//private String wdt;//wifi device type wifi设备类型
	//private String ast;//added services template 增值服务模板
	//private String dt;//device type 设备类型
	private String uof;//up outflow 上行流量
	private String dof;//down outflow 下行流量
	private int cohc;//current online handset count 在线移动设备数量
	
	public String getWid() {
		return wid;
	}
	public void setWid(String wid) {
		this.wid = wid;
	}
	public String getUof() {
		return uof;
	}
	public void setUof(String uof) {
		this.uof = uof;
	}
	public String getDof() {
		return dof;
	}
	public void setDof(String dof) {
		this.dof = dof;
	}
	public int getCohc() {
		return cohc;
	}
	public void setCohc(int cohc) {
		this.cohc = cohc;
	}
	public int getOl() {
		return ol;
	}
	public void setOl(int ol) {
		this.ol = ol;
	}
	public String getAdr() {
		return adr;
	}
	public void setAdr(String adr) {
		this.adr = adr;
	}
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
	}
	public String getOsv() {
		return osv;
	}
	public void setOsv(String osv) {
		this.osv = osv;
	}
}
