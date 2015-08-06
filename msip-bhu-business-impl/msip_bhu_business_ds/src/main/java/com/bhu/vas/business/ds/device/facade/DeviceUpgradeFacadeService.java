package com.bhu.vas.business.ds.device.facade;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceVersionBuilder;
import com.bhu.vas.api.rpc.user.dto.UpgradeDTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceVersionBuilderService;
import com.smartwork.msip.cores.helper.VersionHelper;

@Service
public class DeviceUpgradeFacadeService {
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceGroupService wifiDeviceGroupService;

	@Resource
	private WifiDeviceGroupFacadeService wifiDeviceGroupFacadeService;
	
	@Resource
    private WifiDeviceVersionBuilderService wifiDeviceVersionBuilderService;
	
	public UpgradeDTO checkDeviceUpgrade(String mac,WifiDevice wifiDevice){
		return checkDeviceUpgrade(mac,wifiDevice,null,null);
	}
	
	public UpgradeDTO checkDeviceUpgrade(String mac,WifiDevice wifiDevice,String handset_device,String appVer){
		UpgradeDTO resultDto = null;
		boolean isFirstGray = wifiDeviceGroupFacadeService.isDeviceInGrayGroup(mac);
		if(StringUtils.isEmpty(wifiDevice.getOrig_swver())){
			resultDto = new UpgradeDTO(isFirstGray,false);
		}else{
			WifiDeviceVersionBuilder versionb = wifiDeviceVersionBuilderService.getById(isFirstGray?WifiDeviceVersionBuilder.VersionBuilder_FirstGray:WifiDeviceVersionBuilder.VersionBuilder_Normal);
			if(versionb == null) return new UpgradeDTO(isFirstGray,false);
			int ret = DeviceHelper.compareDeviceVersions(wifiDevice.getOrig_swver(),versionb.getD_firmware_name());
			if(versionb.isForce_device_update() && ret == -1){
				resultDto = new UpgradeDTO(isFirstGray,true,versionb.getD_firmware_name(),versionb.getFirmware_upgrade_url());
			}else{
				resultDto = new UpgradeDTO(isFirstGray,false,versionb.getD_firmware_name(),versionb.getFirmware_upgrade_url());
			}
			resultDto.setCurrentAVB(appVer);
			DeviceEnum handsetDevice = DeviceEnum.getBySName(handset_device);
			if(DeviceEnum.isHandsetDevice(handsetDevice)){
				try{
					if(DeviceEnum.HandSet_ANDROID_Type.endsWith(handsetDevice.getType())){//安卓
						if(versionb.isForce_adr_app_update() && VersionHelper.compareVersion(versionb.getMin_adr_version(),appVer)>0){
							resultDto.setForceAppUpgrade(true);
						}
					}else{//
						if(versionb.isForce_ios_app_update() && VersionHelper.compareVersion(versionb.getMin_ios_version(),appVer)>0){
							resultDto.setForceAppUpgrade(true);
						}
					}
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
			System.out.println(String.format("-----checkDeviceUpgrade [%s] upgradeDTO[%s]",mac,resultDto.toString()));
		}
		
		return resultDto;
		/*
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
	}
	
}
