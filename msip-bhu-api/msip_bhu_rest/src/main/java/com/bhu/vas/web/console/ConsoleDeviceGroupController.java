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
import com.bhu.vas.api.rpc.devicegroup.iservice.IDeviceGroupRpcService;
import com.bhu.vas.api.vto.DeviceGroupDetailVTO;
import com.bhu.vas.api.vto.BackendTaskVTO;
import com.bhu.vas.api.vto.DeviceGroupVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/device/group")
public class ConsoleDeviceGroupController extends BaseController {

    @Resource
    private IDeviceGroupRpcService deviceGroupRpcService;

    /**
     * 获取值为gid所有节点数据
     * 
     * @param request
     * @param response
     * @param uid
     * @param gid
     *            当前节点的gid
     */
    @ResponseBody()
    @RequestMapping(value = "/birthTree", method = { RequestMethod.POST })
    public void birthTree(HttpServletRequest request,
	    HttpServletResponse response,
	    @RequestParam(required = true) Integer uid,
	    @RequestParam(required = false, defaultValue = "0") long gid,
	    @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
	    @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {
	// RpcResponseDTO<List<DeviceGroupVTO>> birthTree =
	// deviceGroupRpcService.birthTree(uid, pid);
	/*
	 * try { TailPage<DeviceGroupVTO> birthTree =
	 * deviceGroupRpcService.birthTree(uid, pid, pageNo, pageSize);
	 * SpringMVCHelper.renderJson(response,
	 * ResponseSuccess.embed(birthTree)); } catch (Exception e) {
	 * e.printStackTrace(); SpringMVCHelper.renderJson(response,
	 * ResponseError.BUSINESS_ERROR);
	 * 
	 * }
	 */
	RpcResponseDTO<TailPage<DeviceGroupVTO>> rpcResult = deviceGroupRpcService
		.birthTree(uid, gid, pageNo, pageSize);
	if (!rpcResult.hasError())
	    SpringMVCHelper.renderJson(response,
		    ResponseSuccess.embed(rpcResult.getPayload()));
	else
	    SpringMVCHelper.renderJson(response,
		    ResponseError.embed(rpcResult));
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
    @RequestMapping(value = "/save", method = { RequestMethod.POST })
    public void save(HttpServletRequest request, HttpServletResponse response,
	    @RequestParam(required = true) Integer uid,
	    @RequestParam(required = true) String name,
	    @RequestParam(required = false, defaultValue = "0") long gid,
	    @RequestParam(required = false, defaultValue = "0") long pid) {
	RpcResponseDTO<DeviceGroupVTO> rpcResult = deviceGroupRpcService
		.deviceGroupSave(uid, gid, pid, name);
	if (!rpcResult.hasError())
	    SpringMVCHelper.renderJson(response,
		    ResponseSuccess.embed(rpcResult.getPayload()));
	else
	    SpringMVCHelper.renderJson(response,
		    ResponseError.embed(rpcResult));
    }

    /**
     * 群组详细信息及分配的搜索条件
     * 
     * @param request
     * @param response
     * @param uid
     * @param gid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/detail", method = { RequestMethod.POST })
    public void detail(HttpServletRequest request, HttpServletResponse response,
	    @RequestParam(required = true) Integer uid,
	    @RequestParam(required = true) long gid) {
	RpcResponseDTO<DeviceGroupDetailVTO> rpcResult = deviceGroupRpcService
		.deviceGroupDetail(uid, gid);
	if (!rpcResult.hasError())
	    SpringMVCHelper.renderJson(response,
		    ResponseSuccess.embed(rpcResult.getPayload()));
	else
	    SpringMVCHelper.renderJson(response,
		    ResponseError.embed(rpcResult));
    }

    /**
     * 删除群组
     * 
     * @param request
     * @param response
     * @param gids
     *            逗号分割
     */
    @ResponseBody()
    @RequestMapping(value = "/remove", method = { RequestMethod.POST })
    public void remove(HttpServletRequest request, HttpServletResponse response,
	    @RequestParam(required = true) Integer uid,
	    @RequestParam(required = true) String gids) {
	RpcResponseDTO<Boolean> rpcResult = deviceGroupRpcService
		.deviceGroupCleanUpByIds(uid, gids);
	if (!rpcResult.hasError())
	    SpringMVCHelper.renderJson(response,
		    ResponseSuccess.embed(rpcResult.getPayload()));
	else
	    SpringMVCHelper.renderJson(response,
		    ResponseError.embed(rpcResult));
    }

    @ResponseBody()
    @RequestMapping(value = "/assign_search_condition", method = {
	    RequestMethod.POST })
    public void store_user_search_condition(HttpServletRequest request,
	    HttpServletResponse response,
	    @RequestParam(required = true) int uid,
	    @RequestParam(required = true) long gid,
	    @RequestParam(required = true) String message,
	    @RequestParam(required = false) String desc) {
	RpcResponseDTO<Boolean> rpcResult = deviceGroupRpcService
		.assignUserSearchCondition4DeviceGroup(uid, gid, message, desc);
	if (!rpcResult.hasError())
	    SpringMVCHelper.renderJson(response,
		    ResponseSuccess.embed(rpcResult.getPayload()));
	else
	    SpringMVCHelper.renderJson(response,
		    ResponseError.embed(rpcResult));
    }

    /**
     * 分页查询现有任务状态
     * 
     * @param request
     * @param response
     * @param uid
     * @param state
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_backendtask", method = {
	    RequestMethod.POST })
    public void fetch_backendtask(HttpServletRequest request,
	    HttpServletResponse response,
	    @RequestParam(required = true) int uid,
	    @RequestParam(required = true, defaultValue = "All") String state,
	    @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
	    @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {

	RpcResponseDTO<TailPage<BackendTaskVTO>> rpcResult = deviceGroupRpcService
		.fetch_backendtask(uid, state, pageNo, pageSize);
	if (!rpcResult.hasError())
	    SpringMVCHelper.renderJson(response,
		    ResponseSuccess.embed(rpcResult.getPayload()));
	else
	    SpringMVCHelper.renderJson(response,
		    ResponseError.embed(rpcResult));

    }

    /**
     * 生成后台群组任务
     * 
     * @param request
     * @param response
     * @param uid
     * @param message
     * @param opt
     * @param subopt
     * @param extparams
     */
    @ResponseBody()
    @RequestMapping(value = "/generate_backendtask", method = {
	    RequestMethod.POST })
    public void generate_backendtask(HttpServletRequest request,
	    HttpServletResponse response,
	    @RequestParam(required = true) int uid,
	    @RequestParam(required = true) String message,
	    @RequestParam(required = true) String opt,
	    @RequestParam(required = false, defaultValue = "00") String subopt,
	    @RequestParam(required = false) String extparams) {

	RpcResponseDTO<Boolean> rpcResult = deviceGroupRpcService.generateBackendTask(uid, message, opt, subopt, extparams);
	if (!rpcResult.hasError())
	    SpringMVCHelper.renderJson(response,
		    ResponseSuccess.embed(rpcResult.getPayload()));
	else
	    SpringMVCHelper.renderJson(response,
		    ResponseError.embed(rpcResult));
    }
    
    /**
     * 修改群组任务
     * @param request
     * @param response
     * @param uid
     * @param taskId  任务id
     * @param extparams json格式{"style":"delete"} or {"style" : "interrupt"}
     */
    @ResponseBody()
    @RequestMapping(value = "modify_backendtask",method = {
	    RequestMethod.POST})
    public void modify_backendtask(HttpServletRequest request,
	    HttpServletResponse response,
	    @RequestParam(required = true) int uid,
	    @RequestParam(required = true) long taskId,
	    @RequestParam(required = true) String extparams) {
	
	RpcResponseDTO<Boolean> rpcResult = deviceGroupRpcService.modifyBackendTask(uid,taskId, extparams);
	if (!rpcResult.hasError())
	    SpringMVCHelper.renderJson(response,
		    ResponseSuccess.embed(rpcResult.getPayload()));
	else
	    SpringMVCHelper.renderJson(response,
		    ResponseError.embed(rpcResult));
	
    }
    

    /**
     * 给指定的群组分配wifi设备
     * 
     * @param request
     * @param response
     * @param gid
     * @param wifi_ids
     */
    /*
     * @ResponseBody()
     * 
     * @RequestMapping(value="/grant",method={RequestMethod.POST}) public void
     * grant( HttpServletRequest request, HttpServletResponse response,
     * 
     * @RequestParam(required = true) Integer uid,
     * 
     * @RequestParam(required = true) Long gid,
     * 
     * @RequestParam(required = true) String wifi_ids) { RpcResponseDTO<Boolean>
     * rpcResult = deviceGroupRpcService.grant(uid, gid, wifi_ids);
     * if(!rpcResult.hasError()) SpringMVCHelper.renderJson(response,
     * rpcResult.getPayload()); else SpringMVCHelper.renderJson(response,
     * ResponseError.embed(rpcResult)); }
     * 
     *//**
       * 从指定的群组删除wifi设备
       * 
       * @param request
       * @param response
       * @param gid
       * @param wifi_ids
       *//*
	 * @ResponseBody()
	 * 
	 * @RequestMapping(value="/ungrant",method={RequestMethod.POST}) public
	 * void ungrant( HttpServletRequest request, HttpServletResponse
	 * response,
	 * 
	 * @RequestParam(required = true) Integer uid,
	 * 
	 * @RequestParam(required = true) long gid,
	 * 
	 * @RequestParam(required = true) String wifi_ids) {
	 * RpcResponseDTO<Boolean> rpcResult =
	 * deviceGroupRpcService.ungrant(uid, gid, wifi_ids);
	 * if(!rpcResult.hasError()) SpringMVCHelper.renderJson(response,
	 * rpcResult.getPayload()); else SpringMVCHelper.renderJson(response,
	 * ResponseError.embed(rpcResult)); }
	 */

}
