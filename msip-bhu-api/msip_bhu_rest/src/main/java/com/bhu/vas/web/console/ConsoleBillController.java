package com.bhu.vas.web.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.api.vto.bill.BillDayVTO;
import com.bhu.vas.api.vto.bill.BillVTO;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
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
            @RequestParam(required = false, defaultValue = "", value = "startTime") String startTime,
            @RequestParam(required = false, defaultValue = "", value = "endTime") String endTime,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "31", value = "ps") int pageSize
    		) {
    	
		//RpcResponseDTO<TailPage<UserWithdrawApplyVTO>> rpcResult = userWalletRpcService.pageWithdrawApplies(uid, tuid,utype,mobileno, withdraw_status,payment_type,startTime,endTime,pageNo, pageSize);
    	RpcResponseDTO<BillVTO> rpcResult = userWalletRpcService.pagebillPlan(startTime,endTime,pageNo, pageSize);
		if(rpcResult != null)
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL, BusinessWebHelper.getLocale(request)));
    }
    public static void main(String[] args) {
    	List<BillDayVTO> billDayList = new ArrayList<BillDayVTO>();
    	BillVTO bill = new BillVTO();
    	bill.setAmountC("223.44");
    	bill.setAmountT("33.44");
    	bill.setAmountU("998.98");
    	bill.setStartTime("2月1日");
    	bill.setEndTtime("2月28日");
    	BillDayVTO billDay = new BillDayVTO();
    	billDay.setDate("2月1日");
    	billDay.setTotalA("1223");
    	billDay.setTotalBHUA("887");
    	billDay.setTotalUserA("98");
    	billDay.setAilpayA("20");
    	billDay.setAilpayN("支付宝支付");
    	billDay.setHeeA("34.34");
    	billDay.setHeeN("汇元支付");
    	billDay.setPaypalA("33.32");
    	billDay.setPaypalN("贝宝支付");
    	billDay.setWifiManageN("wifi安全管家");
    	billDay.setWifiManageA("24.2");
    	billDay.setWeixinA("22.55");
    	billDay.setWeixinN("微信支付");
    	billDay.setWifiHelperA("333.23");
    	billDay.setWifiHelperN("wifi助手");
    	billDayList.add(billDay);
    	bill.setBillDay(billDayList);
    	System.out.println(JsonHelper.getJSONString(bill));;
	}
    
    
    @ResponseBody()
    @RequestMapping(value = "/apply_status", method = {RequestMethod.POST})
    public void fetch_apply(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String applyid
    		) {
		RpcResponseDTO<String> rpcResult = userWalletRpcService.withdrawApplyStatus(uid, applyid);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
    }
}
