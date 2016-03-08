package com.bhu.vas.web.user;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.ThirdpartiesPaymentDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping(value = "/account/pay")
public class UserThirdpartiesPaymentController extends BaseController{
	
	@Resource
	private IUserWalletRpcService userWalletRpcService;

	@ResponseBody()
	@RequestMapping(value="/fetch_thirdpartiespayments", method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_thirdpartiespayments(HttpServletResponse response, 
			@RequestParam(required=true) Integer uid){
		try{
			RpcResponseDTO<List<ThirdpartiesPaymentDTO>> rpcResult = userWalletRpcService.fetchUserThirdpartiesPayments(uid);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/remove", method={RequestMethod.GET,RequestMethod.POST})
	public void remove(
			HttpServletResponse response, 
			@RequestParam(required=true) Integer uid,
			@RequestParam(required=true) String payment_type){
		try{
			RpcResponseDTO<Boolean> rpcResult = userWalletRpcService.removeUserThirdpartiesPayment(uid, paymode);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/create", method={RequestMethod.GET,RequestMethod.POST})
	public void create(
			HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestParam(required=true) Integer uid,
			@RequestParam(required=true) String payment_type,
			@RequestParam(required=true) String id,
			@RequestParam(required = false) String name
			){
		try{
			RpcResponseDTO<List<ThirdpartiesPaymentDTO>> rpcResult = userWalletRpcService.createUserThirdpartiesPayment(uid, paymode, id, name);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}

}
