package com.bhu.vas.api.dto.push;

import com.bhu.vas.api.rpc.user.model.PushType;

/**
 * 终端上线push dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class HandsetDeviceOnlinePushDTO extends PushDTO{
	//上线的终端的mac
	private String hd_mac;

	public String getHd_mac() {
		return hd_mac;
	}

	public void setHd_mac(String hd_mac) {
		this.hd_mac = hd_mac;
	}

	@Override
	public String getPushType() {
		return PushType.HandsetDeviceOnline.getType();
	}
}
