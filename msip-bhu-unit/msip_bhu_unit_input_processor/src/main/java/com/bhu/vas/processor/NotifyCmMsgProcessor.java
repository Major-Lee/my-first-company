package com.bhu.vas.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import com.bhu.vas.business.asyn.spring.activemq.topic.service.DeliverTopicMessageService;
import com.bhu.vas.processor.input.DeliverMessageTopicConsumer;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class NotifyCmMsgProcessor implements DynaMessageListener{
	private final Logger logger = LoggerFactory.getLogger(NotifyCmMsgProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(5);
	//00010000{"name":"cm001","thread":"3","ip":"192.168.0.101"}
	/*private static String Online_Prefix = "00000001";
	private static String Offline_Prefix = "00000002";*/
	@Resource
	private IDeviceMessageDispatchRpcService deviceMessageDispatchRpcService;
	//@Resource
	//private IDaemonRpcService daemonRpcService;
	@Resource
	private DeliverMessageTopicConsumer deliverMessageTopicConsumer;
	@Resource
	private DeliverTopicMessageService deliverTopicMessageService;// =(DeliverTopicMessageService) ctx.getBean("deliverTopicMessageService");
	
	//private Map<String,Set<WifiDeviceDTO>> localCaches = new HashMap<String,Set<WifiDeviceDTO>>();
	@PostConstruct
	public void initialize() {
		logger.info("NotifyCmMsgProcessor initialize...");
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
						//ActiveMQConnectionsManager.getInstance().createNewConsumerQueues(QueueInfo.build(cmInfo.getMq_host(),cmInfo.getMq_port(), cmInfo.toString()),true);
						if(cmInfo.getClient() != null && !cmInfo.getClient().isEmpty()){
							List<String> macs = new ArrayList<String>();
							for(WifiDeviceDTO dto:cmInfo.getClient()){
								macs.add(dto.getMac());
							}
							//daemonRpcService.wifiDevicesOnline(ctx, macs);
							deliverTopicMessageService.sendDevicesOnline(ctx, macs);
							deviceMessageDispatchRpcService.cmupWithWifiDeviceOnlines(ctx, cmInfo.getClient());
						}
						deliverTopicMessageService.sendCmJoinMessage(cmInfo);
					}else if(ParserHeader.Offline_Prefix == type){//移除所有属于此cm的用户，并且down queue不能写入数据
						cmInfo = JsonHelper.getDTO(payload, CmCtxInfo.class);
						deliverTopicMessageService.sendCmLeaveMessage(cmInfo);
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
	
	/*private synchronized void put2CtxLocalCache(String ctx,List<WifiDeviceDTO> dtos){
		if(dtos == null || dtos.isEmpty()) return;
		Set<WifiDeviceDTO> localSet = localCaches.get(ctx);
		if(localSet == null){
			localSet = new HashSet<WifiDeviceDTO>();
			localSet.addAll(dtos);
			localCaches.put(ctx, localSet);
		}else{
			localSet.addAll(dtos);
		}
	}*/
	
	@PreDestroy
	public void destory(){
		if(exec != null){
			String simplename = this.getClass().getSimpleName();
			System.out.println(simplename+" exec正在shutdown");
			exec.shutdown();
			System.out.println(simplename+" exec正在shutdown成功");
			while(true){
				System.out.println(simplename+" 正在判断exec是否执行完毕");
				if(exec.isTerminated()){
					System.out.println(simplename+" exec是否执行完毕,终止exec...");
					exec.shutdownNow();
					System.out.println(simplename+" exec是否执行完毕,终止exec成功");
					break;
				}else{
					System.out.println(simplename+" exec未执行完毕...");
					try {
						Thread.sleep(2*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}
