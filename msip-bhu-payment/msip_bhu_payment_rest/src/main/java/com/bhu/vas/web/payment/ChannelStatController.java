package com.bhu.vas.web.payment;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.payment.model.PaymentChannelStat;
import com.bhu.vas.api.rpc.payment.vto.PaymentChannelStatVTO;
import com.bhu.vas.business.ds.payment.service.PaymentChannelStatService;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseSuccess;

/**
 * @Editor Eclipse
 * @Author Reid
 * @CreateTime 2017年2月8日 上午10:26:53
 */
@Controller
@RequestMapping("/channelStat")
public class ChannelStatController extends BaseController {
	
	@Resource
	PaymentChannelStatService paymentChannelStatService;

	@ResponseBody()
	@RequestMapping(value = "/info", method = { RequestMethod.GET, RequestMethod.POST })
	public void queryRecordInfo(HttpServletRequest request, HttpServletResponse response) {
		Object result = this.doRecordInfo(request);
		SpringMVCHelper.renderJson(response, result);
		return;
	}

	private Object doRecordInfo(HttpServletRequest request) {
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		List<Object> paltformIncomeList = paymentChannelStatService.queryPlanInfo(startTime, endTime);
		List<PaymentChannelStatVTO> infos = new ArrayList<PaymentChannelStatVTO>();
		if(paltformIncomeList != null){
			for (Object object : paltformIncomeList) {
				PaymentChannelStat paltformInfo = (PaymentChannelStat) object;
				PaymentChannelStatVTO paymentRecordVTO = new PaymentChannelStatVTO();
				paymentRecordVTO.setTimeD(paltformInfo.getId());
				paymentRecordVTO.setAmount(paltformInfo.getAmount());
				paymentRecordVTO.setCount(paltformInfo.getCount());
				paymentRecordVTO.setInfo(paltformInfo.getInfo());
				infos.add(paymentRecordVTO);
				//System.out.println("paltformAmount = " + paltformInfoVTO.getAmount() + ", time = " + paltformInfoVTO.getId()+ ", info = " + paltformInfoVTO.getInfo());  
	    	}
		}
		return ResponseSuccess.embed(infos);
			//return ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request));
	}

//	private void updateTodayRecordInfo() {
//		List<PaymentReckoning> listToday = paymentReckoningService.listToday();
//		int todayCount = 0, todayAmount = 0, WapWeixinCount = 0, WapWeixinAmount = 0, WapAlipayCount = 0,
//				WapAlipayAmount = 0, PcWeixinCount = 0, PcWeixinAmount = 0, PcAlipayCount = 0, PcAlipayAmount = 0;
//		String Info = "";
//		for (int i = 0; i < listToday.size(); i++) {
//			todayCount++;
//			todayAmount += listToday.get(i).getAmount();
//			switch (listToday.get(i).getPayment_type()) {
//			case "WapWeixin":
//				WapWeixinCount++;
//				WapWeixinAmount += listToday.get(i).getAmount();
//				break;
//			case "WapAilpay":
//				WapAlipayCount++;
//				WapAlipayAmount += listToday.get(i).getAmount();
//				break;
//			case "PcWeixin":
//				PcWeixinCount++;
//				PcWeixinAmount += listToday.get(i).getAmount();
//				break;
//			case "PcAlipay":
//				PcAlipayCount++;
//				PcAlipayAmount += listToday.get(i).getAmount();
//				break;
//			default:
//				break;
//			}
//			Info = "WapWeixin:count=" + WapWeixinCount + ";amount=" + WapWeixinAmount + "|" + "WapAlipay:count="
//					+ WapAlipayCount + ";amount=" + WapAlipayAmount + "|" + "PcWeixin:count=" + PcWeixinCount
//					+ ";amount=" + PcWeixinAmount + "|" + "PcAlipay:count=" + PcAlipayCount + ";amount="
//					+ PcAlipayAmount;
//
//			PaymentRecord record = new PaymentRecord();
//			String id = new SimpleDateFormat("yyMMdd").format(new Date());
//			record.setId(id);
//			record.setAmount(todayAmount);
//			record.setCount(todayCount);
//			record.setInfo(Info);
//			record.setUpdated_at(new Date());
//			System.out.println(JsonHelper.getJSONString(record));
//			if (paymentRecordService.isExistById(id)) {
//				paymentRecordService.update(record);
//			} else {
//				paymentRecordService.insert(record);
//			}
//		}
//	}
}