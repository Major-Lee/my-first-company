package com.bhu.vas.api.rpc.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
public abstract class UserSettingDTO implements java.io.Serializable{
	@JsonIgnore
	public abstract String getSettingKey();
}
