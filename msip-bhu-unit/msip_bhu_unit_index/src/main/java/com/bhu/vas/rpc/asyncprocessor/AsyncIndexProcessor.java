package com.bhu.vas.rpc.asyncprocessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.rpc.asyncprocessor.dto.AsyncIndexDTO;
import com.bhu.vas.rpc.asyncprocessor.dto.AsyncIndexMessageType;
import com.bhu.vas.rpc.asyncprocessor.queue.AsyncIndexProcessorQueue;
import com.bhu.vas.rpc.asyncprocessor.service.AsyncIndexHandleService;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class AsyncIndexProcessor{
	public static final int ProcessesThreadCount = 10;
	
	private final Logger logger = LoggerFactory.getLogger(AsyncIndexProcessor.class);
	private ThreadPoolExecutor exec_processes = (ThreadPoolExecutor)Executors.newFixedThreadPool(ProcessesThreadCount);
	private ExecutorService exec_dispatcher = Executors.newSingleThreadExecutor();
	@Resource
	private AsyncIndexHandleService asyncIndexHandleService;
	
	@Resource
	private AsyncIndexProcessorQueue asyncIndexProcessorQueue;
	
	@PostConstruct
	public void initialize() {
		logger.info("AsyncIndexProcessor initialize...");
		runDispatcherExecutor();
	}
	/**
	 * 启动运行分发线程
	 */
	public void runDispatcherExecutor(){
		logger.info("AsyncIndexProcessor runDispatcherExecutor");
		exec_dispatcher.submit((new Runnable() {
			@Override
			public void run() {
				while(true){
					try{
						System.out.println("runDispatcherExecutor");
						AsyncIndexDTO asyncIndexDto = asyncIndexProcessorQueue.poll();
						if(asyncIndexDto == null){
							Thread.sleep(1000);
						}else{
							System.out.println("runDispatcherExecutor get");
							onProcessor(asyncIndexDto);
						}
					}catch(Exception ex){
						ex.printStackTrace(System.out);
						logger.error("AsyncIndexProcessor Dispatcher Executor", ex);
					}
				}
			}
		}));
	}
	
	/**
	 * 执行线程处理消息
	 * @param message
	 */
	public void onProcessor(final AsyncIndexDTO asyncIndexDto){
		if(asyncIndexDto == null) return;
		
		exec_processes.submit((new Runnable() {
			@Override
			public void run() {
				try{
					AsyncIndexMessageType messageType = AsyncIndexMessageType.fromPrefix(asyncIndexDto.getActionType());					
					if(messageType == null){
						throwUnsupportedOperationException(null);
					}
					switch(messageType){
						case WifiDevice_BlukFullIndex:
							asyncIndexHandleService.WifiDeviceBlukFullIndexHandle(asyncIndexDto);
							break;
						default:
							throwUnsupportedOperationException(messageType.getType());
					}
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("AsyncIndexProcessor onProcessor", ex);
				}
			}
		}));
	}
	
	public void throwUnsupportedOperationException(String type){
		throw new UnsupportedOperationException(
				String.format("AsyncIndexMessageType not yet implement handler processfull type[%s]", type));
	}

}
