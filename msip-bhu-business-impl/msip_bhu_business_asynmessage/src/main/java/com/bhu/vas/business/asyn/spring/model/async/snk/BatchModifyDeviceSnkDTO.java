//package com.bhu.vas.business.asyn.spring.model.async.snk;
//
//import java.util.List;
//
//import com.bhu.vas.api.helper.SharedNetworkChangeType;
//import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
//import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;
//import com.bhu.vas.business.asyn.spring.model.IDTO;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonInclude.Include;
//
//public class BatchModifyDeviceSnkDTO extends AsyncDTO implements IDTO {
//	private String ssid;
//	private int rate;
//	private List<String> macs;
//	private char dtoType;
//	
//	
//	
//	public String getSsid() {
//		return ssid;
//	}
//
//	public void setSsid(String ssid) {
//		this.ssid = ssid;
//	}
//
//	public int getRate() {
//		return rate;
//	}
//
//	public void setRate(int rate) {
//		this.rate = rate;
//	}
//
//	public List<String> getMacs() {
//		return macs;
//	}
//
//	public void setMacs(List<String> macs) {
//		this.macs = macs;
//	}
//	
//	@Override
//	public String getAsyncType() {
//		return AsyncMessageType.BatchModifyDeviceSnk.getPrefix();
//	}
//
//	@Override
//	public char getDtoType() {
//		return dtoType;
//	}
//	public void setDtoType(char dtoType) {
//		this.dtoType = dtoType;
//	}
//}
