package com.bhu.vas.pa.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.pure.kafka.business.observer.KafkaMsgObserverManager;
import com.bhu.pure.kafka.business.observer.listener.DynaMessageListener;
import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;
import com.bhu.vas.pa.processor.input.DeliverMessageTopicConsumer;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class NotifyCmMsgProcessor implements DynaMessageListener{
	private final Logger logger = LoggerFactory.getLogger(NotifyCmMsgProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(5);

	@Resource
	private DeliverMessageTopicConsumer deliverMessageTopicConsumer;
	
//	
//	@Resource
//	private IDeviceMessageDispatchRpcService deviceMessageDispatchRpcService;

	@PostConstruct
	public void initialize() {
		System.out.println("NotifyCmMsgProcessor Init");
		logger.info("NotifyCmMsgProcessor initialize...");
		exec = ExecObserverManager.buildExecutorService(this.getClass(),"NotifyCmMsg 消息处理",5);
		KafkaMsgObserverManager.CMNotifyCommingObserver.addMsgCommingListener(this);
	}
	
	@Override
	//public void onMessage(final String message){
	public void onMessage(final String topic,int partition,String key,final String message,long offset,String consumerId) {
		System.out.println(String
				.format("CM Received message: topic[%s] partition[%s] key[%s] message[%s] "
						+ "offset[%s] consumerId[%s]",
						topic, partition,
						key, message,
						offset, consumerId));
		//logger.info(String.format("NotifyCmMsgProcessor receive message[%s]", message));
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info(message);
					int type = Integer.parseInt(message.substring(0, 8));
					String payload = message.substring(8);
					CmCtxInfo cmInfo = null;
					if(ParserHeader.Online_Prefix == type){
						cmInfo = JsonHelper.getDTO(payload, CmCtxInfo.class);
						String ctx = cmInfo.toString();
						System.out.println("~~~~~~~~~:"+ctx);
						deliverMessageTopicConsumer.addSubscribeTopic(cmInfo.toUpQueueString());
					}else{
						throw new UnsupportedOperationException(message+" message not yet implement handler process!");
					}
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("NotifyCmMsgProcessor", ex);
				}
			}
		}));
	}
}
