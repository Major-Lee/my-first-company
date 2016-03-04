package com.bhu.vas.business.backendcommdity.asyncprocessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.backendcommdity.asyncprocessor.service.AsyncOrderPaymentNotifyService;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.OrderPaymentNotifyListService;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class AsyncOrderPaymentNotifyProcessor{
	public static final int ProcessesThreadCount = 10;
	
	private final Logger logger = LoggerFactory.getLogger(AsyncOrderPaymentNotifyProcessor.class);
	private ThreadPoolExecutor exec_processes = (ThreadPoolExecutor)Executors.newFixedThreadPool(ProcessesThreadCount);
	private ExecutorService exec_dispatcher = Executors.newSingleThreadExecutor();
	@Resource
	private AsyncOrderPaymentNotifyService asyncOrderPaymentNotifyService;
	
	@PostConstruct
	public void initialize() {
		logger.info("AsyncOrderPaymentNotifyProcessor initialize...");
		runDispatcherExecutor();
	}
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
							String message = OrderPaymentNotifyListService.getInstance().lpopNotify();
							/*if(StringUtils.isNotEmpty(message))
								logger.info(String.format("AsyncOrderPaymentNotifyProcessor receive: message[%s]", message));*/
							
							onProcessor(message);
						}else{
							Thread.sleep(100);
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
					asyncOrderPaymentNotifyService.notifyOrderPaymentHandle(message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("AsyncOrderPaymentNotifyProcessor onProcessor", ex);
				}
			}
		}));
	}

}
