package com.bhu.vas.business.backendonline.asyncprocessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;
import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchsharedeal.BatchSharedealServiceHandler;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.backendonline.plugins.hook.DaemonExecRunnable;
import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.SpringQueueMessageListener;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class AsyncMsgBackendProcessor implements SpringQueueMessageListener{
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgBackendProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(100);
	
	@Resource
	private IMsgHandlerService batchImportConfirmServiceHandler;
	
	@Resource
	private BatchSharedealServiceHandler batchSharedealServiceHandler;
	
	@PostConstruct
	public void initialize() {
		logger.info("AsyncMsgBackendProcessor initialize...");
		QueueMsgObserverManager.SpringQueueAsyncMessageObserver.addSpringQueueMessageListener(this);
	}
	
	@PreDestroy
	public void destory(){
		logger.info("AsyncMsgBackendProcessor destory...");
		Thread desstoryThread = new Thread(new DaemonExecRunnable(exec));
		desstoryThread.start();
	}
	
	@Override
	public void onMessage(final String messagejsonHasPrefix){
		//logger.info(String.format("AnsyncMsgBackendProcessor receive message[%s]", messagejsonHasPrefix));
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					String message = ActionMessageFactoryBuilder.determineActionMessage(messagejsonHasPrefix);
					ActionMessageType type = ActionMessageFactoryBuilder.determineActionType(messagejsonHasPrefix);
					if(type == null){
						throwUnsupportedOperationException(type, messagejsonHasPrefix);
					}
					switch(type){
						case BatchImportConfirm:
							batchImportConfirmServiceHandler.process(message);
							break;
						case BatchSharedealModify:
							batchSharedealServiceHandler.process(message);
							break;
						default:
							throwUnsupportedOperationException(type, messagejsonHasPrefix);
					}
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("AnsyncMsgProcessor", ex);
				}
			}
		}));
	}
	
	public void throwUnsupportedOperationException(ActionMessageType type, String messagejsonHasPrefix){
		throw new UnsupportedOperationException(
				String.format("ActionMessageType[%s] not yet implement handler processfull message[%s]",
						type,messagejsonHasPrefix));
	}
}
