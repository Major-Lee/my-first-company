package com.bhu.vas.business.ds.user.facade;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.procedure.ConsumptiveWalletInOrOutProcedureDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.UConsumptiveWalletTransMode;
import com.bhu.vas.api.helper.BusinessEnumType.UConsumptiveWalletTransType;
import com.bhu.vas.api.rpc.message.dto.TimResponseBasicDTO;
import com.bhu.vas.api.rpc.message.helper.MessageTimHelper;
import com.bhu.vas.api.rpc.user.model.UserConsumptiveWallet;
import com.bhu.vas.api.rpc.user.model.UserConsumptiveWalletLog;
import com.bhu.vas.business.ds.user.service.UserConsumptiveWalletLogService;
import com.bhu.vas.business.ds.user.service.UserConsumptiveWalletService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;

/**
 * @author fengshibo
 *
 */
@Service
public class UserConsumptiveWalletFacadeService{
	private static final Logger logger = LoggerFactory.getLogger(UserConsumptiveWalletFacadeService.class);
	
	@Resource
	private UserConsumptiveWalletService userConsumptiveWalletService;
	@Resource
	private UserConsumptiveWalletLogService userConsumptiveWalletLogService;
	@Resource
	private UserService userService;
	
	private int userConsumptiveWalletInOutWithProcedure(int uid, String orderid,UConsumptiveWalletTransMode transMode, UConsumptiveWalletTransType transType,
			String sourceType, String sysType, double rmoney,double cash,String desc,String memo, Map<String, Object>outParam){
		ConsumptiveWalletInOrOutProcedureDTO processorDTO = ConsumptiveWalletInOrOutProcedureDTO.build(uid, orderid, 
				transMode, transType, sourceType, sysType,
				rmoney, cash, desc, memo);
		int executeRet = userConsumptiveWalletService.executeProcedure(processorDTO);
		double balance = 0;
		double balance_old = 0;
		if(executeRet == 0){
			logger.info( String.format("消费者钱包出入账-成功 uid[%s] orderid[%s] transMode[%s] transType[%s] rmoney[%s] cash[%s] desc[%s] memo[%s] cpmid[%s]",
					uid,orderid,transMode.getName(),transType.getName(),rmoney,cash,desc,memo, processorDTO.getCpmid()));
			if(outParam != null){
				outParam.put("cpmid", processorDTO.getCpmid());
				if(processorDTO.getPbalance() != null)
					balance = ArithHelper.longCurrencyToDouble(processorDTO.getPbalance(), 
							BusinessRuntimeConfiguration.WalletDataBaseDegree);
				if(processorDTO.getPbalance_old() != null)
					balance_old = ArithHelper.longCurrencyToDouble(processorDTO.getPbalance_old(), 
							BusinessRuntimeConfiguration.WalletDataBaseDegree);
				outParam.put("balance", balance);
				outParam.put("balance_old", balance_old);
				needNoticeUserRecharge(uid, balance_old, balance);
			}
		}else if(executeRet == 1){
			logger.info( String.format("消费者钱包出入账-失败  余额不足 uid[%s] orderid[%s] transMode[%s] transType[%s] rmoney[%s] cash[%s] desc[%s] memo[%s]",
					uid,orderid,transMode.getName(),transType.getName(),rmoney,cash,desc,memo));
			if(outParam != null){
				outParam.put("cpmid", processorDTO.getCpmid());
				if(processorDTO.getPbalance() != null)
					balance = ArithHelper.longCurrencyToDouble(processorDTO.getPbalance(), 
							BusinessRuntimeConfiguration.WalletDataBaseDegree);
				if(processorDTO.getPbalance_old() != null)
					balance_old = ArithHelper.longCurrencyToDouble(processorDTO.getPbalance_old(), 
							BusinessRuntimeConfiguration.WalletDataBaseDegree);
				outParam.put("balance", balance);
				outParam.put("balance_old", balance_old);
				needNoticeUserRecharge(uid, balance_old, balance);
			}
		}else{
			logger.info( String.format("消费者钱包出入账-失败 uid[%s] orderid[%s] transMode[%s] transType[%s] rmoney[%s] cash[%s] desc[%s] memo[%s]",
					uid,orderid,transMode.getName(),transType.getName(),rmoney,cash,desc,memo));
		}
		return executeRet;
	}
	
