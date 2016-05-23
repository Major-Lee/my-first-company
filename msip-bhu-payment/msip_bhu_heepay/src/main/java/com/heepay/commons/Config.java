package com.heepay.commons;

public class Config {

	public static String agent_id = "2067044"; // 商户编号 如1001 替换商户的商户号
	public static String sign_key = "5CD5F5305FD74B33A5F827B2";//商户自己的key
	
	// 支付后返回的商户处理页面，URL参数是以http://或https://开头的完整URL地址(后台处理)// 提交的url地址必须外网能访问到,否则无法通知商户。值可以为空，但不可以为null。
	public static String notify_url = "http://pay.bhuwifi.com/msip_bhu_payment_rest/payment/heepayNotifySuccess"; 
	
	// 支付后返回的商户显示页面，URL参数是以http:// 或https://开头的完整URL地址(前台显示)，原则上：该参数与notify_url提交的参数不一致。值可以为空，但不可以为null。
	public static String return_url = "http://pay.bhuwifi.com/msip_bhu_payment_rest/payment/heeReturn"; 
}
