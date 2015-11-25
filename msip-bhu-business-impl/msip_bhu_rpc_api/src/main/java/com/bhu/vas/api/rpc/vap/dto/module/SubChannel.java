package com.bhu.vas.api.rpc.vap.dto.module;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)  
//name = "Sub", 
@XmlType(propOrder = {  
	//"sequence",	
    "src_url",  
    "param",
    "rate"
})
public class SubChannel extends SubItem{
	@XmlAttribute(name = "src_url")  
	private String src_url;
	@XmlAttribute(name = "param")  
	private String param;
	@XmlAttribute
	private String rate;
	
	public String getSrc_url() {
		return src_url;
	}
	public void setSrc_url(String src_url) {
		this.src_url = src_url;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	
}
