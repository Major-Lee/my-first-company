package com.bhu.vas.web.console;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.rpc.user.dto.UserSearchConditionDTO;
import com.bhu.vas.api.vto.WifiDeviceVTO1;
import com.bhu.vas.api.vto.agent.UserAgentVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping({"/console/search","/customize/search"})
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
            @RequestParam(required = false) Integer uid,
            @RequestParam(required = false) String message,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {

/*    	SearchConditionMessage scm = JsonHelper.getDTO(message, SearchConditionMessage.class);
    	System.out.println(uid + "=" + scm.getSearchType() + "=" +pageNo + "="+pageSize);
    	for(SearchCondition searchCondition : scm.getSearchConditions()){
    		System.out.println("for:"+ searchCondition.getKey() + "=" + searchCondition.getPattern() + "=" + searchCondition.getPayload());
    	}*/
        RpcResponseDTO<List<TailPage<WifiDeviceVTO1>>> rpcResult = deviceRestRpcService.fetchBySearchConditionMessages(
        		pageNo, pageSize, message);
		if(!rpcResult.hasError()){
			//兼容老的界面和接口
			List<TailPage<WifiDeviceVTO1>> rpcResultPayload = rpcResult.getPayload();
			if(rpcResultPayload != null && !rpcResultPayload.isEmpty()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(RpcResponseDTOBuilder.
						builderSuccessRpcResponse(rpcResultPayload.get(0))));
			}else{
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(null));
			}
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    

    /**
     * 多个独立的搜索条件进行搜索数据或数量
     * @param uid
     * @param combine_message
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_by_condition_messages", method = {RequestMethod.POST})
    public void fetch_by_condition_messages(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) Integer uid,
            @RequestParam(required = true, value = "cb_message") String combine_message,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {

    	String[] messages = combine_message.split(StringHelper.Split_Special_Str_Outer_Gap);
        RpcResponseDTO<List<TailPage<WifiDeviceVTO1>>> rpcResult = deviceRestRpcService.fetchBySearchConditionMessages(
        		pageNo, pageSize, messages);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
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
            @RequestParam(required = true) String message,
            @RequestParam(required = false) String desc) {
    	
        RpcResponseDTO<UserSearchConditionDTO> result = deviceRestRpcService.storeUserSearchCondition(uid, message, desc);
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
     * 移除用户的搜索条件数据 (多个)
     * @param request
     * @param response
     * @param uid
     * @param message_ts_splits 搜索条件的ts标识多个 逗号分割
     */
    @ResponseBody()
    @RequestMapping(value = "/remove_user_search_conditions", method = {RequestMethod.POST})
    public void remove_user_search_conditions(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true, value = "m_ts_splits") String message_ts_splits) {
    	
        RpcResponseDTO<Boolean> result = deviceRestRpcService.removeUserSearchConditions(uid, message_ts_splits);
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
    	
        RpcResponseDTO<TailPage<UserSearchConditionDTO>> vtos = deviceRestRpcService.fetchUserSearchConditions(uid, pageNo, pageSize);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));
    }
    
    /**
     * 获取分销商的全部列表
     * @param request
     * @param response
     * @param uid
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_agents", method = {RequestMethod.POST})
    public void fetch_agents(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid) {
    	
        RpcResponseDTO<List<UserAgentVTO>> vtos = deviceRestRpcService.fetchAgents(uid);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));
    }
    
    /**
     * 搜索结果可进行导出txt文件
     * @param request
     * @param response
     * @param uid
     * @param message
     */
    @ResponseBody()
    @RequestMapping(value = "/export_result", method = {RequestMethod.POST})
    public void export_result(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String message) {
    	
        RpcResponseDTO<String> result = deviceRestRpcService.exportWifiDeviceResult(uid, message);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
    }
    
    /**
     * 搜索结果打赏订单可进行导出txt文件
     * @param request
     * @param response
     * @param uid
     * @param message
     */
    @ResponseBody()
    @RequestMapping(value = "/export_order_result", method = {RequestMethod.POST})
    public void export_order_result(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false) String message,
            @RequestParam(required = false, defaultValue = "1") Integer messagetype,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date) {
    	
        RpcResponseDTO<String> result = deviceRestRpcService.exportOrderResult(uid, message, messagetype, start_date, end_date);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
    }
}
