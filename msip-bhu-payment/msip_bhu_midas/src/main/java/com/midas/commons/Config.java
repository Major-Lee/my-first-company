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
	//（生产环境：1450006356：0R6Fo6T2YM7SXkBgYsrp3XPMqpl5DjiE） 
	//（测试环境：1450006135：pn8Bp20Z8Vx52VxXbHQeK5rGnHZ2CVpo）
	public static String appid = "1450006135"; 
	public static String secret = "pn8Bp20Z8Vx52VxXbHQeK5rGnHZ2CVpo";
	
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
    
    public static String method = "get";//请求方法

	
}

