package com.bhu.vas.business.ds.device.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceGroup;
import com.bhu.vas.business.ds.device.dao.WifiDeviceGroupDao;
import com.bhu.vas.business.ds.sequence.service.SequenceService;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
//EntityCacheableSpliterService
@Service
@Transactional("coreTransactionManager")
public class WifiDeviceGroupService extends AbstractCoreService<Integer,WifiDeviceGroup, WifiDeviceGroupDao>{//EntityCacheableSpliterService<StorePurchasedItemPK,StorePurchasedItem, StorePurchasedItemDao,Integer>{//EntitySpliterService
	
	@Resource
	SequenceService sequenceService;
	
	@Resource
	@Override
	public void setEntityDao(WifiDeviceGroupDao wifiDeviceGroupDao) {
		super.setEntityDao(wifiDeviceGroupDao);
	}

	public String generateRelativePath(WifiDeviceGroup group){//,boolean hasParent){
		if(group == null) return null;
		boolean hasParent = (group.getPid()!=null && group.getPid().intValue()!= 0);
		if(!hasParent){
			StringBuilder sb = new StringBuilder();
			sb.append(group.getId()).append('/');
			return sb.toString();
		}else{
			WifiDeviceGroup parentCate = this.getById(group.getPid());
			StringBuilder sb = new StringBuilder();
			sb.append(parentCate.getPath()).append(group.getId()).append('/');
			return sb.toString();
		}
	}
	
	@Override
	public WifiDeviceGroup insert(WifiDeviceGroup entity) {
		if(entity.getId() == null)
			sequenceService.onCreateSequenceKey(entity, false);
		entity.setPath(generateRelativePath(entity));
		return super.insert(entity);
	}
}
