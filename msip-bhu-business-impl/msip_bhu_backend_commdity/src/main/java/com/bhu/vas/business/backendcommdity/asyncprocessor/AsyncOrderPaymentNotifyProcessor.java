package com.bhu.vas.business.backendcommdity.asyncprocessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.backendcommdity.asyncprocessor.service.AsyncOrderPaymentNotifyService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.CommdityInternalNotifyListService;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class AsyncOrderPaymentNotifyProcessor{
	public static final int ProcessesThreadCount = 10;
	private final Logger logger = LoggerFactory.getLogger(AsyncOrderPaymentNotifyProcessor.class);
	private ThreadPoolExecutor exec_processes = null;//(ThreadPoolExecutor)Executors.newFixedThreadPool(ProcessesThreadCount);
	private ExecutorService exec_dispatcher = null;//Executors.newSingleThreadExecutor();
	@Resource
	private AsyncOrderPaymentNotifyService asyncOrderPaymentNotifyService;
	//private static final int [] dispatcherSleepStrategy = new int[]{10,20,50,100};
	private static final int [] dispatcherSleepStrategy = new int[]{10};
	private static final int maxStrategyIndex = dispatcherSleepStrategy.length - 1;
	private static int strategyIndex = 0;
	@PostConstruct
	public void initialize() {
		logger.info("AsyncOrderPaymentNotifyProcessor initialize...");
		exec_processes = (ThreadPoolExecutor)ExecObserverManager.buildExecutorService(this.getClass(),"AsyncOrderPaymentNotify processes消息处理",ProcessesThreadCount);
		exec_dispatcher = ExecObserverManager.buildExecutorService(this.getClass(),"AsyncOrderPaymentNotify dispatcher消息处理",1);
		runDispatcherExecutor();
	}
	
	/*@PreDestroy
	public void destory(){
		logger.info("AsyncOrderPaymentNotifyProcessor destory...");
		this.destory(exec_dispatcher, "AsyncOrderPaymentNotifyProcessor exec_dispatcher");
		this.destory(exec_processes, "AsyncOrderPaymentNotifyProcessor exec_processes");
		//Thread desstoryThread = new Thread(new DaemonExecRunnable(exec));
		//desstoryThread.start();
	}*/
	
	/**
	 * 启动运行分发线程
	 */
	public void runDispatcherExecutor(){
		logger.info("AsyncOrderPaymentNotifyProcessor runDispatcherExecutor");
		exec_dispatcher.submit((new Runnable() {
			@Override
			public void run() {
				while(true){
					try{
						if(ProcessesThreadCount > exec_processes.getActiveCount()){
							String message = CommdityInternalNotifyListService.getInstance().lpopOrderPaymentNotify();
							/*if(StringUtils.isNotEmpty(message))
								logger.info(String.format("AsyncOrderPaymentNotifyProcessor receive: message[%s]", message));*/
							if (StringUtils.isNotEmpty(message)){
								onProcessor(message);
								strategyIndex = 0;
							}else{
								if (strategyIndex > maxStrategyIndex)
									strategyIndex = maxStrategyIndex;
								
								Thread.sleep(dispatcherSleepStrategy[strategyIndex]);
								strategyIndex++;
							}
						}else{
							Thread.sleep(10);
						}
					}catch(Exception ex){
						ex.printStackTrace(System.out);
						logger.error("AsyncOrderPaymentNotifyProcessor Dispatcher Executor", ex);
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
					asyncOrderPaymentNotifyService.notifyHandle(message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("AsyncOrderPaymentNotifyProcessor onProcessor", ex);
				}
			}
		}));
	}

}
