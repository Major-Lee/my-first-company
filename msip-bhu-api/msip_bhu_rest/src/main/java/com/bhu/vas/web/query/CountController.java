package com.bhu.vas.web.query;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserDeviceRpcService;
import com.bhu.vas.api.rpc.vap.dto.VapModeUrlViewCountDTO;
import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseSuccess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        response.setHeader("Access-Control-Allow-Origin", "*");
        RpcResponseDTO<VapModeUrlViewCountDTO> ret =  vapRpcService.urlView(key, field);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret.getPayload()));
    }
}