	public int userPurchaseGoods(int uid, String orderid, double cash, UConsumptiveWalletTransType transType, String sourceType, String sysType, String desc, String memo, Map<String, Object>outParam){
		logger.info(String.format("userPurchaseGoods uid[%s] orderid[%s] cash[%s] transType[%s] desc[%s] memo[%s].", uid, orderid, cash, transType, desc, memo));
		UserValidateServiceHelper.validateUser(uid,this.userService);
		return userConsumptiveWalletInOutWithProcedure(uid, orderid, UConsumptiveWalletTransMode.CashPayment, 
				transType, sourceType, sysType, cash, cash,desc, memo, outParam);
	}
	
	
	/**
	 * @param uid
	 * @return
	 */
	private UserConsumptiveWallet userWallet(int uid){
		UserValidateServiceHelper.validateUser(uid,this.userService);
		return userConsumptiveWalletService.getOrCreateById(uid);
	}
	
	public String getUserCash(int uid){
		UserConsumptiveWallet userWallet = userWallet(uid);
		return ArithHelper.longCurrencyToDouble(userWallet.getCash(), BusinessRuntimeConfiguration.WalletDataBaseDegree)+"";
	}
	
	public int rechargeConsumptiveWalletCash(int uid, String amount, String orderid, String desc){
		logger.info(String.format("rechargeConsumptiveWalletCash uid[%s] amount[%s]"
				+ "orderid[%s] desc[%s]", uid, amount, orderid, desc));
		double cash = Double.parseDouble(amount);
		return userConsumptiveWalletInOutWithProcedure(uid, 
				orderid, UConsumptiveWalletTransMode.RealMoneyPayment, 
				UConsumptiveWalletTransType.Recharge2C, null, null, cash, cash, desc, StringHelper.EMPTY_STRING_GAP, null);
	}

	public  List<UserConsumptiveWalletLog> findByParams(Integer uid, long start_created_ts, long end_created_ts, int pageNo, int pageSize) {
		ModelCriteria mc = new ModelCriteria();
		Criteria criteria = mc.createCriteria();
		criteria.andColumnEqualTo("uid", uid);
		if(start_created_ts > 0){
			criteria.andColumnGreaterThanOrEqualTo("updated_at", new Date(start_created_ts));
		}
		if(end_created_ts > 0){
			criteria.andColumnLessThanOrEqualTo("updated_at", new Date(end_created_ts));
		}
		mc.setOrderByClause("updated_at desc");
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		return userConsumptiveWalletLogService.findModelByModelCriteria(mc);
	}

	public int countByParams(Integer uid, long start_created_ts, long end_created_ts) {
		ModelCriteria mc = new ModelCriteria();
		Criteria criteria = mc.createCriteria();
		criteria.andColumnEqualTo("uid", uid);
		if(start_created_ts > 0){
			criteria.andColumnGreaterThanOrEqualTo("updated_at", new Date(start_created_ts));
		}
		if(end_created_ts > 0){
			criteria.andColumnLessThanOrEqualTo("updated_at", new Date(end_created_ts));
		}
		return userConsumptiveWalletLogService.countByCommonCriteria(mc);
	}
	
	public void needNoticeUserRecharge(Integer uid, double oldBalance, double balance){
		logger.info(String.format("needNoticeUserRecharge uid[%s] oldBalance[%s] balance[%s]", 
				uid, oldBalance, balance));
		String msg = null;
		if(fetchRechargelevel(oldBalance, balance) == 1){
			msg = String.format(BusinessRuntimeConfiguration.Internal_ConsumerWallet_NeedCharging_Template,
					DateTimeHelper.formatDate(DateTimeHelper.FormatPattern3), 
					BusinessRuntimeConfiguration.ConsumerWalletNoticeUserRechargel1);
			
		}else if(fetchRechargelevel(oldBalance, balance) == 2){
			msg = String.format(BusinessRuntimeConfiguration.Internal_ConsumerWallet_StopService_Template,
					DateTimeHelper.formatDate(DateTimeHelper.FormatPattern3), 
					BusinessRuntimeConfiguration.ConsumerWalletNoticeUserRechargel2);
		}
		if(StringHelper.isNotEmpty(msg)){
			TimResponseBasicDTO retDto = MessageTimHelper.CreateTimSendMsgUrlCommunication(BusinessEnumType.TimPushChannel.BHUOfficial, 
				uid+"", BusinessEnumType.TimPushMsgType.TIMTextElem, msg);
			logger.info("needNoticeUserRecharge["+msg+"] "+JsonHelper.getJSONString(retDto));
		}
	}
	
	private int fetchRechargelevel(double oldBalance, double balance){
		if(oldBalance >= BusinessRuntimeConfiguration.ConsumerWalletNoticeUserRechargel1 && 
				balance < BusinessRuntimeConfiguration.ConsumerWalletNoticeUserRechargel1){
			return 1;
		}
		if(oldBalance >= BusinessRuntimeConfiguration.ConsumerWalletNoticeUserRechargel2 && 
				balance < BusinessRuntimeConfiguration.ConsumerWalletNoticeUserRechargel2){
			return 2;
		}
		return 0;
	}
	
}
