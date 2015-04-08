package com.bhu.vas.api.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WifiDeviceVTO implements Serializable{
	private String wid;//wifi id
	private int ol;//online wifi设备是否在线 1表示在线 0标识离线
	private String adr;//所在地域的格式化地址
	private String om;//oem设备型号
	private String osv;//oem后软件版本号
	private String ovd;//oem厂商
	private String cfm;//config model
	private String wm;//work model
	//private String wdt;//wifi device type wifi设备类型
	//private String ast;//added services template 增值服务模板
	//private String dt;//device type 设备类型
	private long uof;//up outflow 上行流量
	private long dof;//down outflow 下行流量
	private int cohc;//current online handset count 在线移动设备数量
	private long cts;//创建时间
	private long rts;//最后注册时间
	
	public String getWid() {
		return wid;
	}
	public void setWid(String wid) {
		this.wid = wid;
	}
	public long getUof() {
		return uof;
	}
	public void setUof(long uof) {
		this.uof = uof;
	}
	public long getDof() {
		return dof;
	}
	public void setDof(long dof) {
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
	public String getOm() {
		return om;
	}
	public void setOm(String om) {
		this.om = om;
	}
	public String getOsv() {
		return osv;
	}
	public void setOsv(String osv) {
		this.osv = osv;
	}
	public String getOvd() {
		return ovd;
	}
	public void setOvd(String ovd) {
		this.ovd = ovd;
	}
	public String getCfm() {
		return cfm;
	}
	public void setCfm(String cfm) {
		this.cfm = cfm;
	}
	public String getWm() {
		return wm;
	}
	public void setWm(String wm) {
		this.wm = wm;
	}
	public long getCts() {
		return cts;
	}
	public void setCts(long cts) {
		this.cts = cts;
	}
	public long getRts() {
		return rts;
	}
	public void setRts(long rts) {
		this.rts = rts;
	}
}
