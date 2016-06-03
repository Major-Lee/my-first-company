package com.bhu.vas.api.rpc.user.iservice;

import java.util.Map;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.vto.publishAccount.UserPublishAccountDetailVTO;

public interface IUserPublishAccountRpcService {
	
	/**
	 * 创建用户对公账户
	 * @param uid
	 * @param companyName
	 * @param business_license_number
	 * @param business_license_address
	 * @param address
	 * @param mobile
	 * @param business_license_pic
	 * @param legal_person
	 * @param legal_person_certificate
	 * @param account_name
	 * @param publish_account_number
	 * @param opening_bank
	 * @param city
	 * @param bank_branch_name
	 * @param createTime
	 * @param updateTime
	 * @param status
	 * @return
	 */
	public RpcResponseDTO<Map<String, Object>> createUserPublishAccount(
			int uid,
			String companyName,
			String business_license_number,
			String business_license_address,
			String address,
			String mobile,
			String business_license_pic,
			String legal_person,
			String legal_person_certificate,
			String account_name,
			String publish_account_number,
			String opening_bank,
			String city,
			String bank_branch_name
			);
	
	/**
	 * 根据用户Id查询用户对公账户详细
	 * @param uid
	 * @return
	 */
	public RpcResponseDTO<UserPublishAccountDetailVTO> queryUserPublishAccountDetail(int uid);

}
