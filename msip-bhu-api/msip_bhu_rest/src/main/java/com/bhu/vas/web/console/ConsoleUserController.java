package com.bhu.vas.web.console;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.dto.UserIncomeDTO;
import com.bhu.vas.api.rpc.user.dto.UserManageDTO;
import com.bhu.vas.api.rpc.user.dto.UserManageDeviceDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserManageRpcService;
import com.bhu.vas.api.rpc.user.iservice.IUserRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/user")
public class ConsoleUserController extends BaseController {

	@Resource
	private IUserRpcService userRpcService;
	
	@Resource
	private IUserManageRpcService userMangeRpcService;
    
    @ResponseBody()
    @RequestMapping(value = "/pages", method = {RequestMethod.POST})
    public void pages(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false) String ut,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
            ) {
    	RpcResponseDTO<TailPage<UserDTO>> rpcResult = userRpcService.pageUsers(uid, ut, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    
    @ResponseBody()
    @RequestMapping(value = "/userManage/queryUserList", method = {RequestMethod.POST})
    public void queryUserList(
    		 HttpServletRequest request,
             HttpServletResponse response,
             @RequestParam(required = false) String mobileNo,
             @RequestParam(required = false) String userType,
             @RequestParam(required = false) String regdevice,
 			 @RequestParam(required = false) String boundEquNum,
 			 @RequestParam(required  = false) String createStartTime,
 			 @RequestParam(required  = false) String createEndTime,
 			 @RequestParam(required  = false) String isCashBack,
 			 @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
			 @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
    		){
    	Map<String,Object> map = new HashMap<String,Object>();
    	if(StringUtils.isNotBlank(mobileNo)){
    		map.put("mobileNo", mobileNo);
    	}
    	if(StringUtils.isNotBlank(userType)){
    		map.put("userType", userType);
    	}
    	if(StringUtils.isNotBlank(regdevice)){
    		map.put("regdevice", regdevice);
    	}
    	if(StringUtils.isNotBlank(boundEquNum)){
    		map.put("boundEquNum", boundEquNum);
    	}
    	if(StringUtils.isNotBlank(boundEquNum)){
    		map.put("boundEquNum", boundEquNum);
    	}
    	if(StringUtils.isNotBlank(createStartTime) && StringUtils.isNotBlank(createEndTime)){
    		map.put("createStartTime", createStartTime);
    		map.put("createEndTime", createEndTime);
    	}
    	if(StringUtils.isNotBlank(isCashBack)){
    		map.put("isCashBack", isCashBack);
    	}
    	map.put("pageNo", pageNo);
    	map.put("pageSize", pageSize);
    	System.out.println("****map的值为：【"+map.size()+"】****");
    	RpcResponseDTO<TailPage<UserManageDTO>> rpcResult = userRpcService.pageQueryUserList(map);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    /**
     * 查询用户交易信息【用户管理功能】
     * @param request
     * @param response
     * @param uid
     * @param transmode
     * @param transtype
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/userManage/queryUserIncomeDetail", method = {RequestMethod.POST})
    public void queryUserIncomeDetail(
    		HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false,defaultValue = "") String transmode,
            @RequestParam(required = false,defaultValue = "") String transtype,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
            ){
    	ResponseError validateError = ValidateService.validatePageSize(pageSize);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<TailPage<UserIncomeDTO>> rpcResult = userMangeRpcService.queryUserIncomeDetail(uid,transtype,transmode,pageNo,pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    /**
     * 查询用户设备信息【用户管理功能】
     * @param request
     * @param response
     * @param uid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/userManage/queryUserDeviceInfo", method = {RequestMethod.POST})
    public void queryUserDeviceInfo(
    		HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
            ){
    	ResponseError validateError = ValidateService.validatePageSize(pageSize);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
    	RpcResponseDTO<TailPage<UserManageDeviceDTO>> rpcResult = userMangeRpcService.queryUserDeviceInfo(uid,pageNo,pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    /**
     * 查询用户详细信息【用户管理功能】
     * @param request
     * @param response
     * @param uid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/userManage/queryUserDetail", method = {RequestMethod.POST})
    public void queryUserDetail(
    		HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid
            ){
    	RpcResponseDTO<UserManageDTO> rpcResult = userRpcService.queryUserDetail(uid);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
}
