package com.bhu.vas.business.backendonline.asyncprocessor;

import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.builder.async.AsyncMessageType;
import com.bhu.vas.business.backendonline.asyncprocessor.service.impl.batchuser.BatchUserIdentityRepairServiceHandler;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.SpringQueueMessageListener;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class AsyncMsgBackendProcessor implements SpringQueueMessageListener{
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgBackendProcessor.class);
	private ExecutorService exec;// = Executors.newFixedThreadPool(100);
	@Resource
	private IMsgHandlerService batchImportConfirmServiceHandler;

	@Resource
	private IMsgHandlerService batchImportPreCheckServiceHandler;
	
	@Resource
	private IMsgHandlerService batchSharedealServiceHandler;
	
	@Resource
	private IMsgHandlerService batchDeviceTagServiceHandler;
	
	@Resource
	private IMsgHandlerService batchGroupCmdsServiceHandler;
	
	@Resource
	private IMsgHandlerService batchGroupSendSortMessageServiceHandler;
	
	@Resource
	private IMsgHandlerService batchDeviceSnkApplyServiceHandler;
	
	@Resource
	private IMsgHandlerService batchGroupDeviceSnkApplyServiceHandler;
	
	@Resource
	private IMsgHandlerService batchDeviceSnkClearServiceHandler;
	
	@Resource
	private IMsgHandlerService batchUserIdentityRepairServiceHandler;
	
	@Resource
	private IMsgHandlerService batchDeviceApplyAdvertseServiceHandler;
	
	@PostConstruct
	public void initialize() {
		logger.info("AsyncMsgBackendProcessor initialize...");
		exec = ExecObserverManager.buildExecutorService(this.getClass(),"AsyncBackend消息处理",100);
		QueueMsgObserverManager.SpringQueueAsyncMessageObserver.addSpringQueueMessageListener(this);
		
	}
	
	/*@PreDestroy
	public void destory(){
		logger.info("AsyncMsgBackendProcessor destory...");
		this.destory(exec, "AsyncMsgBackendProcessor");
		//Thread desstoryThread = new Thread(new DaemonExecRunnable(exec));
		//desstoryThread.start();
	}*/
	
	@Override
	public void onMessage(final String messagejsonHasPrefix){
		//logger.info(String.format("AnsyncMsgBackendProcessor receive message[%s]", messagejsonHasPrefix));
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					String message = AsyncMessageFactoryBuilder.determineActionMessage(messagejsonHasPrefix);
					AsyncMessageType type = AsyncMessageFactoryBuilder.determineActionType(messagejsonHasPrefix);
					if(type == null){
						throwUnsupportedOperationException(type, messagejsonHasPrefix);
					}
					switch(type){
						case BatchImportPreCheck:
							batchImportPreCheckServiceHandler.process(message);
							break;
						case BatchImportConfirm:
							batchImportConfirmServiceHandler.process(message);
							break;
						case BatchSharedealModify:
							batchSharedealServiceHandler.process(message);
							break;
						case BatchDeviceOperTag:
							batchDeviceTagServiceHandler.process(message);
							break;
						case BatchGroupDownCmds:
							batchGroupCmdsServiceHandler.process(message);
							break;
						case BatchGroupDeviceSnkApply:
							batchGroupDeviceSnkApplyServiceHandler.process(message);
							break;
						case BatchDeviceSnkApply:
							batchDeviceSnkApplyServiceHandler.process(message);
							break;	
						case BatchDeviceSnkClear:
							batchDeviceSnkClearServiceHandler.process(message);
							break;
						case BatchGroupSendSortMessage:
							batchGroupSendSortMessageServiceHandler.process(message);
							break;
						case BatchUserIdentityRepair:
							batchUserIdentityRepairServiceHandler.process(message);
						case BatchDeviceApplyAdvertise:
							batchDeviceApplyAdvertseServiceHandler.process(message);
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
	
	public void throwUnsupportedOperationException(AsyncMessageType type, String messagejsonHasPrefix){
		throw new UnsupportedOperationException(
				String.format("AsyncMessageType[%s] not yet implement handler processfull message[%s]",
						type,messagejsonHasPrefix));
	}
}
