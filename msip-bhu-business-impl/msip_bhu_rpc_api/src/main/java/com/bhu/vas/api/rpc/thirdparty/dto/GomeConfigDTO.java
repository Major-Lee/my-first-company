package com.bhu.vas.api.rpc.thirdparty.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("serial")
public class GomeConfigDTO implements Serializable {

	@JsonInclude(Include.NON_NULL)
	private String ssid;
	@JsonInclude(Include.NON_NULL)
	private Integer power;
	@JsonInclude(Include.NON_NULL)
	private String password;
	@JsonInclude(Include.NON_NULL)
	private String stalist;
	@JsonInclude(Include.NON_NULL)
	private String blocklist;
	@JsonInclude(Include.NON_NULL)
	private String addblock;
	@JsonInclude(Include.NON_NULL)
	private String delblock;

	public String getAddblock() {
		return addblock;
	}
	public void setAddblock(String addblock) {
		this.addblock = addblock;
	}
	public String getDelblock() {
		return delblock;
	}
	public void setDelblock(String delblock) {
		this.delblock = delblock;
	}
	public String getBlocklist() {
		return blocklist;
	}
	public void setBlocklist(String blocklist) {
		this.blocklist = blocklist;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public Integer getPower() {
		return power;
	}
	public void setPower(Integer power) {
		this.power = power;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getStalist() {
		return stalist;
	}
	public void setStalist(String stalist) {
		this.stalist = stalist;
	}
}
