package com.bhu.vas.web.payment;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.payment.model.PaymentRecord;
import com.bhu.vas.business.ds.payment.service.PaymentReckoningService;
import com.bhu.vas.business.ds.payment.service.PaymentRecordService;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * @Editor Eclipse
 * @Author Zongshuai
 * @CreateTime 2016年5月25日 上午10:26:53
 */
@Controller
@RequestMapping("/record")
public class RecordController extends BaseController{
	@Resource
	PaymentRecordService paymentRecordService;
	@Resource
	PaymentReckoningService paymentReckoningService;
	
	@ResponseBody()
	@RequestMapping(value="/info",method={RequestMethod.GET,RequestMethod.POST})
	public void queryPaymentOrder(HttpServletRequest request,HttpServletResponse response){
		System.out.println(System.currentTimeMillis());
		//List<PaymentRecord> list = paymentRecordService.queryOrderByIdDesc(2);
		PaymentRecord record = new PaymentRecord();
		record.setAmount(22);
		record.setId("45672");
		record.setCount(11);
		record.setInfo("test");
		record.setUpdated_at(new Date());
		paymentRecordService.insert(record);
		PaymentRecord  records =paymentRecordService.getById("45672");
		System.out.println(records.getId()+"");
		int count = paymentRecordService.countOfToday();
		//int count = paymentReckoningService.countOfToday();
		System.out.println(count);
		//for(int i=0;i<list.size();i++){
			//System.out.println(list.get(i).getId());
			//SpringMVCHelper.renderJson(response, list.get(i));
			//return;
		//}
		SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
				ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY)));
		return;
		
	}
}

