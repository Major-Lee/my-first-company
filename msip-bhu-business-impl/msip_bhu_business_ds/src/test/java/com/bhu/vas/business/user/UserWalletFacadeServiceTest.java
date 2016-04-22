package com.bhu.vas.business.user;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bhu.vas.api.dto.commdity.internal.pay.RequestWithdrawNotifyDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.helper.BusinessEnumType.OAuthType;
import com.bhu.vas.api.rpc.charging.dto.WithdrawCostInfo;
import com.bhu.vas.api.rpc.user.dto.UserOAuthStateDTO;
import com.bhu.vas.api.rpc.user.dto.WithdrawRemoteResponseDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityInternalNotifyListService;
import com.bhu.vas.business.ds.user.facade.UserValidateServiceHelper;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
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
    
   /* @Test
	public void test000AestinData(){
    	List<String> arrays = new ArrayList<String>();
    	arrays.add("(select id from t_vas_users_wallet where id ='100153')");
    	ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1").andColumnIn("id", arrays);
		userWalletFacadeService.getUserWalletService().findIdsByModelCriteria(mc);
	}*/
    
    //@Test
	public void test000EmptyAndPrepareData(){
    	ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1");
		userWalletFacadeService.getUserWalletService().deleteByModelCriteria(mc);
		userWalletFacadeService.getUserWalletLogService().deleteByModelCriteria(mc);
		userWalletFacadeService.getUserWalletWithdrawApplyService().deleteByModelCriteria(mc);
	}
    private int testAppid = BusinessEnumType.CommdityApplication.Default.getKey();
    private int testVerfyUserId = 1;
    private int testUserId = 3;
    private double testSharedealCash = 127.44d;
    private double testWithdrawCash = 33.33d;
    private String testWithdrawPwd = "withdarw";
    private String testWithdrawIP = "192.168.66.7";
    private String testOrderId = "08882016030200000000000000000029";
    
    private String weichat_id = "2w22090420";
    private String alipay_id = "2sf!sdfsdf";
    private String alipay_name = "Edmond Lee";
    
    //@Test
	public void test001PrepareUserPayment(){
    	/*userWalletFacadeService.addThirdpartiesPayment(testUserId, 
    			ThirdpartiesPaymentType.Weichat, 
    			ThirdpartiesPaymentDTO.build(ThirdpartiesPaymentType.Weichat,weichat_id,null,null));
    	
    	userWalletFacadeService.addThirdpartiesPayment(testUserId, 
    			ThirdpartiesPaymentType.Alipay, 
    			ThirdpartiesPaymentDTO.build(ThirdpartiesPaymentType.Alipay,alipay_id,alipay_name,null));
    	List<ThirdpartiesPaymentDTO> allPayment = userWalletFacadeService.fetchAllThirdpartiesPayment(testUserId);
    	for(ThirdpartiesPaymentDTO dto:allPayment){
    		System.out.println("0"+dto);
    	}
    	
    	userWalletFacadeService.removeThirdpartiesPayment(testUserId, ThirdpartiesPaymentType.Weichat);
    	
    	allPayment = userWalletFacadeService.fetchAllThirdpartiesPayment(testUserId);
    	for(ThirdpartiesPaymentDTO dto:allPayment){
    		System.out.println("1"+dto);
    	}
    	
    	ThirdpartiesPaymentDTO payment = userWalletFacadeService.fetchThirdpartiesPayment(testUserId, ThirdpartiesPaymentType.Alipay);
    	System.out.println("2"+payment);*/
	}
    
    //@Test
	public void test001SharedealCashToUserWallet(){
    	//UserWallet uWallet = userWalletFacadeService.sharedealCashToUserWallet(testUserId, testSharedealCash, testOrderId,true,StringUtils.EMPTY);
    	//System.out.println(uWallet);
	}	
    
    //@Test
	public void test002DoUpdWithdrawPwd(){
    	//AssertHelper.isTrue(randon_key.equals(userid));
    	/*UserWallet uWallet = userWalletFacadeService.doFirstSetWithdrawPwd(testUserId, testWithdrawPwd);
    	userWalletFacadeService.doChangedWithdrawPwd(testUserId, testWithdrawPwd, testWithdrawPwd);
    	System.out.println(uWallet);*/
	}
    //@Test
    public void test003DoWithdrawApply(){
    	UserWalletWithdrawApply apply = userWalletFacadeService.doWithdrawApply(testAppid,OAuthType.Alipay,testUserId, testWithdrawPwd, testWithdrawCash,testWithdrawIP);
    	System.out.println(apply);
    }

    //@Test
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
			BusinessEnumType.UWithdrawStatus current = BusinessEnumType.UWithdrawStatus.WithdrawDoing;
			applynow.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
			applynow.setWithdraw_oper(current.getKey());
			//User user = userWalletFacadeService.getUserService().getById(withdrawApply.getUid());
			//User user = userWalletFacadeService.validateUser(withdrawApply.getUid());
			User user = UserValidateServiceHelper.validateUser(applynow.getUid(),userWalletFacadeService.getUserService());
			//UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(applynow.getUid());
			WithdrawCostInfo calculateApplyCost = userWalletFacadeService.getChargingFacadeService().calculateWithdrawCost(applynow.getUid(),applynow.getId(),applynow.getCash());
			UserWithdrawApplyVTO withdrawApplyVTO = applynow.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick(), 
					calculateApplyCost);
			//ThirdpartiesPaymentDTO paymentDTO = userWalletFacadeService.fetchThirdpartiesPayment(applynow.getUid(), ThirdpartiesPaymentType.fromType(applynow.getPayment_type()));
			UserOAuthStateDTO paymentDTO = userWalletFacadeService.getUserOAuthFacadeService().fetchRegisterIndetify(applynow.getUid(),OAuthType.fromType(applynow.getPayment_type()),true);
			RequestWithdrawNotifyDTO withdrawNotify = RequestWithdrawNotifyDTO.from(withdrawApplyVTO,paymentDTO, System.currentTimeMillis());
			String jsonNotify = JsonHelper.getJSONString(withdrawNotify);
			System.out.println("to Redis prepare:"+jsonNotify);
			{	//保证写入redis后，提现申请设置成为转账中...状态
				//BusinessEnumType.UWithdrawStatus current = BusinessEnumType.UWithdrawStatus.WithdrawDoing;
				CommdityInternalNotifyListService.getInstance().rpushWithdrawAppliesRequestNotify(jsonNotify);
				//withdrawApply.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
				//withdrawApply.setWithdraw_oper(current.getKey());
				userWalletFacadeService.getUserWalletWithdrawApplyService().update(applynow);
			}
			System.out.println("to Redis ok:"+jsonNotify);
    	}
	}
    
    //@Test
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
    
    
    //@Test
   	public void test008DoWithdrawNotifyFromRemote(){
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
    		UserWalletWithdrawApply applynow = userWalletFacadeService.doWithdrawNotifyFromRemote(apply.getId(), false);
        	System.out.println("RemoteNotifyFailed:"+applynow);
    	}
    	//userWalletFacadeService.getUserWalletService().executeProcedure(pdto)
    	//System.out.println(WithdrawCashDetail.build(100.00d, 0.20d, 0.03d));
    	//System.out.println(WithdrawCashDetail.build(1000.00d, 0.20d, 0.03d));
    	//System.out.println(WithdrawCashDetail.build(485.33d, 0.20d, 0.03d));
   	}
    
    //@Test
    public void test009DoWithdrawAppliesFailedRollbackLoader(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("withdraw_oper", BusinessEnumType.UWithdrawStatus.WithdrawFailed.getKey());
		mc.setOrderByClause(" updated_at ");
    	mc.setPageNumber(1);
    	mc.setPageSize(500);
		EntityIterator<String, UserWalletWithdrawApply> it = new KeyBasedEntityBatchIterator<String,UserWalletWithdrawApply>(String.class
				,UserWalletWithdrawApply.class, userWalletFacadeService.getUserWalletWithdrawApplyService().getEntityDao(), mc);
		while(it.hasNext()){
			List<UserWalletWithdrawApply> applies = it.next();
			for(UserWalletWithdrawApply withdrawApply:applies){
				BusinessEnumType.UWithdrawStatus current = BusinessEnumType.UWithdrawStatus.WithdrawDoing;
				withdrawApply.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
				withdrawApply.setWithdraw_oper(current.getKey());
				User user =UserValidateServiceHelper.validateUser(withdrawApply.getUid(),userWalletFacadeService.getUserService());
				WithdrawCostInfo calculateApplyCost = userWalletFacadeService.getChargingFacadeService().calculateWithdrawCost(withdrawApply.getUid(),withdrawApply.getId(),withdrawApply.getCash());
				//ApplyCost calculateApplyCost = userWalletFacadeService.getUserWalletConfigsService().calculateApplyCost(withdrawApply.getUid(),withdrawApply.getCash());
				//UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(withdrawApply.getUid());
				UserWithdrawApplyVTO withdrawApplyVTO = withdrawApply.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick(), 
						calculateApplyCost);
				
				UserOAuthStateDTO paymentDTO = userWalletFacadeService.getUserOAuthFacadeService().fetchRegisterIndetify(withdrawApply.getUid(),OAuthType.fromType(withdrawApply.getPayment_type()),true);//.fetchThirdpartiesPayment(withdrawApply.getUid(), ThirdpartiesPaymentType.fromType(withdrawApply.getPayment_type()));
				RequestWithdrawNotifyDTO withdrawNotify = RequestWithdrawNotifyDTO.from(withdrawApplyVTO,paymentDTO, System.currentTimeMillis());
				String jsonNotify = JsonHelper.getJSONString(withdrawNotify);
				System.out.println(String.format("to Redis prepare[%s]:%s",withdrawApply.getId(), jsonNotify));
				{	//保证写入redis后，提现申请设置成为转账中...状态
					CommdityInternalNotifyListService.getInstance().rpushWithdrawAppliesRequestNotify(jsonNotify);
					//withdrawApply.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
					//withdrawApply.setWithdraw_oper(current.getKey());
					userWalletFacadeService.getUserWalletWithdrawApplyService().update(withdrawApply);
				}
				System.out.println(String.format("to Redis prepare[%s] ok",withdrawApply.getId()));
			}
		}
    }
    
    //@Test
   	public void test010DoSharedeal(){
    	double cashIncomming = 108.39d;
    	String dmac = "84:82:f4:23:06:e8";
    	
    	//UserWallet wallet = userWalletFacadeService.sharedealCashToUserWallet(dmac, cashIncomming, "10012016031100000000000000000068", "hello world!");
    	//System.out.println(JsonHelper.getJSONString(wallet));
    	
    	int ret  = userWalletFacadeService.sharedealCashToUserWalletWithProcedure(dmac, cashIncomming, "10012016031100000000000000000068", "hello world!",null);
    	System.out.println("dddd:"+ret);
   	}
    
    @Test
   	public void test011DoSharedealSummary(){
    	
    	//double cashIncomming = 108.39d;
    	//String dmac = "84:82:f4:23:06:e8";
    	
    	//UserWallet wallet = userWalletFacadeService.sharedealCashToUserWallet(dmac, cashIncomming, "10012016031100000000000000000068", "hello world!");
    	//System.out.println(JsonHelper.getJSONString(wallet));
    	
    	int ret  = userWalletFacadeService.sharedealSummaryWithProcedure(110);
    	System.out.println("dddd:"+ret);
   	}
}
