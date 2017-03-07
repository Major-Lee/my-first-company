package com.bhu.vas.rpc.service.commdity;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.CommdityOrderCommonVTO;
import com.bhu.vas.api.dto.commdity.HotPlayOrderVTO;
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
import com.bhu.vas.api.dto.commdity.UserValidateCaptchaDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.bhu.vas.api.rpc.commdity.vto.QualityGoodsSharedealVTO;
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
	public RpcResponseDTO<RewardQueryPagesDetailVTO> rewardOrderPagesDetail(Integer uid, String mac, String umac,
			Integer status, String dut, long start_created_ts, long end_created_ts, int pageNo, int pageSize) {
		logger.info(String.format("rewardOrderPagesDetail with uid[%s] mac[%s] umac[%s] status[%s] dut[%s] start_created_ts[%s] end_created_ts[%s] pageNo[%s] pageSize[%s]", uid, 
				mac, umac, status, dut, start_created_ts, end_created_ts, pageNo, pageSize));
		return orderUnitFacadeService.rewardOrderPagesDetail(uid,mac,umac,status,dut,start_created_ts,end_created_ts,pageNo,pageSize);
	}
	@Override
	public RpcResponseDTO<RewardQueryExportRecordVTO> rewardQueryExportRecord(Integer uid, String mac, String umac, 
			Integer status, String dut, long start_created_ts, long end_created_ts, int pageNo, int pageSize){
		logger.info(String.format("rewardQueryExportRecord with uid[%s] mac[%s] umac[%s] status[%s] dut[%s] start_created_ts[%s] end_created_ts[%s] pageNo[%s] pageSize[%s]", uid,
				mac, umac, status, dut, start_created_ts, end_created_ts, pageNo,pageSize));
		return orderUnitFacadeService.rewardQueryExportRecord(uid,mac,umac,status,dut,start_created_ts,end_created_ts,pageNo,pageSize);
	}
	
	public RpcResponseDTO<OrderVideoVTO> createVideoOrder(Integer commdityid,String mac, String umac, Integer umactype, 
			String context, Integer channel,String user_agent){
		logger.info(String.format("createVideoOrder with commdityid[%s] mac[%s] umac[%s] umactype[%s] context[%s] channel[%s] user_agent[%s]",
				commdityid,mac, umac, umactype, context, channel,user_agent));
		return orderUnitFacadeService.createVideoOrder(commdityid,mac, umac, umactype, context, channel, user_agent);
	}
	public RpcResponseDTO<Boolean> authorizeVideoOrder(String token, String context){
		logger.info(String.format("authorizeVideoOrder with token[%s] context[%s]",token,context));
		return orderUnitFacadeService.authorizeVideoOrder(token,context);
	}

	@Override
	public RpcResponseDTO<RewardCreateMonthlyServiceVTO> rewardCreateMonthlyService(Integer commdityid, String mac,
			String umac, String context, Integer umactype, Integer channel, String user_agent, 
			String uname, String acc, String address, int count, boolean needInvoice, String invoiceDetail) {
		logger.info(String.format("rewardCreateMonthlyService with commdityid[%s] mac[%s] umac[%s] umactype[%s] context[%s] channel[%s] user_agent[%s] uname[%s] acc[%s] address[%s] count[%s] needInvoice[%s] invoiceDetail[%s]",
				commdityid,mac, umac, umactype, context, channel,user_agent,uname,acc,address,count,needInvoice,invoiceDetail));
		return orderUnitFacadeService.rewardCreateMonthlyService(commdityid, mac,
				 umac, context, umactype, channel, user_agent, uname, acc, address, count,needInvoice,invoiceDetail);
	}

	@Override
	public RpcResponseDTO<OrderWhiteListVTO> createWhiteListOrder(Integer commdityid, String mac, String umac,
			Integer umactype, String context, String user_agent, Integer channel) {
		logger.info(String.format("createWhiteListOrder with commdityid[%s] mac[%s] umac[%s] umactype[%s] context[%s] user_agent[%s] channel[%s]",
				commdityid, mac, umac, umactype, context, user_agent, channel));
		return orderUnitFacadeService.createWhiteListOrder(commdityid, mac, umac, 
				umactype, context, user_agent, channel);
	}

	@Override
	public RpcResponseDTO<Boolean> commdity_check_user_in_whiteList(String mac, String umac, String acc, String context,
			Integer umactype, Integer commdityid, String user_agent, Integer channel) {
		logger.info(String.format("check_user_in_whiteList mac[%s] umac[%s] acc[%s] context[%s] umactype[%s] commdityid[%s] user_agent[%s] channel[%s]",
				mac, umac, acc, context, umactype, commdityid, user_agent, channel));
		return orderUnitFacadeService.check_user_in_whiteList(mac, umac, acc, context, umactype, commdityid, user_agent, channel);
	}

	@Override
	public RpcResponseDTO<UserValidateCaptchaDTO> validate_code_check_authorize(String mac, String umac,
			int countrycode, String acc, String captcha, String context, Integer umactype, Integer commdityid,
			Integer channel, String user_agent,String remateIp) {
		logger.info(String.format("validate_code_check_authorize mac[%s] umac[%s] countrycode[%s] acc[%s] captcha[%s] context[%s] umactype[%s] commdityid[%s] channel[%s] user_agent[%s] remateIp[%s]",
				mac, umac,
				countrycode, acc, captcha, context, umactype, commdityid,
				channel, user_agent, remateIp));
		return orderUnitFacadeService.validate_code_check_authorize(mac, umac,
				countrycode, acc, captcha, context, umactype, commdityid,
				channel, user_agent,remateIp);
	}

	@Override
	public RpcResponseDTO<HotPlayOrderVTO> createHotPlayOrder(Integer commdityid, String hpid, Integer umactype,
			String payment_type, Integer channel, String user_agent) {
		logger.info(String.format("createHotPlayOrder commdityid[%s] hpid[%s] umactype[%s] payment_type[%s] channel[%s] user_agent[%s]",
				commdityid, hpid, umactype, payment_type, channel, user_agent));
		return orderUnitFacadeService.createHotPlayOrder(commdityid, hpid, umactype, payment_type, channel, user_agent);
	}

	@Override
	public RpcResponseDTO<QualityGoodsSharedealVTO> qualityGoodsSharedealPages(int uid, int pageNo, int pageSize){
		logger.info(String.format("qualityGoodsSharedealPages uid[%s] pageno[%s], pagesize[%s]", uid, pageNo, pageSize));
		return orderUnitFacadeService.qualityGoodsSharedealPages(uid, pageNo, pageSize);
	}

	@Override
	public RpcResponseDTO<Boolean> doOrderSharedealCancel(int uid, String orderid, String remark){
		logger.info(String.format("doOrderSharedealCancel uid[%s] orderid[%s], remark[%s]", uid, orderid, remark));
		return orderUnitFacadeService.doOrderSharedealCancel(uid, orderid, remark);
	}
	
	@Override
	public RpcResponseDTO<OrderVideoVTO> clickAuthorize(Integer commdityid, String mac, String umac, Integer umactype,
			Integer channel, String user_agent) {
		logger.info(String.format("clickAuthorize commdityid[%s] mac[%s] umac[%s] umactype[%s]"
				+ "channel[%s] user_agent[%s] ", commdityid, mac, umac, umactype, channel, user_agent));
		return orderUnitFacadeService.clickAuthorize(commdityid, mac, umac, umactype,
				channel, user_agent);
	}

	@Override
	public RpcResponseDTO<CommdityOrderCommonVTO> createTechServiceOrder(Integer commdityid, Integer uid, String macs,
			String payment_type, Integer channel, String user_agent) {
		logger.info(String.format("createTechServiceOrder commdityid[%s] uid[%s] "
				+ "macs[%s] payment_type[%s] channel[%s] user_agent[%s]", commdityid, uid,
				macs, payment_type, channel, user_agent));
		return orderUnitFacadeService.createTechServiceOrder(commdityid, uid,
				macs, payment_type, channel, user_agent);
	}

	@Override
	public RpcResponseDTO<CommdityOrderCommonVTO> createRechargeCashOrder(Integer commdityid, Integer uid,
			String payment_type, String amount, Integer channel, String context, String user_agent) {
		logger.info(String.format("createRechargeCashOrder commdityid[%s] uid[%s] "
				+ "payment_type[%s] amount[%s] channel[%s] context[%s] user_agent[%s]", commdityid, 
				uid, payment_type, amount, channel, context, user_agent));
		return orderUnitFacadeService.createRechargeCashOrder(commdityid, 
				uid, payment_type, amount, channel, context, user_agent);
	}

	@Override
	public RpcResponseDTO<CommdityOrderCommonVTO> spendBalanceOrder(Integer commdityid, Integer uid, String mac, String umac,
			Integer umactype, String payment_type, String context, String user_agent, Integer channel) {
		logger.info(String.format("spendBalanceOrder with commdityid[%s] uid[%s] mac[%s] umac[%s] umactype[%s] payment_type[%s] context[%s] user_agent[%s] channel[%s]",
				commdityid, uid, mac, umac, umactype, payment_type, context, user_agent, channel));
		return orderUnitFacadeService.spendBalanceOrder(commdityid, uid, mac, umac, umactype, payment_type, context, user_agent, channel);
	}
	
}
