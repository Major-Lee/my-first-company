package com.bhu.vas.plugins.quartz;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
//import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;

/**
 * 此任务暂定5分钟执行一次 根据配置的同时运行的任务数量决定是否需要重新把新的任务加入到任务池中
 * 执行审核通过的用户提现申请，通知给uPay进行处理
 * 实现规则
 * 		当前有任务池100，当前存在任务池的提现申请ID set
 * 		quartz调起是判断任务池的活跃数量，如果活跃数量小于100，则获取新的（not in ID set）提现申请放入任务池
 * @author Edmond Lee
 * 
 */
public class WithdrawAppliesNotify2UPayTaskLoader {
    private static Logger logger = LoggerFactory
	    .getLogger(WithdrawAppliesNotify2UPayTaskLoader.class);
    // 可以放进执行队列的数量
    // private int poolMax
    // 可以同时执行的任务的数量
    // 同时只有三个任务可以执行
    private ExecutorService task_exec = Executors.newFixedThreadPool(3);

    public void execute() throws InterruptedException {
		logger.info("WifiDeviceGroupBackendTaskLoader starting...");
	
		int activeCount = ((ThreadPoolExecutor) task_exec).getActiveCount();
		if (activeCount < 3) {
			logger.info("WifiDeviceGroupBackendTaskLoader ended total[0]");
		}
    }
}
