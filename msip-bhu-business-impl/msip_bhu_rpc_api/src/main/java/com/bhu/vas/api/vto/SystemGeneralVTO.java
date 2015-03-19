package com.bhu.vas.api.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SystemGeneralVTO implements Serializable{
	private long wdc;//wifi device count 总wifi设备数量
	private long cohc;//current online handset count 在线移动设备数量
	private long hdc;//handset device count 总移动设备数量
	private long aut;//average up time 平均访问时长
	private long dahc;//daily average handset count 日均移动设备数量
	
	private long dohc;//daily online handset count 本日在线移动设备数量
	private long wohc;//weekly online handset count 本周在线移动设备数量
	private long mohc;//monthly online handset count 本月在线移动设备数量
	private long yohc;//yearly online handset count 本年在线移动设备数量
	
	public long getWdc() {
		return wdc;
	}
	public void setWdc(long wdc) {
		this.wdc = wdc;
	}
	public long getCohc() {
		return cohc;
	}
	public void setCohc(long cohc) {
		this.cohc = cohc;
	}
	public long getHdc() {
		return hdc;
	}
	public void setHdc(long hdc) {
		this.hdc = hdc;
	}
	public long getAut() {
		return aut;
	}
	public void setAut(long aut) {
		this.aut = aut;
	}
	public long getDahc() {
		return dahc;
	}
	public void setDahc(long dahc) {
		this.dahc = dahc;
	}
	public long getDohc() {
		return dohc;
	}
	public void setDohc(long dohc) {
		this.dohc = dohc;
	}
	public long getWohc() {
		return wohc;
	}
	public void setWohc(long wohc) {
		this.wohc = wohc;
	}
	public long getMohc() {
		return mohc;
	}
	public void setMohc(long mohc) {
		this.mohc = mohc;
	}
	public long getYohc() {
		return yohc;
	}
	public void setYohc(long yohc) {
		this.yohc = yohc;
	}
}
