package com.midas.commons;

/* *
 *类名：Config
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.4
 *修改日期：2016-06-08
 *说明：midas参数配置类
 */

public class Config {
	public static String appid = "1450006356"; // 商户编号 如1001 替换商户的商户号
	public static String secret = "0R6Fo6T2YM7SXkBgYsrp3XPMqpl5DjiE";//商户自己的key
	
	public static String url_path = "/v1/r/"+appid+"/open_buy_goods";
    public static String pay_url =  "http://pay.qq.com/h5sdk/proxy.shtml";//米大师支付跳转链接
    public static String token_url = "http://api.unipay.qq.com";//下单通知地址
    public static String session_id = "hy_gameid";
    public static String session_type = "st_dummy";
    public static String openid = "BHUWIFIUSERMAC000000000000";
    public static String openkey = "openkey";
    public static String pay_token = "";//保留为空
    public static String pfkey = "pfkey";
    public static int zoneid = 1;
    public static String pf = "desktop_m_guest-2001-html5-2011-bhuwifi";//desktop_m_guest-2001-html5-2011-自定义字段

    public static String sig = "";
    public static String url_params = "";
    public static String payitem = "";//订单号*单价*数量
    public static String SerialNumber = "";//订单号
    public static String price = "";//单价(单位:元)
    public static int pay_num = 1;//数量
    public static String goodsmeta = "";//道具名称*描述
    
    public static String method = "GET";//请求方法
	
	// 支付后返回的商户处理页面，URL参数是以http://或https://开头的完整URL地址(后台处理)// 提交的url地址必须外网能访问到,否则无法通知商户。值可以为空，但不可以为null。
	public static String notify_url = "https://pays.bhuwifi.com/msip_bhu_payment_rest/payment/midasNotifySuccess"; 
	
	// 支付后返回的商户显示页面，URL参数是以http:// 或https://开头的完整URL地址(前台显示)，原则上：该参数与notify_url提交的参数不一致。值可以为空，但不可以为null。
	public static String return_url = "https://pays.bhuwifi.com/msip_bhu_payment_rest/payment/midasReturn"; 

	
}

