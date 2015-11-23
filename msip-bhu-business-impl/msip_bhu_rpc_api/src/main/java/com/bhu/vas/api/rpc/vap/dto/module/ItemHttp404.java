package com.bhu.vas.api.rpc.vap.dto.module;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD) 
@XmlType(propOrder = {  
	    "enable",  
	    "url",
	    "codes",
	    "ver"
	})
public class ItemHttp404{
	@XmlAttribute
	private String enable;//="enable";
	@XmlAttribute
	private String url;//="http://vap.bhunetworks.com/vap/rw404?bid=10002";
	@XmlAttribute
	private String codes;//="40*,50*,10*";
	@XmlAttribute
	private String ver;//="style001-00.00.03";
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCodes() {
		return codes;
	}
	public void setCodes(String codes) {
		this.codes = codes;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
}
