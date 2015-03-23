package com.bhu.jorion.message;

public class DevMessage {
	private DevHeader header;
	private byte[] body;
	public DevHeader getHeader() {
		return header;
	}
	public void setHeader(DevHeader header) {
		this.header = header;
	}
	public byte[] getBody() {
		return body;
	}
	public void setBody(byte[] body) {
		this.body = body;
	}

	
}
