package com.bhu.vas.plugins.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 此任务暂定5分钟执行一次 根据配置的同时运行的任务数量决定是否需要重新把新的任务加入到任务池中
 * 
 * @author Edmond Lee
 * 
 */
public class StatisticsBackendTaskLoader {
    private static Logger logger = LoggerFactory
	    .getLogger(StatisticsBackendTaskLoader.class);

    public void execute() throws InterruptedException {
	logger.info("BusinessStatisticsTaskLoader starting...");
	logger.info("BusinessStatisticsTaskLoader 111...");
	logger.info("BusinessStatisticsTaskLoader 222...");
	logger.info("BusinessStatisticsTaskLoader 333...");
	logger.info("BusinessStatisticsTaskLoader end...");
    }
}
