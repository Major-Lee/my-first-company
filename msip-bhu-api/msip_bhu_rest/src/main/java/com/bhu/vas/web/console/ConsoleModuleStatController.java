package com.bhu.vas.web.console;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;
import com.bhu.vas.api.vto.device.DeviceDetailVTO;
import com.bhu.vas.api.vto.modulestat.ModuleDefinedItemVTO;
import com.bhu.vas.api.vto.modulestat.ModuleDefinedVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by bluesand on 12/2/15.
 */
@Controller
@RequestMapping("/console/module/stat")
public class ConsoleModuleStatController extends BaseController {

    @Resource
    private IVapRpcService vapRpcService;


    @ResponseBody()
    @RequestMapping(value = "/fetch_day", method = {RequestMethod.POST})
    public void fetchDayStat(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestParam(required = true) int uid) {

        RpcResponseDTO<List<ModuleDefinedVTO>> rpcResult = vapRpcService.fetchDayStat(uid);
        if(!rpcResult.hasError())
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        else
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));


    }


    @ResponseBody()
    @RequestMapping(value = "/fetch_detail", method = {RequestMethod.POST})
    public void fetchStatDetail(HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestParam(required = true) int uid,
                                @RequestParam(required = true) String style
                                ) {

        RpcResponseDTO<ModuleDefinedItemVTO> rpcResult = vapRpcService.fetchStatDetail(uid, style);
        if(!rpcResult.hasError())
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        else
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
    }

}
