package com.smartwork.im.message.u2u;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 客户端传递过来的jason串中不带c属性，此属性由后台设定
 * @author Edmond
 *
 */
public class ChatMessage{
	public static final char Type_Text = 'T';
	public static final char Type_ZIP = 'Z';
/*	public static final char Type_Picture = 'P';
	public static final char Type_Audio = 'A';
	public static final char Type_Video = 'V';
*/	
	/*//消息类型 
	private char t;
	//如果是 type：P、A、V则是媒体文件fid 如果type：T 则是消息文字内容
	private String body;
	//消息时间
	private long ts;*/
		
	//所属context id
	private String cid;
	//contextmsg
	private boolean c = false;
	//one-way 如果给单向好友发消息，这个值为false
	//private boolean ow = false;
	//是否需要服务器接收到to的回执在删除消息存储空间的消息
	private boolean r = false;
	private ChatDTO dto;
	
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public ChatDTO getDto() {
		return dto;
	}
	public void setDto(ChatDTO dto) {
		this.dto = dto;
	}
	
	public boolean wasContextMsg(){
		if(isC()) return isC();
		if(cid == null) return false;
		if(dto == null) return false;
		return cid.equals(dto.getBody());
	}
	/*public boolean resetContextMsg(){
		if(dto == null){
			this.setC(false);
			return false;
		}
		if(StringHelper.isEmpty(cid)) {
			this.setC(true);
			return true;
		}
		if(cid.equals(dto.getBody())){
			this.setC(true);
			return true;
		}
		return false;
	}*/
	
	public boolean wasRefMsg(){
		if(cid == null) return false;
		if(dto == null) return false;
		return StringHelper.isNotEmpty(dto.getRef());
	}
	
	public boolean isC() {
		return c;
	}
	public void setC(boolean c) {
		this.c = c;
	}
	public boolean isR() {
		return r;
	}
	public void setR(boolean r) {
		this.r = r;
	}
	
	/*public boolean isOw() {
		return ow;
	}
	public void setOw(boolean ow) {
		this.ow = ow;
	}*/
	
}
