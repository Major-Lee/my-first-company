package com.midas.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MidasNotifyResponse {
	private String openid;
	private String appid;
	private String ts;
	private String payitem;
	private String token;
	private String billno;
	private String version;
	private String zoneid;
	private String providetype;
	private String amt;
	private String appmeta;
	private String sig;
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	public void setPayitem(String payitem) {
		this.payitem = payitem;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public void setBillno(String billno) {
		this.billno = billno;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public void setZoneid(String zoneid) {
		this.zoneid = zoneid;
	}
	public void setProvidetype(String providetype) {
		this.providetype = providetype;
	}
	public void setAmt(String amt) {
		this.amt = amt;
	}
	public void setAppmeta(String appmeta) {
		this.appmeta = appmeta;
	}
	public void setSig(String sig) {
		this.sig = sig;
	}
	
	
	public Map<String, Object> toMap() {
	    Map<String, Object> map = new HashMap<String, Object>();
	    Field[] fields = this.getClass().getDeclaredFields();
	    for (Field field : fields) {
	      Object obj;
	      try {
	        obj = field.get(this);
	        if (obj != null) {
	          map.put(field.getName(), obj);
	        }
	      } catch (IllegalArgumentException e) {
	        e.printStackTrace();
	      } catch (IllegalAccessException e) {
	        e.printStackTrace();
	      }
	    }
	    return map;
	  }
}
