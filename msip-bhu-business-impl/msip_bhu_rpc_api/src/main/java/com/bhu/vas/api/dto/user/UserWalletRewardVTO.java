package com.bhu.vas.api.dto.user;

@SuppressWarnings("serial")
public class UserWalletRewardVTO implements java.io.Serializable{
	//设备mac
	private String mac;
	//交易时间
	private String dealTime;
	//交易金额
	private String dealCash;
	//收益
	private String cash;
	//收益方式
	private String dealMethod;
	//角色
	private String role;
	//打赏终端
	private String umac;
	//用户终端厂商
	private String umac_mf;
	//打赏方式
	private String description;
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getDealTime() {
		return dealTime;
	}
	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
	public String getDealCash() {
		return dealCash;
	}
	public void setDealCash(String dealCash) {
		this.dealCash = dealCash;
	}
	public String getCash() {
		return cash;
	}
	public void setCash(String cash) {
		this.cash = cash;
	}
	public String getDealMethod() {
		return dealMethod;
	}
	public void setDealMethod(String dealMethod) {
		this.dealMethod = dealMethod;
	}
	public String getUmac() {
		return umac;
	}
	public void setUmac(String umac) {
		this.umac = umac;
	}
	public String getUmac_mf() {
		return umac_mf;
	}
	public void setUmac_mf(String umac_mf) {
		this.umac_mf = umac_mf;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
		
}
