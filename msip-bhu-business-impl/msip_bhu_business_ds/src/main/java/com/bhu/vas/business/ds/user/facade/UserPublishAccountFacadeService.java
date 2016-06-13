package com.bhu.vas.business.ds.user.facade;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.procedure.FincialStatisticsProdureDTO;
import com.bhu.vas.api.dto.procedure.ShareDealDailyGroupSummaryProcedureDTO;
import com.bhu.vas.api.dto.procedure.ShareDealDailyUserSummaryProcedureDTO;
import com.bhu.vas.api.dto.procedure.ShareDealWalletProcedureDTO;
import com.bhu.vas.api.dto.procedure.ShareDealWalletSummaryProcedureDTO;
import com.bhu.vas.api.dto.procedure.WalletInOrOutProcedureDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.OAuthType;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransMode;
import com.bhu.vas.api.helper.BusinessEnumType.UWalletTransType;
import com.bhu.vas.api.rpc.charging.dto.SharedealInfo;
import com.bhu.vas.api.rpc.user.dto.ShareDealDailyGroupSummaryProcedureVTO;
import com.bhu.vas.api.rpc.user.dto.ShareDealDailyUserSummaryProcedureVTO;
import com.bhu.vas.api.rpc.user.dto.ShareDealWalletSummaryProcedureVTO;
import com.bhu.vas.api.rpc.user.dto.UserOAuthStateDTO;
import com.bhu.vas.api.rpc.user.dto.WithdrawRemoteResponseDTO;
import com.bhu.vas.api.rpc.user.model.UserOAuthState;
import com.bhu.vas.api.rpc.user.model.UserPublishAccount;
import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.api.rpc.user.model.pk.UserOAuthStatePK;
import com.bhu.vas.api.rpc.user.notify.IWalletNotifyCallback;
import com.bhu.vas.api.rpc.user.notify.IWalletSharedealNotifyCallback;
import com.bhu.vas.api.vto.publishAccount.UserPublishAccountDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.statistics.service.FincialStatisticsService;
import com.bhu.vas.business.ds.user.service.UserPublishAccountService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserWalletLogService;
import com.bhu.vas.business.ds.user.service.UserWalletService;
import com.bhu.vas.business.ds.user.service.UserWalletWithdrawApplyService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.encrypt.BCryptHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * @author Jason
 *
 */
@Service
public class UserPublishAccountFacadeService{
	private static final Logger logger = LoggerFactory.getLogger(UserPublishAccountFacadeService.class);
	
	@Resource
	private UserService userService;
	@Resource
	private UserPublishAccountService userPublishAccountService;
	
	/**
	 * 添加用户对公账号
	 * @param uid 用户Id
	 * @param companyName 公司名称
	 * @param business_license_number 营业执照号
	 * @param business_license_address 营业执照号所在地
	 * @param address 联系地址
	 * @param mobile 联系电话
	 * @param business_license_pic 营业执照副本扫描件
	 * @param legal_person 法人名称
	 * @param legal_person_certificate 法人证件号
	 * @param account_name 开户名
	 * @param publish_account_number 对公账号
	 * @param opening_bank 开户银行
	 * @param city 所在城市
	 * @param bank_branch_name 开户银行支行名称
	 * @return
	 */
	public UserPublishAccount insertUserPublishAccount(
			int uid,
			String companyName,
			String business_license_number,
			String business_license_address,
			String address,
			String mobile,
			String business_license_pic,
			String account_name,
			String publish_account_number,
			String opening_bank,
			String city,
			String bank_branch_name
			){
		UserPublishAccount entity = new UserPublishAccount();
		entity.setId(uid);
		entity.setCompanyName(companyName);
		entity.setBusiness_license_number(business_license_number);
		entity.setBusiness_license_address(business_license_address);
		entity.setAddress(address);
		entity.setMobile(mobile);
		entity.setBusiness_license_pic(business_license_pic);
		entity.setLegal_person("");
		entity.setLegal_person_certificate("");
		entity.setAccount_name(account_name);
		entity.setPublish_account_number(publish_account_number);
		entity.setOpening_bank(opening_bank);
		entity.setCity(city);
		entity.setBank_branch_name(bank_branch_name);
		Date date=new Date();
		DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=format.format(date);
		entity.setCreateTime(time);
		entity.setUpdateTime(time);
		entity.setStatus(0);
		return userPublishAccountService.insert(entity);
	}
	
	/**
	 * 根据用户Id查询用户对公账户的详细信息
	 * @param uid
	 * @return
	 */
	public UserPublishAccountDetailVTO publicAccountDetail(int uid){
		UserPublishAccount userPublishAccount = userPublishAccount(uid);
		UserPublishAccountDetailVTO userPublishAccountDetail = userPublishAccount.toUserPulishAccountDetailVTO();
		return userPublishAccountDetail;
	}
	/**
	 * 对于同一uid进行synchronized操作
	 * @param uid
	 * @return
	 */
	private UserPublishAccount userPublishAccount(int uid){
		UserValidateServiceHelper.validateUser(uid,this.userService);
		synchronized(lockObjectFetch(uid)){
			UserPublishAccount userPublishAccount = userPublishAccountService.getOrCreateById(uid);
			return userPublishAccount;
		}
	}
	
	private Map<String,String> lockWithdrawMap = new HashMap<>();
	//给每个用户维护一个锁定key，没有就创建一个
	private synchronized String lockObjectFetch(int uid){
		String lockKey = String.valueOf(uid);
		if(!lockWithdrawMap.containsKey(lockKey)){
			lockWithdrawMap.put(lockKey, lockKey);
			return lockKey;
		}else{
			return lockWithdrawMap.get(lockKey);
		}
	}
	
	
}
