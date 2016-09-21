package com.bhu.vas.pa.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.pure.kafka.business.observer.KafkaMsgObserverManager;
import com.bhu.pure.kafka.business.observer.listener.DynaMessageListener;
import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.pa.processor.task.DaemonProcessesStatusTask;
import com.bhu.vas.pa.service.device.WanganBusinessServiceProcessor;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;
import com.smartwork.msip.cores.helper.task.TaskEngine;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class BusinessDynaMsgProcessor implements DynaMessageListener{
	private final Logger logger = LoggerFactory.getLogger(BusinessDynaMsgProcessor.class);
	private ExecutorService exec_dispatcher = null;//Executors.newFixedThreadPool(1);
	private List<ExecutorService> exec_processes = new ArrayList<ExecutorService>();//Executors.newFixedThreadPool(1);
	private int[] hits;
	private int hash_prime = 50;

	@Resource
	private WanganBusinessServiceProcessor wanganBusinessServiceProcessor;

	//@Resource
	//private DeliverTopicMessageService deliverTopicMessageService;// =(DeliverTopicMessageService) ctx.getBean("deliverTopicMessageService");

	@PostConstruct
	public void initialize(){
		//System.out.println("BusinessDynaMsgProcessor initialize...");
		System.out.println("BusinessDynaMsgProcessor Init");
		logger.info("BusinessDynaMsgProcessor initialize...");
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
		
		hits = new int[hash_prime];
		TaskEngine.getInstance().schedule(new DaemonProcessesStatusTask(this), 30*60*1000,60*60*1000);
		KafkaMsgObserverManager.DynaMsgCommingObserver.addMsgCommingListener(this);
	}

	@Override
	public void onMessage(final String topic,final int partition,final String key,final String message,final long offset,final String consumerId) {
/*		logger.info(String.format("BusinessDynaMsgProcessor Received message: topic[%s] partition[%s] key[%s] message[%s] offset[%s] consumerId[%s]",
						topic, partition,
						key, message,
						offset, consumerId));*/

		validateStep1(message);
		exec_dispatcher.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info(String.format("Dyna Received message: topic[%s] partition[%s] key[%s] message[%s] offset[%s] consumerId[%s]",
							topic, partition,
							key, message,
							offset, consumerId));					
					if(Integer.parseInt(message.substring(0, 8)) != ParserHeader.Transfer_Prefix)
						return;
					ParserHeader headers = ParserHeader.builder(message.substring(8, ParserHeader.Cmd_Header_Length), ParserHeader.Transfer_Prefix);
					if(headers.getMt() == ParserHeader.Transfer_mtype_1 && headers.getSt() == 7){ //7://3.4.16	WLAN用户上下线消息
						String payload = message.substring(ParserHeader.Cmd_Header_Length);
						onProcessor(payload, headers);
					}
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("BusinessDynaMsgProcessor", ex);
				}
			}
		}));
	}
	
	public void onProcessor(final String payload, final ParserHeader headers) {
		String mac = headers.getMac();
		if(mac.startsWith(BusinessRuntimeConfiguration.DeviceTesting_Mac_Prefix)) return;
		int hash = HashAlgorithmsHelper.rotatingHash(mac, hash_prime);
		hits[hash] = hits[hash]+1;
		exec_processes.get(hash).submit((new Runnable() {
			@Override
			public void run() {
				try{
					wanganBusinessServiceProcessor.doPaProcessor(payload,headers);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("BusinessDynaMsgProcessor onProcessor", ex);
				}
			}
		}));
	}
	
	public static void validateStep1(String msg){
		if(StringUtils.isEmpty(msg) || msg.length()<=8) 
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
	}
	
	public int[] getHits() {
		return hits;
	}

	public void setHits(int[] hits) {
		this.hits = hits;
	}
	
}
