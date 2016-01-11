package com.bhu.vas.plugins.quartz;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.WifiDeviceHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.user.dto.UpgradeDTO;
import com.bhu.vas.business.ds.device.facade.WifiDeviceGrayFacadeService;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * 对于所属灰度的设备 进行定时任务自动升级
 * 定时任务只对固件进行升级
 * 前置条件：
 * 	1、灰度定义的设备除了特殊灰度外
 * 	2、其他灰度的设备就是除去所有的t_wifi_devices_grays内的设备外的设备
 *  3、所有条件均需加上设备类型dut
 * @author Edmond Lee
 *
 */
public class WifiDeviceOnlineUpgradeLoader {
	private static Logger logger = LoggerFactory.getLogger(WifiDeviceOnlineUpgradeLoader.class);
	
	@Resource
	private IDaemonRpcService daemonRpcService;
	
	@Resource
	private WifiDeviceGrayFacadeService wifiDeviceGrayFacadeService;
	
	public void execute() {
		logger.info("WifiDeviceOnlineUpgradeLoader starting...");
		//beforeExecute();
		int total = 0;
		try{
			List<DownCmds> downCmds = new ArrayList<DownCmds>();
			//缩小范围，目前只在uRouter中进行
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andColumnEqualTo("hdtype", "H106").andColumnEqualTo("online", 1);
	    	mc.setPageNumber(1);
	    	mc.setPageSize(50);
	    	
	    	EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
					,WifiDevice.class, wifiDeviceGrayFacadeService.getWifiDeviceService().getEntityDao(), mc);
			while(it.hasNext()){
				List<WifiDevice> devices = it.next();
				for(WifiDevice device:devices){
					UpgradeDTO upgrade = wifiDeviceGrayFacadeService.deviceFWUpgradeAutoAction(device.getId(),device.getOrig_swver());
					if(upgrade != null && upgrade.isForceDeviceUpgrade()){
						String payload = upgrade.buildUpgradeCMD(device.getId(), 0, WifiDeviceHelper.Upgrade_Default_BeginTime, WifiDeviceHelper.Upgrade_Default_EndTime);
						downCmds.add(DownCmds.builderDownCmds(device.getId(), payload));
						System.out.println(String.format("mac[%s] cmd[%s]", device.getId(),payload));
					}else{//在固件不需要升级的时候，检测组件的升级
						WifiDeviceModule deviceModule = wifiDeviceGrayFacadeService.getWifiDeviceModuleService().getById(device.getId());
						if(deviceModule!=null && StringUtils.isNotEmpty(deviceModule.getOrig_vap_module())){
							UpgradeDTO omUpgrade = wifiDeviceGrayFacadeService.deviceOMUpgradeAutoAction(device.getId(), device.getOrig_swver(), deviceModule.getOrig_vap_module());
							if(omUpgrade != null && omUpgrade.isForceDeviceUpgrade()){
								String payload = upgrade.buildUpgradeCMD(device.getId(), 0, WifiDeviceHelper.Upgrade_Default_BeginTime, WifiDeviceHelper.Upgrade_Default_EndTime);
								downCmds.add(DownCmds.builderDownCmds(device.getId(), payload));
								System.out.println(String.format("mac[%s] cmd[%s]", device.getId(),payload));
							}
						}
					}
				}
				if(!downCmds.isEmpty()){
					total = total+downCmds.size();
					daemonRpcService.wifiMultiDevicesCmdsDown(downCmds.toArray(new DownCmds[0]));
					downCmds.clear();
				}
			}
			/*EntityIterator<String, WifiDeviceGray> it = new KeyBasedEntityBatchIterator<String,WifiDeviceGray>(String.class
					,WifiDeviceGray.class, wifiDeviceGrayFacadeService.getWifiDeviceGrayService().getEntityDao(), mc);
			while(it.hasNext()){
				List<WifiDeviceGray> wdgs = it.next();
				for(WifiDeviceGray wdg:wdgs){
					GrayLevel gl = VapEnumType.GrayLevel.fromIndex(wdg.getGl());
					String dmac = wdg.getId();
					grayMacs.add(dmac);
					if(gl == null || gl == GrayLevel.Special) continue;
					WifiDeviceGrayVersion wdgv = wifiDeviceGrayFacadeService.getWifiDeviceGrayVersionService().getById(new WifiDeviceGrayVersionPK(wdg.getDut(),wdg.getGl()));
					if(wdgv == null) 	continue;
					WifiDevice wifiDevice = wifiDeviceGrayFacadeService.getWifiDeviceService().getById(dmac);
					if(wifiDevice == null || !wifiDevice.isOnline() || wifiDevice.getOrig_swver().equals(wdgv.getD_fwid()))	continue;
					int ret = DeviceVersion.compareVersions(wifiDevice.getOrig_swver(), wdgv.getD_fwid());
					if(ret == -1){
						
					}
				}
			}*/
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
		}finally{
		}
		logger.info(String.format("WifiDeviceOnlineUpgradeLoader ended total[%s]",total));
	}
	
	/*public void beforeExecute(){
		wifiDeviceGrayFacadeService.updateRelatedFieldAction();
		wifiDeviceGrayFacadeService.updateRelatedDevice4GrayVersion();
	}*/
}
