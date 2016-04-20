package com.bhu.vas.business.ds.charging.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.charging.dto.WithdrawCostInfo;
import com.bhu.vas.api.rpc.charging.model.UserWithdrawCostConfigs;
import com.bhu.vas.business.ds.charging.dao.UserWithdrawCostConfigsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserWithdrawCostConfigsService extends AbstractCoreService<Integer,UserWithdrawCostConfigs, UserWithdrawCostConfigsDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	@Resource
	@Override
	public void setEntityDao(UserWithdrawCostConfigsDao userWithdrawCostConfigsDao) {
		super.setEntityDao(userWithdrawCostConfigsDao);
	}
	
	/**
	 * 获取当前对此用户生效的钱包配置
	 * 目前直接取Default_ConfigsID对应的数据
	 * @return
	 */
	public UserWithdrawCostConfigs userfulWithdrawConfigs(int uid){
		return this.getById(UserWithdrawCostConfigs.Default_ConfigsID);
	}
	
	public WithdrawCostInfo calculateWithdrawCost(int uid,String applyid,double cash){
		UserWithdrawCostConfigs walletConfigs = this.userfulWithdrawConfigs(uid);
		WithdrawCostInfo cost = WithdrawCostInfo.calculate(uid, applyid, cash, walletConfigs.getWithdraw_tax_percent(), walletConfigs.getWithdraw_trancost_percent());
		return cost;
	}
	
	public UserWithdrawCostConfigs addDefault(){
		UserWithdrawCostConfigs configs = new UserWithdrawCostConfigs();
		configs.setId(-1);
		configs.setWithdraw_tax_percent(0.20d);
		configs.setWithdraw_tax_percent(0.03d);
		this.insert(configs);
		return configs;
	}
}
