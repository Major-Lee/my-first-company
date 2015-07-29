package com.bhu.vas.web.device;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bhu.vas.api.vto.DeviceGroupVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseSuccess;
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
	
	/**
	 * 获取值为pid所有节点数据
	 * @param request
	 * @param response
	 * @param uid
	 * @param pid
	 */
	@ResponseBody()
	@RequestMapping(value="/birthTree",method={RequestMethod.POST})
	public void birthTree(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,defaultValue="0") int pid,
			@RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {
		//RpcResponseDTO<List<DeviceGroupVTO>> birthTree = deviceGroupRpcService.birthTree(uid, pid);
		try {
			TailPage<DeviceGroupVTO> birthTree = deviceGroupRpcService.birthTree(uid, pid, pageNo, pageSize);
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(birthTree));
		} catch (Exception e) {
			SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);

		}
		
	}
	
	
	/**
	 * 增加及修改群组
	 *
	 * 目前限制最多3级节点，限制只允许末节点有数据
	 *
	 * @param request
	 * @param response
	 * @param uid
	 * @param gid
	 * @param name
	 * @param pid
	 */
	@ResponseBody()
	@RequestMapping(value="/save",method={RequestMethod.POST})
	public void save(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = false,defaultValue="0") int gid,
			@RequestParam(required = true) String name,
			@RequestParam(required = false,defaultValue="0") int pid
			) {
		System.out.println("~~~~~~~~~~~~save");
		System.out.println("~~~~~~~~~~~~save:"+deviceGroupRpcService);
		RpcResponseDTO<DeviceGroupVTO> save = deviceGroupRpcService.save(uid, gid, pid, name);
		if(save.getErrorCode() == null)
			SpringMVCHelper.renderJson(response, save.getPayload());
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(save.getErrorCode()));
	}
	

	/**
	 * 群组详细信息
	 * @param request
	 * @param response
	 * @param uid
	 * @param gid
	 * @param pageNo
	 * @param pageSize
	 */
	@ResponseBody()
	@RequestMapping(value="/detail",method={RequestMethod.POST})
	public void detail(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) int gid,
			@RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {
		RpcResponseDTO<DeviceGroupVTO> detail = deviceGroupRpcService.detail(uid, gid, pageNo, pageSize);
		if(detail.getErrorCode() == null)
			SpringMVCHelper.renderJson(response, detail.getPayload());
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(detail.getErrorCode()));
	}
	
	/**
	 * 删除群组
	 * @param request
	 * @param response
	 * @param gids 逗号分割
	 */
	@ResponseBody()
	@RequestMapping(value="/remove",method={RequestMethod.POST})
	public void remove(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String gids) {
		RpcResponseDTO<Boolean> remove = deviceGroupRpcService.remove(uid, gids);
		if(remove.getErrorCode() == null)
			SpringMVCHelper.renderJson(response, remove.getPayload());
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(remove.getErrorCode()));
	}
	
	/**
	 * 给指定的群组分配wifi设备
	 * @param request
	 * @param response
	 * @param gid
	 * @param wifi_ids
	 */
	@ResponseBody()
	@RequestMapping(value="/grant",method={RequestMethod.POST})
	public void grant(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) Integer gid,
			@RequestParam(required = true) String wifi_ids) {
		RpcResponseDTO<Boolean> grant = deviceGroupRpcService.grant(uid, gid, wifi_ids);
		if(grant.getErrorCode() == null)
			SpringMVCHelper.renderJson(response, grant.getPayload());
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(grant.getErrorCode()));
	}
	
	/**
	 * 从指定的群组删除wifi设备
	 * @param request
	 * @param response
	 * @param gid
	 * @param wifi_ids
	 */
	@ResponseBody()
	@RequestMapping(value="/ungrant",method={RequestMethod.POST})
	public void ungrant(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) int gid,
			@RequestParam(required = true) String wifi_ids) {
		RpcResponseDTO<Boolean> ungrant = deviceGroupRpcService.ungrant(uid, gid, wifi_ids);
		if(ungrant.getErrorCode() == null)
			SpringMVCHelper.renderJson(response, ungrant.getPayload());
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(ungrant.getErrorCode()));
	}
	
}
