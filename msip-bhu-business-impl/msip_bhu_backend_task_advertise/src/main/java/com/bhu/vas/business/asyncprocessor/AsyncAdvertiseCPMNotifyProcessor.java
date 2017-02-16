package com.bhu.vas.business.asyncprocessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.bucache.redis.serviceimpl.advertise.AdvertiseCPMListService;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

@Service
public class AsyncAdvertiseCPMNotifyProcessor {
	public static final int ProcessesThreadCount = 10;
	private final Logger logger = LoggerFactory.getLogger(AsyncAdvertiseCPMNotifyProcessor.class);
	private ThreadPoolExecutor exec_processes = null;
	private ExecutorService exec_dispatcher = null;
	
	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;
	@PostConstruct
	public void initialize() {
		logger.info("AsyncAdvertiseCPMNotifyProcessor initialize...");
		exec_processes = (ThreadPoolExecutor)ExecObserverManager.buildExecutorService(this.getClass(),"AsyncAdvertiseCPMNotify processes消息处理",ProcessesThreadCount);
		exec_dispatcher = ExecObserverManager.buildExecutorService(this.getClass(),"AsyncAdvertiseCPMNotify dispatcher消息处理",1);
		runDispatcherExecutor();
	}
	
	/**
	 * 启动运行分发线程
	 */
	public void runDispatcherExecutor(){
		logger.info("AsyncAdvertiseCPMNotifyProcessor runDispatcherExecutor");
		exec_dispatcher.submit((new Runnable() {
			@Override
			public void run() {
				while(true){
					try{
						if(ProcessesThreadCount > exec_processes.getActiveCount()){
							String message = AdvertiseCPMListService.getInstance().AdCPMNotify();
							if (StringUtils.isNotEmpty(message)){
								onProcessor(message);
							}else{
								System.out.println("sleep 11111");
								Thread.sleep(10);
							}
						}else{
							System.out.println("sleep 22222");
							Thread.sleep(10);
						}
					}catch(Exception ex){
						ex.printStackTrace(System.out);
						logger.error("AsyncAdvertiseCPMNotifyProcessor Dispatcher Executor", ex);
					}
				}
			}
		}));
	}
	
	/**
	 * 执行线程处理消息
	 * @param message
	 */
	public void onProcessor(final String message){
		if(StringUtils.isEmpty(message)) return;
		
		exec_processes.submit((new Runnable() {
			@Override
			public void run() {
				try{
					System.out.println("onProcessor");
					asyncDeliverMessageService.sendBatchAdvertiseCPMNotifyActionMessage(message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("AsyncAdvertiseCPMNotify onProcessor", ex);
				}
			}
		}));
	}
}
