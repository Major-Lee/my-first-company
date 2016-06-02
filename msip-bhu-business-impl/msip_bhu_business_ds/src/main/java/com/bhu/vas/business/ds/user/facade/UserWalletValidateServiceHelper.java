package com.bhu.vas.business.ds.user.facade;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.business.ds.user.service.UserWalletService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class UserWalletValidateServiceHelper {
	public static UserWallet validateUserWalletForWithdrawCash(int uid,String pwd,double cash,UserWalletService userWalletService){
		UserWallet uwallet = userWalletService.getById(uid);
		if(uwallet == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"用户钱包"});
		}
		if(uwallet.isWithdraw()){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_OPER_DOING);
		}
		if(StringUtils.isEmpty(uwallet.getPassword())){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAWPWD_SETOPER_NEEDED);
		}
		if(uwallet.getCash() < BusinessRuntimeConfiguration.User_WalletWithdraw_Default_Remainer_MinLimit){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_CASH_LOWERTHAN_WITHDRAW_MINLIMIT,new String[]{String.valueOf(BusinessRuntimeConfiguration.User_WalletWithdraw_Default_Remainer_MinLimit)});
		}
		if(uwallet.getCash() < cash){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_CASH_NOT_SUFFICIENT);
		}
		if(!BCryptHelper.checkpw(pwd,uwallet.getPassword())){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_VALIDATEPWD_FAILED);
		}
		return uwallet;
	}
	
	public static UserWallet validateUserWalletForVCurrencySpend(int uid,double cash_willspend,UserWalletService userWalletService){
		UserWallet uwallet = userWalletService.getById(uid);
		if(uwallet == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_NOTEXIST,new String[]{"用户钱包"});
		}
		/*if(uwallet.isWithdraw()){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_OPER_DOING);
		}
		if(StringUtils.isEmpty(uwallet.getPassword())){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAWPWD_SETOPER_NEEDED);
		}
		if(uwallet.getCash() < BusinessRuntimeConfiguration.User_WalletWithdraw_Default_Remainer_MinLimit){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_CASH_LOWERTHAN_WITHDRAW_MINLIMIT,new String[]{String.valueOf(BusinessRuntimeConfiguration.User_WalletWithdraw_Default_Remainer_MinLimit)});
		}*/
		if(uwallet.getVcurrency() < cash_willspend){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_VCURRENCY_NOT_SUFFICIENT);
		}
		/*if(!BCryptHelper.checkpw(pwd,uwallet.getPassword())){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_VALIDATEPWD_FAILED);
		}*/
		return uwallet;
	}
}
