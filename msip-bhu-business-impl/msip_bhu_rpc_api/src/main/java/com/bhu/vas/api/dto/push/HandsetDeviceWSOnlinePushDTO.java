package com.bhu.vas.api.dto.push;

import com.bhu.vas.api.rpc.user.model.PushType;

/**
 * 终端探测上线push dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class HandsetDeviceWSOnlinePushDTO extends PushDTO{
	//终端mac
	private String hd_mac;
	//终端昵称
	private String n;
	
	public String getHd_mac() {
		return hd_mac;
	}

	public void setHd_mac(String hd_mac) {
		this.hd_mac = hd_mac;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	@Override
	public String getPushType() {
		return PushType.HandsetDeviceWSOnline.getType();
	}
	
}
