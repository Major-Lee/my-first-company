package com.bhu.vas.api.vto.advertise;

import com.smartwork.msip.cores.orm.support.page.TailPage;

@SuppressWarnings("serial")
public class AdvertiseListVTO implements java.io.Serializable{
	private TailPage<AdvertiseVTO> advertises;
	private int pubComNum;
	private int verifyFalNum;
	private int unPaidNum;
	private int unVerifyNum;
	private int unPublishNum;
	private int onPublishNum;
	private int escapeNum;
	
	public int getUnPaidNum() {
		return unPaidNum;
	}
	public void setUnPaidNum(int unPaidNum) {
		this.unPaidNum = unPaidNum;
	}
	public int getUnVerifyNum() {
		return unVerifyNum;
	}
	public void setUnVerifyNum(int unVerifyNum) {
		this.unVerifyNum = unVerifyNum;
	}
	public int getUnPublishNum() {
		return unPublishNum;
	}
	public void setUnPublishNum(int unPublishNum) {
		this.unPublishNum = unPublishNum;
	}
	public int getOnPublishNum() {
		return onPublishNum;
	}
	public void setOnPublishNum(int onPublishNum) {
		this.onPublishNum = onPublishNum;
	}
	public int getEscapeNum() {
		return escapeNum;
	}
	public void setEscapeNum(int escapeNum) {
		this.escapeNum = escapeNum;
	}
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
