package com.whisper.web.handset;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.cores.plugins.filterhelper.StringHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.whisper.api.release.dto.HandsetReleaseDTO;
import com.whisper.api.release.model.HandsetRelease;
import com.whisper.api.release.model.HandsetReleasePK;
import com.whisper.api.user.model.DeviceEnum;
import com.whisper.business.release.service.HandsetReleaseService;
import com.whisper.msip.cores.web.mvc.spring.BaseController;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;

@Controller
@RequestMapping("/handset/release")
public class HandsetReleaseController extends BaseController{
	
	@Resource
	private HandsetReleaseService handsetReleaseService;
	
	private final String Common_Ios_Channelid = "appstore";
	
	@ResponseBody()
	@RequestMapping(value="/ios",method={RequestMethod.GET,RequestMethod.POST})
	public void ios(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false) String channelid) {
		try{
			if(StringHelper.isEmpty(channelid)){
				channelid = Common_Ios_Channelid;
			}
			
			HandsetReleasePK pk = new HandsetReleasePK(channelid,DeviceEnum.HandSet_IOS_Type);
			HandsetRelease handsetRelease = handsetReleaseService.getById(pk);
			if(handsetRelease == null){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST));
				return;
			}
			HandsetReleaseDTO handsetReleaseDto = handsetRelease.getInnerModel();
			if(handsetReleaseDto == null){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST));
				return;
			}
			
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(handsetReleaseDto));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/android",method={RequestMethod.GET,RequestMethod.POST})
	public void android(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String channelid) {
		try{
			HandsetReleasePK pk = new HandsetReleasePK(channelid,DeviceEnum.HandSet_ANDROID_Type);
			HandsetRelease handsetRelease = handsetReleaseService.getById(pk);
			if(handsetRelease == null){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST));
				return;
			}
			HandsetReleaseDTO handsetReleaseDto = handsetRelease.getInnerModel();
			if(handsetReleaseDto == null){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_NOTEXIST));
				return;
			}
			
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(handsetReleaseDto));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
}
