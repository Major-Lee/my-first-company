package com.bhu.vas.web.console;

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
import com.bhu.vas.api.vto.device.CurrentGrayUsageVTO;
import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;
import com.bhu.vas.api.vto.device.VersionVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/ver")
public class ConsoleVersionController extends BaseController {

    @Resource
    private IVapRpcService vapRpcService;

    /**
     * 获取设备定义的类型
     *
     * @param request
     * @param response
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_duts", method = {RequestMethod.POST})
    public void fetch_duts(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid) {
    	RpcResponseDTO<List<DeviceUnitTypeVTO>> rpcResult = vapRpcService.deviceUnitTypes(uid);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
    }

    /**

     * 获取设备类型的灰度列表
     * @param request
     * @param response
     * @param uid
     * @param dut
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_grays", method = {RequestMethod.POST})
    public void fetch_grays(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) int dut) {
    	RpcResponseDTO<CurrentGrayUsageVTO> rpcResult = vapRpcService.currentGrays(uid, dut);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
    }

    /**
     * 获取指定设备型号的固件版本分页列表或者增值模块版本分页列表
     * @param request
     * @param response
     */
    @ResponseBody()
    @RequestMapping(value = "/pagesdv", method = {RequestMethod.POST})
    public void pagesdv(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) int dut,
            @RequestParam(required = true) boolean fw,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
    		) {
    		RpcResponseDTO<TailPage<VersionVTO>> rpcResult = vapRpcService.pagesDeviceVersions(uid, dut, fw, pageNo, pageSize);
    		if(!rpcResult.hasError())
    			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
    		else
    			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
    }
}
