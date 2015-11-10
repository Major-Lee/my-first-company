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
            @RequestParam(required = false) int uid,
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

}
