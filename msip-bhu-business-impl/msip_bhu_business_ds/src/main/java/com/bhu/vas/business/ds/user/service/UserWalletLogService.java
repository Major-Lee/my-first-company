package com.bhu.vas.business.ds.user.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.user.model.UserWalletLog;
import com.bhu.vas.business.ds.user.dao.UserWalletLogDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

@Service
@Transactional("coreTransactionManager")
public class UserWalletLogService extends AbstractCoreService<Long,UserWalletLog, UserWalletLogDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	//@Resource
	//private SequenceService sequenceService;
	
	@Resource
	@Override
	public void setEntityDao(UserWalletLogDao userWalletLogDao) {
		super.setEntityDao(userWalletLogDao);
	}
	/*@Override
    public UserWalletLog insert(UserWalletLog entity) {
        if(entity.getId() == null)
            SequenceService.getInstance().onCreateSequenceKey(entity, false);
        return super.insert(entity);
    }*/
	
	public List<UserWalletLog> findModelByOrderids(List<String> orderids){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIn("orderid", orderids);
    	return super.findModelByModelCriteria(mc);
	}
}
