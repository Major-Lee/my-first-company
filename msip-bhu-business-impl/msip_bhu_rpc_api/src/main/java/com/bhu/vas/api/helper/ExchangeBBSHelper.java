package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.encrypt.DigestHelper;

/**
 * 用户注册接口
注册地址: http://bbs.bhuwifi.com/urouter_server_add_user.php
请求方式: POST
发送数据: username=手机号码&regTocken=bhuwifiandurouter (接口与必虎服务器之间的秘钥)
返回数据（json格式） 例如 {"rcode":1}
                                     rcode参数对照表
rcode
 说明
 1 注册成功
 0 秘钥错误/请求不合法
 -1 用户名不合法
 -2 包含要允许注册的词语
 -3 用户名已经存在
 其它 未知错误

登录密码规则
密码由手机号和秘钥组合而成
秘钥为urouter
以15812345678为例
1. 15812345678urouter进行md5加密【e263b07a6d52ee2e2cfff97d510c4ffe】
2. 取前8位+urouter再次md5加密【a45fc8a8ca1b943b095347fd66ed3178】
3. 取前11位则为该手机号的密码【a45fc8a8ca1】
 * @author Edmond
 *
 */
public class ExchangeBBSHelper {
	private static String Security_Raw_Template = "%surouter";
	
	public static String bbsPwdGen(String mobileno){
		String rawString = String.format(Security_Raw_Template, mobileno);
		
		String firstmd5 = DigestHelper.md5ToHex(rawString);
		String secondmd5 = DigestHelper.md5ToHex(String.format(Security_Raw_Template, firstmd5.substring(0,8)));
		return secondmd5.substring(0,11);
	}
	
	private static String regToken="bhuwifiandurouter";
	private static String ExchangeBBSUrl = "http://bbs.bhuwifi.com/urouter_server_add_user.php";
	private static String Field_Username = "username";
	private static String Field_RegToken = "regTocken";
	public static int userAdd2BBS(String mobileno){
		//username=手机号码&regTocken=bhuwifiandurouter
		Map<String,String> params = new HashMap<String,String>();
		params.put(Field_Username, mobileno);
		params.put(Field_RegToken, regToken);
    	try {
			String response = HttpHelper.postUrlAsString(ExchangeBBSUrl, params, null, StringHelper.CHATSET_UTF8);
			ResultDTO result = JsonHelper.getDTO(response, ResultDTO.class);
			return result.getRcode();
		} catch (Exception e) {
			e.printStackTrace();
			return -4;
		}
	}
	public static class ResultDTO{
		private int rcode;
		public int getRcode() {
			return rcode;
		}
		public void setRcode(int rcode) {
			this.rcode = rcode;
		}
	}
	public static void main(String[] argv){
		System.out.println(bbsPwdGen("15812345678"));
		System.out.println(userAdd2BBS("15812345678"));
		System.out.println(userAdd2BBS("18612272826"));
	}
}
