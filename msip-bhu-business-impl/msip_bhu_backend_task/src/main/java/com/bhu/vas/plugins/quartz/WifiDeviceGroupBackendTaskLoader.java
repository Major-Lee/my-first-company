package com.bhu.vas.plugins.quartz;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devicegroup.model.WifiDeviceBackendTask;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;
import com.bhu.vas.business.ds.devicegroup.facade.WifiDeviceGroupFacadeService;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
/**
 * 此任务暂定5分钟执行一次
 * 根据配置的同时运行的任务数量决定是否需要重新把新的任务加入到任务池中
 * @author Edmond Lee
 *
 */
public class WifiDeviceGroupBackendTaskLoader {
	private static Logger logger = LoggerFactory.getLogger(WifiDeviceGroupBackendTaskLoader.class);
	//可以放进执行队列的数量
	//private int poolMax
	//可以同时执行的任务的数量
	//同时只有三个任务可以执行
	private ExecutorService task_exec = Executors.newFixedThreadPool(3);
	@Resource
	private IDaemonRpcService daemonRpcService;
	
	@Resource
	private WifiDeviceGroupFacadeService wifiDeviceGroupFacadeService;
	
	public void execute() {
		logger.info("WifiDeviceGroupBackendTaskLoader starting...");
		
		int activeCount = ((ThreadPoolExecutor)task_exec).getActiveCount();
		if(activeCount < 3){
			List<WifiDeviceBackendTask> pendingTask = wifiDeviceGroupFacadeService.fetchRecentPendingBackendTask(1, 3-activeCount);
			if(pendingTask != null && !pendingTask.isEmpty()){
				task_exec.submit((new Runnable() {
					@Override
					public void run() {
						
					}
				}));
			}
		}
		
		int total = 0;
		try{
			List<DownCmds> downCmds = new ArrayList<DownCmds>();
			//缩小范围，目前只在uRouter中进行
			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria()/*.andColumnEqualTo("hdtype", "H106")*/.andColumnEqualTo("online", 1);
	    	mc.setPageNumber(1);
	    	mc.setPageSize(50);
	    	
	    	/*EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String,WifiDevice>(String.class
					,WifiDevice.class, wifiDeviceGrayFacadeService.getWifiDeviceService().getEntityDao(), mc);
			while(it.hasNext()){
				List<WifiDevice> devices = it.next();
				for(WifiDevice device:devices){
					if(StringUtils.isEmpty(device.getOrig_swver())) continue;
					if(!device.getId().startsWith("84:82")) continue;
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
								String payload = omUpgrade.buildUpgradeCMD(device.getId(), 0, WifiDeviceHelper.Upgrade_Default_BeginTime, WifiDeviceHelper.Upgrade_Default_EndTime);
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
			}*/
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			logger.error(ex.getMessage(), ex);
		}finally{
		}
		logger.info(String.format("WifiDeviceGroupBackendTaskLoader ended total[%s]",total));
	}
	
	//public 
}
