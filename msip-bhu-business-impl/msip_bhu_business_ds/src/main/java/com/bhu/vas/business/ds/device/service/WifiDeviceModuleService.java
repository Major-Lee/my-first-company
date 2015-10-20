package com.bhu.vas.business.ds.device.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.business.ds.device.dao.WifiDeviceModuleDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.TailPage;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceModuleService extends AbstractCoreService<String,WifiDeviceModule, WifiDeviceModuleDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceModuleDao wifiDeviceModuleDao) {
		super.setEntityDao(wifiDeviceModuleDao);
	}

	public TailPage<WifiDeviceModule> findModelByOnline(int pageNo, int pageSize){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", true);
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		return super.findModelTailPageByModelCriteria(mc);
	}
	
	public List<String> filterOnlineIdsWith(List<String> ids, boolean online){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIn("id", ids).andColumnEqualTo("online", online);
		return super.findIdsByModelCriteria(mc);
	}
	
	public long countByOnline(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", true);
		return super.countByModelCriteria(mc);
	}
	
	public long count(){
		return super.countByModelCriteria(new ModelCriteria());
	}
}
