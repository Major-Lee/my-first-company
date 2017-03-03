package com.bhu.vas.business.ds.user.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.tag.model.TagGroupHandsetDetail;
import com.bhu.vas.api.rpc.user.model.UserConsumptiveWalletLog;
import com.bhu.vas.business.ds.user.dao.UserConsumptiveWalletLogDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;

@Service
@Transactional("coreTransactionManager")
public class UserConsumptiveWalletLogService extends AbstractCoreService<Long,UserConsumptiveWalletLog, UserConsumptiveWalletLogDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	//@Resource
	//private SequenceService sequenceService;
	
	@Resource
	@Override
	public void setEntityDao(UserConsumptiveWalletLogDao userConsumptiveWalletLogDao) {
		super.setEntityDao(userConsumptiveWalletLogDao);
	}
	
	
	public List<Map<String, Object>> selectListBySumCash(String pattern,String orderid ,String transtype,String start,String end,int pageNo ,int pageSize) {
		Map<String, Object> map = new HashMap<>();
		map.put("pattern", pattern);
		map.put("orderid", orderid);
		map.put("transtype", transtype);
		if (pageNo != 0 && pageSize !=0) {
			map.put("pn", (pageNo - 1) * pageSize);
			map.put("ps", pageSize);
		}
		if(start !=null && end !=null){
			map.put("start", start);
			map.put("end", end);
		}
		
		return this.getEntityDao().getSqlSessionSlaverTemplate().selectList(
						UserConsumptiveWalletLog.class.getName()+ ".selectListBySumCash", map);
	}
	
}
