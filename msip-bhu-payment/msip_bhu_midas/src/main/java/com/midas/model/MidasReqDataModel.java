package com.midas.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MidasReqDataModel {
	private String session_id;
	private String session_type;
	private String openid;
	private String openkey;
	private String pay_token;
	private String appid;
	private String ts;
	private String payitem;
	private String goodsmeta;
	private String sig;
	private String pf;
	private String pfkey;
	private String zoneid;
	private String max_num;
	private String appmode;
	private String app_metadata;
	private String userip;
	private String format;
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public void setSession_type(String session_type) {
		this.session_type = session_type;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public void setOpenkey(String openkey) {
		this.openkey = openkey;
	}
	public void setPay_token(String pay_token) {
		this.pay_token = pay_token;
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
	public void setGoodsmeta(String goodsmeta) {
		this.goodsmeta = goodsmeta;
	}
	public void setSig(String sig) {
		this.sig = sig;
	}
	public void setPf(String pf) {
		this.pf = pf;
	}
	public void setPfkey(String pfkey) {
		this.pfkey = pfkey;
	}
	public void setZoneid(String zoneid) {
		this.zoneid = zoneid;
	}
	public void setMax_num(String max_num) {
		this.max_num = max_num;
	}
	public void setAppmode(String appmode) {
		this.appmode = appmode;
	}
	public void setApp_metadata(String app_metadata) {
		this.app_metadata = app_metadata;
	}
	public void setUserip(String userip) {
		this.userip = userip;
	}
	public void setFormat(String format) {
		this.format = format;
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
