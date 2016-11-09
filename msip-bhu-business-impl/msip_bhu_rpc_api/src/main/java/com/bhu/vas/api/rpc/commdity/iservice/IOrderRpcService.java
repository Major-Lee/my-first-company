package com.bhu.vas.api.rpc.commdity.iservice;

import com.bhu.vas.api.dto.commdity.OrderDetailDTO;
import com.bhu.vas.api.dto.commdity.OrderRechargeVCurrencyVTO;
import com.bhu.vas.api.dto.commdity.OrderRewardNewlyDataVTO;
import com.bhu.vas.api.dto.commdity.OrderRewardVTO;
import com.bhu.vas.api.dto.commdity.OrderSMSVTO;
import com.bhu.vas.api.dto.commdity.OrderStatusDTO;
import com.bhu.vas.api.dto.commdity.OrderVideoVTO;
import com.bhu.vas.api.dto.commdity.OrderWhiteListVTO;
import com.bhu.vas.api.dto.commdity.RewardCreateMonthlyServiceVTO;
import com.bhu.vas.api.dto.commdity.RewardQueryExportRecordVTO;
import com.bhu.vas.api.dto.commdity.RewardQueryPagesDetailVTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.statistics.RewardOrderStatisticsVTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;


public interface IOrderRpcService {
	
	public RpcResponseDTO<OrderRewardVTO> createRewardOrder(Integer commdityid, String mac, String umac, 
			Integer umactype, String payment_type, String context, String user_agent, Integer channel);
	
	public RpcResponseDTO<OrderStatusDTO> orderStatusByUmac(String umac, String orderid);
	
	public RpcResponseDTO<TailPage<OrderRewardVTO>> rewardOrderPages(Integer uid, String mac, String umac, 
			Integer status, String dut, long start_created_ts, long end_created_ts, int pageNo, int pageSize);
	
	public RpcResponseDTO<RewardOrderStatisticsVTO> rewardOrderStatisticsBetweenDate(String start_date, String end_date);
	public RpcResponseDTO<Integer> rewardOrderFinishCountRecent7Days();
	
	public RpcResponseDTO<OrderRechargeVCurrencyVTO> createRechargeVCurrencyOrder(Integer uid, 
			Integer commdityid, String payment_type, Integer umactype, String user_agent);

	public RpcResponseDTO<TailPage<OrderRechargeVCurrencyVTO>> rechargeVCurrencyOrderPages(Integer uid, 
			Integer status, int pageNo, int pageSize);
	
	public RpcResponseDTO<OrderStatusDTO> orderStatusByUid(Integer uid, String orderid);
	
	public RpcResponseDTO<OrderDetailDTO> orderDetailByUid(Integer uid, String orderid);
	
	public RpcResponseDTO<OrderSMSVTO> createSMSOrder(String mac, String umac, Integer umactype, 
			String context, String user_agent, boolean spendvcurrency);
	
	public RpcResponseDTO<TailPage<OrderSMSVTO>> smsOrderPages(Integer uid, String mac, String umac, 
			Integer status, String dut, int pageNo, int pageSize);

	public RpcResponseDTO<OrderRewardNewlyDataVTO> rewardOrderNewlyDataByUid(Integer uid);
	public RpcResponseDTO<RewardQueryPagesDetailVTO> rewardOrderPagesDetail(Integer uid, String mac, String umac, 
			Integer status, String dut, long start_created_ts, long end_created_ts, int pageNo, int pageSize);
	public RpcResponseDTO<RewardQueryExportRecordVTO> rewardQueryExportRecord(Integer uid, String mac, String umac, 
			Integer status, String dut, long start_created_ts, long end_created_ts,int pageNo, int pageSize);
	
	public RpcResponseDTO<OrderVideoVTO> createVideoOrder(Integer commdityid,String mac, String umac, Integer umactype, 
			String context, Integer channel, String user_agent);

	public RpcResponseDTO<Boolean> authorizeVideoOrder(String token, String context);

	public RpcResponseDTO<RewardCreateMonthlyServiceVTO> rewardCreateMonthlyService(Integer commdityid, String mac, String umac,
			String context, Integer umactype, Integer channel, String user_agent, String uname, String acc, String address, int count);

	public RpcResponseDTO<OrderWhiteListVTO> createWhiteListOrder(Integer commdityid, String mac, String umac,
			Integer umactype, String context, String user_agent, Integer channel);

	public RpcResponseDTO<Boolean> commdity_check_user_in_whiteList(String mac, String umac, String acc, String context,
			Integer umactype, Integer commdityid, String user_agent, Integer channel);
}
