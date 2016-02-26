package com.bhu.vas.plugins.quartz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
import com.bhu.vas.api.helper.IGenerateDeviceSetting;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devicegroup.model.WifiDeviceBackendTask;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;
import com.bhu.vas.business.ds.devicegroup.facade.WifiDeviceGroupFacadeService;
import com.bhu.vas.business.ds.devicegroup.service.WifiDeviceBackendTaskService;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;
import com.bhu.vas.business.search.model.WifiDeviceDocument;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementProcesser;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

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

    @Resource
    private TaskFacadeService taskFacadeService;

    @Resource
    private WifiDeviceIndexIncrementProcesser wifiDeviceIndexIncrementProcesser;

    public void execute() throws InterruptedException {
	logger.info("WifiDeviceGroupBackendTaskLoader starting...");

	int activeCount = ((ThreadPoolExecutor) task_exec).getActiveCount();
	if (activeCount < 3) {
	    final List<WifiDeviceBackendTask> pendingTask = wifiDeviceGroupFacadeService
		    .fetchRecentPendingBackendTask(3 - activeCount);
	    if (pendingTask != null && !pendingTask.isEmpty()) {
		for (final WifiDeviceBackendTask task : pendingTask) {
		    try {
			    task_exec.submit((new Runnable() {
				@Override
				public void run() {
				    // io写入txt类型日志
				    String path = "/BHUData/logs/backendtask/BackendTaskLoader/%s.log";
				    final File logFile = new File(
					    String.format(path, task.getId()));

				    task.setState(WifiDeviceBackendTask.State_Reading);
				    task.setStarted_at(new Date());
				    wifiDeviceBackendTaskService.update(task);
				    final List<DownCmds> downCmdsList = new ArrayList<DownCmds>();
				    final List<String> macList = new ArrayList<String>();
				    BufferedWriter bw = null;
				    try {
					bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(logFile),
						"UTF-8"));
					final StringBuilder sb = new StringBuilder();
					// 获取搜索条件下的设备mac
					wifiDeviceDataSearchService.iteratorAll(
						task.getMessage(),
						new IteratorNotify<Page<WifiDeviceDocument>>() {
					    @Override
					    public void notifyComming(
						    Page<WifiDeviceDocument> pages) {
						//判断是否需要强行中断线程
						if(taskForceInterrupt(task.getId())) {
						    throw new BusinessI18nCodeException(ResponseErrorCode.TASK_FORCE_INTERRUPT);
						}
						
						for (WifiDeviceDocument doc : pages) {
						    // 判断是否在线
						    if (doc.getD_online().equals("1")) {
							String payload = autoGenerateCmds(
								task, doc.getD_mac(),
								doc.getD_workmodel());
							downCmdsList.add(
								DownCmds.builderDownCmds(
									doc.getD_mac(),
									payload));
							macList.add(doc.getD_mac());
						    }
						    // 每个设备信息都写入txt日志
						    sb.append(String.format(
							    "WifiDeviceGroupBackendTaskLoader mac:[%s] online:[%s] sn:[%s]",
							    doc.getD_mac(),
							    doc.getD_online(),
							    doc.getD_sn()))
							    .append("\n");
						}

						task.setTotal(pages.getTotalElements());
						downCmds(task, downCmdsList);
						syncData(task,macList);
					    }
					});
					bw.write(sb.toString());
					sb.delete(0, sb.length());
				    }  catch (IOException e) {
					e.printStackTrace();
					logger.info(
						"WifiDeviceGroupBackendTaskLoader create log error ");
				    }finally {
					if (bw != null) {
					    try {
						bw.write(String.format(
							"WifiDeviceGroupBackendTaskLoader total:[%s]  on_line device:[%s]",
							task.getTotal(),
							downCmdsList.size()));
						bw.flush();
						bw.close();
						task.setCompleted_at(new Date());
						task.setState(
							WifiDeviceBackendTask.State_Completed);
						wifiDeviceBackendTaskService
							.update(task);
					    } catch (IOException e) {
						e.printStackTrace();
					    }
					}
				    }
				}
			    }));
		    } catch (BusinessI18nCodeException e) {
			e.printStackTrace();
			logger.info(
				"WifiDeviceGroupBackendTaskLoader task force interrupt... ");
		    }
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
    public String autoGenerateCmds(WifiDeviceBackendTask task, String wifi_mac,
	    String workmodel) {

	task.setState(WifiDeviceBackendTask.State_Reading);
	wifiDeviceBackendTaskService.update(task);
	OperationCMD opt_cmd = OperationCMD
		.getOperationCMDFromNo(task.getOpt());
	OperationDS ods_cmd = OperationDS
		.getOperationDSFromNo(task.getSubopt());
	String extparams = task.getContext_var();

	String payload = taskFacadeService.apiCmdGenerate(task.getUid(),
		wifi_mac, opt_cmd, ods_cmd, extparams, task.getId(), workmodel);

	return payload;
    }

    /**
     * 下发指令
     * 
     * @param task
     * @param macList
     */
    public void downCmds(WifiDeviceBackendTask task,
	    List<DownCmds> downCmdsList) {
	task.setState(WifiDeviceBackendTask.State_Doing);
	wifiDeviceBackendTaskService.update(task);
	// 下发指令

	try {
	    daemonRpcService.wifiMultiDevicesCmdsDown(
		    downCmdsList.toArray(new DownCmds[0]));
	} catch (Exception e) {
	    task.setState(WifiDeviceBackendTask.State_Failed);
	    wifiDeviceBackendTaskService.update(task);
	    logger.info(String
		    .format("WifiDeviceGroupBackendTaskLoader downCmds error"));
	    e.printStackTrace(System.out);

	} finally {
	    downCmdsList.clear();
	}
    }
    /**
     * 当指令为开启或关闭增值模板时，同步搜索引擎数据
     * @param task
     * @param ids
     */
    public void syncData(WifiDeviceBackendTask task,List<String> ids){
	if (OperationDS.DS_Http_VapModuleCMD_Start == OperationDS
		.getOperationDSFromNo(
			task.getSubopt())
		|| OperationDS.DS_Http_VapModuleCMD_Stop == OperationDS
			.getOperationDSFromNo(
				task.getSubopt())) {// 开启增值
	    
	    String extparams = task.getContext_var();
	    try {
		wifiDeviceIndexIncrementProcesser
		    .templateMultiUpdIncrement(
			    ids,JsonHelper.getString(extparams, "style"));
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
    
    /**
     * 判断是否需要强制中断
     * @param taskId 任务ID
     * @return 
     */
    public boolean taskForceInterrupt(Long taskId) {

	WifiDeviceBackendTask task = wifiDeviceBackendTaskService.getById(taskId);
	if(task.getState().equals(WifiDeviceBackendTask.State_Interrupt)) {
	    return true;
	}else {
	    return false;
	}
    }
}
