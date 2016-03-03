package com.bhu.commdity.business.backendcommdity.asyncprocessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.commdity.business.backendcommdity.asyncprocessor.service.AsyncMsgHandleCommdityService;
import com.bhu.vas.business.asyn.spring.builder.ActionCommdityMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.builder.ActionCommdityMessageType;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;
import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.SpringQueueMessageListener;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class AsyncMsgBackendCommdityProcessor implements SpringQueueMessageListener{
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgBackendCommdityProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(100);
	
	@Resource
	private AsyncMsgHandleCommdityService asyncMsgHandleCommdityService;
	
	@PostConstruct
	public void initialize() {
		logger.info("AsyncMsgBackendCommdityProcessor initialize...");
		QueueMsgObserverManager.SpringQueueMessageObserver.addSpringQueueMessageListener(this);
	}
	
	@Override
	public void onMessage(final String messagejsonHasPrefix){
		//logger.info(String.format("AnsyncMsgBackendProcessor receive message[%s]", messagejsonHasPrefix));
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					String message = ActionCommdityMessageFactoryBuilder.determineActionMessage(messagejsonHasPrefix);
					ActionCommdityMessageType type = ActionCommdityMessageFactoryBuilder.determineActionType(messagejsonHasPrefix);
					if(type == null){
						throwUnsupportedOperationException(type, messagejsonHasPrefix);
					}
					switch(type){
						case OrderPaySuccessed:
							asyncMsgHandleCommdityService.orderPaySuccessedHandle(message);
							break;
						default:
							throwUnsupportedOperationException(type, messagejsonHasPrefix);
					}
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("AsyncMsgBackendCommdityProcessor", ex);
				}
			}
		}));
	}
	
	public void throwUnsupportedOperationException(ActionCommdityMessageType type, String messagejsonHasPrefix){
		throw new UnsupportedOperationException(
				String.format("ActionCommdityMessageType[%s] not yet implement handler processfull message[%s]",
						type,messagejsonHasPrefix));
	}
}
