package com.bhu.vas.web.console;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/user")
public class ConsoleUserController extends BaseController {

	@Resource
	private IUserRpcService userRpcService;
    
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
    @RequestMapping(value = "/queryUserList", method = {RequestMethod.POST})
    public void queryUserList(
    		 HttpServletRequest request,
             HttpServletResponse response,
             @RequestParam(required = false) String mobileNo,
             @RequestParam(required = false) String userType,
             @RequestParam(required = false) String regdevice,
 			 @RequestParam(required = false) String boundEquNum,
 			 @RequestParam(required  = false) String createTime,
 			 @RequestParam(required  = false) String isCashBack,
 			 @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
			 @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
    		){
    	
    	
    }
}
