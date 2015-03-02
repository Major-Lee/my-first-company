package com.bhu.vas.api.subject.dto;

public class SubjectDetailDTO {
	private String estimate;
	private String originalAbstract;
	private String customAbstract;
	private String image;
	
	public String getEstimate() {
		return estimate;
	}
	public void setEstimate(String estimate) {
		this.estimate = estimate;
	}
	public String getOriginalAbstract() {
		return originalAbstract;
	}
	public void setOriginalAbstract(String originalAbstract) {
		this.originalAbstract = originalAbstract;
	}
	public String getCustomAbstract() {
		return customAbstract;
	}
	public void setCustomAbstract(String customAbstract) {
		this.customAbstract = customAbstract;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

}
