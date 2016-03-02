package com.bhu.vas.business.ds.user.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserWalletConfigs;
import com.bhu.vas.business.ds.user.dao.UserWalletConfigsDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

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
}
