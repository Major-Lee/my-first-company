package com.bhu.vas.plugins.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.helper.IGenerateDeviceSetting;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devicegroup.model.WifiDeviceBackendTask;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;
import com.bhu.vas.business.ds.devicegroup.facade.WifiDeviceGroupFacadeService;
import com.bhu.vas.business.ds.devicegroup.service.WifiDeviceBackendTaskService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

/**
 * 此任务暂定5分钟执行一次 根据配置的同时运行的任务数量决定是否需要重新把新的任务加入到任务池中
 * 
 * @author Edmond Lee
 *
 */
public class WifiDeviceGroupBackendTaskLoader {
    private static Logger logger = LoggerFactory
	    .getLogger(WifiDeviceGroupBackendTaskLoader.class);
    // 可以放进执行队列的数量
    // private int poolMax
    // 可以同时执行的任务的数量
    // 同时只有三个任务可以执行
    private ExecutorService task_exec = Executors.newFixedThreadPool(3);

    @Resource
    private WifiDeviceDataSearchService wifiDeviceDataSearchService;

    @Resource
    private IDaemonRpcService daemonRpcService;

    @Resource
    private IGenerateDeviceSetting generateDeviceSetting;

    @Resource
    private WifiDeviceBackendTaskService wifiDeviceBackendTaskService;

    @Resource
    private WifiDeviceGroupFacadeService wifiDeviceGroupFacadeService;

