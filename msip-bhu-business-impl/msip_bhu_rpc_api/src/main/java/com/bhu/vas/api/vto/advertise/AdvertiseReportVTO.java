package com.bhu.vas.api.vto.advertise;

import java.util.Map;

public class AdvertiseReportVTO {
	private AdvertiseVTO adDetail;
	private Map<String,Integer> adResult;
	private AdvertiseBillsVTO adBills;
	
	public AdvertiseVTO getAdDetail() {
		return adDetail;
	}
	public void setAdDetail(AdvertiseVTO adDetail) {
		this.adDetail = adDetail;
	}
	public Map<String, Integer> getAdResult() {
		return adResult;
	}
	public void setAdResult(Map<String, Integer> adResult) {
		this.adResult = adResult;
	}
	public AdvertiseBillsVTO getAdBills() {
		return adBills;
	}
	public void setAdBills(AdvertiseBillsVTO adBills) {
		this.adBills = adBills;
	}
}
