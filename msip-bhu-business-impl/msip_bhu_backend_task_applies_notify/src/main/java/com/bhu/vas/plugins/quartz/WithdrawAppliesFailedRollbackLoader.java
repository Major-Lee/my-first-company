package com.bhu.vas.plugins.quartz;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;





import com.bhu.vas.api.dto.commdity.internal.pay.RequestWithdrawNotifyDTO;
import com.bhu.vas.api.helper.BusinessEnumType;
import com.bhu.vas.api.rpc.user.dto.WithdrawRemoteResponseDTO;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserWalletConfigs;
import com.bhu.vas.api.rpc.user.model.UserWalletWithdrawApply;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityInternalNotifyListService;
import com.bhu.vas.business.ds.user.facade.UserWalletFacadeService;
import com.bhu.vas.business.ds.user.service.UserWalletWithdrawApplyService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 提现申请UPay响应失败的后续操作处理
 * 由于提现操作本身不允许失败，因此对于uPay响应失败的的申请需要重新写入redis队列中并进行状态重置为提现中...
 * @author Edmond Lee
 * 
 */
public class WithdrawAppliesFailedRollbackLoader {
    private static Logger logger = LoggerFactory
	    .getLogger(WithdrawAppliesFailedRollbackLoader.class);
    
    
	@Resource
	private UserWalletFacadeService userWalletFacadeService;

	@Resource
	private UserWalletWithdrawApplyService userWalletWithdrawApplyService;
    
    public void execute(){
		logger.info("WithdrawAppliesFailedRollbackLoader starting...");
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
				System.out.println("to Redis prepare:"+jsonNotify);
				{	//保证写入redis后，提现申请设置成为转账中...状态
					BusinessEnumType.UWithdrawStatus current = BusinessEnumType.UWithdrawStatus.WithdrawDoing;
					CommdityInternalNotifyListService.getInstance().rpushWithdrawAppliesRequestNotify(jsonNotify);
					withdrawApply.addResponseDTO(WithdrawRemoteResponseDTO.build(current.getKey(), current.getName()));
					withdrawApply.setWithdraw_oper(current.getKey());
					userWalletFacadeService.getUserWalletWithdrawApplyService().update(withdrawApply);
				}
				System.out.println("to Redis ok:"+jsonNotify);
			}
			
		}
		logger.info("WithdrawAppliesFailedRollbackLoader done");
    }
}
