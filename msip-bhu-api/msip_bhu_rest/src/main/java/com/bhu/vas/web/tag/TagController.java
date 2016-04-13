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
import com.bhu.vas.api.rpc.tag.vto.TagItemsVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
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
            @RequestParam(required = false, defaultValue = "5", value = "ps") int pageSize) {
    	TagItemsVTO vto = tagRpcService.fetchTag(pageNo, pageSize);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
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
            @RequestParam() String mac,
            @RequestParam() String tag) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.bindTag(mac, tag);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }   
}
