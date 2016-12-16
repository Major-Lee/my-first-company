package com.bhu.vas.business.asyn.spring.model.async.message;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class AsyncTimUserAddTagDTO extends AsyncDTO{

	private String acc;
	private String sig;
	private String utype;
	private Integer channel;
	private boolean newly;
	
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


	public String getSig() {
		return sig;
	}


	public void setSig(String sig) {
		this.sig = sig;
	}



	public boolean isNewly() {
		return newly;
	}


	public void setNewly(boolean newly) {
		this.newly = newly;
	}


	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchTimUserAddTag.getPrefix();
	}
}
