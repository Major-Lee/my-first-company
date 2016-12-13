package com.bhu.vas.api.vto.advertise;

import java.util.List;

@SuppressWarnings("serial")
public class AdvertiseResultVTO implements java.io.Serializable{
	private int adApplySum;
	private int adPVSum;
	private int adUVSum;
	private List<AdvertiseDailyResultVTO> adResult;
	
	public int getAdApplySum() {
		return adApplySum;
	}
	public void setAdApplySum(int adApplySum) {
		this.adApplySum = adApplySum;
	}
	public int getAdPVSum() {
		return adPVSum;
	}
	public void setAdPVSum(int adPVSum) {
		this.adPVSum = adPVSum;
	}
	public int getAdUVSum() {
		return adUVSum;
	}
	public void setAdUVSum(int adUVSum) {
		this.adUVSum = adUVSum;
	}
	public List<AdvertiseDailyResultVTO> getAdResult() {
		return adResult;
	}
	public void setAdResult(List<AdvertiseDailyResultVTO> adResult) {
		this.adResult = adResult;
	}
}
