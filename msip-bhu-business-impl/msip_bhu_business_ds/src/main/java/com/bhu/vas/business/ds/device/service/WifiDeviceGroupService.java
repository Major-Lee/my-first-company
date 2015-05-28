package com.bhu.vas.business.ds.device.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDeviceGroup;
import com.bhu.vas.business.ds.device.dao.WifiDeviceGroupDao;
import com.bhu.vas.business.ds.sequence.service.SequenceService;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.helper.StringHelper;
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
		/*ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		createCriteria.andSimpleCaulse(" 1=1 ");
		if(!withSelf){
			createCriteria.andColumnNotEqualTo("path", path);
		}
		createCriteria.andColumnLike("path", path+"%");*/
		List<WifiDeviceGroup> groups = this.findModelByModelCriteria(builderModelCriteriaByPath(path,withSelf));
		return groups;
	}
	
	public int countAllByPath(String path,boolean withSelf){
		/*ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		createCriteria.andSimpleCaulse(" 1=1 ");
		if(!withSelf){
			createCriteria.andColumnNotEqualTo("path", path);
		}
		createCriteria.andColumnLike("path", path+"%");*/
		return this.countByModelCriteria(builderModelCriteriaByPath(path,withSelf));
	}
	
	/**
	 * 此种删除方式不会更新缓存，可能存在问题，但效率相对高
	 * @param path
	 * @param withSelf
	 * @return
	 */
	public boolean removeAllByPath(String path,boolean withSelf){
		/*ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		createCriteria.andSimpleCaulse(" 1=1 ");
		if(!withSelf){
			createCriteria.andColumnNotEqualTo("path", path);
		}
		createCriteria.andColumnLike("path", path+"%");*/
		this.deleteByCommonCriteria(builderModelCriteriaByPath(path,withSelf));
		return true;
	}
	
	/**
	 * 此种删除方式会更新缓存，不存在问题，但效率相对低
	 * @param path
	 * @param withSelf
	 * @return
	 */
	public boolean removeAllByPathStepByStep(String path,boolean withSelf){
		List<WifiDeviceGroup> allByPath = fetchAllByPath(path,withSelf);
		for(WifiDeviceGroup group:allByPath){
			this.deleteById(group.getId());
		}
		return true;
	}
	
	private static ModelCriteria builderModelCriteriaByPath(String path,boolean withSelf){
		ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		createCriteria.andSimpleCaulse(" 1=1 ");
		if(!withSelf){
			createCriteria.andColumnNotEqualTo("path", path);
		}
		createCriteria.andColumnLike("path", path+"%");
		return mc;
	}
	
	/**
	 * 还需要删除gid及其所有的子节点
	 * @param uid
	 * @param gids
	 */
	public void cleanUpByIds(Integer uid,String gids){
		String[] arrayresids = gids.split(StringHelper.COMMA_STRING_GAP);
		for(String residstr:arrayresids){
			Integer resid = new Integer(residstr);
			WifiDeviceGroup group = this.getById(resid);
			if(group != null){
				//int gid = group.getId().intValue();
				int pid = group.getPid();
				removeAllByPathStepByStep(group.getPath(),true);
				//判定每个gid的parentid是否为hanchild
				if(pid != 0){
					WifiDeviceGroup parent_group = this.getById(pid);
					int count  = countAllByPath(parent_group.getPath(),false);
					if(count == 0 && parent_group.isHaschild()){
						parent_group.setHaschild(false);
						this.update(parent_group);
					}
				}else{//pid == 0 本身是根节点，被删除后，无需动作
					;
				}
				
			}
		}
	}
}
