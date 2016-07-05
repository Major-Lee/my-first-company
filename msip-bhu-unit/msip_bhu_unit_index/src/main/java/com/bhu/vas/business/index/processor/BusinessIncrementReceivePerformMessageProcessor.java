package com.bhu.vas.business.index.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.bhu.pure.kafka.business.observer.KafkaMsgIndexManager;
import com.bhu.pure.kafka.business.observer.listener.DynaMessageListener;
import com.bhu.vas.business.index.processor.service.IncrementPerformService;
import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class BusinessIncrementReceivePerformMessageProcessor implements DynaMessageListener{
	private final Logger logger = LoggerFactory.getLogger(BusinessIncrementReceivePerformMessageProcessor.class);
	private ExecutorService exec_dispatcher = null;//Executors.newFixedThreadPool(1);
	private List<ExecutorService> exec_processes = new ArrayList<ExecutorService>();//Executors.newFixedThreadPool(1);
	//private int[] hits;
	//private int hash_prime = 50;
	private int hash_prime = 50;
	
	@Resource
	private IncrementPerformService incrementPerformService;
	
	@PostConstruct
	public void initialize(){
		//System.out.println("BusinessDynaMsgProcessor initialize...");
		System.out.println("BusinessIncrementReceivePerformMessageProcessor Init");
		logger.info("BusinessIncrementReceivePerformMessageProcessor initialize...");
		/*exec_dispatcher = ExecObserverManager.buildExecutorService(this.getClass(),"DynaMsg dispatcher消息处理",per_threads);
		for(int i=0;i<hash_prime;i++){
			ExecutorService exec_process = ExecObserverManager.buildExecutorService(this.getClass(),"DynaMsg process消息处理".concat(String.valueOf(i)),per_threads);
			exec_processes.add(exec_process);//Executors.newFixedThreadPool(per_threads));
		}*/
		exec_dispatcher = ExecObserverManager.buildSingleThreadExecutor(this.getClass(),"DynaMsg dispatcher消息处理");
		for(int i=0;i<hash_prime;i++){
			ExecutorService exec_process = ExecObserverManager.buildSingleThreadExecutor(this.getClass(),"DynaMsg process消息处理".concat(String.valueOf(i)));
			exec_processes.add(exec_process);//Executors.newFixedThreadPool(per_threads));
		}
		
		//hits = new int[hash_prime];
		KafkaMsgIndexManager.IncrementPerformMsgCommingObserver.addMsgCommingListener(this);
		//初始化ActiveMQConnectionManager
		//ActiveMQConnectionManager.getInstance().initConsumerQueues();
	}

	@Override
	public void onMessage(final String topic,final int partition,final String key,final String message,final long offset,final String consumerId) {
		validateStep1(message);
		exec_dispatcher.submit((new Runnable() {
			@Override
			public void run() {
				//logger.info(String.format("BusinessDynaMsgProcessor receive:ctx[%s] message[%s]", ctx,message));
				try{
					/*System.out.println(String.format("Dyna Received message: topic[%s] partition[%s] key[%s] message[%s] offset[%s] consumerId[%s]",
							topic, partition,
							key, message,
							offset, consumerId));*/
					//System.out.println(String.format("BusinessNotifyMsgProcessor receive:ctx[%s] message[%s]", ctx,message));
					logger.info(String.format("BusinessIncrementReceivePerformMessageProcessor message: topic[%s] partition[%s] key[%s] message[%s] offset[%s] consumerId[%s]",
							topic, partition,
							key, message,
							offset, consumerId));					
					
					onProcessor(key, message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("BusinessIncrementReceivePerformMessageProcessor", ex);
				}
			}
		}));
	}
	
	public void onProcessor(final String key, final String message) {
		int hash = HashAlgorithmsHelper.rotatingHash(key, hash_prime);
		//hits[hash] = hits[hash]+1;
		exec_processes.get(hash).submit((new Runnable() {
			@Override
			public void run() {
				try{
					incrementPerformService.incrementDocument(message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("BusinessIncrementReceivePerformMessageProcessor onProcessor", ex);
				}
			}
		}));
	}
	
	public static void validateStep1(String msg){
		if(StringUtils.isEmpty(msg)) 
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
	}

/*	public int[] getHits() {
		return hits;
	}

	public void setHits(int[] hits) {
		this.hits = hits;
	}*/
	
}
