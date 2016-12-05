package com.bhu.vas.api.rpc.message.dto;

@SuppressWarnings("serial")
public class TimCustomMsgContentDTO implements java.io.Serializable{
	
	private int type;
	private String title;
	private String desc;
	private String pic;
	private String url;
	private boolean displayPic;
	private boolean isClick;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isDisplayPic() {
		return displayPic;
	}
	public void setDisplayPic(boolean displayPic) {
		this.displayPic = displayPic;
	}
	public boolean isClick() {
		return isClick;
	}
	public void setClick(boolean isClick) {
		this.isClick = isClick;
	}
	
	public static TimCustomMsgContentDTO buildTimCustomMsgContentDTO(int type, String title,
			String desc, String pic, String url, boolean displayPic, boolean isClick){
		TimCustomMsgContentDTO dto = new TimCustomMsgContentDTO();
		dto.setType(type);
		dto.setTitle(title);
		dto.setDesc(desc);
		dto.setPic(pic);
		dto.setUrl(url);
		dto.setDisplayPic(displayPic);
		dto.setClick(isClick);
		return dto;
	}
}
