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
	public static final String managerSig = "eJxljstOg0AARfd8BZltjQ7zqNTEBSWYmvpKhVrcE"
			+ "JxHO7QyEzogxvjvttjESbzbc27u-fJ83wfp3fN5yZhua1vYTyOAf*UDCM7*oDGKF6UtcMP-"
			+ "QdEb1YiilFY0AwwopQhC11Fc1FZJdTLWre6lg-d8Wwwbv31yKAchHVNXUesB3idZfJuUmPB"
			+ "ZRPNdZ6qpSbVktlq2vVxd5CNWtSnZbF7nixy*wUglkZ080ulkJTMzf-iAWUer5TZGu9moZi"
			+ "SKX7Bd6BCp7OkmJNfOpFXv4nQohAHB4fjSoZ1o9krXg4BgQAOE4THA*-Z*ABmDXJo_eJxlz"
			+ "11rgzAUBuB7f0Xw1jESY1ztnXNutdugXbuCuwlBkxisH7XRKWP-fSUUKuzcPu-hPefHAgDY"
			+ "*7fdPcuypq811VPLbbAENrTvbti2KqdMU9zl-5CPreo4ZULzziBGBPvwMrOQynmtlVDXiOy"
			+ "bUcz4nJfUlBhF3mUZLYhP5hElDb7HaZRso4mzSq2zaSVCf08KXCargzw66QfrP08*kQ-DU1"
			+ "NGm2cyfCdFuE43Azo*bl-l4HxVpB*d9qBlHModdgvBIhGok*-FTfWSzSq1qvj1oAVEHg5gM"
			+ "NOBd2fV1CbgQkSQi6H52vq1-gC6nl3d";
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
	
	public static final String Tim_Url = "https://console.tim.qq.com/v4";
	public static final String Account_Manage = "/im_open_login_svc";
	public static final String OpenIM = "/openim";
	public static final String Action_Account_Import = "/account_import";
	public static final String Action_MUL_Import = "/multiaccount_import";
	public static final String Action_IM_Push = "/im_push";
	public static final String Action_Add_Tag = "/im_add_tag";
	public static final String Action_Get_Tag = "/im_get_tag";
	public static final String Action_Remove_Tag = "/im_remove_tag";
	public static final String Action_Remove_All_Tags = "/im_remove_all_tags";
	
	
	
}
