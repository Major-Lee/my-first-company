package com.bhu.vas.business.ds.user.facade;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.user.model.UserPublishAccount;
import com.bhu.vas.api.vto.publishAccount.UserPublishAccountDetailVTO;
import com.bhu.vas.business.ds.user.service.UserPublishAccountService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * @author Jason
 *
 */
@Service
public class UserPublishAccountFacadeService{
	//private static final Logger logger = LoggerFactory.getLogger(UserPublishAccountFacadeService.class);
	
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
		//根据用户Id判断当前用户是否已绑定对公账号
		UserPublishAccount currUserPAccount = userPublishAccountService.getById(uid);
		if(currUserPAccount != null){
			return null;
		}
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
		if(userPublishAccount == null){
			//throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_PUBLISHACCOUNT_NOTEXIST);
			return null;
		}
		UserPublishAccountDetailVTO userPublishAccountDetail = userPublishAccount.toUserPulishAccountDetailVTO();
		return userPublishAccountDetail;
	}
	/**
	 * 对于同一uid进行synchronized操作
	 * @param uid
	 * @return
	 */
	private UserPublishAccount userPublishAccount(int uid){
		UserPublishAccount userPublishAccount = userPublishAccountService.getById(uid);
		return userPublishAccount;
		/*UserValidateServiceHelper.validateUser(uid,this.userService);
		synchronized(lockObjectFetch(uid)){
			UserPublishAccount userPublishAccount = userPublishAccountService.getById(uid);
			if(userPublishAccount == null){
				return null;
			}
			return userPublishAccount;
		}*/
	}
	
	/*private Map<String,String> lockWithdrawMap = new HashMap<>();
	//给每个用户维护一个锁定key，没有就创建一个
	private synchronized String lockObjectFetch(int uid){
		String lockKey = String.valueOf(uid);
		if(!lockWithdrawMap.containsKey(lockKey)){
			lockWithdrawMap.put(lockKey, lockKey);
			return lockKey;
		}else{
			return lockWithdrawMap.get(lockKey);
		}
	}*/
	
	/**
	 * 解绑对公账号
	 * @param uid
	 */
	public void deletePublicAccount(int uid){
		UserPublishAccount userPublishAccount = userPublishAccount(uid);
		if(userPublishAccount == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.USER_WALLET_WITHDRAW_PUBLISHACCOUNT_NOTEXIST);
		}
		ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andColumnEqualTo("uid", uid);
        userPublishAccountService.deleteById(uid);
	}
}
