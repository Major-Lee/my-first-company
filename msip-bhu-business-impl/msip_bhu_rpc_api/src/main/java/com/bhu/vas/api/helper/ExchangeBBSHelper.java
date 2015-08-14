package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;

import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.encrypt.DigestHelper;

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
