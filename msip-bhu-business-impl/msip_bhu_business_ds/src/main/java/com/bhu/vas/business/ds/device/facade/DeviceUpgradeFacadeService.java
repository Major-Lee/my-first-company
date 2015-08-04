package com.bhu.vas.business.ds.device.facade;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionBuilder;
import com.bhu.vas.api.rpc.user.dto.UpgradeDTO;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupRelationService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceVersionBuilderService;

@Service
public class DeviceUpgradeFacadeService {
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceGroupService wifiDeviceGroupService;

	@Resource
	private WifiDeviceGroupRelationService wifiDeviceGroupRelationService;
	
	@Resource
    private WifiDeviceVersionBuilderService wifiDeviceVersionBuilderService;
	
	public UpgradeDTO checkDeviceUpdate(String mac,WifiDevice wifiDevice){
		boolean isFirstGray = false;
		/*WifiDeviceVersionBuilder versionb = wifiDeviceVersionBuilderService.getById(isFirstGray?WifiDeviceVersionBuilder.VersionBuilder_FirstGray:WifiDeviceVersionBuilder.VersionBuilder_Normal);
		if(versionb == null || StringUtils.isEmpty(wifiDevice.getOrig_swver())) return false;
		int ret = DeviceHelper.compareDeviceVersions(wifiDevice.getOrig_swver(),versionb.getD_firmware_name());
		if(versionb.isForce_device_update() && ret == -1 ){
			return true;
		}else{
			return false;
		}
		
    	boolean forceDeviceUpdate = wifiDeviceVersionBuilderService.deviceVersionUpdateCheck(isFirstGray, wifiDevice.getOrig_swver());
    	if(forceDeviceUpdate){
    		//发送异步Device升级指令，指定早上4点升级 
        	long new_taskid = CMDBuilder.auto_taskid_fragment.getNextSequence();
        	String firmwareUpdateUrl = wifiDeviceVersionBuilderService.deviceVersionUpdateURL(isFirstGray);
        	if(StringUtils.isNotEmpty(firmwareUpdateUrl)){
        		String cmdPayload = CMDBuilder.builderDeviceUpgrade(mac, new_taskid, "02:00:00", "04:00:00", firmwareUpdateUrl);
        		deliverMessageService.sendWifiCmdCommingNotifyMessage(mac, new_taskid,OperationCMD.DeviceUpgrade.getNo(), cmdPayload);
        	}
    		//deliverMessageService.sendWifiCmdCommingNotifyMessage(mac, taskid, opt, payload);
    	}*/
		return null;
	}
	
}
