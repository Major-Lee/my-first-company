/*package com.bhu.vas.business.backendonline.asyncprocessor.buservice;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.search.increment.IncrementBulkDocumentDTO;
import com.bhu.vas.api.dto.search.increment.IncrementEnum.IncrementActionEnum;
import com.bhu.vas.api.dto.search.increment.IncrementSingleDocumentDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.marker.BusinessMarkerService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.WifiDeviceRealtimeRateStatisticsStringService;
import com.bhu.vas.business.ds.charging.facade.ChargingFacadeService;
import com.bhu.vas.business.ds.charging.service.WifiDeviceSharedealConfigsService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGrayService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.ds.user.service.UserWifiDeviceService;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.bhu.vas.business.search.increment.KafkaMessageIncrementProducer;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;

*//**
 * backend专属业务service
 * @author tangzichao
 * @author Edmond Lee 修改设备清除和用户的绑定关系
 *//*
@Service
public class BackendBusinessService {
	
	@Resource
	private KafkaMessageIncrementProducer incrementMessageTopicProducer;
	

	*//**********************************     设备全量索引数据业务 start   *****************************************//*
	
	public void blukIndexs(List<String> macs){
		if(macs == null || macs.isEmpty()) return;
		
		incrementMessageTopicProducer.incrementDocument(IncrementBulkDocumentDTO.builder(macs, 
				IncrementActionEnum.WD_FullCreate, BusinessIndexDefine.WifiDevice.IndexUniqueId));
	}
	
	public void blukIndexs(List<String> macs){
		if(macs != null && !macs.isEmpty()){
			List<WifiDevice> wifiDevices = wifiDeviceService.findByIds(macs);
			if(wifiDevices != null && !wifiDevices.isEmpty()){
				List<WifiDeviceDocument> docs = new ArrayList<WifiDeviceDocument>();
				for(WifiDevice wifiDevice : wifiDevices){
					WifiDeviceDocument doc = new WifiDeviceDocument();
					
					String mac = wifiDevice.getId();
					//System.out.println("2="+mac);
					WifiDeviceGray wifiDeviceGray = wifiDeviceGrayService.getById(mac);
					WifiDeviceModule deviceModule = wifiDeviceModuleService.getById(mac);
					TagDevices tagDevices = tagDevicesService.getById(mac);
					//AgentDeviceClaim agentDeviceClaim = agentDeviceClaimService.getById(wifiDevice.getSn());
					String o_template = wifiDevicePersistenceCMDStateService.fetchDeviceVapModuleStyle(mac);
					long hoc = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(mac);

					User bindUser = null;
					String bindUserDNick = null;
					//Integer bindUserId = userDeviceService.fetchBindUid(mac);
					UserDevice userDevice = userDeviceService.fetchBindByMac(mac);
					if(userDevice != null){
						bindUserDNick = userDevice.getDevice_name();
						Integer bindUserId = userDevice.getId().getUid();
						if(bindUserId != null){
							bindUser = userService.getById(bindUserId);
						}
					}
					
					UserWifiDevice userWifiDevice = userWifiDeviceService.getById(mac);
					if(userWifiDevice != null){
						bindUserDNick = userWifiDevice.getDevice_name();
						Integer bindUserId = userWifiDevice.getUid();
						if(bindUserId != null){
							bindUser = userService.getById(bindUserId);
						}
					}
					
					WifiDeviceSharedNetwork wifiDeviceSharedNetwork = wifiDeviceSharedNetworkService.getById(mac);
					WifiDeviceSharedealConfigs wifiDeviceShareConfig = wifiDeviceSharedealConfigsService.getById(mac);
					String t_uc_extension = tagGroupRelationService.fetchPathWithMac(mac);
					
					if(wifiDeviceShareConfig != null){
						System.out.println("~~~~~~~~~~~~~~~~~~~~Canbe_turnoff:"+wifiDeviceShareConfig.isCanbe_turnoff());
					}
					doc = WifiDeviceDocumentHelper.fromNormalWifiDevice(wifiDevice, deviceModule, 
							wifiDeviceGray, bindUser, bindUserDNick, tagDevices,
							o_template, (int)hoc, wifiDeviceSharedNetwork, wifiDeviceShareConfig,t_uc_extension);
					docs.add(doc);
				}
				
				if(!docs.isEmpty()){
					wifiDeviceDataSearchService.bulkIndex(docs,true,true);
				}
			}
		}
	}
	
	*//**********************************     设备全量索引数据业务 end   *****************************************//*
}
*/