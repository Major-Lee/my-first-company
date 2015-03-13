package com.bhu.vas.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.CmInfo;
import com.bhu.vas.business.mq.activemq.observer.QueueMsgObserverManager;
import com.bhu.vas.business.mq.activemq.observer.listener.CmMessageListener;

@Service
public class NotifyCmMsgProcessor implements CmMessageListener{
	private final Logger logger = LoggerFactory.getLogger(NotifyCmMsgProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(5);
	//00010000{"name":"cm001","thread":"3","ip":"192.168.0.101"}
	
	@PostConstruct
	public void initialize() {
		QueueMsgObserverManager.CmMessageObserver.addCmMessageListener(this);
	}
	@Override
	public void onCmOnline(CmInfo info) {
		
	}

	@Override
	public void onCmOffline(CmInfo info) {
		
	}
	/*public void handler(final String message){
		
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info("NotifyMsgProcessorService receive:"+message);
					String type = message.substring(0, 8);
					String payload = message.substring(8);
					CmInfo cmInfo = null;
					if(Online_Prefix.equals(type)){
						cmInfo = JsonHelper.getDTO(payload, CmInfo.class);
						QueueMsgObserverManager.CmMessageObserver.notifyCmOnline(cmInfo);
					}else if(Offline_Prefix.equals(type)){
						cmInfo = JsonHelper.getDTO(payload, CmInfo.class);
						QueueMsgObserverManager.CmMessageObserver.notifyCmOffline(cmInfo);
					}else{
						throw new UnsupportedOperationException(message+" message not yet implement handler process!");
					}
					
					System.out.println("NotifyMsgProcessorService receive type:"+type+" payload:"+payload);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("DeliverMessageQueueConsumer", ex);
				}
			}
		}));
	}*/

}
