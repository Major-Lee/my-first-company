package com.bhu.vas.push.common.dto;

public class PushMsg extends PushBasicMsg{
	//应用icon上显示的数字
	private int badge = 1;
	private String sound = "default";
	//push text
	private String text;
	//push title
	private String title;
	
	public int getBadge() {
		return badge;
	}
	public void setBadge(int badge) {
		this.badge = badge;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
