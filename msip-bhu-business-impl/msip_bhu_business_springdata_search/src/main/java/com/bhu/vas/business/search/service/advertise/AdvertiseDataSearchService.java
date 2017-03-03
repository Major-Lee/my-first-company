package com.bhu.vas.business.search.service.advertise;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;

import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.builder.advertise.AdvertiseSearchMessageBuilder;
import com.bhu.vas.business.search.builder.device.WifiDeviceSearchMessageBuilder;
import com.bhu.vas.business.search.core.condition.AbstractDataSearchConditionService;
import com.bhu.vas.business.search.core.condition.component.SearchConditionMessage;
import com.bhu.vas.business.search.core.field.FieldDefine;
import com.bhu.vas.business.search.model.advertise.AdvertiseDocument;
import com.bhu.vas.business.search.model.device.WifiDeviceDocument;
import com.bhu.vas.business.search.repository.advertise.AdvertiseDocumentRepository;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


@Service
public class AdvertiseDataSearchService extends AbstractDataSearchConditionService<AdvertiseDocument>{
    @Resource
    private AdvertiseDocumentRepository advertiseDocumentRepository;
	
	public AdvertiseDocumentRepository getRepository(){
		return advertiseDocumentRepository;
	}
	
	@Override
	public FieldDefine getFieldByName(String fieldName) {
		if(StringUtils.isEmpty(fieldName)) return null;
		return BusinessIndexDefine.Advertise.Field.getByName(fieldName);	}

	@Override
	public int retryOnConflict() {
		return BusinessIndexDefine.Advertise.RetryOnConflict;
	}

	public AdvertiseDocument searchById(String id){
		return this.getRepository().findOne(id);
	}
	
	/**
	 * 根据经纬度查询设备数量
	 * @param contextId
	 * @param lat
	 * @param lon
	 * @param distance
	 * @return
	 */
	public long searchCountByGeoPointDistance(String contextId, double lat, double lon, String distance){
		
		SearchConditionMessage scm = AdvertiseSearchMessageBuilder.builderSearchMessageWithGeoPointDistance(contextId, lat, lon, distance);
		if(scm == null || scm.equals(null)){
			return 0L;
		}
		return super.searchCountByConditionMessage(scm);
	}
	
	public Page<AdvertiseDocument> searchPageByCreated(){
		SearchConditionMessage scm = AdvertiseSearchMessageBuilder.builderSearchMessageByCreated();
		return super.searchByConditionMessage(scm, 0, 3);
	}
	
	/**
	 * 根据经纬度和adcode进行scan的iterator
	 * @param contextId
	 * @param lat
	 * @param lon
	 * @param distance
	 * @return 
	 * @return
	 */
	public Page<AdvertiseDocument> searchByGeoPointDistanceAndAdcode(String contextId, double lat, double lon, String distance,String adcode,int pageSize,int pageNo){
		
		SearchConditionMessage scm = AdvertiseSearchMessageBuilder.builderSearchMessageWithGeoPointDistanceAndAdcode(contextId, lat, lon, distance,adcode);
		return super.searchByConditionMessage(scm, pageNo, pageSize);
	}
	
}
