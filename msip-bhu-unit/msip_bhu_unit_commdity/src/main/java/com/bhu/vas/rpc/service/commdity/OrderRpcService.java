package com.bhu.vas.rpc.service.commdity;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.dto.commdity.OrderRechargeVCurrencyDTO;
import com.bhu.vas.api.dto.commdity.OrderSMSDTO;
import com.bhu.vas.api.dto.commdity.OrderStatusDTO;
import com.bhu.vas.api.dto.commdity.UserOrderDTO;
import com.bhu.vas.api.dto.commdity.UserRechargeVCurrencyOrderDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.bhu.vas.api.vto.statistics.OrderStatisticsVTO;
import com.bhu.vas.rpc.facade.OrderUnitFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

@Service("orderRpcService")
public class OrderRpcService implements IOrderRpcService{
	private final Logger logger = LoggerFactory.getLogger(OrderRpcService.class);
	
	@Resource
	private OrderUnitFacadeService orderUnitFacadeService;
	
/*	@Override
	public RpcResponseDTO<OrderCreatedRetDTO> createOrder(Integer commdityid, Integer appid, 
			String mac, String umac, Integer uid, String context) {
		logger.info(String.format("createNewOrder with commdityid[%s] appid[%s] mac[%s] umac[%s] uid[%s] context[%s]",
				commdityid, appid, mac, umac, uid, context));
		return orderUnitFacadeService.createOrder(commdityid, appid, mac, umac, uid, context);
	}*/
	
/*	@Override
	public RpcResponseDTO<String> orderPaymentUrlCreated(String orderid, ResponseCreatePaymentUrlDTO rcp_dto) {
		logger.info(String.format("orderPaymentUrlCreated with orderid[%s] rcp_dto[%s]", orderid, rcp_dto));
		return orderUnitFacadeService.orderPaymentUrlCreated(orderid, rcp_dto);
	}*/
	
/*	@Override
	public RpcResponseDTO<Boolean> notifyOrderPaymentSuccessed(String orderid) {
		logger.info(String.format("notifyOrderPaymentSuccessed with orderid[%s]", orderid));
		return orderUnitFacadeService.notifyOrderPaymentSuccessed(orderid);
	}*/
	
/*	@Override
	public RpcResponseDTO<OrderDTO> validateOrderPaymentUrl(String orderid, Integer appid) {
		logger.info(String.format("validateOrderPaymentUrl with orderid[%s] appid[%s]", orderid, appid));
		return orderUnitFacadeService.validateOrderPaymentUrl(orderid, appid);
	}*/
	@Override
	public RpcResponseDTO<OrderDTO> createRewardOrder(Integer commdityid, String mac, String umac, 
			Integer umactype, String payment_type, String context){
		logger.info(String.format("createRewardOrder with commdityid[%s] mac[%s] umac[%s] umactype[%s] payment_type[%s] context[%s]",
				commdityid, mac, umac, umactype, payment_type, context));
		return orderUnitFacadeService.createRewardOrder(commdityid, mac, umac, umactype, payment_type, context);
	}
	
	@Override
	public RpcResponseDTO<OrderStatusDTO> orderStatusByUmac(String umac, String orderid) {
		logger.info(String.format("orderStatusByUmac with umac[%s] orderid[%s]", umac, orderid));
		return orderUnitFacadeService.orderStatusByUmac(umac, orderid);
	}
	
	@Override
	public RpcResponseDTO<TailPage<UserOrderDTO>> rewardOrderPagesByUid(Integer uid, String mac, String umac, 
			Integer status, String dut, int pageNo, int pageSize) {
		logger.info(String.format("rewardOrderPagesByUid with uid[%s] mac[%s] umac[%s] status[%s] dut[%s] pageNo[%s] pageSize[%s]", uid, 
				mac, umac, status, dut, pageNo, pageSize));
		return orderUnitFacadeService.rewardOrderPagesByUid(uid, mac, umac, status, dut, pageNo, pageSize);
	}
	
	@Override
	public RpcResponseDTO<OrderStatisticsVTO> rewardOrderStatisticsBetweenDate(String start_date, String end_date) {
		logger.info(String.format("rewardOrderStatisticsBetweenDate with start_date[%s] end_date[%s]", start_date, end_date));
		return orderUnitFacadeService.rewardOrderStatisticsBetweenDate(start_date, end_date);
	}

	@Override
	public RpcResponseDTO<OrderRechargeVCurrencyDTO> createRechargeVCurrencyOrder(Integer uid, Integer commdityid, 
			String payment_type, Integer umactype) {
		logger.info(String.format("createRechargeVCurrencyOrder with uid[%s] commdityid[%s] payment_type[%s] umactype[%s]", uid, 
				commdityid, payment_type, umactype));
		return orderUnitFacadeService.createRechargeVCurrencyOrder(uid, commdityid, payment_type, umactype);
	}

	@Override
	public RpcResponseDTO<TailPage<UserRechargeVCurrencyOrderDTO>> rechargeVCurrencyOrderPagesByUid(
			Integer uid, Integer status, int pageNo, int pageSize) {
		logger.info(String.format("rechargeVCurrencyOrderPagesByUid with uid[%s] status[%s] pageNo[%s] pageSize[%s]", uid, 
				status, pageNo, pageSize));
		return orderUnitFacadeService.rechargeVCurrencyOrderPagesByUid(uid, status, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<OrderStatusDTO> orderStatusByUid(Integer uid, String orderid) {
		logger.info(String.format("orderStatusByUid with uid[%s] orderid[%s]", uid, orderid));
		return orderUnitFacadeService.orderStatusByUid(uid, orderid);
	}
	
	@Override
	public RpcResponseDTO<OrderSMSDTO> createSMSOrder(String mac, String umac, Integer umactype, String context){
		logger.info(String.format("createSMSOrder with mac[%s] umac[%s] umactype[%s] context[%s]",
				mac, umac, umactype, context));
		//return orderUnitFacadeService.createRewardOrder(commdityid, mac, umac, umactype, payment_type, context);
		return orderUnitFacadeService.createSMSOrder(mac, umac, umactype, context);
	}
	

}
