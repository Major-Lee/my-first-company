package com.bhu.vas.business.user;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.dto.WithdrawCashDetail;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.localunit.BaseTest;

/**
 * 主键索引
 * 其他字段不索引的前提下
 * @author Edmond
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserWalletFacadeServiceTest extends BaseTest{

	@Resource
	private UserWalletFacadeService userWalletFacadeService;
	@BeforeClass
    public static void setUp() throws Exception {
		System.out.println("111111111");
		Thread.sleep(1000);
    }

    @AfterClass
    public static void tearDown() throws Exception {
    	System.out.println("0000000");
    	Thread.sleep(1000);
    }
    
    @Test
	public void test000EmptyAndPrepareData(){
    	ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1");
		userWalletFacadeService.getUserWalletService().deleteByModelCriteria(mc);
		userWalletFacadeService.getUserWalletLogService().deleteByModelCriteria(mc);
		userWalletFacadeService.getUserWalletWithdrawApplyService().deleteByModelCriteria(mc);
	}
    private int testVerfyUserId = 1;
    private int testUserId = 3;
    private double testSharedealCash = 127.44d;
    private double testWithdrawCash = 33.33d;
    private String testWithdrawPwd = "withdarw";
    private String testOrderId = "08882016030200000000000000000029";
    
    
    @Test
	public void test001SharedealCashToUserWallet(){
    	UserWallet uWallet = userWalletFacadeService.sharedealCashToUserWallet(testUserId, testSharedealCash, testOrderId);
    	System.out.println(uWallet);
	}	
    
    @Test
	public void test002DoUpdWithdrawPwd(){
    	//AssertHelper.isTrue(randon_key.equals(userid));
    	UserWallet uWallet = userWalletFacadeService.doUpdWithdrawPwd(testUserId, testWithdrawPwd);
    	System.out.println(uWallet);
	}
    @Test
    public void test003DoWithdrawApply(){
    	UserWalletWithdrawApply apply = userWalletFacadeService.doWithdrawApply(testUserId, testWithdrawPwd, testWithdrawCash);
    	System.out.println(apply);
    }

    @Test
	public void test004DoWithdrawVerifyPassed(){
    	ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		createCriteria.andColumnEqualTo("uid", testUserId);
		createCriteria.andColumnEqualTo("withdraw_oper", BusinessEnumType.UWithdrawStatus.Apply.getKey());
		createCriteria.andSimpleCaulse(" 1=1 ");
    	mc.setPageNumber(1);
    	mc.setPageSize(10);
    	mc.setOrderByClause(" created_at desc ");
    	List<UserWalletWithdrawApply> applies = userWalletFacadeService.getUserWalletWithdrawApplyService().findModelByModelCriteria(mc);
    	for(UserWalletWithdrawApply apply:applies){
    		UserWalletWithdrawApply applynow = userWalletFacadeService.doWithdrawVerify(testVerfyUserId, apply.getId(), true);
        	System.out.println(applynow);
    	}
	}
    
    @Test
    public void test005PageWithdrawApplies(){
    	TailPage<UserWalletWithdrawApply> pages = userWalletFacadeService.pageWithdrawApplies(testUserId, BusinessEnumType.UWithdrawStatus.VerifyPassed, 1, 10);
    	System.out.println(pages.getItems().size());
		System.out.println(pages.isFirstPage());
		System.out.println(pages.isLastPage());
		System.out.println(pages.getPageNumber());
		for(UserWalletWithdrawApply apply:pages){
			System.out.println(apply);
		}
	}
    
    //@Test
	public void test006DoWithdrawVerifyFailed(){
    	ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		createCriteria.andColumnEqualTo("uid", testUserId);
		createCriteria.andColumnEqualTo("withdraw_oper", BusinessEnumType.UWithdrawStatus.VerifyPassed.getKey());
		createCriteria.andSimpleCaulse(" 1=1 ");
    	mc.setPageNumber(1);
    	mc.setPageSize(10);
    	mc.setOrderByClause(" created_at desc ");
    	List<UserWalletWithdrawApply> applies = userWalletFacadeService.getUserWalletWithdrawApplyService().findModelByModelCriteria(mc);
    	for(UserWalletWithdrawApply apply:applies){
    		UserWalletWithdrawApply applynow = userWalletFacadeService.doWithdrawVerify(testVerfyUserId, apply.getId(), false);
        	System.out.println("VerifyFailed:"+applynow);
    	}
	}
    
    
    //@Test
    public void test007PageWithdrawApplies(){
    	TailPage<UserWalletWithdrawApply> pages = userWalletFacadeService.pageWithdrawApplies(testUserId, BusinessEnumType.UWithdrawStatus.VerifyFailed, 1, 10);
    	System.out.println(pages.getItems().size());
		System.out.println(pages.isFirstPage());
		System.out.println(pages.isLastPage());
		System.out.println(pages.getPageNumber());
		for(UserWalletWithdrawApply apply:pages){
			System.out.println("VerifyFailed:"+apply);
		}
	}
    
    
    @Test
   	public void test008DoWithdrawRemoteNotify(){
    	ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		createCriteria.andColumnEqualTo("uid", testUserId);
		createCriteria.andColumnEqualTo("withdraw_oper", BusinessEnumType.UWithdrawStatus.VerifyPassed.getKey());
		createCriteria.andSimpleCaulse(" 1=1 ");
    	mc.setPageNumber(1);
    	mc.setPageSize(10);
    	mc.setOrderByClause(" created_at desc ");
    	List<UserWalletWithdrawApply> applies = userWalletFacadeService.getUserWalletWithdrawApplyService().findModelByModelCriteria(mc);
    	for(UserWalletWithdrawApply apply:applies){
    		UserWalletWithdrawApply applynow = userWalletFacadeService.doWithdrawRemoteNotify(apply.getId(), false);
        	System.out.println("RemoteNotifyFailed:"+applynow);
    	}
    	
    	
    	System.out.println(WithdrawCashDetail.build(100.00d, 0.20d, 0.03d));
    	System.out.println(WithdrawCashDetail.build(1000.00d, 0.20d, 0.03d));
    	System.out.println(WithdrawCashDetail.build(485.33d, 0.20d, 0.03d));
   	}
}
