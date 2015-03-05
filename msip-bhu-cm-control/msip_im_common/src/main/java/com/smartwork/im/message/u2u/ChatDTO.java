package com.smartwork.im.message.u2u;

import com.smartwork.msip.cores.helper.StringHelper;

public class ChatDTO {
	private String from;
	//消息类型 
	private char t;
	//如果是 type：P、A、V则是媒体文件fid 如果type：T 则是消息文字内容
	private String body;
	//如果有音频或视频 则代表音频视频字段的时间长度
	private String dur;
	//扩展字段 | 线分割 用于客户端自定义属性
	private String ext;
	//消息时间
	private long ts;
	
	private String ref;
	public char getT() {
		return t;
	}
	public void setT(char t) {
		this.t = t;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getDur() {
		return dur;
	}
	public void setDur(String dur) {
		this.dur = dur;
	}
	
	public static ChatDTO builder(char t,String from,String body,long ts){
		/*ChatDTO dto = new ChatDTO();
		dto.setT(t);
		dto.setFrom(from);
		dto.setBody(body);
		dto.setTs(ts);*/
		return builderWithRef(t,from,body,StringHelper.EMPTY_STRING_GAP,ts,StringHelper.EMPTY_STRING_GAP);
	}
	
	public static ChatDTO builder(char t,String from,String body,String dur,long ts){
		/*ChatDTO dto = new ChatDTO();
		dto.setT(t);
		dto.setFrom(from);
		dto.setBody(body);
		dto.setTs(ts);*/
		return builderWithRef(t,from,body,dur,ts,StringHelper.EMPTY_STRING_GAP);
	}
	
	public static ChatDTO builderWithRef(char t,String from,String body,String dur,long ts,String ref){
		ChatDTO dto = new ChatDTO();
		dto.setT(t);
		dto.setFrom(from);
		dto.setBody(body);
		dto.setDur(dur);
		dto.setTs(ts);
		dto.setRef(ref);
		return dto;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	
	
}
