package com.bhu.vas.business.ds.device.facade;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.user.dto.UpgradeDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceCheckUpdateDTO;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
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
	private WifiDeviceGrayFacadeService wifiDeviceGrayFacadeService;
	
	private UpgradeDTO checkDeviceUpgrade(String dmac,String orig_swver){
		return wifiDeviceGrayFacadeService.deviceFWUpgradeAutoAction(dmac, orig_swver);
	}
	
	private UpgradeDTO checkDeviceUpgradeWithClientVer(String dmac,WifiDevice wifiDevice,String handset_device,String appCurrentVer){
		return wifiDeviceGrayFacadeService.deviceFWUpgradeAutoAction(dmac, wifiDevice.getOrig_swver());
	}
	
	private UpgradeDTO checkDeviceOMUpgrade(String dmac,String orig_swver,String orig_vap_module){
		return wifiDeviceGrayFacadeService.deviceOMUpgradeAutoAction(dmac,orig_swver, orig_vap_module);
	}
	
	public String clientForceDeviceUpgrade(String dmac,String orig_swver){
		UpgradeDTO upgrade = this.checkDeviceUpgrade(dmac, orig_swver);
		//UpgradeDTO upgrade = deviceUpgradeFacadeService.checkDeviceUpgrade(mac, wifiDevice);
    	if(upgrade != null && upgrade.isForceDeviceUpgrade()){
    		//long new_taskid = CMDBuilder.auto_taskid_fragment.getNextSequence();
    		//String cmdPayload = CMDBuilder.builderDeviceUpgrade(mac, new_taskid, StringHelper.EMPTY_STRING, StringHelper.EMPTY_STRING, upgrade.getUpgradeurl());
    		String cmdPayload = upgrade.buildUpgradeCMD(dmac, 0, StringHelper.EMPTY_STRING_GAP, StringHelper.EMPTY_STRING_GAP);
    		return cmdPayload;
    		//deliverMessageService.sendWifiCmdsCommingNotifyMessage(mac, /*new_taskid,OperationCMD.DeviceUpgrade.getNo(),*/ cmdPayload);
    	}
    	return null;
	}
	
	/**
	 * 绑定的设备版本小于该灰度要求的最小版本，APP在进行设备管理时，服务器将触发APP提示设备固件需要立即升级。
	 * @param dmac
	 * @param wifiDevice
	 * @param handset_device
	 * @param appver
	 * @return
	 */
	public UserDeviceCheckUpdateDTO clientCheckDeviceUpgrade(String dmac, WifiDevice wifiDevice,String handset_device,String appver){
		/*UpgradeDTO upgrade = null;
    	if(!BusinessRuntimeConfiguration.isInitialDeviceFirmwareVersion(wifiDevice.getOrig_swver())){
    		//非固件固化定义的版本，直接返回不需要强制升级
    		System.out.println(String.format("not initial device firmware version:[%s] for[%s]", wifiDevice.getOrig_swver(),wifiDevice.getId()));
    	}else{
        	upgrade = this.checkDeviceUpgradeWithClientVer(dmac, wifiDevice,handset_device,appver);
    	}*/
		UpgradeDTO upgrade = this.checkDeviceUpgradeWithClientVer(dmac, wifiDevice,handset_device,appver);
		if(upgrade != null){
			System.out.println(String.format("clientCheckDeviceUpgrade A decide upgrade[%s]",upgrade.toString()));
			if(StringUtils.isEmpty(upgrade.getMinid()) || StringHelper.MINUS_STRING_GAP.equals(upgrade.getMinid())){
				//这种情况则默认为minid == grayid
				System.out.println(String.format("clientCheckDeviceUpgrade A1  need upgrade"));
			}else{//如果定义了minid
				int ret = DeviceVersion.compareVersions(wifiDevice.getOrig_swver(), upgrade.getMinid());
				if(ret == -1){//设备版本小于该灰度要求的最小版本
					System.out.println(String.format("clientCheckDeviceUpgrade A2  need upgrade"));
				}else{
					System.out.println(String.format("clientCheckDeviceUpgrade B no upgrade"));
					//System.out.println(String.format("clientCheckDeviceUpgrade upgrade[%s]",upgrade.toString()));
					upgrade = null;
				}
			}
		}else{
			System.out.println("clientCheckDeviceUpgrade no need upgrade");
		}
		
    	UserDeviceCheckUpdateDTO retDTO = new UserDeviceCheckUpdateDTO();
    	retDTO.setMac(dmac);
    	//retDTO.setUid(uid);
    	retDTO.setOnline(wifiDevice.isOnline());
    	retDTO.setDut(upgrade!=null?upgrade.getDut():StringHelper.MINUS_STRING_GAP);
    	retDTO.setGray(upgrade!=null?upgrade.getGl():0);
    	retDTO.setForceDeviceUpdate(upgrade!=null?upgrade.isForceDeviceUpgrade():false);
    	retDTO.setForceAppUpdate(upgrade!=null?upgrade.isForceAppUpgrade():false);
    	retDTO.setCurrentDVB(wifiDevice.getOrig_swver());
    	retDTO.setCurrentAVB(upgrade!=null?upgrade.getCurrentAVB():null);
    	return retDTO;
	}
	
	public String deviceUpgradeFWCMDAfterOnline(String dmac,String orig_swver){
		UpgradeDTO upgrade = this.checkDeviceUpgrade(dmac, orig_swver);
		if (upgrade != null && upgrade.isForceDeviceUpgrade()) {
			//【1】如果设备版本为出厂版本，将会触发15分钟后升级策略；
			//【2】针对某个灰度指定版本的时间点为x，设备上线时，如果发现当前时间点y>x+48小时（暂定），将会触发设备15分钟后升级策略。
			if (BusinessRuntimeConfiguration.isInitialDeviceFirmwareVersion(upgrade.getCurrentDVB())) {
				String upgradeBeginTime = DateTimeHelper.formatDate(DateTimeHelper.dateMinuteAfter(new Date(), 15), DateTimeHelper.FormatPattern_Hms);
				String upgradeEndTime = DateTimeHelper.formatDate(DateTimeHelper.dateMinuteAfter(new Date(), 30), DateTimeHelper.FormatPattern_Hms);
				return upgrade.buildUpgradeCMD(dmac, 0, upgradeBeginTime,upgradeEndTime);
			}else{
				Date current  = new Date();
				Date grayPublished_at = upgrade.getCurrentGrayPublished_at();
				if(current.after(DateTimeHelper.getDateDaysAfter(grayPublished_at, 2))){
					String upgradeBeginTime = DateTimeHelper.formatDate(DateTimeHelper.dateMinuteAfter(new Date(), 15), DateTimeHelper.FormatPattern_Hms);
					String upgradeEndTime = DateTimeHelper.formatDate(DateTimeHelper.dateMinuteAfter(new Date(), 30), DateTimeHelper.FormatPattern_Hms);
					return upgrade.buildUpgradeCMD(dmac, 0, upgradeBeginTime,upgradeEndTime);
				}else{//缺省的2点到4点升级
					return upgrade.buildUpgradeCMD(dmac, 0, WifiDeviceHelper.Upgrade_Default_BeginTime,WifiDeviceHelper.Upgrade_Default_EndTime);
				}
			}
			/*// 如果是指定的版本出厂版本 并且 第一次注册创建时间超过指定定义的天数,立刻升级
			if (BusinessRuntimeConfiguration.isInitialDeviceFirmwareVersion(upgrade.getCurrentDVB())
					&& !DateTimeHelper.isTimeDaysRecent(wifiDevice.getCreated_at().getTime(),
							BusinessRuntimeConfiguration.Device_Firmware_ForceUpdateImmediately_AfterDays)) {
				payloads.add(upgrade.buildUpgradeCMD(dto.getMac(), 0, StringHelper.EMPTY_STRING_GAP,
						StringHelper.EMPTY_STRING_GAP));
			} else {
				// modify by EdmondLee@20160419
				// 针对某个灰度指定版本的时间点为x，设备上线时，如果发现当前时间点y>x+24小时（暂定），将会触发设备立即升级。降低产品运营成本
				if (upgrade.needImmediatelyUpgrade()) {
					payloads.add(upgrade.buildUpgradeCMD(dto.getMac(), 0, StringHelper.EMPTY_STRING_GAP,
							StringHelper.EMPTY_STRING_GAP));
				} else {
					payloads.add(
							upgrade.buildUpgradeCMD(dto.getMac(), 0, WifiDeviceHelper.Upgrade_Default_BeginTime,
									WifiDeviceHelper.Upgrade_Default_EndTime));
				}
			}*/
		}
		return null;
	}
	
	public String deviceUpgradeOMCMDAfterOnline(String dmac,String d_orig_swver,String orig_vap_module){
		UpgradeDTO upgrade = this.checkDeviceOMUpgrade(dmac,d_orig_swver, orig_vap_module);
		if (upgrade != null && upgrade.isForceDeviceUpgrade()
				&& WifiDeviceHelper.WIFI_DEVICE_UPGRADE_OM == upgrade.isFw()) {
			String cmd = upgrade.buildUpgradeCMD(dmac, 0l, WifiDeviceHelper.Upgrade_Default_BeginTime,
					WifiDeviceHelper.Upgrade_Default_EndTime);
			return cmd;
			//afterDeviceModuleOnlineThenCmdDown(dto.getMac(), cmd);
		}
		return null;
	}
	
	public String deviceUpgradeCMDFwOrOM4BackTaskTimer(String dmac,String d_orig_swver){
		UpgradeDTO upgrade = wifiDeviceGrayFacadeService.deviceFWUpgradeAutoAction(dmac,d_orig_swver);
		if(upgrade != null && upgrade.isForceDeviceUpgrade()){
			String payload = upgrade.buildUpgradeCMD(dmac, 0, WifiDeviceHelper.Upgrade_Default_BeginTime, WifiDeviceHelper.Upgrade_Default_EndTime);
			//downCmds.add(DownCmds.builderDownCmds(device.getId(), payload));
			System.out.println(String.format("mac[%s] cmd[%s]", dmac,payload));
			return payload;
		}else{//在固件不需要升级的时候，检测组件的升级
			WifiDeviceModule deviceModule = wifiDeviceGrayFacadeService.getWifiDeviceModuleService().getById(dmac);
			if(deviceModule!=null && StringUtils.isNotEmpty(deviceModule.getOrig_vap_module())){
				UpgradeDTO omUpgrade = wifiDeviceGrayFacadeService.deviceOMUpgradeAutoAction(dmac, d_orig_swver, deviceModule.getOrig_vap_module());
				if(omUpgrade != null && omUpgrade.isForceDeviceUpgrade()){
					String payload = omUpgrade.buildUpgradeCMD(dmac, 0, WifiDeviceHelper.Upgrade_Default_BeginTime, WifiDeviceHelper.Upgrade_Default_EndTime);
					//downCmds.add(DownCmds.builderDownCmds(device.getId(), payload));
					System.out.println(String.format("mac[%s] cmd[%s]", dmac,payload));
					return payload;
				}
			}
		}
		return null;
	}
	
	public WifiDeviceGrayFacadeService getWifiDeviceGrayFacadeService() {
		return wifiDeviceGrayFacadeService;
	}
}
