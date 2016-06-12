package com.bhu.vas.rpc.service.user;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserPublishAccountRpcService;
import com.bhu.vas.api.vto.publishAccount.UserPublishAccountDetailVTO;
import com.bhu.vas.rpc.facade.UserPublishAccountUnitFacadeService;

@Service("userPublishAccountRpcService")
public class UserPublishAccountRpcService implements IUserPublishAccountRpcService{
	private final Logger logger = LoggerFactory.getLogger(UserPublishAccountRpcService.class);
	
	@Resource
	private UserPublishAccountUnitFacadeService userPublishAccountUnitFacadeService;
	
	@Override
	public RpcResponseDTO<UserPublishAccountDetailVTO> createUserPublishAccount(
			int uid, String companyName, String business_license_number,
			String business_license_address, String address, String mobile,
			String business_license_pic, String legal_person,
			String legal_person_certificate, String account_name,
			String publish_account_number, String opening_bank, String city,
			String bank_branch_name) {
		logger.info(String.format("addUserPublishAccount with uid[%s] companyName[%s] business_license_number[%s] business_license_address[%s] address[%s] mobile[%s] business_license_pic[%s] legal_person[%s] legal_person_certificate[%s] account_name[%s] publish_account_number[%s] opening_bank[%s] city[%s] bank_branch_name[%s]",
				uid,companyName,uid,business_license_number,business_license_address,address,mobile,business_license_pic,legal_person,legal_person_certificate,account_name,publish_account_number,opening_bank,city,bank_branch_name));
		return userPublishAccountUnitFacadeService.addUserPublishAccount(uid, companyName, business_license_number, business_license_address, address, mobile, business_license_pic, legal_person, legal_person_certificate, account_name, publish_account_number, opening_bank, city, bank_branch_name);
	}

	@Override
	public RpcResponseDTO<UserPublishAccountDetailVTO> queryUserPublishAccountDetail(
			int uid) {
		return userPublishAccountUnitFacadeService.queryUserPublishAccount(uid);
	}

}
