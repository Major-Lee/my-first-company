package com.bhu.vas.business.search.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.core.condition.AbstractDataSearchConditionService;
import com.bhu.vas.business.search.core.field.FieldDefine;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.repository.WifiDeviceDocumentRepository;

@Service
public class WifiDeviceDataSearchService extends AbstractDataSearchConditionService<WifiDeviceDocument>{
    @Resource
    private WifiDeviceDocumentRepository wifiDeviceDocumentRepository;
	
	public WifiDeviceDocumentRepository getRepository(){
		return wifiDeviceDocumentRepository;
	}
	
	@Override
	public FieldDefine getFieldByName(String fieldName){
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
	 * @param d_
	 * @return
	 */
/*	public Page<WifiDeviceDocument> searchPageByUidAndDut(Integer u_id, String d_dut, int pageNo, int pageSize){
		if(u_id == null || StringUtils.isEmpty(d_dut)) return null;
		
//		SearchCondition sc_d_dut = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_dut);
//		SearchCondition sc_u_id = new SearchCondition(BusinessIndexDefine.WifiDevice.
//				Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(u_id));
		
		SearchCondition sc_d_dut = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.D_DEVICEUNITTYPE.getName(), SearchConditionPattern.StringEqual.getPattern(), d_dut);
		SearchCondition sc_u_id = SearchCondition.builderSearchCondition(BusinessIndexDefine.WifiDevice.
				Field.U_ID.getName(), SearchConditionPattern.StringEqual.getPattern(), String.valueOf(u_id));
		
		SearchConditionPack pack_must = SearchConditionPack.builderSearchConditionPackWithConditions(sc_d_dut, sc_u_id);
		
		SearchConditionMessage scm = SearchConditionMessage.builderSearchConditionMessage(pack_must);
		return super.searchByConditionMessage(scm, pageNo, pageSize);
	}*/
}
