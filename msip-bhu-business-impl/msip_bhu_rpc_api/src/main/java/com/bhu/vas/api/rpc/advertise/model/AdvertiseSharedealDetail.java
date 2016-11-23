package com.bhu.vas.api.rpc.advertise.model;

import com.smartwork.msip.cores.orm.model.BaseLongModel;

/**
 * 广告分润明细表
 * @author yetao
 *
 */
@SuppressWarnings("serial")
public class AdvertiseSharedealDetail extends BaseLongModel{// implements IRedisSequenceGenable {
	private String advertiseid;
	private String target;	//refund为真时，取值为用户uid， 否则取值为设备mac
	private boolean refund = false; //是否退款
	private int count;
	private double cash;
	private int status = 0;
	
	
	public boolean isRefund() {
		return refund;
	}
	public void setRefund(boolean refund) {
		this.refund = refund;
	}
	public String getAdvertiseid() {
		return advertiseid;
	}
	public void setAdvertiseid(String adid) {
		this.advertiseid = adid;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String mac) {
		this.target = mac;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
