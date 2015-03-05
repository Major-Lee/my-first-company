package com.smartwork.im.message;

public class Message {
	//谁发送的消息
	private String from;
	//谁应该接收到消息 
	//点对点消息 A给B发送的消息 from=A to=B
	//群组消息（G中有A，B，C三人） A 在群组G发出的的消息  from=A to=@G  对于B收到的消息 from=A to=B@G 对于C收到的消息 from=A to=C@G
	private String to;
	//private String[] to;
	//消息类别message type
	private String mt;
	/*//消息发送端设备编号
	private String fromtp;
	//消息接收端设备编号
	private String top;*/
	//消息交互类别 interactive type eg:C2S S2S S2C...
	//private String it;
	//存储消息体，本身是个json
	private String payload;

	//消息传递过程中是否是出现错误，比如在定位到客户端后，发送消息时发现客户端连接已经关闭，转发不到客户端
	private boolean e = false;
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	
	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	/*public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}
*/
	public String getMt() {
		return mt;
	}

	public void setMt(String mt) {
		this.mt = mt;
	}

/*	public String getFromtp() {
		return fromtp;
	}

	public void setFromtp(String fromtp) {
		this.fromtp = fromtp;
	}

	public String getTop() {
		return top;
	}

	public void setTop(String top) {
		this.top = top;
	}*/

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public boolean isE() {
		return e;
	}

	public void setE(boolean e) {
		this.e = e;
	}
	
}
