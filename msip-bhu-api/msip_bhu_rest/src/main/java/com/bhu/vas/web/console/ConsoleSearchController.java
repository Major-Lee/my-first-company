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
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.vto.SearchConditionVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO1;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/search")
public class ConsoleSearchController extends BaseController {

    @Resource
    private IDeviceRestRpcService deviceRestRpcService;


    /**
     * 多条件组合搜索接口
     * @param uid
     * @param conditions
     * @param pageNo
     * @param pageSize
     * @param request
     * @param response
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_by_condition_message", method = {RequestMethod.POST})
    public void fetch_by_condition_message(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false) String message,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {

/*    	SearchConditionMessage scm = JsonHelper.getDTO(message, SearchConditionMessage.class);
    	System.out.println(uid + "=" + scm.getSearchType() + "=" +pageNo + "="+pageSize);
    	for(SearchCondition searchCondition : scm.getSearchConditions()){
    		System.out.println("for:"+ searchCondition.getKey() + "=" + searchCondition.getPattern() + "=" + searchCondition.getPayload());
    	}*/
        RpcResponseDTO<TailPage<WifiDeviceVTO1>> vtos = deviceRestRpcService.fetchBySearchConditionMessage(
        		message, pageNo, pageSize);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));
    }
    
    /**
     * 保存用户的搜索条件数据
     * @param request
     * @param response
     * @param uid
     * @param message
     */
    @ResponseBody()
    @RequestMapping(value = "/store_user_search_condition", method = {RequestMethod.POST})
    public void store_user_search_condition(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String message) {
    	
        RpcResponseDTO<Boolean> result = deviceRestRpcService.storeUserSearchCondition(uid, message);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
    }
    
    /**
     * 移除用户的搜索条件数据
     * @param request
     * @param response
     * @param uid
     * @param ts
     */
    @ResponseBody()
    @RequestMapping(value = "/remove_user_search_condition", method = {RequestMethod.POST})
    public void remove_user_search_condition(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) long ts) {
    	
        RpcResponseDTO<Boolean> result = deviceRestRpcService.removeUserSearchCondition(uid, ts);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
    }
    
    /**
     * 分页查询用户保存的搜索条件数据列表
     * @param request
     * @param response
     * @param uid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_user_search_condition", method = {RequestMethod.POST})
    public void fetch_user_search_condition(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {
    	
        RpcResponseDTO<TailPage<SearchConditionVTO>> vtos = deviceRestRpcService.fetchUserSearchConditions(uid, pageNo, pageSize);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));
    }

}
