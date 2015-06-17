package com.bhu.vas.api.vto;

import java.io.Serializable;

/**
 * urouter的终端hostname vto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class URouterHdHostNameVTO implements Serializable{
	//终端mac
	private String hd_mac;
	//host name
	private String hn;
	
	public String getHd_mac() {
		return hd_mac;
	}
	public void setHd_mac(String hd_mac) {
		this.hd_mac = hd_mac;
	}
	public String getHn() {
		return hn;
	}
	public void setHn(String hn) {
		this.hn = hn;
	}

}
