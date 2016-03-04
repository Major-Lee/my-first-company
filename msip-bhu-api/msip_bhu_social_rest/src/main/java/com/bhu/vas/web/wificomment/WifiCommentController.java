package com.bhu.vas.web.wificomment;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;

public class WifiCommentController extends BaseController {
	
	@Resource
	private ISocialRpcService socialRpcService;
	
	
	/**
	 * 评论wifi
	 * @param response
	 * @param bssid
	 * @param uid
	 * @param message
	 */
	@ResponseBody()
	@RequestMapping(value="/app/wifi/comment",method={RequestMethod.POST})
	public void detail(
			HttpServletResponse response,
			 @RequestParam(required = true, value = "bssid") String bssid,
             @RequestParam(required = true, value = "uid") long uid,
             @RequestParam(required = true, value = "message") String message
			) {
		try{
			
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}

	}
}