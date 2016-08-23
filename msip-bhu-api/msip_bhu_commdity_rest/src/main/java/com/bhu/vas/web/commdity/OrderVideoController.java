package com.bhu.vas.web.commdity;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.commdity.OrderSMSVTO;
import com.bhu.vas.api.dto.commdity.OrderVideoVTO;
import com.bhu.vas.api.helper.BusinessEnumType.CaptchaCodeActType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.bhu.vas.api.rpc.user.iservice.IUserCaptchaCodeRpcService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.web.business.validate.ValidateService;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;
@Controller
@RequestMapping("/order/video")
public class OrderVideoController extends BaseController{
	private final static Logger logger = LoggerFactory.getLogger(OrderVideoController.class);
	
	@Resource
	private IUserCaptchaCodeRpcService userCaptchaCodeRpcService;
	
	@Resource
	private IOrderRpcService orderRpcService;
	
	
	/**
	 * 视频放行接口
	 * @param response
	 * @param mac 设备mac
	 * @param umac 终端mac
	 * @param umactype 用户类型
	 */
	@ResponseBody()
	@RequestMapping(value="/validate_video",method={RequestMethod.POST})
	public void validate_captcha(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String umac,
			@RequestParam(required = false) String context,
			@RequestParam(required = false, defaultValue = "2") Integer umactype
			) {
			ValidateService.validateDeviceMac(mac);
			String user_agent = request.getHeader("User-Agent");
			RpcResponseDTO<OrderVideoVTO> orderRpcResult = orderRpcService.createVideoOrder(mac, umac, 
							umactype, context, user_agent);
			
			if(!orderRpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(orderRpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(orderRpcResult));
			}
	}
}
