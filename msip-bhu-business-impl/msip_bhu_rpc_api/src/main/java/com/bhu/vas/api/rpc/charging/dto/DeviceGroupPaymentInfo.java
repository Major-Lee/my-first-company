package com.bhu.vas.api.rpc.charging.dto;

import com.smartwork.msip.cores.helper.ArithHelper;

/**
 * 
 * @author Edmond
 *
 */
public class DeviceGroupPaymentInfo {
	//当日收入额度
	private String incoming_amount;
	//当日打赏次数
	private String times;
	
	public String getIncoming_amount() {
		return incoming_amount;
	}
	public void setIncoming_amount(String incoming_amount) {
		this.incoming_amount = incoming_amount;
	}
	public String getTimes() {
		return times;
	}
	public void setTimes(String times) {
		this.times = times;
	}
}
