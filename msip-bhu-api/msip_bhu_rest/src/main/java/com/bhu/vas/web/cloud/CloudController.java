package com.bhu.vas.web.cloud;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceCloudDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserDeviceRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

/**
 * cloud rest 
 */
@Controller
@RequestMapping("/cloud")
public class CloudController extends BaseController {

    @Resource
    private IUserDeviceRpcService userDeviceRpcService;

    /**
     * 根据用户id 获取绑定的设备列表
     * @param response
     * @param uid
     * @param dut
     */
    @ResponseBody()
    @RequestMapping(value="/query/uid/device/pages",method={RequestMethod.POST})
    public void query_uid_devices(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false) String dut,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {
    	ResponseError validateError = ValidateService.validatePageSize(pageSize);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
        RpcResponseDTO<TailPage<UserDeviceCloudDTO>> rpcResult = userDeviceRpcService.devicePagesByUid(uid, dut, pageNo, pageSize);
        if (!rpcResult.hasError()) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
        }
    }
    
}
