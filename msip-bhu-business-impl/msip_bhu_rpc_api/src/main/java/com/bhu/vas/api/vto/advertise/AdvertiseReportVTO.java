package com.bhu.vas.api.vto.advertise;


@SuppressWarnings("serial")
public class AdvertiseReportVTO implements java.io.Serializable{
	private AdvertiseVTO adDetail;
	private AdvertiseResultVTO adResult;
	private AdvertiseBillsVTO adBills;
	
	public AdvertiseVTO getAdDetail() {
		return adDetail;
	}
	public void setAdDetail(AdvertiseVTO adDetail) {
		this.adDetail = adDetail;
	}
	public AdvertiseResultVTO getAdResult() {
		return adResult;
	}
	public void setAdResult(AdvertiseResultVTO adResult) {
		this.adResult = adResult;
	}
	public AdvertiseBillsVTO getAdBills() {
		return adBills;
	}
	public void setAdBills(AdvertiseBillsVTO adBills) {
		this.adBills = adBills;
	}
}
