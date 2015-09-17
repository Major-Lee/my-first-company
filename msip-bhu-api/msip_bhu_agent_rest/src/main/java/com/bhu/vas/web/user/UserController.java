package com.bhu.vas.web.user;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.agent.iservice.IAgentUserRpcService;
import com.bhu.vas.api.rpc.agent.vto.AgentUserDetailVTO;
import com.bhu.vas.api.rpc.user.dto.UserTokenDTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.WebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.Response;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/account")
public class UserController extends BaseController{
	@Resource
	private IAgentUserRpcService agentUserRpcService;

	/**
	 * 用户账号创建
	 * 1、支持用户快速直接注册
	 * 2、支持输入accemail或者mobileno 和密码注册
	 * 3、支持注册是填写 sex，lang，region
	 * @param request
	 * @param response
	 * @param deviceuuid 设备uuid
	 * @param acc 登录帐号指email或者mobileno
	 * @param nick 昵称
	 * @param pwd 密码 在acc不为空的情况下 pwd必须不为空
	 * @param lang 语言
	 * @param region 区域
	 * @param device 设备类型
	 * @param itoken 注册邀请码
	 * @param token  渠道邀请码
	 * 
	 */
	@ResponseBody()
	@RequestMapping(value="/create",method={RequestMethod.GET,RequestMethod.POST})
	public void create(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
			@RequestParam(required = false) String nick,
			@RequestParam(required = true) String pwd,
			@RequestParam(required = false) String sex,
			@RequestParam(required = false) String org,
			@RequestParam(required = false) String addr1,
			@RequestParam(required = false) String addr2,
			@RequestParam(required = false) String memo,
			@RequestParam(required = false, value="d",defaultValue="P") String device//,
			) {
		//step 1.deviceuuid 验证
		ResponseError validateError = null;
		String remoteIp = WebHelper.getRemoteAddr(request);
		String from_device = DeviceEnum.getBySName(device).getSname();
		try{
			//step 2.手机号正则验证及手机是否存在验证
			validateError = ValidateService.validateMobileno(countrycode,acc);
			if(validateError != null){
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}
			System.out.println(countrycode+" "+acc+" "+pwd+" "+nick+" "+sex+" "+from_device+" "+remoteIp);
			RpcResponseDTO<Map<String, Object>> rpcResult = agentUserRpcService.createNewUser(countrycode, acc,pwd, nick, sex,
					org,
					addr1,
					addr2,
					memo,
					from_device, remoteIp);
			if(rpcResult.getErrorCode() == null){
				UserTokenDTO tokenDto =UserTokenDTO.class.cast(rpcResult.getPayload().get(RpcResponseDTOBuilder.Key_UserToken));
				rpcResult.getPayload().remove(RpcResponseDTOBuilder.Key_UserToken);
				BusinessWebHelper.setCustomizeHeader(response, tokenDto.getAtoken(),tokenDto.getRtoken());
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
			}
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/check_mobileno",method={RequestMethod.POST})
	public void check_unique(
			HttpServletResponse response,
			//@RequestParam(required = true,value="t") int type,
			@RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
			@RequestParam(required = true) String acc,
            @RequestParam(required = false) String oldacc) {
		try{
			if (acc == null || acc.equals(oldacc)) {
				SpringMVCHelper.renderJson(response, Response.SUCCESS);//renderHtml(response, html, headers)
				return;
			}
			ResponseError validateError = ValidateService.validateMobileno(countrycode,acc);
			if(validateError != null){//本地正则验证
				SpringMVCHelper.renderJson(response, validateError);
				return;
			}else{
				RpcResponseDTO<Boolean> checkAcc = agentUserRpcService.checkAcc(countrycode, acc);
				if(checkAcc.getErrorCode() == null)
					SpringMVCHelper.renderJson(response, Response.SUCCESS);
				else
					SpringMVCHelper.renderJson(response, ResponseError.embed(checkAcc.getErrorCode()));
				return;
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/pages",method={RequestMethod.POST})
	public void pages(
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,value = "k") String keywords,
			@RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
			) {
		try{
			RpcResponseDTO<TailPage<AgentUserDetailVTO>> rpcResult = agentUserRpcService.pageAgentUsers(uid,keywords, pageNo, pageSize);
			if(!rpcResult.hasError())
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			else
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/detail",method={RequestMethod.POST})
	public void detail(
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid
			) {
		try{
			RpcResponseDTO<AgentUserDetailVTO> rpcResult = agentUserRpcService.userDetail(uid);
			if(!rpcResult.hasError())
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			else
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/modify",method={RequestMethod.POST})
	public void modify(
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) String nick,
			@RequestParam(required = false) String org,
			@RequestParam(required = false) String addr1,
			@RequestParam(required = false) String addr2,
			@RequestParam(required = false) String memo
			) {
		try{
			RpcResponseDTO<AgentUserDetailVTO> rpcResult = agentUserRpcService.userModify(uid, nick, org, addr1, addr2, memo);
			if(!rpcResult.hasError())
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			else
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
}
