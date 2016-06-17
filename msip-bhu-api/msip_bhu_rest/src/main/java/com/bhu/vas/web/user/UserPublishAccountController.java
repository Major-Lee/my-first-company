package com.bhu.vas.web.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserPublishAccountRpcService;
import com.bhu.vas.api.vto.publishAccount.UserPublishAccountDetailVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping(value = "/publicAccount")
public class UserPublishAccountController extends BaseController{
	private static Logger log = LoggerFactory.getLogger(UserPublishAccountController.class);
	@Resource
	private IUserPublishAccountRpcService userPublishAccountRpcService;

	@ResponseBody()
	@RequestMapping(value="/addPublishAccount", method={RequestMethod.GET,RequestMethod.POST})
	public void addPublishAccount(
			HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) int uid,
			@RequestParam(required = true) String companyName,
			@RequestParam(required = true) String business_license_number,
			@RequestParam(required = true) String business_license_address,
			@RequestParam(required = true) String address,
			@RequestParam(required = true) String mobile,
			@RequestParam(required = true) String business_license_pic,
			@RequestParam(required = true) String account_name,
			@RequestParam(required = true) String publish_account_number,
			@RequestParam(required = true) String opening_bank,
			@RequestParam(required = true) String city,
			@RequestParam(required = true) String bank_branch_name
			){
		log.info(String.format(
                "addPublishAccount uid[%s] companyName[%s] business_license_number[%s] business_license_address[%s] address[%s] mobile[%s] business_license_pic[%s]  account_name[%s] publish_account_number[%s] opening_bank[%s] city[%s] bank_branch_name[%s]",
                uid, companyName, business_license_number, business_license_address, address, mobile, business_license_pic,account_name, publish_account_number, opening_bank, city, bank_branch_name));
		try{
			RpcResponseDTO<UserPublishAccountDetailVTO> rpcResult = userPublishAccountRpcService.createUserPublishAccount(uid, companyName, business_license_number, business_license_address, address, mobile, business_license_pic, account_name, publish_account_number, opening_bank, city, bank_branch_name);
					
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
			//SpringMVCHelper.renderJson(response, ex.getMessage());
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/publicAccountdetail", method={RequestMethod.GET,RequestMethod.POST})
	public void publicAccountdetail(
			HttpServletResponse response, 
			@RequestParam(required=true) Integer uid){
		log.info(String.format("publicAccountdetail uid[%s] ",uid));
		try{
			RpcResponseDTO<UserPublishAccountDetailVTO> rpcResult = userPublishAccountRpcService.queryUserPublishAccountDetail(uid);
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
