package com.bhu.vas.api.rpc.user.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserConfigsStateDTO implements Serializable{
	//打赏push通知开关
	private boolean rn_on = true;

	public boolean isRn_on() {
		return rn_on;
	}

	public void setRn_on(boolean rn_on) {
		this.rn_on = rn_on;
	}
}