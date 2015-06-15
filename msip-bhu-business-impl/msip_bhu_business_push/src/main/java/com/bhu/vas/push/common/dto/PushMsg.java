package com.bhu.vas.push.common.dto;

public class PushMsg extends PushBasicMsg{
	//应用icon上显示的数字
	private int badge = 1;
	private String sound = "default";
	private String logo = "logo_notify.png";
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
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("uid [").append(String.valueOf(this.getUid())).append("] ");
		sb.append("dt [").append(this.getDt()).append("] ");
		sb.append("d [").append(this.getD()).append("] ");
		sb.append("paylod [").append(this.getPaylod()).append("] ");
		sb.append("pt [").append(this.getPt()).append("] ");
		sb.append("badge [").append(this.getBadge()).append("] ");
		sb.append("sound [").append(this.getSound()).append("] ");
		sb.append("text [").append(this.getText()).append("] ");
		sb.append("title [").append(this.getTitle()).append("] ");
		return sb.toString();
	}
	
//	public static void main(String[] args){
//		PushMsg msg = new PushMsg();
//		System.out.println(msg);
//	}
}
