package com.bhu.vas.api.rpc.commdity.iservice;

import com.bhu.vas.api.dto.commdity.OrderDTO;
import com.bhu.vas.api.dto.commdity.OrderRechargeVCurrencyDTO;
import com.bhu.vas.api.dto.commdity.OrderStatusDTO;
import com.bhu.vas.api.dto.commdity.UserOrderDTO;
import com.bhu.vas.api.dto.commdity.UserRechargeVCurrencyOrderDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.statistics.OrderStatisticsVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;


public interface IOrderRpcService {
	
	public RpcResponseDTO<OrderDTO> createRewardOrder(Integer commdityid, String mac, String umac, 
			Integer umactype, String payment_type, String context);
	
	public RpcResponseDTO<OrderStatusDTO> orderStatusByUmac(String umac, String orderid);
	
	public RpcResponseDTO<TailPage<UserOrderDTO>> rewardOrderPagesByUid(Integer uid, String mac, String umac, 
			Integer status, String dut, int pageNo, int pageSize);
	
	public RpcResponseDTO<OrderStatisticsVTO> rewardOrderStatisticsBetweenDate(String start_date, String end_date);
	
	public RpcResponseDTO<OrderRechargeVCurrencyDTO> createRechargeVCurrencyOrder(Integer uid, Integer commdityid, String payment_type, Integer umactype);

	public RpcResponseDTO<TailPage<UserRechargeVCurrencyOrderDTO>> rechargeVCurrencyOrderPagesByUid(Integer uid, 
			Integer status, int pageNo, int pageSize);
	
	public RpcResponseDTO<OrderStatusDTO> orderStatusByUid(Integer uid, String orderid);
}
