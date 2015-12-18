package com.bhu.vas.web.user;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.qiniu.CurrentKey;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.rs.PutPolicy;
import com.smartwork.msip.cores.helper.JsonHelper;
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
	static{
		Config.ACCESS_KEY = "p6XNq4joNqiFtqJ9EFWdyvnZ6ZBnuwISxvVGdHZg";
		Config.SECRET_KEY = "edcDVKq1YESjRCk_h5aBx2jqb-rtmcrmwBEBH8-z";
	    mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
	    bucketNameSupported.add("applogs4bhu");
	    bucketNameSupported.add("appavatar4bhu");
	}
	
	private static final String avatar_suffix_name = ".jpeg";
	@ResponseBody()
	@RequestMapping(value="/fetch",method={RequestMethod.POST})
	public void fetch(
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true,value="b") String bucketName) {
        try {
        	if(!bucketNameSupported.contains(bucketName)){
        		SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"bucketName",bucketName}));
        		return;
        	}
    		CurrentKey ckey = new CurrentKey();
            PutPolicy putPolicy = new PutPolicy(bucketName.concat(StringHelper.COLON_STRING_GAP).concat(avatar_suffix_name));
            //单位秒
            putPolicy.expires = 24*60*60*365;//默认一小时 ，所以*24*265为一年
			String uptoken = putPolicy.token(mac);
			ckey.setBn(bucketName);
			ckey.setUt(uptoken);
			System.out.println(JsonHelper.getJSONString(ckey));
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ckey));
		} catch (AuthException e) {
			e.printStackTrace(System.out);
			SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
		} catch (JSONException e) {
			e.printStackTrace(System.out);
			SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
		}

	}
}
