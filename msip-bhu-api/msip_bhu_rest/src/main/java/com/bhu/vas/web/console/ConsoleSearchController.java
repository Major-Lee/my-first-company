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

import com.bhu.vas.api.dto.search.condition.SearchCondition;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/search")
public class ConsoleSearchController extends BaseController {

    @Resource
    private IDeviceRestRpcService deviceRestRpcService;


    /**
     * 获取最繁忙的TOP5wifi设备
     *
     * @param request
     * @param response
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch_by_conditions", method = {RequestMethod.POST})
    public void fetch_by_conditions(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) int uid,
            @RequestParam(required = false) List<SearchCondition> conditions,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "5", value = "ps") int pageSize) {

        List<WifiDeviceMaxBusyVTO> vtos = deviceRestRpcService.fetchWDevicesOrderMaxHandset(pageNo, pageSize);
        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));

    }

}
