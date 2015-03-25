package com.bhu.vas.business.search.service.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.bulk.BulkResponse;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.search.WifiDeviceIndexDTO;
import com.bhu.vas.business.search.constants.BusinessIndexConstants;
import com.bhu.vas.business.search.indexable.WifiDeviceIndexableComponent;
import com.bhu.vas.business.search.mapable.WifiDeviceMapableComponent;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.es.exception.ESException;
import com.smartwork.msip.es.service.IndexService;

/**
 * 索引wifi设备的业务service类 
 * @author lawliet
 *
 */
@Service
public class WifiDeviceIndexService extends IndexService<WifiDeviceMapableComponent,WifiDeviceIndexableComponent>{
	
	public void createIndexComponent(WifiDeviceIndexDTO indexDto) throws IOException, ESException{
		super.createIndexComponent(buildIndexableComponent(indexDto));
	}
	
	public boolean createIndexComponents(List<WifiDeviceIndexDTO> indexDtos) throws IOException, ESException{
		List<WifiDeviceIndexableComponent> components = new ArrayList<WifiDeviceIndexableComponent>();
		for(WifiDeviceIndexDTO indexDto : indexDtos){
			components.add(buildIndexableComponent(indexDto));
		}
		BulkResponse repsonse = super.createIndexComponents(components);
		if(repsonse.hasFailures()){
			return false;
		}
		return true;
	}
	
	public WifiDeviceIndexableComponent buildIndexableComponent(WifiDeviceIndexDTO indexDto){
		WifiDeviceIndexableComponent component = new WifiDeviceIndexableComponent();
		component.setId(indexDto.getWifiId());
		component.addLocation(indexDto.getPoint());
		component.setOnline(indexDto.getOnline());
		component.setCount(indexDto.getCount());
		component.setRegister_at(indexDto.getRegister_at());
		component.setShowaddress(indexDto.getFormat_address());
		component.setAddress(indexDto.getAddress());
		component.setI_update_at(DateTimeHelper.getDateTime());
		return component;
		
	}
		
	public void warmup() throws Exception{
		super.createResponseAndMapping();
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
	public String getIndexRefresh(){
		return null;
	}
	
	@Override
	public Class<WifiDeviceMapableComponent> getMapableComponent() {
		return WifiDeviceMapableComponent.class;
	}
}
