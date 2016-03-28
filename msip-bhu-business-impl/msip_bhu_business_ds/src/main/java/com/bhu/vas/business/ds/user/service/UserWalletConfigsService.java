package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.dto.ApplyCost;
import com.bhu.vas.api.rpc.user.model.UserWalletConfigs;
import com.bhu.vas.business.ds.user.dao.UserWalletConfigsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.helper.ArithHelper;

@Service
@Transactional("coreTransactionManager")
public class UserWalletConfigsService extends AbstractCoreService<Integer,UserWalletConfigs, UserWalletConfigsDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	@Resource
	@Override
	public void setEntityDao(UserWalletConfigsDao userWalletConfigsDao) {
		super.setEntityDao(userWalletConfigsDao);
	}
	
	/**
	 * 获取当前对此用户生效的钱包配置
	 * 目前直接取Default_ConfigsID对应的数据
	 * @return
	 */
	public UserWalletConfigs userfulWalletConfigs(int uid){
		return this.getById(UserWalletConfigs.Default_ConfigsID);
	}
	
	/**
	 * 计算收益分成
	 * @param uid
	 * @param cash
	 * @return
	 */
	public double calculateSharedeal(int uid,double cash){
		UserWalletConfigs walletConfigs = this.userfulWalletConfigs(uid);
		double realIncommingCash = ArithHelper.round(ArithHelper.mul(cash, walletConfigs.getSharedeal_percent()),2);
		return realIncommingCash;
	}
	
	public ApplyCost calculateApplyCost(int uid,double cash){
		UserWalletConfigs walletConfigs = this.userfulWalletConfigs(uid);
		ApplyCost cost = new ApplyCost(cash);
		cost.calculate(walletConfigs.getWithdraw_tax_percent(),walletConfigs.getWithdraw_trancost_percent());
		return cost;
		/*if(cash > 0){
			this.setTaxcost(ArithHelper.round(ArithHelper.mul(cash, withdraw_tax_percent),2));
			this.setTranscost(ArithHelper.round(ArithHelper.mul(cash, withdraw_trancost_percent),2));
		}*/
	}
}
