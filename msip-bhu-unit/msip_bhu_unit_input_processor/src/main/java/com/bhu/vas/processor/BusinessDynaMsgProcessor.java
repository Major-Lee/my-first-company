package com.bhu.vas.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.dto.WifiDeviceContextDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRpcService;
import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.DynaQueueMessageListener;

@Service
public class BusinessDynaMsgProcessor implements DynaQueueMessageListener{
	private final Logger logger = LoggerFactory.getLogger(BusinessDynaMsgProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(50);
	
	@Resource
	private IDeviceRpcService deviceRpcService;
	
	@PostConstruct
	public void initialize(){
		QueueMsgObserverManager.DynaMsgCommingObserver.addMsgCommingListener(this);
		//初始化ActiveMQConnectionManager
		//ActiveMQConnectionManager.getInstance().initConsumerQueues();
	}

	@Override
	public void onMessage(final String ctx,final String message) {
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info("BusinessNotifyMsgProcessor receive:"+ctx+"~~~~"+message);
					String type = message.substring(0, 8);
					String payload = message.substring(8);
					deviceRpcService.wifiDeviceRegister(payload, new WifiDeviceContextDTO(CmCtxInfo.builderCtx(ctx),""));
					System.out.println("BusinessNotifyMsgProcessor receive type:"+type+" payload:"+payload);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("DeliverMessageQueueConsumer", ex);
				}
			}
		}));
	}
}
