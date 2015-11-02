package com.bhu.vas.web.commons;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Controller
public class CommonController extends BaseController{

	@ResponseBody()
	@RequestMapping(value = {"/commons/{commonCode}"},method={RequestMethod.POST})
	public void commons(HttpServletRequest request,
			HttpServletResponse response, 
			@PathVariable String commonCode) {
		System.out.println("-------------------------------commons : " + commonCode);
		try {
		loggingWarn(commonCode, request);
		if(StringUtils.isNotEmpty(commonCode)){
			if(commonCode.equals("404")){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_404_ERROR));
				return;
			}
			if(commonCode.equals("403")){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_403_ERROR));
				return;
			}
			if(commonCode.equals("500")){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_500_ERROR));
				return;
			}
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_UNKNOW_ERROR));
		}else
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.REQUEST_UNKNOW_ERROR));

		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	

}
