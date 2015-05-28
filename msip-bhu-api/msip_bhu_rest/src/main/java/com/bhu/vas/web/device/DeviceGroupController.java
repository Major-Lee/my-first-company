package com.bhu.vas.web.device;

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
import com.bhu.vas.api.rpc.devices.dto.DeviceGroupDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceGroupRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;

@Controller
@RequestMapping("/devices/group")
public class DeviceGroupController extends BaseController{
	
	@Resource
	private IDeviceGroupRpcService deviceGroupRpcService;
	
	
	@ResponseBody()
	@RequestMapping(value="/birthTree",method={RequestMethod.POST})
	public void birthTree(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) Integer pid) {
		RpcResponseDTO<List<DeviceGroupDTO>> birthTree = deviceGroupRpcService.birthTree(uid, pid);
		if(birthTree.getErrorCode() == null)
			SpringMVCHelper.renderJson(response, birthTree.getPayload());
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(birthTree.getErrorCode()));
	}
	
	/**
	 * 增加及修改群组
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value="/save",method={RequestMethod.POST})
	public void save(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) Integer id,
			@RequestParam(required = true) String name,
			@RequestParam(required = false) Integer pid
			) {
		RpcResponseDTO<DeviceGroupDTO> save = deviceGroupRpcService.save(uid, id, pid, name);
		if(save.getErrorCode() == null)
			SpringMVCHelper.renderJson(response, save.getPayload());
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(save.getErrorCode()));
	}
	
	/**
	 * 群组详细信息
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value="/detail",method={RequestMethod.POST})
	public void detail(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) Integer id) {
		RpcResponseDTO<DeviceGroupDTO> detail = deviceGroupRpcService.detail(uid, id);
		if(detail.getErrorCode() == null)
			SpringMVCHelper.renderJson(response, detail.getPayload());
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(detail.getErrorCode()));
	}
	
	/**
	 * 删除群组
	 * @param request
	 * @param response
	 * @param ids 逗号分割
	 */
	@ResponseBody()
	@RequestMapping(value="/remove",method={RequestMethod.POST})
	public void remove(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) String ids) {
		RpcResponseDTO<Boolean> remove = deviceGroupRpcService.remove(uid, ids);
		if(remove.getErrorCode() == null)
			SpringMVCHelper.renderJson(response, remove.getPayload());
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(remove.getErrorCode()));
	}
	
	/**
	 * 给指定的群组分配wifi设备
	 * @param request
	 * @param response
	 * @param id
	 * @param wifi_ids
	 */
	@ResponseBody()
	@RequestMapping(value="/grant",method={RequestMethod.POST})
	public void grant(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String wifi_ids) {
		RpcResponseDTO<Boolean> grant = deviceGroupRpcService.grant(uid, id, wifi_ids);
		if(grant.getErrorCode() == null)
			SpringMVCHelper.renderJson(response, grant.getPayload());
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(grant.getErrorCode()));
	}
	
	/**
	 * 从指定的群组删除wifi设备
	 * @param request
	 * @param response
	 * @param id
	 * @param wifi_ids
	 */
	@ResponseBody()
	@RequestMapping(value="/ungrant",method={RequestMethod.POST})
	public void ungrant(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String wifi_ids) {
		RpcResponseDTO<Boolean> ungrant = deviceGroupRpcService.ungrant(uid, id, wifi_ids);
		if(ungrant.getErrorCode() == null)
			SpringMVCHelper.renderJson(response, ungrant.getPayload());
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(ungrant.getErrorCode()));
	}
	
}
