package com.bhu.vas.web.payment;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.payment.model.PaymentOrder;
import com.bhu.vas.business.ds.payment.service.PaymentOrderService;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/payment")
public class PaymentController extends BaseController{
	private final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	@Resource
	private PaymentOrderService paymentOrderService;
	
	@ResponseBody()
	@RequestMapping(value="/order/{orderid}",method={RequestMethod.GET,RequestMethod.POST})
	public void query_paymentorder(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long orderid
			) {
		logger.info(String.format(" query_paymentorder order[%s]", orderid));
		PaymentOrder order = paymentOrderService.getById(orderid);
		if(order != null){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(order));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(RpcResponseDTOBuilder.builderErrorRpcResponse(
					ResponseErrorCode.COMMON_DATA_NOTEXIST)));
		}
	}
}
