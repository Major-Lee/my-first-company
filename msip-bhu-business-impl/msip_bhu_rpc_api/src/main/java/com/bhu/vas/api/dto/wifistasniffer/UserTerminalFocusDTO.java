package com.bhu.vas.api.dto.wifistasniffer;

import java.io.Serializable;
/**
 * 用户与终端关系dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class UserTerminalFocusDTO implements Serializable{
	//是否关注
	private boolean focus;
	//昵称
	private String nick;

	public boolean isFocus() {
		return focus;
	}

	public void setFocus(boolean focus) {
		this.focus = focus;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
	
}
