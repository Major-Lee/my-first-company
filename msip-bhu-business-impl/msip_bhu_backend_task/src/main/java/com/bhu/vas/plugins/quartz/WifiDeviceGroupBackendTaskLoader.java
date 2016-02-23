package com.bhu.vas.plugins.quartz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
	    final List<WifiDeviceBackendTask> pendingTask = wifiDeviceGroupFacadeService.fetchRecentPendingBackendTask(3 - activeCount);
	    if (pendingTask != null && !pendingTask.isEmpty()) {
		for (final WifiDeviceBackendTask task : pendingTask) {
		    task_exec.submit((new Runnable() {
			@Override
			public void run() {
			    //io写入txt类型日志
			    String path = "/BHUData/logs/backendtask/BackendTaskLoader/";
			    final File logFile = new File(path + task.getId());
			    	
			    task.setState(WifiDeviceBackendTask.State_Reading);
			    task.setStarted_at(new Date());
			    wifiDeviceBackendTaskService.update(task);
			    final List<String> macList = new ArrayList<String>();

			    try {
				// 获取搜索条件下的设备mac
				wifiDeviceDataSearchService.iteratorAll(task.getMessage(),new IteratorNotify<Page<WifiDeviceDocument>>() {
				@Override
				 public void notifyComming(Page<WifiDeviceDocument> pages) {
				     for (WifiDeviceDocument doc : pages) {
					 // 判断是否在线
					 if (doc.getD_online().equals("1")) {
					     macList.add(doc.getD_mac());
					 }
					    //每个设备信息都写入txt日志	
					    try {
						BufferedWriter bw2 = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (logFile), "UTF-8")); 
						bw2.write(String.format("WifiDeviceGroupBackendTaskLoader mac:[%s] online:[%s] sn:[%s]", doc.getD_mac(),doc.getD_online(),doc.getD_sn()));
						bw2.flush();
						bw2.close();
					    } catch (IOException e) {
						e.printStackTrace();
					    }
					}
					task.setTotal(pages.getTotalElements());
				    }
				});
			    } catch (Exception e) {
				e.printStackTrace();
				logger.info("WifiDeviceGroupBackendTaskLoader  create txt error ");
			    }finally {
				try {
				    BufferedWriter bw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (logFile), "UTF-8"));
				    bw.write(String.format("WifiDeviceGroupBackendTaskLoader total:[%s]  on_line device:[%s]",task.getTotal(),macList.size()));
				    bw.flush();
				    bw.close();
				} catch (Exception e) {
				    e.printStackTrace();
				}
			    }
				downCmds(task, macList);
			    } 
		    }));
		}
	    }
	    if (pendingTask == null || pendingTask.isEmpty()) {
		logger.info("WifiDeviceGroupBackendTaskLoader ended total[0]");
	    }
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

    /**
     * 下发指令
     * 
     * @param task
     * @param macList
     */
    public void downCmds(WifiDeviceBackendTask task, List<String> macList) {
	List<DownCmds> downCmds = new ArrayList<DownCmds>();
	task.setState(WifiDeviceBackendTask.State_Doing);
	wifiDeviceBackendTaskService.update(task);
	// 下发指令
	for (int i = 0; i < macList.size(); i++) {
	    String payload = autoGenerateCmds(task, macList.get(i));
	    downCmds.add(DownCmds.builderDownCmds(macList.get(i), payload));
	    if ((i % 100 == 0 && i != 0) || i == macList.size() - 1) {
		try {
		    daemonRpcService.wifiMultiDevicesCmdsDown(
			    downCmds.toArray(new DownCmds[0]));
		} catch (Exception e) {
		    logger.info(String.format(
			    "WifiDeviceGroupBackendTaskLoader error mac : [%s]",
			    macList.get(i)));
		    e.printStackTrace(System.out);
		} finally {
		    downCmds.clear();
		}
		// 每1000条更新一次数据库
		if (i % 1000 == 0) {
		    task.setCurrent(i + 1);
		    task.setUpdated_at(new Date());
		    wifiDeviceBackendTaskService.update(task);
		}
		// 最后一条下发完毕
		if (i == macList.size() - 1) {
		    task.setCurrent(i + 1);
		    task.setState(WifiDeviceBackendTask.State_Completed);
		    task.setCompleted_at(new Date());
		    wifiDeviceBackendTaskService.update(task);
		}
	    }
	}
    }
}
