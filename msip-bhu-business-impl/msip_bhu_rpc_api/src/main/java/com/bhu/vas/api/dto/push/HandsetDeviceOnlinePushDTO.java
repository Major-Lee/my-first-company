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
	//此终端是否第一次连接到此设备
	private boolean newed;
	
	public String getHd_mac() {
		return hd_mac;
	}

	public void setHd_mac(String hd_mac) {
		this.hd_mac = hd_mac;
	}

	public boolean isNewed() {
		return newed;
	}

	public void setNewed(boolean newed) {
		this.newed = newed;
	}

	@Override
	public String getPushType() {
		return PushType.HandsetDeviceOnline.getType();
	}
}
