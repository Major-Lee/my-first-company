package com.bhu.vas.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.business.asyn.normal.activemq.ActiveMQConnectionManager;
import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.CmMessageListener;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class NotifyCmMsgProcessor implements CmMessageListener{
	private final Logger logger = LoggerFactory.getLogger(NotifyCmMsgProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(5);
	//00010000{"name":"cm001","thread":"3","ip":"192.168.0.101"}
	/*private static String Online_Prefix = "00000001";
	private static String Offline_Prefix = "00000002";*/
	
	@Resource
	private IDaemonRpcService daemonRpcService;
	
	@PostConstruct
	public void initialize() {
		logger.info("NotifyCmMsgProcessor initialize...");
		QueueMsgObserverManager.CmMessageObserver.addCmMessageListener(this);
	}
	
	@Override
	public void onMessage(final String message){
		logger.info(String.format("NotifyCmMsgProcessor receive message[%s]", message));
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
						//QueueMsgObserverManager.CmMessageObserver.notifyCmOnline(cmInfo);
						ActiveMQConnectionManager.getInstance().createNewConsumerQueues("up", cmInfo.toString(),true);
						daemonRpcService.cmJoinService(cmInfo);
					}else if(ParserHeader.Offline_Prefix == type){
						cmInfo = JsonHelper.getDTO(payload, CmCtxInfo.class);
						//QueueMsgObserverManager.CmMessageObserver.notifyCmOffline(cmInfo);
					}else{
						throw new UnsupportedOperationException(message+" message not yet implement handler process!");
					}
					//System.out.println("NotifyMsgProcessorService receive type:"+type+" payload:"+payload);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("NotifyCmMsgProcessor", ex);
				}
			}
		}));
	}
	
	/*@Override
	public void onCmOnline(CmInfo info) {
		
	}

	@Override
	public void onCmOffline(CmInfo info) {
		
	}*/
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
