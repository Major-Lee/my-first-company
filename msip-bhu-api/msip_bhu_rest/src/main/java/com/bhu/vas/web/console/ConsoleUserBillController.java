package com.bhu.vas.web.console;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.api.vto.bill.UserBillMonthVTO;
import com.bhu.vas.api.vto.bill.UserBillVTO;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/user_bill")
public class ConsoleUserBillController extends BaseController {
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
            @RequestParam(required = false, defaultValue = "12", value = "ps") int pageSize
    		) {
    	UserBillVTO rpcResult = null;
    	try{
    		//rpcResult = userWalletRpcService.pagebillPlan(uid,startTime,endTime,pageNo, pageSize);
    		System.out.println(JsonHelper.getJSONString(rpcResult));
    	}catch(Exception e){
    		System.out.println("fetch_bill:"+e.getCause()+e.getMessage());
    	}
		if(rpcResult != null)
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL, BusinessWebHelper.getLocale(request)));
    }
    
    public static void main(String[] args) {
    	UserBillVTO rpcResult = new UserBillVTO();
    	rpcResult.setStartTime("2016-11");
    	rpcResult.setEndTtime("2016-12");
    	rpcResult.setTotalBalance("0.2");
    	rpcResult.setTotalEndIncome("56.75");
    	rpcResult.setTotalBeginIncome("4556.09");
    	rpcResult.setTotalMonthCount("433");
    	rpcResult.setTotalMonthIncome("34.55");
    	rpcResult.setTotalWithdrawApply("567.66");
    	rpcResult.setTotalWithdrawPast("3456.62");
    	rpcResult.setTotalCash("234.5");
    	List<UserBillMonthVTO> billMonth = new ArrayList<UserBillMonthVTO>();
    	UserBillMonthVTO oneMonthVTO = new UserBillMonthVTO();
    	oneMonthVTO.setDate("2016-11");
    	oneMonthVTO.setBalance("0.2");
    	oneMonthVTO.setBeginIncome("34.55");
    	oneMonthVTO.setCash("3445.64");
    	oneMonthVTO.setEndIncome("566.32");
    	oneMonthVTO.setMonthCount("302");
    	oneMonthVTO.setMonthIncome("33");
    	oneMonthVTO.setWithdrawApply("344.98");
    	oneMonthVTO.setWithdrawPast("997.2");
    	billMonth.add(oneMonthVTO);
    	
    	UserBillMonthVTO twoMonthVTO = new UserBillMonthVTO();
    	twoMonthVTO.setDate("2016-12");
    	twoMonthVTO.setBalance("34.54");
    	twoMonthVTO.setBeginIncome("445.6");
    	twoMonthVTO.setEndIncome("566.32");
    	twoMonthVTO.setCash("98.22");
    	twoMonthVTO.setMonthCount("302");
    	twoMonthVTO.setMonthIncome("33");
    	twoMonthVTO.setWithdrawApply("344.98");
    	twoMonthVTO.setWithdrawPast("997.2");
    	billMonth.add(twoMonthVTO);
    	
    	rpcResult.setMonthBill(billMonth);
    	System.out.println(JsonHelper.getJSONString(rpcResult));
	}
}
