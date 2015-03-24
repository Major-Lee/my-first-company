package com.bhu.vas.business.search.service.device;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.search.constants.BusinessIndexConstants;
import com.bhu.vas.business.search.indexable.WifiDeviceIndexableComponent;
import com.bhu.vas.business.search.mapable.WifiDeviceMapableComponent;
import com.bhu.vas.business.search.service.device.dto.WifiDeviceIndexDTO;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.es.service.IndexService;

/**
 * 索引wifi设备的业务service类 
 * @author lawliet
 *
 */
@Service
public class WifiDeviceIndexService extends IndexService<WifiDeviceMapableComponent,WifiDeviceIndexableComponent>{
	
	public WifiDeviceIndexableComponent buildIndexableComponent(WifiDeviceIndexDTO indexDto){
		WifiDeviceIndexableComponent component = new WifiDeviceIndexableComponent();
		component.setId(indexDto.getWifiId());
		component.addLocations(indexDto.getPoint());
		component.setOnline(indexDto.getOnline());
		component.setCount(indexDto.getCount());
		component.setRegister_at(indexDto.getRegister_at());
		component.setShowaddress(indexDto.getFormat_address());
		component.setAddress(indexDto.getAddress());
		component.setI_update_at(DateTimeHelper.getDateTime());
		return component;
	}
		
	@Override
	public String getIndexName() {
		return BusinessIndexConstants.WifiDeviceIndex;
	}

	@Override
	public String getIndexTypeName() {
		return BusinessIndexConstants.Types.WifiDeviceType;
	}

	@Override
	public Class<WifiDeviceMapableComponent> getMapableComponent() {
		return WifiDeviceMapableComponent.class;
	}
}
