package com.bhu.vas.api.vto.guest;

import java.io.Serializable;

/**
 * Created by bluesand on 10/26/15.
 */
@SuppressWarnings("serial")
public class URouterVisitorDetailVTO implements Serializable {

    private String hd_mac;

    private String n;
    
    /**
     * 昵称
     */
    private String alias;

    /**
     * 终端状态: online:0, authonline: 时间戳 , authoffline:1,
     */
    private String s;

    public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getHd_mac() {
        return hd_mac;
    }

    public void setHd_mac(String hd_mac) {
        this.hd_mac = hd_mac;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
