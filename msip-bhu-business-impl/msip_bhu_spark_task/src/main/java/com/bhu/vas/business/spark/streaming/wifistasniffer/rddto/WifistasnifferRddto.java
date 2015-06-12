package com.bhu.vas.business.spark.streaming.wifistasniffer.rddto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class WifistasnifferRddto implements Serializable{
	@JsonProperty("dev")
	private String mac;//设备mac
	@JsonProperty("item")
	private List<WifistasnifferItemRddto> items;//探测的终端列表
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public List<WifistasnifferItemRddto> getItems() {
		return items;
	}
	public void setItems(List<WifistasnifferItemRddto> items) {
		this.items = items;
	}
}
