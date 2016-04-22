package com.bhu.vas.api.dto.push;

import com.bhu.vas.api.rpc.user.model.PushType;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 打赏分成push dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class SharedealNotifyPushDTO extends NotificationPushDTO{
	//终端mac
	@JsonIgnore
	private String hd_mac;
	//绑定设备的用户id
	@JsonIgnore
	private Integer uid;
	//分成金额
	@JsonIgnore
	private String cash;
	//支付方式
	@JsonIgnore
	private String payment_type;
	//终端类型
	@JsonIgnore
	private int umac_type;
	
	public String getHd_mac() {
		return hd_mac;
	}

	public void setHd_mac(String hd_mac) {
		this.hd_mac = hd_mac;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}

	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}

	public int getUmac_type() {
		return umac_type;
	}

	public void setUmac_type(int umac_type) {
		this.umac_type = umac_type;
	}

	@Override
	public String getPushType() {
		return PushType.SharedealNotify.getType();
	}
}
