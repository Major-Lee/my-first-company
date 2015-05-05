package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.ds.device.dao.WifiDeviceDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceService extends AbstractCoreService<String,WifiDevice, WifiDeviceDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceDao wifiDeviceDao) {
		super.setEntityDao(wifiDeviceDao);
	}

	public TailPage<WifiDevice> findModelByOnline(int pageNo, int pageSize){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", true);
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		return super.findModelTailPageByModelCriteria(mc);
	}
	
//	public List<WifiDevice> findByIdsAndOnline(List<String> ids, boolean online){
//		ModelCriteria mc = new ModelCriteria();
//		mc.createCriteria().andColumnIn("id", ids).an.andColumnEqualTo("online", online);
//		return super.findModelByModelCriteria(mc);
//	}
	
	public long countByOnline(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", true);
		return super.countByModelCriteria(mc);
	}
	
	public long count(){
		return super.countByModelCriteria(new ModelCriteria());
	}
}
