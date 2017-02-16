package com.bhu.vas.api.dto.commdity.internal.pay;

import java.util.List;


/**
 * 支付系统接口返回数据基类DTO
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class ResponsePaymentInfoDTO{
	
	private List<ResponsePaymentInfoDetailDTO> paypal;
	private List<ResponsePaymentInfoDetailDTO> alipay;
	private List<ResponsePaymentInfoDetailDTO> weixin;
	private List<ResponsePaymentInfoDetailDTO> wifiHelper;
	private List<ResponsePaymentInfoDetailDTO> wifiManage;
	private List<ResponsePaymentInfoDetailDTO> hee;
	private List<ResponsePaymentInfoDetailDTO> now;
	public List<ResponsePaymentInfoDetailDTO> getPaypal() {
		return paypal;
	}
	public void setPaypal(List<ResponsePaymentInfoDetailDTO> paypal) {
		this.paypal = paypal;
	}
	public List<ResponsePaymentInfoDetailDTO> getAlipay() {
		return alipay;
	}
	public void setAlipay(List<ResponsePaymentInfoDetailDTO> alipay) {
		this.alipay = alipay;
	}
	public List<ResponsePaymentInfoDetailDTO> getWeixin() {
		return weixin;
	}
	public void setWeixin(List<ResponsePaymentInfoDetailDTO> weixin) {
		this.weixin = weixin;
	}
	public List<ResponsePaymentInfoDetailDTO> getWifiHelper() {
		return wifiHelper;
	}
	public void setWifiHelper(List<ResponsePaymentInfoDetailDTO> wifiHelper) {
		this.wifiHelper = wifiHelper;
	}
	public List<ResponsePaymentInfoDetailDTO> getWifiManage() {
		return wifiManage;
	}
	public void setWifiManage(List<ResponsePaymentInfoDetailDTO> wifiManage) {
		this.wifiManage = wifiManage;
	}
	public List<ResponsePaymentInfoDetailDTO> getHee() {
		return hee;
	}
	public void setHee(List<ResponsePaymentInfoDetailDTO> hee) {
		this.hee = hee;
	}
	public List<ResponsePaymentInfoDetailDTO> getNow() {
		return now;
	}
	public void setNow(List<ResponsePaymentInfoDetailDTO> now) {
		this.now = now;
	}
}

