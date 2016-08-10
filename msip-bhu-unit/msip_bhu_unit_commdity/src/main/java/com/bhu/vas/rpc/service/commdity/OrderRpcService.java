package com.bhu.vas.rpc.service.commdity;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.OrderDetailDTO;
import com.bhu.vas.api.dto.commdity.OrderRechargeVCurrencyVTO;
import com.bhu.vas.api.dto.commdity.OrderRewardNewlyDataVTO;
import com.bhu.vas.api.dto.commdity.OrderRewardVTO;
import com.bhu.vas.api.dto.commdity.OrderSMSVTO;
import com.bhu.vas.api.dto.commdity.OrderStatusDTO;
import com.bhu.vas.api.dto.commdity.RewardIncomeStatisticsVTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.bhu.vas.api.vto.statistics.RewardOrderStatisticsVTO;
import com.bhu.vas.rpc.facade.OrderUnitFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;

@Service("orderRpcService")
public class OrderRpcService implements IOrderRpcService{
	private final Logger logger = LoggerFactory.getLogger(OrderRpcService.class);
	
	@Resource
	private OrderUnitFacadeService orderUnitFacadeService;
	
	@Override
	public RpcResponseDTO<OrderRewardVTO> createRewardOrder(Integer commdityid, String mac, String umac, 
			Integer umactype, String payment_type, String context, String user_agent, Integer channel){
		logger.info(String.format("createRewardOrder with commdityid[%s] mac[%s] umac[%s] umactype[%s] payment_type[%s] context[%s] user_agent[%s] channel[%s]",
				commdityid, mac, umac, umactype, payment_type, context, user_agent, channel));
		return orderUnitFacadeService.createRewardOrder(commdityid, mac, umac, umactype, payment_type, context, user_agent, channel);
	}
	
	@Override
	public RpcResponseDTO<OrderStatusDTO> orderStatusByUmac(String umac, String orderid) {
		logger.info(String.format("orderStatusByUmac with umac[%s] orderid[%s]", umac, orderid));
		return orderUnitFacadeService.orderStatusByUmac(umac, orderid);
	}
	
	@Override
	public RpcResponseDTO<TailPage<OrderRewardVTO>> rewardOrderPages(Integer uid, String mac, String umac, 
			Integer status, String dut, long start_created_ts, long end_created_ts, int pageNo, int pageSize) {
		logger.info(String.format("rewardOrderPages with uid[%s] mac[%s] umac[%s] status[%s] dut[%s] start_created_ts[%s] end_created_ts[%s] pageNo[%s] pageSize[%s]", uid, 
				mac, umac, status, dut, start_created_ts, end_created_ts, pageNo, pageSize));
		return orderUnitFacadeService.rewardOrderPages(uid, mac, umac, status, dut, start_created_ts, end_created_ts, pageNo, pageSize);
	}
	
	@Override
	public RpcResponseDTO<RewardOrderStatisticsVTO> rewardOrderStatisticsBetweenDate(String start_date, String end_date) {
		logger.info(String.format("rewardOrderStatisticsBetweenDate with start_date[%s] end_date[%s]", start_date, end_date));
		return orderUnitFacadeService.rewardOrderStatisticsBetweenDate(start_date, end_date);
	}
	
	@Override
	public RpcResponseDTO<Integer> rewardOrderFinishCountRecent7Days() {
		logger.info("rewardOrderFinishCountRecent7Days with no params");
		return orderUnitFacadeService.rewardOrderFinishCountRecent7Days();
	}

	@Override
	public RpcResponseDTO<OrderRechargeVCurrencyVTO> createRechargeVCurrencyOrder(Integer uid, Integer commdityid, 
			String payment_type, Integer umactype, String user_agent) {
		logger.info(String.format("createRechargeVCurrencyOrder with uid[%s] commdityid[%s] payment_type[%s] umactype[%s] user_agent[%s]", uid, 
				commdityid, payment_type, umactype, user_agent));
		return orderUnitFacadeService.createRechargeVCurrencyOrder(uid, commdityid, payment_type, umactype, user_agent);
	}

	@Override
	public RpcResponseDTO<TailPage<OrderRechargeVCurrencyVTO>> rechargeVCurrencyOrderPages(
			Integer uid, Integer status, int pageNo, int pageSize) {
		logger.info(String.format("rechargeVCurrencyOrderPages with uid[%s] status[%s] pageNo[%s] pageSize[%s]", uid, 
				status, pageNo, pageSize));
		return orderUnitFacadeService.rechargeVCurrencyOrderPages(uid, status, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<OrderStatusDTO> orderStatusByUid(Integer uid, String orderid) {
		logger.info(String.format("orderStatusByUid with uid[%s] orderid[%s]", uid, orderid));
		return orderUnitFacadeService.orderStatusByUid(uid, orderid);
	}
	
	@Override
	public RpcResponseDTO<OrderRewardNewlyDataVTO> rewardOrderNewlyDataByUid(Integer uid) {
		logger.info(String.format("rewardCountTimeByUid with uid[%s]", uid));
		return orderUnitFacadeService.rewardOrderNewlyDataByUid(uid);
	}
	
	@Override
	public RpcResponseDTO<OrderSMSVTO> createSMSOrder(String mac, String umac, Integer umactype, 
			String context, String user_agent, boolean spendvcurrency){
		logger.info(String.format("createSMSOrder with mac[%s] umac[%s] umactype[%s] context[%s] user_agent[%s] spendvcurrency[%s]",
				mac, umac, umactype, context, user_agent, spendvcurrency));
		//return orderUnitFacadeService.createRewardOrder(commdityid, mac, umac, umactype, payment_type, context);
		return orderUnitFacadeService.createSMSOrder(mac, umac, umactype, context, user_agent, spendvcurrency);
	}

	@Override
	public RpcResponseDTO<TailPage<OrderSMSVTO>> smsOrderPages(Integer uid,
			String mac, String umac, Integer status, String dut, int pageNo,int pageSize) {
		logger.info(String.format("smsOrderPages with uid[%s] mac[%s] umac[%s] status[%s] dut[%s] pageNo[%s] pageSize[%s]", uid, 
				mac, umac, status, dut, pageNo, pageSize));
		//return orderUnitFacadeService.createRewardOrder(commdityid, mac, umac, umactype, payment_type, context);
		return orderUnitFacadeService.smsOrderPages(uid, mac, umac, status, dut, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<OrderDetailDTO> orderDetailByUid(Integer uid, String orderid) {
		logger.info(String.format("orderDetailByUid with uid[%s] orderid[%s]", uid, orderid));
		return orderUnitFacadeService.orderDetailByUid(uid, orderid);
	}

	@Override
	public RpcResponseDTO<RewardIncomeStatisticsVTO> rewardIncomeStatisticsBetweenDate(Integer uid, String mac,
			String dut, long start_created_ts, long end_created_ts) {
		logger.info(String.format("rewardIncomeStatisticsBetweenDate with uid[%s] mac[%s] dut[%s] start_created_ts[%s] end_created_ts[%s]",
				uid, mac,dut,start_created_ts,end_created_ts));
		return orderUnitFacadeService.rewardIncomeStatisticsBetweenDate(uid, mac,
				dut, start_created_ts, end_created_ts);
	}
}
