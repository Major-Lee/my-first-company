package com.bhu.vas.business.asyn.spring.model.async.message;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class AsyncTimUserRegisterDTO extends AsyncDTO{
	
	private String user;
	private String nick;
	private String utype;
	
	public String getUtype() {
		return utype;
	}

	public void setUtype(String utype) {
		this.utype = utype;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchTimUserRegister.getPrefix();
	}
}
