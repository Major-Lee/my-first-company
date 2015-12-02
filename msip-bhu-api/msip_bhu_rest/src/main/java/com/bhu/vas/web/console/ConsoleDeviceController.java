package com.bhu.vas.web.console;

import java.util.Arrays;
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
import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;
import com.bhu.vas.api.vto.device.DeviceDetailVTO;
import com.bhu.vas.api.vto.device.ModuleStyleVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/device")
public class ConsoleDeviceController extends BaseController {

    @Resource
    private IVapRpcService vapRpcService;

    /**
     * 设备详细信息
     * @param request
     * @param response
     * @param uid
     * @param mac
     */
    @ResponseBody()
    @RequestMapping(value = "/detail", method = {RequestMethod.POST})
    public void detail(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String mac
    		) {
		RpcResponseDTO<DeviceDetailVTO> rpcResult = vapRpcService.deviceDetail(uid, mac.toLowerCase());
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
    }
    
    /**
     * 给指定设备进行升级，包括固件和组件
     * @param request
     * @param response
     * @param uid
     * @param macs 逗号分割
     * @param fw
     * @param versionid
     * @param beginTime
     * @param endTime
     */
    @ResponseBody()
    @RequestMapping(value = "/forceupgrade", method = {RequestMethod.POST})
    public void forceupgrade(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) boolean fw,
            @RequestParam(required = true) String macs,
            @RequestParam(required = true) String versionid,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime
    		) {
    	String[] macarray = StringHelper.split(macs.toLowerCase(), StringHelper.COMMA_STRING_GAP);
    	List<String> masList = Arrays.asList(macarray);
    	if(!StringHelper.isValidMacs(masList)){
    		SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR,new String[]{"macs"}));
    		return;
    	}
    	RpcResponseDTO<Boolean> rpcResult = vapRpcService.forceDeviceUpgrade(uid, fw, versionid, masList, beginTime, endTime);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
    
    @ResponseBody()
    @RequestMapping(value = "/fetch_styles", method = {RequestMethod.POST})
    public void fetch_styles(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
    		) {
		RpcResponseDTO<TailPage<ModuleStyleVTO>> rpcResult = vapRpcService.pagesVapStyles(uid,pageNo, pageSize);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
    
    
    /*@ResponseBody()
    @RequestMapping(value = "/upd_style", method = {RequestMethod.POST})
    public void upd_style(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) int style
    		) {
    }*/
    
    @ResponseBody()
    @RequestMapping(value = "/savemacs2gray", method = {RequestMethod.POST})
    public void saveMacs2Gray(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String dut,
            @RequestParam(required = true) int gl,
            @RequestParam(required = true) String macs
            ) {
    	String[] macarray = StringHelper.split(macs.toLowerCase(), StringHelper.COMMA_STRING_GAP);
    	List<String> masList = Arrays.asList(macarray);
    	if(!StringHelper.isValidMacs(masList)){
    		SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR,new String[]{"macs"}));
    		return;
    	}
    	RpcResponseDTO<Boolean> rpcResult = vapRpcService.saveMacs2Gray(uid, dut, gl, masList);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
}
