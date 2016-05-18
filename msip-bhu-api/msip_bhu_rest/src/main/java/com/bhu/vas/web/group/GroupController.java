package com.bhu.vas.web.group;

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
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/group")
public class GroupController extends BaseController{
    @Resource
    private ITagRpcService tagRpcService;
    
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
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
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
    @RequestMapping(value = "/del", method = {RequestMethod.POST})
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
    @RequestMapping(value = "/fetch", method = {RequestMethod.POST})
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
    @RequestMapping(value = "/check", method = {RequestMethod.POST})
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
    

    @ResponseBody()
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public void save_Devices_Group(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) int gid,
            @RequestParam(required = true) String path,
            @RequestParam(required = true) String macs) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.saveDevices2Group(uid, gid, path, macs);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    } 
    
    @ResponseBody()
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public void modify(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) int gid,
            @RequestParam(required = false,defaultValue= "0",value = "newGid") int newGid,
            @RequestParam(required = true) String macs) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.modifyDeciceWithNode(uid, gid, newGid, macs);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/config", method = {RequestMethod.POST})
    public void cmd_generate(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String message,
            @RequestParam(required = true) String opt,
            @RequestParam(required = false,defaultValue = "00") String subopt,
            @RequestParam(required = false) String extparams) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.batchGroupDownCmds(uid, message, opt, subopt, extparams);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
}
