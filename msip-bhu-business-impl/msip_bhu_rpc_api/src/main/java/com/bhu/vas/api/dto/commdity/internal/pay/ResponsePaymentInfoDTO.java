package com.bhu.vas.api.dto.commdity.internal.pay;

/**
 * 支付系统接口返回数据基类DTO
 * @author tangzichao
 *
 */
public class ResponsePaymentInfoDTO{
	
	private ResponsePaymentInfoDetailDTO paypal;
	private ResponsePaymentInfoDetailDTO alipay;
	private ResponsePaymentInfoDetailDTO weixin;
	private ResponsePaymentInfoDetailDTO wifiHelper;
	private ResponsePaymentInfoDetailDTO wifiManage;
	private ResponsePaymentInfoDetailDTO hee;
	private ResponsePaymentInfoDetailDTO now;
	public ResponsePaymentInfoDetailDTO getPaypal() {
		return paypal;
	}
	public void setPaypal(ResponsePaymentInfoDetailDTO paypal) {
		this.paypal = paypal;
	}
	public ResponsePaymentInfoDetailDTO getAlipay() {
		return alipay;
	}
	public void setAlipay(ResponsePaymentInfoDetailDTO alipay) {
		this.alipay = alipay;
	}
	public ResponsePaymentInfoDetailDTO getWeixin() {
		return weixin;
	}
	public void setWeixin(ResponsePaymentInfoDetailDTO weixin) {
		this.weixin = weixin;
	}
	public ResponsePaymentInfoDetailDTO getWifiHelper() {
		return wifiHelper;
	}
	public void setWifiHelper(ResponsePaymentInfoDetailDTO wifiHelper) {
		this.wifiHelper = wifiHelper;
	}
	public ResponsePaymentInfoDetailDTO getWifiManage() {
		return wifiManage;
	}
	public void setWifiManage(ResponsePaymentInfoDetailDTO wifiManage) {
		this.wifiManage = wifiManage;
	}
	public ResponsePaymentInfoDetailDTO getHee() {
		return hee;
	}
	public void setHee(ResponsePaymentInfoDetailDTO hee) {
		this.hee = hee;
	}
	public ResponsePaymentInfoDetailDTO getNow() {
		return now;
	}
	public void setNow(ResponsePaymentInfoDetailDTO now) {
		this.now = now;
	}
}

