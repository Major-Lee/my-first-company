package com.bhu.vas.api.dto.advertise;

import com.smartwork.msip.cores.orm.support.page.TailPage;

@SuppressWarnings("serial")
public class AdvertiseListVTO implements java.io.Serializable{
	private TailPage<AdvertiseVTO> advertises;
	private int pubComNum;
	private int verifyFalNum;
	public TailPage<AdvertiseVTO> getAdvertises() {
		return advertises;
	}
	public void setAdvertises(TailPage<AdvertiseVTO> advertises) {
		this.advertises = advertises;
	}
	public int getPubComNum() {
		return pubComNum;
	}
	public void setPubComNum(int pubComNum) {
		this.pubComNum = pubComNum;
	}
	public int getVerifyFalNum() {
		return verifyFalNum;
	}
	public void setVerifyFalNum(int verifyFalNum) {
		this.verifyFalNum = verifyFalNum;
	}
	
}
