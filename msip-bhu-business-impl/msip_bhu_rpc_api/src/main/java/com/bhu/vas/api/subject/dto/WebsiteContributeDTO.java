package com.bhu.vas.api.subject.dto;
/**
 * 网站贡献DTO
 * @author lawliet
 *
 */
public class WebsiteContributeDTO {
	private String domain;//网站域名
	private long share;//分享次数
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public long getShare() {
		return share;
	}
	public void setShare(long share) {
		this.share = share;
	}
}
