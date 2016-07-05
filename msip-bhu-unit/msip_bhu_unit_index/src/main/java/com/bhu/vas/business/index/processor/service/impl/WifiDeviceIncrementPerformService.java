package com.bhu.vas.business.index.processor.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.search.increment.IncrementEnum.IncrementActionEnum;
import com.bhu.vas.business.index.processor.service.IncrementPerformActionService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;

@Service
public class WifiDeviceIncrementPerformService extends IncrementPerformActionService{
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;

	@Override
	public void incrementDocumentActionHandle(String id, IncrementActionEnum actionEnum) {
		switch(actionEnum){
			case WD_FullCreate:
				incrementDocumentByFullCreate(id);
				break;
			default:
				break;
		}
	}
	
	/**
	 * 设备索引完整创建数据
	 * @param id
	 */
	public void incrementDocumentByFullCreate(String id){
		
	}


}