    public void execute() throws InterruptedException {
	logger.info("WifiDeviceGroupBackendTaskLoader starting...");

	int activeCount = ((ThreadPoolExecutor) task_exec).getActiveCount();
	if (activeCount < 3) {
	    List<WifiDeviceBackendTask> pendingTask = wifiDeviceGroupFacadeService.fetchRecentPendingBackendTask(3 - activeCount);

	    if (pendingTask != null && !pendingTask.isEmpty()) {
		for (final WifiDeviceBackendTask task : pendingTask) {
		    task_exec.submit((new Runnable() {
			@Override
			public void run() {
			    task.setState(WifiDeviceBackendTask.State_Reading);
			    task.setStarted_at(new Date());
			    wifiDeviceBackendTaskService.update(task);
			    final List<String> macList = new ArrayList<String>();
			    //获取搜索条件下的设备mac
			    wifiDeviceDataSearchService.iteratorAll(task.getMessage(),new IteratorNotify<Page<WifiDeviceDocument>>(){
				@Override
				public void notifyComming(Page<WifiDeviceDocument> pages) {
				    for (WifiDeviceDocument doc : pages) {
					// 判断是否在线
					if (doc.getD_online().equals("1")) {
					    macList.add(doc.getD_mac());
					}else {
					    logger.info("WifiDeviceGroupBackendTaskLoader off-line mac:[%s]",doc.getD_mac());
					}
					System.out.println(doc.getD_mac());
				    }
				    task.setTotal(pages.getTotalElements());
				}
			    });

			    List<DownCmds> downCmds = new ArrayList<DownCmds>();
			    task.setState(WifiDeviceBackendTask.State_Doing);
			    wifiDeviceBackendTaskService.update(task);
			    //下发指令
			    for (int i = 0; i < macList.size(); i++) {
				String payload = autoGenerateCmds(task,macList.get(i));
				downCmds.add(DownCmds.builderDownCmds(macList.get(i), payload));
				if((i%100==0&&i!=0) || i==macList.size()-1) {
					try {
					    daemonRpcService.wifiMultiDevicesCmdsDown(downCmds.toArray(new DownCmds[0]));
					} catch (Exception e) {
					    logger.info(String.format("WifiDeviceGroupBackendTaskLoader error mac : [%s]", macList.get(i)));
					    e.printStackTrace(System.out);
					} finally {
					    downCmds.clear();
					}
				   //每1000条更新一次数据库
				   if (i%1000==0) {
				       task.setCurrent(i+1);
				       task.setUpdated_at(new Date());
				       wifiDeviceBackendTaskService.update(task);
				   }
				   //最后一条下发完毕
				   if (i==macList.size()-1) {
				       task.setCurrent(i+1);
				       task.setState(WifiDeviceBackendTask.State_Completed);
				       task.setCompleted_at(new Date());
				       wifiDeviceBackendTaskService.update(task);
				   }
				}
			    }
			    Long total = task.getTotal();
			    logger.info(String.format("WifiDeviceGroupBackendTaskLoader ended total[%s]", total));
			}
		    }));
		}
	    }


	    // try {
	    // List<DownCmds> downCmds = new ArrayList<DownCmds>();
	    // // 缩小范围，目前只在uRouter中进行
	    // ModelCriteria mc = new ModelCriteria();
	    // mc.createCriteria()/*
	    // * .andColumnEqualTo("hdtype", "H106")
	    // */.andColumnEqualTo("online", 1);
	    // mc.setPageNumber(1);
	    // mc.setPageSize(50);
	    //
	    // EntityIterator<String, WifiDevice> it = new
	    // KeyBasedEntityBatchIterator<String, WifiDevice>(
	    // String.class, WifiDevice.class,
	    // wifiDeviceGrayFacadeService.getWifiDeviceService()
	    // .getEntityDao(),
	    // mc);
	    // while (it.hasNext()) {
	    // List<WifiDevice> devices = it.next();
	    // for (WifiDevice device : devices) {
	    // if (StringUtils.isEmpty(device.getOrig_swver()))
	    // continue;
	    // if (!device.getId().startsWith("84:82"))
	    // continue;
	    // UpgradeDTO upgrade = wifiDeviceGrayFacadeService
	    // .deviceFWUpgradeAutoAction(device.getId(),
	    // device.getOrig_swver());
	    // if (upgrade != null && upgrade.isForceDeviceUpgrade()) {
	    // String payload = upgrade.buildUpgradeCMD(
	    // device.getId(), 0,
	    // WifiDeviceHelper.Upgrade_Default_BeginTime,
	    // WifiDeviceHelper.Upgrade_Default_EndTime);
	    // downCmds.add(DownCmds
	    // .builderDownCmds(device.getId(), payload));
	    // System.out.println(String.format("mac[%s] cmd[%s]",
	    // device.getId(), payload));
	    // } else {// 在固件不需要升级的时候，检测组件的升级
	    // WifiDeviceModule deviceModule = wifiDeviceGrayFacadeService
	    // .getWifiDeviceModuleService()
	    // .getById(device.getId());
	    // if (deviceModule != null && StringUtils.isNotEmpty(
	    // deviceModule.getOrig_vap_module())) {
	    // UpgradeDTO omUpgrade = wifiDeviceGrayFacadeService
	    // .deviceOMUpgradeAutoAction(
	    // device.getId(),
	    // device.getOrig_swver(),
	    // deviceModule
	    // .getOrig_vap_module());
	    // if (omUpgrade != null
	    // && omUpgrade.isForceDeviceUpgrade()) {
	    // String payload = omUpgrade.buildUpgradeCMD(
	    // device.getId(), 0,
	    // WifiDeviceHelper.Upgrade_Default_BeginTime,
	    // WifiDeviceHelper.Upgrade_Default_EndTime);
	    // downCmds.add(DownCmds.builderDownCmds(
	    // device.getId(), payload));
	    // System.out.println(
	    // String.format("mac[%s] cmd[%s]",
	    // device.getId(), payload));
	    // }
	    // }
	    // }
	    // }
	    // if (!downCmds.isEmpty()) {
	    // total = total + downCmds.size();
	    // daemonRpcService.wifiMultiDevicesCmdsDown(
	    // downCmds.toArray(new DownCmds[0]));
	    // downCmds.clear();
	    // }
	    // }
	    //
	    // } catch (Exception ex) {
	    // ex.printStackTrace(System.out);
	    // logger.error(ex.getMessage(), ex);
	    // } finally {
	    // }

	}
    }

    /**
     * 根据task生成cmd指令
     * 
     * @param BackendTask
     * @throws Throwable
     */
    public String autoGenerateCmds(WifiDeviceBackendTask backendTask,
	    String wifi_mac) {

	backendTask.setState(WifiDeviceBackendTask.State_Reading);

	OperationCMD opt = OperationCMD
		.getOperationCMDFromNo(backendTask.getOpt());
	OperationDS ods = OperationDS
		.getOperationDSFromNo(backendTask.getSubopt());
	String extparams = backendTask.getContext_var();

	String payload = CMDBuilder.autoBuilderCMD4Opt(opt, ods, wifi_mac,
		backendTask.getId(), extparams, generateDeviceSetting);

	wifiDeviceBackendTaskService.update(backendTask);

	return payload;
    }
}
