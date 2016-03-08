package com.bhu.vas.web.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.msip.cores.web.mvc.WebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping(value = "/account")
public class UserWalletController extends BaseController{
	
	@Resource
	private IUserWalletRpcService userWalletRpcService;

	@ResponseBody()
	@RequestMapping(value="/wallet/withdraw", method={RequestMethod.GET,RequestMethod.POST})
	public void walletWithdraw(
			HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) int uid,
			@RequestParam(required = true) int appid,
			@RequestParam(required = false) String payment_type,
			@RequestParam(required = false) String pwd,
			@RequestParam(required = true) double cash
			){
		try{
			if(cash <= 0){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_RANGE_ERROR));
			}
			String remoteIp = WebHelper.getRemoteAddr(request);
			RpcResponseDTO<UserWithdrawApplyVTO> rpcResult = userWalletRpcService.withdrawOper(appid,payment_type,uid, pwd, cash, remoteIp);
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
	@RequestMapping(value="/wallet/detail", method={RequestMethod.GET,RequestMethod.POST})
	public void walletDetail(
			HttpServletResponse response, 
			@RequestParam(required=true) Integer uid){
		try{
			RpcResponseDTO<UserWalletDetailVTO> rpcResult = userWalletRpcService.walletDetail(uid);
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
	@RequestMapping(value="/wallet/pwdset", method={RequestMethod.GET,RequestMethod.POST})
	public void withdrawPwdSet(
			HttpServletResponse response, 
			@RequestParam(required=true) Integer uid,
			@RequestParam(required=true) String pwd){
		try{
			RpcResponseDTO<Boolean> rpcResult = userWalletRpcService.withdrawPwdSet(uid, pwd);
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
	@RequestMapping(value="/wallet/pwdupd", method={RequestMethod.GET,RequestMethod.POST})
	public void withdrawPwdSet(
			HttpServletResponse response, 
			@RequestParam(required=true) Integer uid,
			@RequestParam(required=true) String pwd,
			@RequestParam(required=true) String npwd){
		try{
			RpcResponseDTO<Boolean> rpcResult = userWalletRpcService.withdrawPwdUpd(uid, pwd, npwd);
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
