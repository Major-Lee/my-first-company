package com.smartwork.im.message.u2u;

/**
 * 根据指定的消息内容 生成多个 t类别的消息
 * @author Edmond
 *
 */
public class BroadcastMessage {
	//多个to
	private String tos;
	//消息生成的类别
	private String mt;
	//消息内容，对于chat消息，body代表cid
	private String body;
	//对于chat消息，dur代表资源时长
	private String dur;
	public String getMt() {
		return mt;
	}

	public void setMt(String mt) {
		this.mt = mt;
	}

	public String getTos() {
		return tos;
	}

	public void setTos(String tos) {
		this.tos = tos;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getDur() {
		return dur;
	}

	public void setDur(String dur) {
		this.dur = dur;
	}

}
