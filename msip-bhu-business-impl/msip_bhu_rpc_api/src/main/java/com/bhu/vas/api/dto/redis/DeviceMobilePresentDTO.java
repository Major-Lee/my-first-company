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
	//mobile设备token
	private String dt;
	//push type
	private String pt;
	
	public DeviceMobilePresentDTO(){
		
	}
	
	public DeviceMobilePresentDTO(Integer uid, String dt, String pt){
		this.uid = uid;
		this.dt = dt;
		this.pt = pt;
	}
	
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
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
}
