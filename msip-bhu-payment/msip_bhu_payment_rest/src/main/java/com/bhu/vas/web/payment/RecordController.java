package com.bhu.vas.web.payment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.bhu.vas.api.rpc.payment.model.PaymentReckoning;
import com.bhu.vas.api.rpc.payment.model.PaymentRecord;
import com.bhu.vas.api.rpc.payment.vto.PaymentRecordVTO;
import com.bhu.vas.api.vto.payment.RecordInfoVTO;
import com.bhu.vas.business.ds.payment.service.PaymentReckoningService;
import com.bhu.vas.business.ds.payment.service.PaymentRecordService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年5月25日 上午10:26:53
 */
@Controller
@RequestMapping("/record")
public class RecordController extends BaseController {
	@Resource
	PaymentRecordService paymentRecordService;
	@Resource
	PaymentReckoningService paymentReckoningService;

	@ResponseBody()
	@RequestMapping(value = "/info", method = { RequestMethod.GET, RequestMethod.POST })
	public void queryRecordInfo(HttpServletRequest request, HttpServletResponse response) {
		Object result = this.doRecordInfo();
		SpringMVCHelper.renderJson(response, result);
		return;
	}

	private Object doRecordInfo() {
		//this.updateTodayRecordInfo();
		List<PaymentRecord> list = paymentRecordService.queryOrderByIdDesc(15);
		List<PaymentRecordVTO> infos = new ArrayList<PaymentRecordVTO>();
		for (int i = 0; i < list.size(); i++) {
			PaymentRecordVTO paymentRecordVTO = new PaymentRecordVTO();
			paymentRecordVTO.setTimeD(list.get(i).getId().toString().substring(4));
			paymentRecordVTO.setAmount(list.get(i).getAmount());
			paymentRecordVTO.setCount(list.get(i).getCount());
			paymentRecordVTO.setDx_count(list.get(i).getDx_count());
			paymentRecordVTO.setVideo_count(list.get(i).getVideo_count());
			paymentRecordVTO.setInfo(list.get(i).getInfo());
			infos.add(paymentRecordVTO);
		}
		RecordInfoVTO recordInfo = new RecordInfoVTO();
		PaymentRecordInfoDTO recordInfoDTO = paymentRecordService.paymentRecordInfo();
		recordInfo.setDetail(infos);
		recordInfo.setHistory_order_count(recordInfoDTO.getHistory_order_count());
		recordInfo.setHistory_order_user(recordInfoDTO.getHistory_order_user());
		recordInfo.setPay_again_user_count(recordInfoDTO.getPay_again_user_count());
		if (recordInfo.getDetail().size() > 0)
			return ResponseSuccess.embed(recordInfo);
		else
			return ResponseError
					.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY));
	}

	private void updateTodayRecordInfo() {
		List<PaymentReckoning> listToday = paymentReckoningService.listToday();
		int todayCount = 0, todayAmount = 0, WapWeixinCount = 0, WapWeixinAmount = 0, WapAlipayCount = 0,
				WapAlipayAmount = 0, PcWeixinCount = 0, PcWeixinAmount = 0, PcAlipayCount = 0, PcAlipayAmount = 0;
		String Info = "";
		for (int i = 0; i < listToday.size(); i++) {
			todayCount++;
			todayAmount += listToday.get(i).getAmount();
			switch (listToday.get(i).getPayment_type()) {
			case "WapWeixin":
				WapWeixinCount++;
				WapWeixinAmount += listToday.get(i).getAmount();
				break;
			case "WapAilpay":
				WapAlipayCount++;
				WapAlipayAmount += listToday.get(i).getAmount();
				break;
			case "PcWeixin":
				PcWeixinCount++;
				PcWeixinAmount += listToday.get(i).getAmount();
				break;
			case "PcAlipay":
				PcAlipayCount++;
				PcAlipayAmount += listToday.get(i).getAmount();
				break;
			default:
				break;
			}
			Info = "WapWeixin:count=" + WapWeixinCount + ";amount=" + WapWeixinAmount + "|" + "WapAlipay:count="
					+ WapAlipayCount + ";amount=" + WapAlipayAmount + "|" + "PcWeixin:count=" + PcWeixinCount
					+ ";amount=" + PcWeixinAmount + "|" + "PcAlipay:count=" + PcAlipayCount + ";amount="
					+ PcAlipayAmount;

			PaymentRecord record = new PaymentRecord();
			String id = new SimpleDateFormat("yyMMdd").format(new Date());
			record.setId(id);
			record.setAmount(todayAmount);
			record.setCount(todayCount);
			record.setInfo(Info);
			record.setUpdated_at(new Date());
			System.out.println(JsonHelper.getJSONString(record));
			if (paymentRecordService.isExistById(id)) {
				paymentRecordService.update(record);
			} else {
				paymentRecordService.insert(record);
			}
		}
	}
}