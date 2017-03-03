package com.bhu.vas.web.http.response;

import java.util.Map;

import com.bhu.vas.web.http.WxResponse;

public class WithDrawNotifyResponse extends WxResponse {

    String return_code;
    String return_msg;

    String mch_appid;
    String mchid;
    String device_info;
    String nonce_str;
    String result_code;
    
    String partner_trade_no;
    String payment_no;
    String payment_time;


    @Override
    public void getValueFromMap(Map map) {
        super.getValueFromMap(map);
        return_code=map.get("return_code").toString();
        return_msg=map.get("return_msg").toString();
        nonce_str=map.get("nonce_str").toString();
        result_code=map.get("result_code").toString();
        return_code=map.get("return_code").toString();
        
        payment_no=map.get("payment_no").toString();
        partner_trade_no=map.get("partner_trade_no").toString();
        payment_time=map.get("payment_time").toString();
    }


	public String getReturn_code() {
		return return_code;
	}


	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}


	public String getReturn_msg() {
		return return_msg;
	}


	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}


	public String getMch_appid() {
		return mch_appid;
	}


	public void setMch_appid(String mch_appid) {
		this.mch_appid = mch_appid;
	}


	public String getMchid() {
		return mchid;
	}


	public void setMchid(String mchid) {
		this.mchid = mchid;
	}


	public String getDevice_info() {
		return device_info;
	}


	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}


	public String getNonce_str() {
		return nonce_str;
	}


	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}


	public String getResult_code() {
		return result_code;
	}


	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}


	public String getPartner_trade_no() {
		return partner_trade_no;
	}


	public void setPartner_trade_no(String partner_trade_no) {
		this.partner_trade_no = partner_trade_no;
	}


	public String getPayment_no() {
		return payment_no;
	}


	public void setPayment_no(String payment_no) {
		this.payment_no = payment_no;
	}


	public String getPayment_time() {
		return payment_time;
	}


	public void setPayment_time(String payment_time) {
		this.payment_time = payment_time;
	}

}
