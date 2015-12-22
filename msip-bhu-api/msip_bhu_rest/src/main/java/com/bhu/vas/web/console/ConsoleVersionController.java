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
import com.bhu.vas.api.vto.device.GrayUsageVTO;
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
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
            @RequestParam(required = true) String dut) {
    	RpcResponseDTO<CurrentGrayUsageVTO> rpcResult = vapRpcService.currentGrays(uid, dut);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
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
            @RequestParam(required = true) String dut,
            @RequestParam(required = true) boolean fw,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
    		) {
		RpcResponseDTO<TailPage<VersionVTO>> rpcResult = vapRpcService.pagesDeviceVersions(uid, dut, fw, pageNo, pageSize);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
    
    /**
     * 变更指定产品类型的灰度关联的固件版本号和增值组件版本号
     * @param request
     * @param response
     * @param uid
     * @param dut
     * @param gl
     * @param fwid
     * @param omid
     */
    @ResponseBody()
    @RequestMapping(value = "/modifyrv4gv", method = {RequestMethod.POST})
    public void modifyrv4gv(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String dut,
            @RequestParam(required = true) int gl,
            @RequestParam(required = true) String fwid,
            @RequestParam(required = true) String omid
    		) {
		RpcResponseDTO<GrayUsageVTO> rpcResult = vapRpcService.modifyRelatedVersion4GrayVersion(uid, dut, gl, fwid, omid);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
    
    /**
     * 增加固件版本或者增值组件版本信息
     * @param request
     * @param response
     * @param uid
     * @param dut
     * @param fw
     * @param versionid
     * @param upgrade_url
     */
    @ResponseBody()
    @RequestMapping(value = "/adddv", method = {RequestMethod.POST})
    public void adddv(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String dut,
            @RequestParam(required = true) boolean fw,
            @RequestParam(required = true) String versionid, 
            @RequestParam(required = true) String upgrade_url,
            @RequestParam(required = false) String upgrade_slaver_urls
            ) {
    	RpcResponseDTO<VersionVTO> rpcResult = vapRpcService.addDeviceVersion(uid, dut, fw, versionid, upgrade_url,upgrade_slaver_urls);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
    
    
    @ResponseBody()
    @RequestMapping(value = "/removedv", method = {RequestMethod.POST})
    public void removedv(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String dut,
            @RequestParam(required = true) boolean fw,
            @RequestParam(required = true) String versionid
            ) {
    	RpcResponseDTO<VersionVTO> rpcResult = vapRpcService.removeDeviceVersion(uid, dut, fw, versionid);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
    
}
