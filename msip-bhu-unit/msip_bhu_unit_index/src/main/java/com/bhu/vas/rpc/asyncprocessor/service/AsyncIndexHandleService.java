package com.bhu.vas.rpc.asyncprocessor.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
//import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceRelationMService;


import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGray;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSharedNetwork;
import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.api.rpc.user.model.User;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGrayService;
import com.bhu.vas.business.ds.device.service.WifiDeviceModuleService;
import com.bhu.vas.business.ds.device.service.WifiDevicePersistenceCMDStateService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSharedNetworkService;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.model.WifiDeviceDocumentHelper;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.rpc.asyncprocessor.dto.AsyncIndexDTO;
import com.bhu.vas.rpc.asyncprocessor.dto.AsyncWifiDeviceBlukFullIndexDTO;

@Service
public class AsyncIndexHandleService {
	private final Logger logger = LoggerFactory.getLogger(AsyncIndexHandleService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceGrayService wifiDeviceGrayService;
	
	@Resource
	private WifiDeviceModuleService wifiDeviceModuleService;
	
	@Resource
	private TagDevicesService tagDevicesService;
	
	@Resource
	private WifiDevicePersistenceCMDStateService wifiDevicePersistenceCMDStateService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserDeviceService userDeviceService;
	
	@Resource
	private WifiDeviceSharedNetworkService wifiDeviceSharedNetworkService;
	
	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;
	/**
	 * 订单支付成功后续处理
	 * @param message
	 */
	public void WifiDeviceBlukFullIndexHandle(AsyncIndexDTO asyncIndexDto){
		System.out.println("WifiDeviceBlukFullIndexHandle");
		if(asyncIndexDto == null) return;
		AsyncWifiDeviceBlukFullIndexDTO action_dto = (AsyncWifiDeviceBlukFullIndexDTO)asyncIndexDto;
		List<String> macs = action_dto.getMacs();
		
		logger.info(String.format("AsyncMsgHandleCommdityService wifiDeviceBlukFullIndexHandle message[%s]", macs));
		
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
					
					WifiDeviceSharedNetwork wifiDeviceSharedNetwork = wifiDeviceSharedNetworkService.getById(mac);
					
					doc = WifiDeviceDocumentHelper.fromNormalWifiDevice(wifiDevice, deviceModule, 
							wifiDeviceGray, bindUser, bindUserDNick, tagDevices,
							o_template, (int)hoc, wifiDeviceSharedNetwork);
					docs.add(doc);
				}
				
				if(!docs.isEmpty()){
					wifiDeviceDataSearchService.bulkIndex(docs);
				}
			}
		}
		logger.info(String.format("AsyncMsgHandleCommdityService wifiDeviceBlukFullIndexHandle message[%s] successful", macs));
	}
}
