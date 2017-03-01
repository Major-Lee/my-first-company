package com.bhu.vas.api.rpc.thirdparty.helper;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;

import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.encrypt.CryptoHelper;
import com.smartwork.msip.cores.helper.encrypt.MD5Helper;

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
	
	public static void main(String[] args) throws UnsupportedEncodingException{
		byte[] xx = 
				MD5Helper.md5(BusinessRuntimeConfiguration.BhuToGomeAppKey).substring(0, 16).getBytes();
		System.out.println("key:" + MD5Helper.md5(BusinessRuntimeConfiguration.BhuToGomeAppKey).substring(0, 16));
//		String id = CryptoHelper.aesEncryptToHex("62:68:75:af:00:1c", xx);
//		System.out.println(MD5Helper.md5(BusinessRuntimeConfiguration.GomeToBhuAppKey).substring(0, 16));
//		System.out.println(id);
//		http://61.149.47.34:8082?thirdCloudId=bhu-gome-0001&timestamp=20170223142638&nonce=&sign=8e44eaaf51726e480ad8a5500840d49ab2e06db5ffedcfb41591d84ac0283720
			
//		System.out.println(GomeParam.getSign("/device/status/get", "20170223142638", "lxiiz8vvab", BusinessRuntimeConfiguration.GomeToBhuAppKey,
//				"{\"deviceId\":\"c0b3a189d60a91167c6e241b9f3dc45c7b46b7a78a234a87a60d3f27611db694\",\"gid\":\"bffrfg\",\"isOwner\":true,\"thirdUId\":\"381756d0fa31be8b91bf6856edba73a6f2899330e339c5df59b03a55f1840c4c80d49ab205ecf8cbf4ed835231b829a5\"}"));
		System.out.println(GomeParam.getSign(null, "20170301182545", "cT2rrGSzyO", "055a8f416acb40d3b3d6e38070e8a9e0", 
				"{\"gid\":\"bffrfg\",\"deviceId\":\"6c78d2bae4aec24e02599626b8e5f198d6061bed9d9702359dfc6d10fb7c6a65\",\"online\":\"0\",\"time\":\"2017-03-01 18:25:45\"}"));
	}
}
