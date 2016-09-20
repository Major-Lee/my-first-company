package com.bhu.vas.business.user;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.rpc.user.model.UserPublishAccount;
import com.bhu.vas.api.vto.publishAccount.UserPublishAccountDetailVTO;
import com.bhu.vas.business.ds.user.facade.UserPublishAccountFacadeService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.smartwork.msip.localunit.BaseTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserPublishAccountFacadeServiceTest extends BaseTest{
	
	@Resource
	private UserPublishAccountFacadeService userPublicAccountService;
	@Resource
	private UserService userService;
	
	@AfterClass
    public static void tearDown() throws Exception {
    	System.out.println("0000000");
    	Thread.sleep(1000);
    }
	
	@Test
	public void insertUserPublishAccount(){
		/*//用户Id
		 int uid = 1;
		//公司名称
		 String companyName = "北京必虎科技公司";
		//营业执照号
		 String business_license_number = "123456789456";
		//营业执照号所在地
		 String business_license_address = "北京市海淀区学清路汇智大厦A座";
		//联系地址
		 String address = "北京市海淀区学清路汇智大厦A座";
		//联系电话
		 String mobile = "12345678912";
		//营业执照号扫描副本
		 String business_license_pic = "test.png";
		//法人名称
		 String legal_person = "test";
		//法人证件号
		 String legal_person_certificate = "12345798789454";
		//开户名
		 String account_name = "北京必虎科技公司";
		//对公账号
		 String publish_account_number = "12314589745789";
		//开户银行
		 String opening_bank = "北京银行";
		//所在城市
		 String city = "北京";
		//开户银行支行名称
		 String bank_branch_name = "北京支行";
		userPublicAccountService.insertUserPublishAccount(uid, companyName, business_license_number, business_license_address, address, mobile, business_license_pic, account_name, publish_account_number, opening_bank, city, bank_branch_name);*/
	}
	
	@Test
	public void queryUserPublishAccountDetail(){
		UserPublishAccountDetailVTO userPublishAccountDetailVTO = userPublicAccountService.publicAccountDetail(101006);
		System.out.println(userPublishAccountDetailVTO.getUid());
		System.out.println(userPublishAccountDetailVTO.getAccount_name());
	}
}
