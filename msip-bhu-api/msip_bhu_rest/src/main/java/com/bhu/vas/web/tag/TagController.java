package com.bhu.vas.web.tag;

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
import com.bhu.vas.api.rpc.tag.vto.TagNameVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/tag")
public class TagController extends BaseController{
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
}
