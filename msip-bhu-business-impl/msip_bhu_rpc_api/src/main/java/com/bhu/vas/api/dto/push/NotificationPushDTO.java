package com.bhu.vas.api.dto.push;

/**
 * 用于通知类型的业务数据的传递的pushDto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public abstract class NotificationPushDTO extends PushDTO{
	//通知的显示标题
	private String title;
	//通知的显示内容
	private String text;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
