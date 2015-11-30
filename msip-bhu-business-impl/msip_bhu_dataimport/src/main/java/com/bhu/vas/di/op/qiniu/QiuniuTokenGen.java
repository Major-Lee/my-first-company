package com.bhu.vas.di.op.qiniu;

import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.rs.PutPolicy;

public class QiuniuTokenGen {
	 public static void main(String[] args) throws Exception {
	        Config.ACCESS_KEY = "p6XNq4joNqiFtqJ9EFWdyvnZ6ZBnuwISxvVGdHZg";
	        Config.SECRET_KEY = "edcDVKq1YESjRCk_h5aBx2jqb-rtmcrmwBEBH8-z";
	        Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
	        // 请确保该bucket已经存在
	        String bucketName = "applogs4bhu";
	        PutPolicy putPolicy = new PutPolicy(bucketName);
	        String uptoken = putPolicy.token(mac);
	        
	        System.out.println("uptoken:"+uptoken);
	        //p6XNq4joNqiFtqJ9EFWdyvnZ6ZBnuwISxvVGdHZg:t6tWHn3NWL1zj_e9wkFnBp-k_bo=:eyJzY29wZSI6ImFwcGxvZ3M0Ymh1IiwiZGVhZGxpbmUiOjE0NDg4NTg0MTR9
	 }
}
