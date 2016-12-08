package com.bhu.vas.business.asyn.spring.model.async.message;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class AsyncTimUserRegisterDTO extends AsyncDTO{

	private String nick;

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchTimUserRegister.getPrefix();
	}
}
