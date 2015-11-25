package com.bhu.vas.api.rpc.vap.dto.module;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
//<SUB src_url=”www.sina.com.cn,www.sohu.com” dest_url=”…” />
@XmlAccessorType(XmlAccessType.FIELD)  
//name = "Sub", 
@XmlType(propOrder = {  
	//"sequence",		
    "src_url",  
    "dest_url"
})
public class SubBrand extends SubItem{
	@XmlAttribute(name = "src_url")  
	private String src_url;
	@XmlAttribute(name = "dest_url")  
	private String dest_url;
	
	public String getSrc_url() {
		return src_url;
	}
	public void setSrc_url(String src_url) {
		this.src_url = src_url;
	}
	
	public String getDest_url() {
		return dest_url;
	}
	public void setDest_url(String dest_url) {
		this.dest_url = dest_url;
	}
	
}
