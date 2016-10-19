package com.bhu.vas.business.ds.device.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.tag.model.TagGroupHandsetDetail;
import com.bhu.vas.business.ds.device.dao.WifiDeviceDao;
import com.smartwork.msip.business.abstractmsd.service.AbstractCoreService;
import com.smartwork.msip.cores.helper.StringHelper;
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
	
	/*public List<WifiDevice> findOnlineByIds(List<String> ids, boolean online){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIn("id", ids).an.andColumnEqualTo("online", online);
		return super.findModelByModelCriteria(mc);
	}*/
	
	public List<String> filterOnlineIdsWith(List<String> ids, boolean online){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIn("id", ids).andColumnEqualTo("online", online);
		return super.findIdsByModelCriteria(mc);
	}
	
	public List<WifiDevice> findListByChannelLv1(String channel_lv1){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("channel_lv1", channel_lv1);
		return super.findModelByModelCriteria(mc);
	}
	
	public long countByOnline(){
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("online", true);
		return super.countByModelCriteria(mc);
	}
	
	public long count(){
		return super.countByModelCriteria(new ModelCriteria());
	}
	
	/**
	 * 
	 * @param field 要搜索的字段名
	 * @param isDistinct 是否去重
	 * @param isNotNull 是否不能为空
	 * @param judge where的字段
	 * @param param  字段限定
	 * @return
	 */
	public List<Map<String, Object>> selectByField(String field,boolean isDistinct,boolean isNotNull,String judge,String param) {
		Map<String, Object> map = new HashMap<>();
		map.put("field", field);
		
		if(isDistinct){
			map.put("isDistinct", 1);
		}else{
			map.put("isDistinct", 0);
		}
		
		if(isNotNull){
			map.put("isNotNull", 1);
		}else{
			map.put("isNotNull", 0);
		}
		if(judge !=null){
			map.put("judge", judge);
		}
		if(param !=null){
			map.put("param", param);
		}
		return super
				.getEntityDao()
				.getSqlSessionMasterTemplate()
				.selectList(
						WifiDevice.class.getName()
								+ ".selectByField", map);
	}
	
}
