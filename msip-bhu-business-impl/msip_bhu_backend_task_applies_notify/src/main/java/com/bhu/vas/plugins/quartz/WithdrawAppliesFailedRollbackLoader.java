package com.bhu.vas.plugins.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

/**
 * 提现申请UPay响应失败的后续操作处理
 * 由于提现操作本身不允许失败，因此对于uPay响应失败的的申请需要重新写入redis队列中并进行状态重置为提现中...
 * @author Edmond Lee
 * 
 */
@Deprecated
public class WithdrawAppliesFailedRollbackLoader {
    private static Logger logger = LoggerFactory.getLogger(WithdrawAppliesFailedRollbackLoader.class);
	/*@Resource
	private UserWalletFacadeService userWalletFacadeService;

	@Resource
	private UserWalletWithdrawApplyService userWalletWithdrawApplyService;*/
    
    public void execute(){
		logger.info("WithdrawAppliesFailedRollbackLoader starting...");
		/*int failed_count = 0;
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("withdraw_oper", BusinessEnumType.UWithdrawStatus.WithdrawFailed.getKey());
		mc.setOrderByClause(" updated_at ");
    	mc.setPageNumber(1);
    	mc.setPageSize(500);
		EntityIterator<String, UserWalletWithdrawApply> it = new KeyBasedEntityBatchIterator<String,UserWalletWithdrawApply>(String.class
				,UserWalletWithdrawApply.class, userWalletWithdrawApplyService.getEntityDao(), mc);
		while(it.hasNext()){
			List<UserWalletWithdrawApply> applies = it.next();
			for(UserWalletWithdrawApply withdrawApply:applies){
				User user =userWalletFacadeService.validateUser(withdrawApply.getUid());
				UserWalletConfigs walletConfigs = userWalletFacadeService.getUserWalletConfigsService().userfulWalletConfigs(withdrawApply.getUid());
				UserWithdrawApplyVTO withdrawApplyVTO = withdrawApply.toUserWithdrawApplyVTO(user.getMobileno(), user.getNick(), 
						walletConfigs.getWithdraw_tax_percent(), 
						walletConfigs.getWithdraw_trancost_percent());
				RequestWithdrawNotifyDTO withdrawNotify = RequestWithdrawNotifyDTO.from(withdrawApplyVTO, System.currentTimeMillis());
				String jsonNotify = JsonHelper.getJSONString(withdrawNotify);
				System.out.println(String.format("to Redis prepare[%s]:%s",withdrawApply.getId(), jsonNotify));
				{	//保证写入redis后，提现申请设置成为转账中...状态
					BusinessEnumType.UWithdrawStatus current = BusinessEnumType.UWithdrawStatus.WithdrawDoing;
					CommdityInternalNotifyListService.getInstance().rpushWithdrawAppliesRequestNotify(jsonNotify);
					withdrawApply.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
					withdrawApply.setWithdraw_oper(current.getKey());
					userWalletFacadeService.getUserWalletWithdrawApplyService().update(withdrawApply);
				}
				System.out.println(String.format("to Redis prepare[%s] ok",withdrawApply.getId()));
				failed_count++;
			}
			
		}
		logger.info("WithdrawAppliesFailedRollbackLoader done, total:"+failed_count);*/
    }
}
