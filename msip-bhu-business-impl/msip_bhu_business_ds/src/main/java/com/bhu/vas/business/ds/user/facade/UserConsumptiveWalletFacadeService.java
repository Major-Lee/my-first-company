package com.bhu.vas.business.ds.user.facade;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.procedure.ConsumptiveWalletInOrOutProcedureDTO;
import com.bhu.vas.api.helper.BusinessEnumType.UConsumptiveWalletTransMode;
import com.bhu.vas.api.helper.BusinessEnumType.UConsumptiveWalletTransType;
import com.bhu.vas.api.rpc.user.model.UserConsumptiveWallet;
import com.bhu.vas.business.ds.user.service.UserConsumptiveWalletService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArithHelper;

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
	private UserService userService;
	
	public int userConsumptiveWalletInOutWithProcedure(int uid,String orderid,UConsumptiveWalletTransMode transMode, UConsumptiveWalletTransType transType,double rmoney,double cash,String desc,String memo){
		ConsumptiveWalletInOrOutProcedureDTO processorDTO = ConsumptiveWalletInOrOutProcedureDTO.build(uid, orderid, 
				transMode, transType,
				rmoney, cash, desc, memo);
		int executeRet = userConsumptiveWalletService.executeProcedure(processorDTO);
		if(executeRet == 0){
			logger.info( String.format("消费者钱包出入账-成功 uid[%s] orderid[%s] transMode[%s] transType[%s] rmoney[%s] cash[%s] desc[%s] memo[%s]",
					uid,orderid,transMode.getName(),transType.getName(),rmoney,cash,desc,memo));
		}else if(executeRet == 1){
			logger.info( String.format("消费者钱包出入账-失败  余额不足 uid[%s] orderid[%s] transMode[%s] transType[%s] rmoney[%s] cash[%s] desc[%s] memo[%s]",
					uid,orderid,transMode.getName(),transType.getName(),rmoney,cash,desc,memo));
		}else{
			logger.info( String.format("消费者钱包出入账-失败 uid[%s] orderid[%s] transMode[%s] transType[%s] rmoney[%s] cash[%s] desc[%s] memo[%s]",
					uid,orderid,transMode.getName(),transType.getName(),rmoney,cash,desc,memo));
		}
		return executeRet;
	}
	
	public int userPurchaseGoods(int uid, String orderid, double cash, UConsumptiveWalletTransType transType, String desc, String memo){
		logger.info(String.format("userPurchaseGoods uid[%s] orderid[%s] cash[%s] transType[%s] desc[%s] memo[%s].", uid, orderid, cash, transType, desc, memo));
		UserValidateServiceHelper.validateUser(uid,this.userService);
		return userConsumptiveWalletInOutWithProcedure(uid, orderid,UConsumptiveWalletTransMode.CashPayment, transType, cash, cash,desc, memo);
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
	
}
