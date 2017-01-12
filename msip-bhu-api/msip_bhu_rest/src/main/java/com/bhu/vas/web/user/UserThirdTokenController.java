package com.bhu.vas.web.user;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.qiniu.CurrentKey;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.business.yun.YunConstant;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.rs.PutPolicy;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

/**
 * 七牛动态token生成
 * 缺省设置过期时间为1年 24*60*60*365 秒
 * @author Edmond
 *
 */
@Controller
@RequestMapping("/user/token")
public class UserThirdTokenController extends BaseController{
	private static Mac mac = null;
	private static Set<String> bucketNameSupported = new HashSet<String>();
	
	private static final String bucketName_Avatar = "appavatar4bhu";
	private static final String bucketName_Cloud_Controller = "cloudcontroller";
	private static final String bucketName_Avatar_Test = "test";
	private static final String bucketName_Log = "applogs4bhu";
	private static final String domain_test = YunConstant.QU_BUCKET_TEST_DOMAIN;
	private static final String domain_cloudcontroller = YunConstant.QU_BUCKET_CLOUD_CONTROLLER_DOMAIN;
	private static final String avatar_suffix_name = ".jpeg";
	private static final String log_suffix_name = ".log";
	
	static{
		Config.ACCESS_KEY = YunConstant.QN_ACCESS_KEY;//"p6XNq4joNqiFtqJ9EFWdyvnZ6ZBnuwISxvVGdHZg";
		Config.SECRET_KEY = YunConstant.QN_SECRET_KEY;//"edcDVKq1YESjRCk_h5aBx2jqb-rtmcrmwBEBH8-z";
	    mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
	    bucketNameSupported.add(bucketName_Avatar);
	    bucketNameSupported.add(bucketName_Avatar_Test);
	    bucketNameSupported.add(bucketName_Log);
	    bucketNameSupported.add(bucketName_Cloud_Controller);
	}
	
	/**
	 * 
	 * @param response
	 * @param uid
	 * @param upd 是否是需要更新
	 * @param bucketName
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch",method={RequestMethod.POST})
	public void fetch(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,defaultValue = "true") boolean upd,
			@RequestParam(required = true,value="b") String bucketName) {
        try {
        	if(!bucketNameSupported.contains(bucketName)){
        		SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"bucketName",bucketName}, BusinessWebHelper.getLocale(request)));
        		return;
        	}
        	PutPolicy putPolicy = null;
        	CurrentKey ckey = new CurrentKey();
        	if(upd){
        		String fid = null;//
            	if(bucketName_Log.equals(bucketName)){
            		fid = uid.toString().concat(log_suffix_name);
            	}else{
            		fid = uid.toString().concat(avatar_suffix_name);
            	}
            	putPolicy = new PutPolicy(bucketName.concat(StringHelper.COLON_STRING_GAP).concat(fid));
            	ckey.setFid(fid);
        	}else{
            	putPolicy = new PutPolicy(bucketName);
        	}
            //单位秒
            putPolicy.expires = 24*60*60*365;//默认一小时 ，所以*24*265为一年
			String uptoken = putPolicy.token(mac);
			ckey.setBn(bucketName);
			ckey.setUt(uptoken);
			if(bucketName.equals(bucketName_Avatar_Test)){
				ckey.setDomain(domain_test);
			}else if(bucketName.equals(bucketName_Cloud_Controller)){
				ckey.setDomain(domain_cloudcontroller);
			}
			//System.out.println(JsonHelper.getJSONString(ckey));
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ckey));
		} catch (AuthException e) {
			e.printStackTrace(System.out);
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		} catch (JSONException e) {
			e.printStackTrace(System.out);
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
	}
}
