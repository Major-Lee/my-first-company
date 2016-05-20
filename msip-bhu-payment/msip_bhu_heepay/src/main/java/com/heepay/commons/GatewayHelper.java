package com.heepay.commons;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.heepay.model.GatewayModel;


public class GatewayHelper {
	public static String SignMd5(String key,GatewayModel gatewayModel)
	{
		StringBuilder _StringSign = new StringBuilder();
        //注意拼接顺序,详情请看《汇付宝支付网关开发指南》3.4签名
        _StringSign.append("version=1")
            .append("&agent_id=" + gatewayModel.getagent_id())
            .append("&agent_bill_id=" + gatewayModel.getagent_bill_id())
            .append("&agent_bill_time=" + gatewayModel.getagent_bill_time())
            .append("&pay_type=" + gatewayModel.getpay_type());
        if (gatewayModel.getpay_flag() != null && gatewayModel.getpay_flag().trim().length() > 0)
        {
            _StringSign.append("&pay_flag=" + gatewayModel.getpay_flag());
        }
        _StringSign.append("&pay_amt=" + gatewayModel.getpay_amt())
            .append("&notify_url=" + gatewayModel.getnotify_url())
            .append("&return_url=" + gatewayModel.getreturn_url())
            .append("&user_ip=" + gatewayModel.getuser_ip());
        if (gatewayModel.getis_test() != null && gatewayModel.getis_test().trim().length() > 0)
        {
            _StringSign.append("&is_test=" + gatewayModel.getis_test());
        }
        _StringSign.append("&key=" + key);
        return Md5Tools.MD5(_StringSign.toString()).toLowerCase();
	}
	
	@SuppressWarnings("deprecation")
	public static String GatewaySubmitUrl(String sign,GatewayModel gatewayModel) throws UnsupportedEncodingException
	{
		StringBuilder sbURL = new StringBuilder();
		sbURL.append("https://pay.heepay.com/Payment/Index.aspx?");
		sbURL.append("version=" + gatewayModel.getversion());
        sbURL.append("&pay_type=" + gatewayModel.getpay_type());
        sbURL.append("&pay_code=" + gatewayModel.getpay_code());
        sbURL.append("&agent_id=" + gatewayModel.getagent_id());
        sbURL.append("&agent_bill_id=" + gatewayModel.getagent_bill_id());
        sbURL.append("&pay_amt=" + gatewayModel.getpay_amt());
        
        sbURL.append("&notify_url=" + gatewayModel.getnotify_url());
        sbURL.append("&return_url=" + gatewayModel.getreturn_url());
        sbURL.append("&user_ip=" + gatewayModel.getuser_ip());
        sbURL.append("&agent_bill_time=" + gatewayModel.getagent_bill_time());
        sbURL.append("&goods_name=" + URLEncoder.encode(gatewayModel.getgoods_name()));
        sbURL.append("&goods_num=" + gatewayModel.getgoods_num());
        sbURL.append("&remark=" + URLEncoder.encode(gatewayModel.getremark()));
        sbURL.append("&is_phone=" + gatewayModel.getis_phone());
        sbURL.append("&is_frame=" + gatewayModel.getis_frame());
        if (gatewayModel.getis_test() != null && gatewayModel.getis_test().trim().length() > 0)
        {
        	sbURL.append("&is_test=" + gatewayModel.getis_test());
        }     
        sbURL.append("&goods_note=" + URLEncoder.encode(gatewayModel.getgoods_note()));
        sbURL.append("&sign=" + sign);
		return sbURL.toString();
	}
	
	//汇付宝查询构建签名
	public static String SignQueryMd5(String key,GatewayModel gatewayModel)
	{
		StringBuilder _StringSign = new StringBuilder();
		
		_StringSign.append("version=" + gatewayModel.getversion())
        .append("&agent_id=" + gatewayModel.getagent_id())
        .append("&agent_bill_id=" + gatewayModel.getagent_bill_id())
        .append("&agent_bill_time=" + gatewayModel.getagent_bill_time())
        .append("&return_mode=1")//默认值
		.append("&key=" + key);
		return Md5Tools.MD5(_StringSign.toString()).toLowerCase();
	}
	
	//汇付宝查询构建提交url地址
	@SuppressWarnings("deprecation")
	public static String GatewaySubmitQuery(String sign,GatewayModel gatewayModel) throws UnsupportedEncodingException
	{
		StringBuilder sbURL = new StringBuilder();
		sbURL.append("version=1");
        sbURL.append("&agent_id=" + gatewayModel.getagent_id());
        sbURL.append("&agent_bill_id=" + gatewayModel.getagent_bill_id());
        sbURL.append("&agent_bill_time=" + gatewayModel.getagent_bill_time());
        sbURL.append("&remark=" + URLEncoder.encode(gatewayModel.getremark()));
        sbURL.append("&return_mode=1");
        sbURL.append("&sign=" + sign);
        
		return sbURL.toString();
	}
	
	
	public static boolean VerifiSign(String urlStr)
	{
		String[] str = urlStr.split("sign=");
		
		String mySign = Md5Tools.MD5(str[0] + "key="+Config.sign_key).toLowerCase();
		System.out.println(str[0]+"-------"+str[1]+"======="+mySign);
		
		if(mySign.equals(str[1]))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
