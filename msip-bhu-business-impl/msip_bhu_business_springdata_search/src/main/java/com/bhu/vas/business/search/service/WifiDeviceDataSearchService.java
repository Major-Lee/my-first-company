package com.bhu.vas.business.search.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.core.condition.AbstractDataSearchConditionService;
import com.bhu.vas.business.search.core.condition.component.SearchCondition;
import com.bhu.vas.business.search.core.condition.component.SearchConditionMessage;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPack;
import com.bhu.vas.business.search.core.condition.component.SearchConditionPattern;
import com.bhu.vas.business.search.core.field.FieldDefine;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.repository.WifiDeviceDocumentRepository;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

@Service
public class WifiDeviceDataSearchService extends AbstractDataSearchConditionService<WifiDeviceDocument>{
    @Resource
    private WifiDeviceDocumentRepository wifiDeviceDocumentRepository;
	
	public WifiDeviceDocumentRepository getRepository(){
		return wifiDeviceDocumentRepository;
	}
	
	@Override
	public FieldDefine getFieldByName(String fieldName){
		if(StringUtils.isEmpty(fieldName)) return null;
		return BusinessIndexDefine.WifiDevice.Field.getByName(fieldName);
	}
	
	@Override
	public int retryOnConflict(){
		return BusinessIndexDefine.WifiDevice.RetryOnConflict;
	}
	/**
	 * 根据条件搜索数据
	 * 绑定设备的用户id
	 * 设备的业务线类型
	 * @param u_id
	 * @param d_dut
	 * @return
	 */
	public List<WifiDeviceDocument> searchListByUidAndDut(Integer u_id, String d_dut){
		if(u_id == null) return Collections.emptyList();
		
		SearchConditionPack pack_must = null;
		
		SearchCondition sc_u_id = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(u_id));
		
		if(StringUtils.isNotEmpty(d_dut)){
			SearchCondition sc_d_dut = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
					Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_dut);
			pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_d_dut, sc_u_id);
		}else{
			pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_u_id);
		}

		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		return super.searchByConditionMessage(scm);
	}
	
	/**
	 * 根据message动态条件进行scan的iterator
	 * @param message
	 * @param notify
	 */
	public void iteratorAll(String message, IteratorNotify<Page<WifiDeviceDocument>> notify){
		super.iteratorAll(BusinessIndexDefine.WifiDevice.IndexNameNew, BusinessIndexDefine.WifiDevice.Type, 
				message, notify);
	}
}
