package com.whisper.web.handset;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
import com.whisper.api.feedback.model.HandsetFeedback;
import com.whisper.business.feedback.service.HandsetFeedbackService;
import com.whisper.msip.cores.web.mvc.spring.BaseController;
import com.whisper.msip.cores.web.mvc.spring.helper.SpringMVCHelper;

@Controller
@RequestMapping("/handset/feedback")
public class HandsetFeedbackController extends BaseController{
	
	@Resource
	private HandsetFeedbackService handsetFeedbackService;
	/**
	 * 提交异常状态线索接口
	 * @param request
	 * @param response
	 * @param uid
	 * @param device
	 * @param device_detail
	 * @param feedback_ts
	 * @param clue
	 */
	@ResponseBody()
	@RequestMapping(value="/post",method={RequestMethod.GET,RequestMethod.POST})
	public void post(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false, value="d",defaultValue="R") String device,
			@RequestParam(required = false, value="dd") String device_detail,
			@RequestParam(required = false, value="pv") String pv,
			@RequestParam(required = true) String clue) {
		try{
			HandsetFeedback entity = new HandsetFeedback();
			entity.setUid(uid);
			entity.setDevice(device);
			entity.setDevice_detail(device_detail);
			entity.setPv(pv);
			entity.setClue(clue);
			handsetFeedbackService.insert(entity);
			SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
}
