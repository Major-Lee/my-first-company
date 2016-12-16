package com.bhu.vas.web.commdity;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.commdity.OrderVideoVTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.bhu.vas.api.rpc.user.iservice.IUserCaptchaCodeRpcService;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
@Controller
@RequestMapping("/order/video")
public class OrderVideoController extends BaseController{
	@Resource
	private IUserCaptchaCodeRpcService userCaptchaCodeRpcService;
	
	@Resource
	private IOrderRpcService orderRpcService;
	
	/**
	 * 获取视频认证订单id
	 * 
	 */
	@ResponseBody()
	@RequestMapping(value="/get_orderid",method={RequestMethod.POST})
	public void video_get_orderid(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false, defaultValue = "12") Integer commdityid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String umac,
			@RequestParam(required = false) String context,
			@RequestParam(required = false, defaultValue = "0") Integer channel,
			@RequestParam(required = false, defaultValue = "2") Integer umactype
			) {
		String user_agent = request.getHeader("User-Agent");
		RpcResponseDTO<OrderVideoVTO> orderRpcResult = orderRpcService.createVideoOrder(commdityid,mac, umac, 
					umactype, context, channel, user_agent);
		if(!orderRpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(orderRpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(orderRpcResult));
		}
	}
	/**
	 * 视频放行接口
	 * @param response
	 * @param mac 设备mac
	 * @param umac 终端mac
	 * @param umactype 用户类型
	 */
	@ResponseBody()
	@RequestMapping(value="/authorize",method={RequestMethod.POST})
	public void video_authorize(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String token,
			@RequestParam(required = false) String context
			) {
		RpcResponseDTO<Boolean> RpcResult = orderRpcService.authorizeVideoOrder(token,context);
		
		if(!RpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(RpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResult));
		}
	}
	/**
	 * 点击免费上网
	 * @param request
	 * @param response
	 * @param commdityid
	 * @param mac
	 * @param umac
	 * @param channel
	 * @param umactype
	 */
	@ResponseBody()
	@RequestMapping(value="/click_authorize",method={RequestMethod.POST})
	public void video_click_authorize(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false, defaultValue = "21") Integer commdityid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String umac,
			@RequestParam(required = false, defaultValue = "0") Integer channel,
			@RequestParam(required = false, defaultValue = "2") Integer umactype
			) {
		String user_agent = request.getHeader("User-Agent");
		
		RpcResponseDTO<OrderVideoVTO> orderRpcResult = orderRpcService.clickAuthorize(commdityid, mac, umac, 
					umactype, channel, user_agent);
		if(!orderRpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(orderRpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(orderRpcResult));
		}
	}
}
