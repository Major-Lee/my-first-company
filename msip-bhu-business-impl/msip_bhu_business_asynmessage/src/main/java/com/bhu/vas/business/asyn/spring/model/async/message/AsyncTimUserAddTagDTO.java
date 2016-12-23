package com.bhu.vas.business.asyn.spring.model.async.message;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class AsyncTimUserAddTagDTO extends AsyncDTO{

	private String acc;
	private String utype;
	private Integer channel;
	
	public String getUtype() {
		return utype;
	}


	public void setUtype(String utype) {
		this.utype = utype;
	}


	public Integer getChannel() {
		return channel;
	}


	public void setChannel(Integer channel) {
		this.channel = channel;
	}


	public String getAcc() {
		return acc;
	}


	public void setAcc(String acc) {
		this.acc = acc;
	}


	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchTimUserAddTag.getPrefix();
	}
}
