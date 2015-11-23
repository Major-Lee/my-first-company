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
    "interval",
    "subs"
})
public class ItemBrand {
	@XmlAttribute
	private String enable;
	@XmlAttribute
	private String interval;
	@XmlAttribute 
	private String ver;
	
	@XmlElementWrapper(name = "list")
	@XmlElement(name = "SUB")
	private List<SubBrand> subs;
	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	public String getInterval() {
		return interval;
	}
	public void setInterval(String interval) {
		this.interval = interval;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public List<SubBrand> getSubs() {
		return subs;
	}
	public void setSubs(List<SubBrand> subs) {
		this.subs = subs;
	}
	
	
}
