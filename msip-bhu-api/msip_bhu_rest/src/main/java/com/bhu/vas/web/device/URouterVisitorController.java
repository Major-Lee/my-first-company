package com.bhu.vas.web.device;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceURouterRestRpcService;
import com.bhu.vas.api.vto.guest.URouterVisitorListVTO;
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

/**
 * Created by bluesand on 10/26/15.
 */

@Controller
@RequestMapping("/urouter/visitor")
public class URouterVisitorController extends BaseController {

    @Resource
    private IDeviceURouterRestRpcService deviceURouterRestRpcService;

    /**
     * 访客网络列表
     * @param request
     * @param response
     * @param uid
     * @param mac
     */
    @ResponseBody()
    @RequestMapping(value="/list",method={RequestMethod.POST})
    public void list(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = true) String mac,
            @RequestParam(required = false, defaultValue="0", value = "st") int start,
            @RequestParam(required = false, defaultValue="5", value = "ps") int size
            ) {

        RpcResponseDTO<URouterVisitorListVTO> rpcResponse = deviceURouterRestRpcService.urouterVisitorList(uid, mac,start, size);
        if(rpcResponse.getErrorCode() == null){
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
        }else{
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
        }
    }


    /**
     * 踢出终端
     * @param request
     * @param response
     * @param uid
     * @param mac
     */
    @ResponseBody()
    @RequestMapping(value="/remove",method={RequestMethod.POST})
    public void remove(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = true) String mac,
            @RequestParam(required = true) String hd_mac
    ) {
        RpcResponseDTO<Boolean> rpcResponse = deviceURouterRestRpcService.urouterVisitorRemoveHandset(uid, mac, hd_mac);
        if(rpcResponse.getErrorCode() == null){
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResponse.getPayload()));
        }else{
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResponse.getErrorCode()));
        }
    }


//    /**
//     * 访客网络详细信息
//     * @param request
//     * @param response
//     * @param uid
//     * @param mac
//     */
//    @ResponseBody()
//    @RequestMapping(value="/detail",method={RequestMethod.POST})
//    public void detail(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @RequestParam(required = true) Integer uid,
//            @RequestParam(required = true) String mac) {
//        deviceURouterRestRpcService.urouterGuestDetail(uid, mac);
//    }





//    /**
//     * 修改访客网络昵称
//     * @param request
//     * @param response
//     * @param uid
//     * @param mac
//     * @param name
//     */
//    @ResponseBody()
//    @RequestMapping(value="/rename",method={RequestMethod.POST})
//    public void modify(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @RequestParam(required = true) Integer uid,
//            @RequestParam(required = true) String mac,
//            @RequestParam(required = true) String name) {
//        deviceURouterRestRpcService.urouterGuestRename(uid, mac, name);
//    }




}
