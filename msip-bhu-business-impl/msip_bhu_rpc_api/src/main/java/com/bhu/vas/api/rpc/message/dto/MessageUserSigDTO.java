package com.bhu.vas.api.rpc.message.dto;

@SuppressWarnings("serial")
public class MessageUserSigDTO implements java.io.Serializable{
	
	private String sig;
	private Integer channel;
	public Integer getChannel() {
		return channel;
	}
	public void setChannel(Integer channel) {
		this.channel = channel;
	}
	public String getSig() {
		return sig;
	}
	public void setSig(String sig) {
		this.sig = sig;
	}
}
