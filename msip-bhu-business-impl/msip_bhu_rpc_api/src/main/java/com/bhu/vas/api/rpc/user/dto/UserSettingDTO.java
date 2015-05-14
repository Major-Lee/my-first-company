package com.bhu.vas.api.rpc.user.dto;

@SuppressWarnings("serial")
public class UserSettingDTO implements java.io.Serializable{
	private int s_pai;

	public int getS_pai() {
		return s_pai;
	}
	public void setS_pai(int s_pai) {
		this.s_pai = s_pai;
	}
	public UserSettingDTO() {
	}
	public UserSettingDTO(int s_pai) {
		super();
		this.s_pai = s_pai;
	}
	
}
