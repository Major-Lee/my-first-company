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
/**
 * 前置条件 当前设备版本如果低于服务端指定的设备最新版本，则会自动进行升级，当然不是实时的
 * 支持规则
 * 1、服务端判定设备更新规则
 * 		根据需要升级的版本号以及设备本身的版本号进行判定
 * 2、客户端版本号判定设备更新规则
 * 		根据客户端的版本号参数A 服务端指定的客户端最小版本号B，设备当前的版本C 服务端指定的设备最新版本D  进行判定
 * 		如果 A > B
 * 				如C < D 则 设备必须升级，客户端不升级
 * 				否则  设备不升级，客户端不升级
 * 		如果 A < B
 * 				如C < D 则 设备不升级，客户端不升级
 * 				如C = D 则 设备不升级，客户端升级
 * 				否则C > D  设备不升级，客户端升级
 * 		如果 A = B
 * 				如C < D 设备必须升级 客户端不升级
 * 				否则 设备不升级，客户端不升级
 * @author Edmond
 *
 */
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
	
	
	public UpgradeDTO fetchForceDeviceUpgrade(String mac){
		boolean isFirstGray = wifiDeviceGroupFacadeService.isDeviceInGrayGroup(mac);
		WifiDeviceVersionBuilder versionb = wifiDeviceVersionBuilderService.getById(isFirstGray?WifiDeviceVersionBuilder.VersionBuilder_FirstGray:WifiDeviceVersionBuilder.VersionBuilder_Normal);
		if(versionb == null) return new UpgradeDTO(isFirstGray,false);
		return new UpgradeDTO(isFirstGray,true,versionb.getD_firmware_name(),versionb.getFirmware_upgrade_url());
	}
	
	public UpgradeDTO checkDeviceUpgrade(String mac,WifiDevice wifiDevice){
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
			resultDto.setCurrentDVB(wifiDevice.getOrig_swver());
			System.out.println(String.format("-----checkDeviceUpgrade [%s] upgradeDTO[%s]",mac,resultDto.toString()));
		}
		return resultDto;
		//return checkDeviceUpgrade(mac,wifiDevice,null,null);
	}
/*	
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
			resultDto.setCurrentDVB(wifiDevice.getOrig_swver());
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
	}*/
	
	public UpgradeDTO checkDeviceUpgradeWithClientVer(String mac,WifiDevice wifiDevice,String handset_device,String appVer){
		UpgradeDTO resultDto = null;
		boolean isFirstGray = wifiDeviceGroupFacadeService.isDeviceInGrayGroup(mac);
		if(StringUtils.isEmpty(wifiDevice.getOrig_swver()) || StringUtils.isEmpty(handset_device) || StringUtils.isEmpty(appVer)){
			resultDto = new UpgradeDTO(isFirstGray,false);
		}else{
			WifiDeviceVersionBuilder versionb = wifiDeviceVersionBuilderService.getById(isFirstGray?WifiDeviceVersionBuilder.VersionBuilder_FirstGray:WifiDeviceVersionBuilder.VersionBuilder_Normal);
			if(versionb == null) return new UpgradeDTO(isFirstGray,false);
			boolean needDeviceUpdate = false;
			boolean needAppUpdate = false;
			try{
				int clientver_ret = VersionHelper.compareVersion(appVer,versionb.getMin_adr_version());
				int devicever_ret = DeviceHelper.compareDeviceVersions(wifiDevice.getOrig_swver(),versionb.getD_firmware_name());
				if(clientver_ret >= 0){
					if(devicever_ret < 0){
						needDeviceUpdate = true;
					}
				}else/*(clientver_ret < 0)*/{
					if(devicever_ret >=0 ){
						needDeviceUpdate = false;
						needAppUpdate = true;
					}/*else if(devicever_ret == 0){
						needDeviceUpdate = false;
						needAppUpdate = true;
					}*/
				}
			}catch(Exception ex){
				ex.printStackTrace(System.out);
			}
			if(needDeviceUpdate && versionb.isForce_device_update()){
				resultDto = new UpgradeDTO(isFirstGray,true,versionb.getD_firmware_name(),versionb.getFirmware_upgrade_url());
			}else{
				resultDto = new UpgradeDTO(isFirstGray,false,versionb.getD_firmware_name(),versionb.getFirmware_upgrade_url());
			}
			resultDto.setCurrentDVB(wifiDevice.getOrig_swver());
			resultDto.setCurrentAVB(appVer);
			DeviceEnum handsetDevice = DeviceEnum.getBySName(handset_device);
			if(DeviceEnum.isHandsetDevice(handsetDevice)){
				try{
					if(DeviceEnum.HandSet_ANDROID_Type.endsWith(handsetDevice.getType())){//安卓
						if(versionb.isForce_adr_app_update() && needAppUpdate){
							resultDto.setForceAppUpgrade(true);
						}
					}else{//
						if(versionb.isForce_ios_app_update() && needAppUpdate){
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
	}
}
