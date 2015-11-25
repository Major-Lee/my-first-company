package com.bhu.vas.api.rpc.vap.dto.module;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)  
//name = "ITEM", 
@XmlType(propOrder = {  
  "enable",  
  "ver",
  "subs"
})
public class ItemRedirect{
	@XmlAttribute
	private String enable;//="enable";
	@XmlAttribute
	private String ver;//="style004-00.00.01";
	
	@XmlElementWrapper(name = "list")
	@XmlElement(name = "SUB")
	private List<SubRedirect> subs;
	
	//@XmlAttribute
	//private String rule;//="100,12:00:00,20:00:00,http://sina.cn,http://m.hao123.com/?union=1&amp;from=1012546c&amp;tn=ops1012546c,http://m.sohu.com,http://m.hao123.com/?union=1&amp;from=1012546c&amp;tn=ops1012546c,http://h5.mse.360.cn,http://m.hao123.com/?union=1&amp;from=1012546c&amp;tn=ops1012546c,http://hao.360.cn,http://www.hao123.com";

	public String getEnable() {
		return enable;
	}
	public void setEnable(String enable) {
		this.enable = enable;
	}
	/*public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}*/
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public List<SubRedirect> getSubs() {
		return subs;
	}
	public void setSubs(List<SubRedirect> subs) {
		this.subs = subs;
	}
	
	
}
