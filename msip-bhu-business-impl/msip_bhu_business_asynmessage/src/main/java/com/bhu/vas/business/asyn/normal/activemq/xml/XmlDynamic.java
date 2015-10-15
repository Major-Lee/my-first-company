package com.bhu.vas.business.asyn.normal.activemq.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement(name = "dynamic")
public class XmlDynamic {
	@XmlElementWrapper(name = "mqservers")
	@XmlElement(name = "server")
	private List<XmlServer> servers;

	public List<XmlServer> getServers() {
		return servers;
	}

	public void setServers(List<XmlServer> servers) {
		this.servers = servers;
	}
	
}
