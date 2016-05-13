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
import com.bhu.vas.api.rpc.tag.iservice.ITagRpcService;
import com.bhu.vas.api.rpc.tag.vto.TagGroupVTO;
import com.bhu.vas.api.rpc.tag.vto.TagNameVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/tag")
public class ConsoleTagController extends BaseController{
    @Resource
    private ITagRpcService tagRpcService;
    
    
    /**
     * 分页获取标签
     * @param request
     * @param response
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch", method = {RequestMethod.POST})
    public void fetch_tag(
            HttpServletRequest request,
            HttpServletResponse response,
    	    @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
    	    @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {
    	RpcResponseDTO<TailPage<TagNameVTO>> rpcResult = tagRpcService.fetchTag(pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    /**
     * 设备捆绑标签
     * @param request
     * @param response
     * @param mac
     * @param tag
     */
    @ResponseBody()
    @RequestMapping(value = "/bind", method = {RequestMethod.POST})
    public void buid_tag(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String mac,
            @RequestParam(required = true) String tag) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.bindTag(uid, mac, tag);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    /**
     * 设备删除标签
     * @param request
     * @param response
     * @param uid
     * @param mac
     * @param tag
     */
    @ResponseBody()
    @RequestMapping(value = "/del", method = {RequestMethod.POST})
    public void del_tag(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String mac) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.delTag(uid, mac);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    } 
    
    /**
     * 批量 绑定标签
     * @param request
     * @param response
     * @param uid
     * @param message
     * @param tag
     */
    @ResponseBody()
    @RequestMapping(value = "/batch/bind", method = {RequestMethod.POST})
    public void device_Batch_Bind_Tag(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String message,
            @RequestParam(required = true) String tag) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.deviceBatchBindTag(uid, message, tag);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }   
    
    
    /**
     * 批量删除标签
     * @param request
     * @param response
     * @param uid
     * @param message
     * @param tag
     */
    @ResponseBody()
    @RequestMapping(value = "/batch/del", method = {RequestMethod.POST})
    public void device_Batch_Del_Tag(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String message) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.deviceBatchDelTag(uid, message);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }   
    
    /**
     * 新建或修改分组
     * @param request
     * @param response
     * @param uid
     * @param gid
     * @param pid
     * @param name
     */
    @ResponseBody()
    @RequestMapping(value = "/group/save", method = {RequestMethod.POST})
    public void tag_Group_Save(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false,defaultValue = "0", value = "gid") int gid,
            @RequestParam(required = false, defaultValue = "0", value = "pid") int pid,
            @RequestParam(required = true) String name) {
    	RpcResponseDTO<TagGroupVTO> rpcResult = tagRpcService.saveTreeNode(uid, gid, pid, name);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    } 
    
    /**
     * 批量删除分组
     * @param request
     * @param response
     * @param uid
     * @param gids
     */
    @ResponseBody()
    @RequestMapping(value = "/group/del", method = {RequestMethod.POST})
    public void tag_Group_Del_Node(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String gids) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.delNode(uid, gids);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    /**
     * 分页查询
     * @param request
     * @param response
     * @param uid
     * @param pid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/group/fetch", method = {RequestMethod.POST})
    public void tag_Group_Fetch(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false, defaultValue = "0", value = "pid") int pid,
    	    @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
    	    @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {
    	RpcResponseDTO<TailPage<TagGroupVTO>> rpcResult = tagRpcService.fetchChildGroup(uid, pid, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    /**
     * 添加分组的验证
     * @param request
     * @param response
     * @param uid
     * @param gid
     * @param pid
     * @param name
     */
    @ResponseBody()
    @RequestMapping(value = "/group/check", method = {RequestMethod.POST})
    public void tag_Group_check(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false,defaultValue = "0", value = "gid") int gid,
            @RequestParam(required = false, defaultValue = "0", value = "pid") int pid,
            @RequestParam(required = true) String name) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.CanSaveNode(uid, gid, pid, name);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    } 
}
