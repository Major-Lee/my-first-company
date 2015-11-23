package com.bhu.vas.api.rpc.vap.dto.module;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
//@XmlRootElement(name = "ITEM")
@XmlAccessorType(XmlAccessType.FIELD)  
//name = "ITEM", 
@XmlType(propOrder = {  
    "enable",  
    "ver",
    //"rate",
    "subs"
})
public class ItemChannel {
	@XmlAttribute
	private String enable;
	/*@XmlAttribute
	private String rate;*/
	@XmlAttribute 
	private String ver;
	
	@XmlElementWrapper(name = "list")
	@XmlElement(name = "SUB")
	private List<SubChannel> subs;
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	
	/*public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}*/
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public List<SubChannel> getSubs() {
		return subs;
	}
	public void setSubs(List<SubChannel> subs) {
		this.subs = subs;
	}
	
	
}
