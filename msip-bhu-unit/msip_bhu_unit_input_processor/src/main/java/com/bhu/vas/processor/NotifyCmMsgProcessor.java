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

import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;
import com.bhu.vas.business.asyn.normal.activemq.ActiveMQConnectionManager;
import com.bhu.vas.business.asyn.spring.activemq.topic.service.DeliverTopicMessageService;
import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.SpringQueueMessageListener;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class NotifyCmMsgProcessor implements SpringQueueMessageListener{
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
	private DeliverTopicMessageService deliverTopicMessageService;// =(DeliverTopicMessageService) ctx.getBean("deliverTopicMessageService");
	
	//private Map<String,Set<WifiDeviceDTO>> localCaches = new HashMap<String,Set<WifiDeviceDTO>>();
	@PostConstruct
	public void initialize() {
		logger.info("NotifyCmMsgProcessor initialize...");
		QueueMsgObserverManager.SpringQueueMessageObserver.addSpringQueueMessageListener(this);
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
						String ctx = cmInfo.toString();
						ActiveMQConnectionManager.getInstance().createNewConsumerQueues("up", cmInfo.toString(),true);
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
						//daemonRpcService.cmJoinService(cmInfo);
						/*if(cmInfo.getLast_frag() == 1){//最后一条拆包指令,数据发送成功后需要清除缓存中的ctx数据
							if(cmInfo != null && cmInfo.getClient() != null && !cmInfo.getClient().isEmpty()){//有同步过来的在线用户
								put2CtxLocalCache(ctx,cmInfo.getClient());
								Set<WifiDeviceDTO> localSet = localCaches.get(ctx);
								logger.info("~~~~~~~共~~Client:"+localSet.size());
								if(!localSet.isEmpty()){
									List<String> macs = new ArrayList<String>();
									for(WifiDeviceDTO dto:localSet){
										macs.add(dto.getMac());
									}
									daemonRpcService.wifiDevicesOnline(ctx, macs);
									deviceMessageDispatchRpcService.cmupWithWifiDeviceOnlines(ctx, new ArrayList<WifiDeviceDTO>(localSet));
								}
							}
							daemonRpcService.cmJoinService(cmInfo);
						}else{//不是最后一条拆包指令，则缓存相关client数据
							put2CtxLocalCache(ctx,cmInfo.getClient());
						}*/
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
