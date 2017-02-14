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

import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.payment.dto.PaymentRecordInfoDTO;
import com.bhu.vas.api.rpc.payment.model.PaymentChannelStat;
import com.bhu.vas.api.rpc.payment.vto.PaymentChannelStatVTO;
import com.bhu.vas.api.rpc.payment.vto.PaymentRecordVTO;
import com.bhu.vas.business.ds.payment.service.PaymentChannelStatService;
import com.smartwork.msip.cores.web.business.helper.BusinessWebHelper;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

/**
 * @Editor Eclipse
 * @Author Reid
 * @CreateTime 2017年2月8日 上午10:26:53
 */
@Controller
@RequestMapping("/channelStat")
public class ChannelStatController extends BaseController {
//	@Resource
//	PaymentRecordService paymentRecordService;
	@Resource
	PaymentChannelStatService paymentChannelStatService;
//	@Resource
//	PaymentReckoningService paymentReckoningService;

	@ResponseBody()
	@RequestMapping(value = "/info", method = { RequestMethod.GET, RequestMethod.POST })
	public void queryRecordInfo(HttpServletRequest request, HttpServletResponse response) {
		Object result = this.doRecordInfo(request);
		SpringMVCHelper.renderJson(response, result);
		return;
	}

	private Object doRecordInfo(HttpServletRequest request) {
		//this.updateTodayRecordInfo();
		List<PaymentChannelStat> list = paymentChannelStatService.queryOrderByIdDesc(15);
		List<PaymentChannelStatVTO> infos = new ArrayList<PaymentChannelStatVTO>();
		for (int i = 0; i < list.size(); i++) {
			PaymentChannelStatVTO paymentRecordVTO = new PaymentChannelStatVTO();
			String curDay = list.get(i).getId().toString();
			String mouth = curDay.substring(2, 4);
			String day = curDay.substring(4);
			paymentRecordVTO.setTimeD(mouth+"月"+day+"日");
			paymentRecordVTO.setAmount(list.get(i).getAmount());
			paymentRecordVTO.setCount(list.get(i).getCount());
			paymentRecordVTO.setInfo(list.get(i).getInfo());
			infos.add(paymentRecordVTO);
		}
		if (list.size() > 0)
			return ResponseSuccess.embed(infos);
		else
			return ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY), BusinessWebHelper.getLocale(request));
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