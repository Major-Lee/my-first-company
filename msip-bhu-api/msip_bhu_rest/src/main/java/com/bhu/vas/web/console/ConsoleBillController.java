package com.bhu.vas.web.console;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.api.vto.bill.BillTotalVTO;
import com.bhu.vas.api.vto.bill.BillVTO;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/bill")
public class ConsoleBillController extends BaseController {
	@Resource
	private IUserWalletRpcService userWalletRpcService;
	
	/**
	 * 对账审核列表
	 * @param request
	 * @param response
	 * @param startTime 查询开始时间
	 * @param endTime 查询结束时间
	 * @param pageNo 分页no
	 * @param pageSize 分页size
	 */
    @ResponseBody()
    @RequestMapping(value = "/fetch_bill", method = {RequestMethod.POST})
    public void fetch_bill(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false, defaultValue = "", value = "startTime") String startTime,
            @RequestParam(required = false, defaultValue = "", value = "endTime") String endTime,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "31", value = "ps") int pageSize
    		) {
    	System.out.println("sssssOK"+uid);
    	BillVTO rpcResult = null;
    	try{
    		rpcResult = userWalletRpcService.pagebillPlan(uid,startTime,endTime,pageNo, pageSize);
    		System.out.println(JsonHelper.getJSONString(rpcResult));
    	}catch(Exception e){
    		System.out.println("eee:"+e.getCause()+e.getMessage());
    	}
		if(rpcResult != null)
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL, BusinessWebHelper.getLocale(request)));
    }
    public static void main(String[] args) {
	}
    
    
    @ResponseBody()
    @RequestMapping(value = "/bill_total", method = {RequestMethod.POST})
    public void bill_total(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid) {
    	BillTotalVTO rpcResult =  userWalletRpcService.billTotal(uid);
    	if(rpcResult != null)
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL, BusinessWebHelper.getLocale(request)));
    
    }
}
