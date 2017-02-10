package com.bhu.vas.api.rpc.thirdparty.helper;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.cores.helper.encrypt.CryptoHelper;

public class GomeParam {

	public static final String GomeRequestParam_Timestamp = "timestamp";
	public static final String GomeRequestParam_Nonce = "nonce";
	public static final String GomeRequestParam_Sign = "sign";
	public static final String GomeRequestParam_Appid = "appId";
	

	public static String getSign(String uri, String tm, String nonce, String appkey, String body){
		StringBuffer sb = new StringBuffer();
		if(StringUtils.isNotEmpty(uri))
			sb.append(uri);
		sb.append(tm).append(nonce).append(appkey);
		if(body != null)
			sb.append(body);
		String oraStr = sb.toString();
		return CryptoHelper.sha256(oraStr);
	}
	
}
