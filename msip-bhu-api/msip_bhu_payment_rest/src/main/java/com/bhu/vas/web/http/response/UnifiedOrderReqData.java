package com.bhu.vas.web.http.response;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class UnifiedOrderReqData {

	private String appid;
	  private String mch_id;
	  private String device_info;
	  private String nonce_str;
	  private String sign;
	  private String body;
	  private String detail;
	  private String attach;
	  private String out_trade_no;
	  private String fee_type;
	  private int total_fee;
	  private String spbill_create_ip;
	  private String time_start;
	  private String time_expire;
	  private String goods_tag;
	  private String notify_url;
	  private String trade_type;
	  private String product_id;
	  private String limit_pay;
	  private String openid;
	  public void setAppid(String appid) {
		    this.appid = appid;
		  }
		  public void setMch_id(String mch_id) {
		    this.mch_id = mch_id;
		  }
		  public void setDevice_info(String device_info) {
		    this.device_info = device_info;
		  }
		  public void setNonce_str(String nonce_str) {
		    this.nonce_str = nonce_str;
		  }
		  public void setSign(String sign) {
		    this.sign = sign;
		  }
		  public void setBody(String body) {
		    this.body = body;
		  }
		  public void setDetail(String detail) {
		    this.detail = detail;
		  }
		  public void setAttach(String attach) {
		    this.attach = attach;
		  }
		  public void setOut_trade_no(String out_trade_no) {
		    this.out_trade_no = out_trade_no;
		  }
		  public void setFee_type(String fee_type) {
		    this.fee_type = fee_type;
		  }
		  public void setTotal_fee(int total_fee) {
		    this.total_fee = total_fee;
		  }
		  public void setSpbill_create_ip(String spbill_create_ip) {
		    this.spbill_create_ip = spbill_create_ip;
		  }
		  public void setTime_start(String time_start) {
		    this.time_start = time_start;
		  }
		  public void setTime_expire(String time_expire) {
		    this.time_expire = time_expire;
		  }
		  public void setGoods_tag(String goods_tag) {
		    this.goods_tag = goods_tag;
		  }
		  public void setNotify_url(String notify_url) {
		    this.notify_url = notify_url;
		  }
		  public void setTrade_type(String trade_type) {
		    this.trade_type = trade_type;
		  }
		  public void setProduct_id(String product_id) {
		    this.product_id = product_id;
		  }
		  public void setLimit_pay(String limit_pay) {
		    this.limit_pay = limit_pay;
		  }
		  public void setOpenid(String openid) {
		    this.openid = openid;
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
