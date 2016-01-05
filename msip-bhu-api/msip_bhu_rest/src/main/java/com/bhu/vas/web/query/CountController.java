package com.bhu.vas.web.query;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.vap.dto.VapModeUrlViewCountDTO;
import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

/**
 * Created by bluesand on 5/26/15.
 */
@Controller
@RequestMapping("/noauth/vap")
public class CountController extends BaseController {

    @Resource
    private IVapRpcService vapRpcService;

    @ResponseBody()
    @RequestMapping(value="/urlview",method={RequestMethod.POST,RequestMethod.GET})
    public void urlview(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) String key,
            @RequestParam(required = true) String field
            ) {
//        response.setHeader("Access-Control-Allow-Origin", "*");
        RpcResponseDTO<VapModeUrlViewCountDTO> rpcResult =  vapRp cService.urlView(key, field);
        if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
        //SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret.getPayload()));
    }
}
