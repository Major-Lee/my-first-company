package com.bhu.vas.business.asyn.spring.model.async.message;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncDTO;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;

public class AsyncTimUserAddTagDTO extends AsyncDTO{

	private String acc;
	private String tags;
	private String sig;
	

	public String getSig() {
		return sig;
	}


	public void setSig(String sig) {
		this.sig = sig;
	}


	public String getAcc() {
		return acc;
	}


	public void setAcc(String acc) {
		this.acc = acc;
	}


	public String getTags() {
		return tags;
	}


	public void setTags(String tags) {
		this.tags = tags;
	}


	@Override
	public String getAsyncType() {
		return AsyncMessageType.BatchTimUserAddTag.getPrefix();
	}
}
