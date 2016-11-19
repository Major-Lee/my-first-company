package com.bhu.vas.api.rpc.message.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.tls.sigcheck.tls_sigcheck;

public class MessageTimHelper {
	public static final String sdkAppid = "1400018565";
	public static final String manager = "guoxf";
	public static final String timLibPath = "/BHUData/apps/tim_msg_lib/jnisigcheck_mt_x64.so";
	public static final String privateKeyPath = "/BHUData/apps/tim_msg_lib/private_key";
	public static String priKeyContent = null;
	public static String visitorExpire = "604800";
	public static String defaultExpire = "315360000";
	static{
		try {
			priKeyContent = getTimKey(privateKeyPath);
		} catch (Exception e) {
			System.out.println("getTimKey error");
			e.printStackTrace();
		}
	}
	
	public static String createUserSig(String user, String expire){
		tls_sigcheck tls = new tls_sigcheck();
		tls.loadJniLib(timLibPath);
		int result = tls.tls_gen_signature_ex2_with_expire(sdkAppid, user, priKeyContent, expire);
		if (result != 0){
			return null;
		}
		return tls.getSig();
	}
	
	private static String getTimKey(String filePath) throws Exception{
		File priKeyFile = new File(filePath);
        StringBuilder strBuilder = new StringBuilder();
        String s = "";
        BufferedReader br = new BufferedReader(new FileReader(priKeyFile));
        while ((s = br.readLine()) != null) {
            strBuilder.append(s + '\n');
        }
        br.close();
		return strBuilder.toString();
	}
	
}
