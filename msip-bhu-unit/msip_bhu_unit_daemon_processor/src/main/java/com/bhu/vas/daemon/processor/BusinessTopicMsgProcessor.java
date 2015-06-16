package com.bhu.vas.daemon.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.business.asyn.normal.activemq.ActiveMQConnectionManager;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;
import com.bhu.vas.business.asyn.spring.model.topic.CmJoinNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.topic.CmLeaveNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.topic.DeviceOfflineNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.topic.DeviceOnlineNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.topic.DevicesOnlineNotifyDTO;
import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.SpringTopicMessageListener;
import com.bhu.vas.daemon.SessionInfo;
import com.bhu.vas.daemon.SessionManager;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 * 由于此部分notify消息涉及的都是内存操作，因此目前以单线程执行实现，保证消息次序问题
 * 如果此部分存在性能问题，则分发消息 以 mac hash策略 到n个ExecutorService ，每个ExecutorService保证一个线程，这样就是 n线程消息处理
 */
@Service
public class BusinessTopicMsgProcessor implements SpringTopicMessageListener{
	private final Logger logger = LoggerFactory.getLogger(BusinessTopicMsgProcessor.class);
	private ExecutorService exec_dispatcher = Executors.newFixedThreadPool(1);
	/*private List<ExecutorService> exec_processes = new ArrayList<ExecutorService>();//Executors.newFixedThreadPool(1);
	private int[] hits;
	private int hash_prime = 5;
	private int per_threads = 1;*/
	@PostConstruct
	public void initialize(){
		logger.info("initialize...");
		/*for(int i=0;i<hash_prime;i++){
			exec_processes.add(Executors.newFixedThreadPool(per_threads));
		}
		hits = new int[hash_prime];*/
		QueueMsgObserverManager.SpringTopicMessageObserver.addSpringTopicMessageListener(this);
		logger.info("initialize successfully!");
	}

	@Override
	public void onMessage(final String messagejsonHasPrefix) {
		exec_dispatcher.submit((new Runnable() {
			@Override
			public void run() {
				logger.info(String.format("receive message[%s]", messagejsonHasPrefix));
				try{
					ActionMessageType type = ActionMessageFactoryBuilder.determineActionType(messagejsonHasPrefix);
					if(type == null){
						throwUnsupportedOperationException(type, messagejsonHasPrefix);
					}
					String message = ActionMessageFactoryBuilder.determineActionMessage(messagejsonHasPrefix);
					switch(type){
						case TOPICCMJoinNotify:
							processCmJoinNotify(message);
							break;
						case TOPICCMLeaveNotify:
							processCmLeaveNotify(message);
							break;
						case TOPICDeviceOnlineNotify:
							processDeviceOnlineNotify(message);
							break;
						case TOPICDevicesOnlineNotify:
							processDevicesOnlineNotify(message);
							break;
						case TOPICDeviceOfflineNotify:
							processDeviceOfflineNotify(message);
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
				String.format("Action Topic MessageType[%s] not yet implement handler processfull message[%s]",
						type,messagejsonHasPrefix));
	}
	
	@PreDestroy
	public void destory(){
		String simplename = this.getClass().getSimpleName();
		if(exec_dispatcher != null){
			System.out.println(simplename+" exec_dispatcher正在shutdown");
			exec_dispatcher.shutdown();
			System.out.println(simplename+" exec_dispatcher正在shutdown成功");
			while(true){
				System.out.println(simplename+" 正在判断exec_dispatcher是否执行完毕");
				if(exec_dispatcher.isTerminated()){
					System.out.println(simplename+" exec_dispatcher是否执行完毕,终止exec...");
					exec_dispatcher.shutdownNow();
					System.out.println(simplename+" exec_dispatcher是否执行完毕,终止exec成功");
					break;
				}else{
					System.out.println(simplename+" exec_dispatcher未执行完毕...");
					try {
						Thread.sleep(2*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	private void processCmJoinNotify(String message){
		CmJoinNotifyDTO dto = JsonHelper.getDTO(message, CmJoinNotifyDTO.class);
		ActiveMQConnectionManager.getInstance().createNewProducerQueues("down", dto.toString(), true);
		logger.info(String.format("processCmJoinNotify receive message[%s] successfully!", message));
	}

	private void processCmLeaveNotify(String message){
		CmLeaveNotifyDTO dto = JsonHelper.getDTO(message, CmLeaveNotifyDTO.class);
		SessionManager.getInstance().removeSessionByCtx(dto.toString());
		logger.info(String.format("processCmLeaveNotify receive message[%s] successfully!", message));
	}
	
	
	private void processDeviceOnlineNotify(String message){
		DeviceOnlineNotifyDTO dto = JsonHelper.getDTO(message, DeviceOnlineNotifyDTO.class);
		SessionManager.getInstance().addSession(dto.getMac(), dto.getCtx());
		logger.info(String.format("processDeviceOnlineNotify receive message[%s] successfully!", message));
	}
	
	private void processDevicesOnlineNotify(String message){
		DevicesOnlineNotifyDTO dto = JsonHelper.getDTO(message, DevicesOnlineNotifyDTO.class);
		for(String mac:dto.getMacs()){
			SessionManager.getInstance().addSession(mac, dto.getCtx());
		}
		logger.info(String.format("processDevicesOnlineNotify receive message[%s] successfully!", message));
	}
	
	private void processDeviceOfflineNotify(String message){
		DeviceOfflineNotifyDTO dto = JsonHelper.getDTO(message, DeviceOfflineNotifyDTO.class);
		SessionInfo sessionCtx = SessionManager.getInstance().getSession(dto.getMac());
		if(sessionCtx != null && dto.getCtx().equals(sessionCtx.getCtx())){
			SessionManager.getInstance().removeSession(dto.getMac());
		}else{
			;//TODO:如何处理
		}
		logger.info(String.format("processDeviceOfflineNotify receive message[%s] successfully!", message));
	}
	
}
