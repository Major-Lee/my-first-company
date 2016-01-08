package com.bhu.vas.api.dto.push;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 用于通知类型的业务数据的传递的pushDto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public abstract class NotificationPushDTO extends PushDTO{
	//通知的显示标题
	@JsonProperty("tt")
	private String title;
	//通知的显示内容
	@JsonProperty("tx")
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
