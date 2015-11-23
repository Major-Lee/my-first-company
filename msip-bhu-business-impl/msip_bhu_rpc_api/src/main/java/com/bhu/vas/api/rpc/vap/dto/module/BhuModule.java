package com.bhu.vas.api.rpc.vap.dto.module;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement(name = "bhu_module")
public class BhuModule {
	@XmlElementWrapper(name = "brand")
	@XmlElement(name = "ITEM")
	private List<ItemBrand> brands;
	
	@XmlElementWrapper(name = "channel")
	@XmlElement(name = "ITEM")
	private List<ItemChannel> channels;

	@XmlElementWrapper(name = "redirect")
	@XmlElement(name = "ITEM")
	private List<ItemRedirect> redirects;
	
	
	@XmlElementWrapper(name = "http404")
	@XmlElement(name = "ITEM")
	private List<ItemHttp404> http404s;
	public List<ItemBrand> getBrands() {
		return brands;
	}

	public void setBrands(List<ItemBrand> brands) {
		this.brands = brands;
	}

	public List<ItemChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<ItemChannel> channels) {
		this.channels = channels;
	}

	public List<ItemRedirect> getRedirects() {
		return redirects;
	}

	public void setRedirects(List<ItemRedirect> redirects) {
		this.redirects = redirects;
	}

	public List<ItemHttp404> getHttp404s() {
		return http404s;
	}

	public void setHttp404s(List<ItemHttp404> http404s) {
		this.http404s = http404s;
	}

}
