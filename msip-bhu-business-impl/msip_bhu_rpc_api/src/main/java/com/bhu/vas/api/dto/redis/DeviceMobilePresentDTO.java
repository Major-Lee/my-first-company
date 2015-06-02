package com.bhu.vas.api.dto.redis;
/**
 * mobile设备的信息dto
 * 用于发送push的信息
 * @author tangzichao
 *
 */
public class DeviceMobilePresentDTO {
	//用户id
	private Integer uid;
	//device type
	private String d;
	//mobile设备token
	private String dt;
	//push type
	private String pt;
	//终端mac
	private String dm;
	
	public DeviceMobilePresentDTO(){
		
	}
	
	public DeviceMobilePresentDTO(Integer uid, String d, String dt, String pt, String dm){
		this.uid = uid;
		this.d = d;
		this.dt = dt;
		this.pt = pt;
		this.dm = dm;
	}
	
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	public String getDt() {
		return dt;
	}
	public void setDt(String dt) {
		this.dt = dt;
	}
	public String getPt() {
		return pt;
	}
	public void setPt(String pt) {
		this.pt = pt;
	}

	public String getDm() {
		return dm;
	}

	public void setDm(String dm) {
		this.dm = dm;
	}
}
