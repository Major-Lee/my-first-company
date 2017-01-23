package com.bhu.vas.api.rpc.thirdparty.dto;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@SuppressWarnings("serial")
public class GomeDeviceStaDTO implements Serializable {
	@JsonInclude(Include.NON_NULL)
	private String hd_mac;
	
	@JsonInclude(Include.NON_NULL)
	private String ip;
	
	@JsonInclude(Include.NON_NULL)
	private String n;
	
	@JsonInclude(Include.NON_NULL)
	private String tt;
	
	@JsonInclude(Include.NON_NULL)
	private String hd_vapname;
	
	@JsonInclude(Include.NON_NULL)
	private boolean online;

	public String getHd_mac() {
		return hd_mac;
	}

	public void setHd_mac(String hd_mac) {
		this.hd_mac = hd_mac;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getTt() {
		return tt;
	}

	public void setTt(String tt) {
		this.tt = tt;
	}

	public String getHd_vapname() {
		return hd_vapname;
	}

	public void setHd_vapname(String hd_vapname) {
		this.hd_vapname = hd_vapname;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
	
	public static GomeDeviceStaDTO builder(String hd_mac, String tt, HandsetDeviceDTO hd_entity, boolean online, String alia){
		GomeDeviceStaDTO dto = new GomeDeviceStaDTO();
		dto.setHd_mac(hd_mac);
		dto.setIp(hd_entity.getIp());
		dto.setN(alia);
		dto.setTt(tt);
		dto.setOnline(online);
		if(hd_entity != null){
			if(StringUtils.isEmpty(dto.getN())){
				dto.setN(hd_entity.getDhcp_name());
			}
			dto.setHd_vapname(hd_entity.getVapname());
		}
		return dto;
		
	}
	
}
