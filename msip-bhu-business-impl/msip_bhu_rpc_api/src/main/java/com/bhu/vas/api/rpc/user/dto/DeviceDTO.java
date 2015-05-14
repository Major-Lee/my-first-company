package com.bhu.vas.api.rpc.user.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DeviceDTO implements Serializable{
	//每台设备mac地址
	//private String mac;
	//设备的名称
	private String dn;
	//每台设备mac地址
	private String dm;
	//每台设备的厂商授权token
	private String dt;
	//每台设备的厂商授权uuid
	private String du;
	//client 系统版本号
	private String cv;
	//client production 版本号
	private String pv;
	//设备型号 unittype
	private String ut;
	//push type
	private String pt;
	//client signedoff是否此设备登出
	private boolean so;
	//此设备注册到库中时间
	private long drt;
	public String getDn() {
		return dn;
	}
	public void setDn(String dn) {
		this.dn = dn;
	}
	public String getDm() {
		return dm;
	}
	public void setDm(String dm) {
		this.dm = dm;
	}
	public String getDt() {
		return dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}

	public String getDu() {
		return du;
	}
	public void setDu(String du) {
		this.du = du;
	}
	public long getDrt() {
		return drt;
	}
	public void setDrt(long drt) {
		this.drt = drt;
	}
	
	public String getCv() {
		return cv;
	}
	public void setCv(String cv) {
		this.cv = cv;
	}
	public String getPv() {
		return pv;
	}
	public void setPv(String pv) {
		this.pv = pv;
	}
	
	public boolean isSo() {
		return so;
	}
	public void setSo(boolean so) {
		this.so = so;
	}
	public String getPt() {
		return pt;
	}
	public void setPt(String pt) {
		this.pt = pt;
	}
	public boolean hasChanged(DeviceDTO dto){
		if(!dto.getDm().equals(this.getDm())) return true;
		//if(!dto.getDn().equals(this.getDn())) return true;
		if(!dto.getDt().equals(this.getDt())) return true;
		if(!dto.getDu().equals(this.getDu())) return true;
		
		if(!dto.getCv().equals(this.getCv())) return true;
		if(!dto.getPv().equals(this.getPv())) return true;
		if(!dto.getUt().equals(this.getUt())) return true;
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(this.getDm() == null) return false;
		if(obj instanceof DeviceDTO){
			DeviceDTO dto = (DeviceDTO)obj;
			if(dto.getDm() == null) return false;
			if(dto.getDm().equals(this.getDm())) return true;
			else return false;
		}else return false;
	}
	
	
	public String getUt() {
		return ut;
	}
	public void setUt(String ut) {
		this.ut = ut;
	}
	@Override
	public int hashCode() {
		return this.getDm().hashCode();
	}
}
