package com.bhu.vas.api.subject.dto;

@SuppressWarnings("serial")
public class SubjectSearchDTO implements java.io.Serializable{
	private int id;
	private String highlight_content;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHighlight_content() {
		return highlight_content;
	}
	public void setHighlight_content(String highlight_content) {
		this.highlight_content = highlight_content;
	}
}
