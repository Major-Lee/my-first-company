package com.bhu.vas.plugins.quartz;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.helper.IGenerateDeviceSetting;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;
import com.bhu.vas.business.ds.devicegroup.facade.WifiDeviceGroupFacadeService;
import com.bhu.vas.business.ds.devicegroup.service.WifiDeviceBackendTaskService;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.WifiDeviceIndexIncrementProcesser;

/**
 * 此任务暂定5分钟执行一次 根据配置的同时运行的任务数量决定是否需要重新把新的任务加入到任务池中
 * 
 * @author Edmond Lee
 * 
 */
public class StatisticsBackendTaskLoader {
    private static Logger logger = LoggerFactory
	    .getLogger(StatisticsBackendTaskLoader.class);
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
	logger.info("BusinessStatisticsTaskLoader starting...");
		
    }
}
