package com.bhu.vas.business.ds.device.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceGroup;
import com.bhu.vas.business.ds.device.dao.WifiDeviceGroupDao;
import com.bhu.vas.business.ds.sequence.service.SequenceService;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
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
		boolean hasParent = group.getPid() != 0;//(group.getPid()!=null && group.getPid().intValue()!= 0);
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
		entity.setHaschild(false);
		return super.insert(entity);
	}
	
	/**
	 * 通过path获取所有子节点，包括子节点的子节点
	 * @param path
	 * @return
	 */
	public List<WifiDeviceGroup> fetchAllByPath(String path,boolean withSelf){
		ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		createCriteria.andSimpleCaulse(" 1=1 ");
		if(!withSelf){
			createCriteria.andColumnNotEqualTo("path", path);
		}
		createCriteria.andColumnLike("path", path+"%");
		List<WifiDeviceGroup> groups = this.findModelByModelCriteria(mc);
		return groups;
	}
	
	public int countAllByPath(String path,boolean withSelf){
		ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		createCriteria.andSimpleCaulse(" 1=1 ");
		if(!withSelf){
			createCriteria.andColumnNotEqualTo("path", path);
		}
		createCriteria.andColumnLike("path", path+"%");
		return this.countByModelCriteria(mc);
	}
}
