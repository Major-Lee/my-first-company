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
	public List<UserWalletLog> findListByTime(String time){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("transmode", "SDP").andColumnEqualTo("transtype", "P2C").andColumnLike("updated_at", time+"%");
		//mc.createCriteria().andColumnEqualTo("transtype", "P2C");
		//mc.createCriteria().andColumnLike("updated_at", time+"%");
		return super.findModelByModelCriteria(mc);
	}
	public List<UserWalletLog> findListByTimeField(String startTime,String endTime,int pageIndex,int pageSize){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("transmode", "SDP").andColumnEqualTo("transtype", "P2C").andColumnBetween("updated_at", startTime, endTime);
		mc.setLimit(pageSize);
		mc.setStart(pageIndex);
		return super.findModelByModelCriteria(mc);
	}
}
