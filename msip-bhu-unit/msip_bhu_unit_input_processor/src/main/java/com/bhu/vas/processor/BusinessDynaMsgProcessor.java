package com.bhu.vas.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;




/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.bhu.pure.kafka.business.observer.KafkaMsgObserverManager;
import com.bhu.pure.kafka.business.observer.listener.DynaMessageListener;
import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.charging.ActionBuilder;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;
import com.bhu.vas.business.asyn.spring.activemq.topic.service.DeliverTopicMessageService;
import com.bhu.vas.processor.bulogs.DynamicLogWriter;
import com.bhu.vas.processor.task.DaemonProcessesStatusTask;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.task.TaskEngine;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class BusinessDynaMsgProcessor implements DynaMessageListener{
	private final Logger logger = LoggerFactory.getLogger(BusinessDynaMsgProcessor.class);
	private ExecutorService exec_dispatcher = Executors.newFixedThreadPool(1);
	private List<ExecutorService> exec_processes = new ArrayList<ExecutorService>();//Executors.newFixedThreadPool(1);
	private int[] hits;
	//private int hash_prime = 50;
	private int hash_prime = 50;
	private int per_threads = 1;
	//private static String Online_Prefix = "00000001";
	/*private static final int DeviceOffline_Prefix = 3;
	private static final int DeviceNotExist_Prefix = 4;
	private static final int Transfer_Prefix = 5;*/
	//@Resource
	//private IDaemonRpcService daemonRpcService;

	@Resource
	private IDeviceMessageDispatchRpcService deviceMessageDispatchRpcService;

	@Resource
	private DeliverTopicMessageService deliverTopicMessageService;// =(DeliverTopicMessageService) ctx.getBean("deliverTopicMessageService");

	@PostConstruct
	public void initialize(){
		System.out.println("BusinessDynaMsgProcessor initialize...");
		logger.info("BusinessDynaMsgProcessor initialize...");
		
		for(int i=0;i<hash_prime;i++){
			exec_processes.add(Executors.newFixedThreadPool(per_threads));
		}
		hits = new int[hash_prime];
		TaskEngine.getInstance().schedule(new DaemonProcessesStatusTask(this), 30*60*1000,60*60*1000);
		KafkaMsgObserverManager.DynaMsgCommingObserver.addMsgCommingListener(this);
		//初始化ActiveMQConnectionManager
		//ActiveMQConnectionManager.getInstance().initConsumerQueues();
	}

	@Override
	//public void onMessage(final String ctx,final String message) {
	public void onMessage(final String topic,final int partition,final String key,final String message,final long offset,final String consumerId) {
/*		logger.info(String.format("BusinessDynaMsgProcessor Received message: topic[%s] partition[%s] key[%s] message[%s] offset[%s] consumerId[%s]",
						topic, partition,
						key, message,
						offset, consumerId));*/

		validateStep1(message);
		exec_dispatcher.submit((new Runnable() {
			@Override
			public void run() {
				//logger.info(String.format("BusinessDynaMsgProcessor receive:ctx[%s] message[%s]", ctx,message));
				try{
					System.out.println(String.format("Dyna Received message: topic[%s] partition[%s] key[%s] message[%s] offset[%s] consumerId[%s]",
							topic, partition,
							key, message,
							offset, consumerId));
					//System.out.println(String.format("BusinessNotifyMsgProcessor receive:ctx[%s] message[%s]", ctx,message));
					logger.info(String.format("Dyna Received message: topic[%s] partition[%s] key[%s] message[%s] offset[%s] consumerId[%s]",
							topic, partition,
							key, message,
							offset, consumerId));					
					int type = Integer.parseInt(message.substring(0, 8));
					ParserHeader headers = null;
					String payload = null;
					switch(type){
						case ParserHeader.DeviceOffline_Prefix://0000000362687500003e
							headers = ParserHeader.builder(null,type);
							payload = StringHelper.formatMacAddress(message.substring(8));
							headers.setMac(payload);
							break;
						case ParserHeader.DeviceNotExist_Prefix:
							headers = ParserHeader.builder(null,type);
							payload = StringHelper.formatMacAddress(message.substring(8));
							headers.setMac(payload);
							break;
						case ParserHeader.Transfer_Prefix:
							headers = ParserHeader.builder(message.substring(8, ParserHeader.Cmd_Header_Length),type);
							if(headers.getMt() == ParserHeader.Transfer_mtype_1 && headers.getSt() == ParserHeader.Transfer_stype_12){
								//新版本增值模块上报的指令
								String vap_header = message.substring(ParserHeader.Cmd_Header_Length,ParserHeader.Cmd_Vap_Header_Length);
								headers.append(vap_header);
								payload = message.substring(ParserHeader.Cmd_Vap_Header_Length);
								//System.out.printf("~~~~~~~~~~ctx[%s] ParserHeader[%s] playload[%s]",ctx, headers,payload);
							}else{
								payload = message.substring(ParserHeader.Cmd_Header_Length);
							}
							break;
						default:
							throw new UnsupportedOperationException(
									String.format( "MessageType[%s] not yet implement handler process!full topic[%s] message[%s]",
											type,topic,message));
					}
					if(headers != null){
						onProcessor(topic,payload,type,headers);
						//deviceMessageDispatchRpcService.messageDispatch(ctx,payload,headers);
					}
					//System.out.println("BusinessNotifyMsgProcessor receive type:"+type+" message:"+message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("BusinessDynaMsgProcessor", ex);
				}
			}
		}));
	}
	
	public void doSpecialProcessor(final String ctx,final String payload,final int type,final ParserHeader headers){
		switch(type){
			case ParserHeader.DeviceOffline_Prefix:
				DynamicLogWriter.doLogger(headers.getMac(), 
						ActionBuilder.toJsonHasPrefix(
								ActionBuilder.builderDeviceOfflineAction(headers.getMac(), System.currentTimeMillis()))
						);
				deliverTopicMessageService.sendDeviceOffline(ctx, headers.getMac());
				break;
			case ParserHeader.DeviceNotExist_Prefix:
				DynamicLogWriter.doLogger(headers.getMac(), 
						ActionBuilder.toJsonHasPrefix(
								ActionBuilder.builderDeviceNotExistAction(headers.getMac(), System.currentTimeMillis()))
						);
				deliverTopicMessageService.sendDeviceOffline(ctx, headers.getMac());
				break;
			case ParserHeader.Transfer_Prefix:
					if(headers.getMt() == ParserHeader.Transfer_mtype_0 && headers.getSt()==1){//设备上线
						DynamicLogWriter.doLogger(headers.getMac(), 
								ActionBuilder.toJsonHasPrefix(
										ActionBuilder.builderDeviceOnlineAction(headers.getMac(), System.currentTimeMillis())));
						deliverTopicMessageService.sendDeviceOnline(ctx, headers.getMac());
						//daemonRpcService.wifiDeviceOnline(ctx, headers.getMac());
					}
					if(headers.getMt() == ParserHeader.Transfer_mtype_1 && headers.getSt()==7){//终端上下线
						//DynamicLogWriter.doLogger(headers.getMac(), payload);
						List<HandsetDeviceDTO> dtos = RPCMessageParseHelper.generateDTOListFromMessage(payload, 
								HandsetDeviceDTO.class);
						if(dtos != null && !dtos.isEmpty()){
							HandsetDeviceDTO fristDto = dtos.get(0);
							if(HandsetDeviceDTO.Action_Online.equals(fristDto.getAction())){
								DynamicLogWriter.doLogger(headers.getMac(), 
										ActionBuilder.toJsonHasPrefix(
												ActionBuilder.builderHandsetOnlineAction(fristDto.getMac(),headers.getMac(), System.currentTimeMillis())));
							}
							else if(HandsetDeviceDTO.Action_Offline.equals(fristDto.getAction())){
								DynamicLogWriter.doLogger(headers.getMac(), 
										ActionBuilder.toJsonHasPrefix(
												ActionBuilder.builderHandsetOfflineAction(fristDto.getMac(),headers.getMac(),
														Long.parseLong(fristDto.getTx_bytes()),Long.parseLong(fristDto.getRx_bytes()), System.currentTimeMillis())));
							}
							else if(HandsetDeviceDTO.Action_Sync.equals(fristDto.getAction())){
								List<String> hmacs = new ArrayList<String>();
								for(HandsetDeviceDTO dto:dtos){
									hmacs.add(dto.getMac());
								}
								DynamicLogWriter.doLogger(headers.getMac(), 
										ActionBuilder.toJsonHasPrefix(
												ActionBuilder.builderHandsetSyncAction(hmacs,headers.getMac(), System.currentTimeMillis())));
								hmacs.clear();
								hmacs = null;
							}
							else if(HandsetDeviceDTO.Action_Authorize.equals(fristDto.getAction())) {

							}
						}
					}
				break;
		}
	}
	
	public void onProcessor(final String topic,final String payload,final int type,final ParserHeader headers) {
		String mac = headers.getMac();
		if(mac.startsWith(BusinessRuntimeConfiguration.DeviceTesting_Mac_Prefix)) return;
		int hash = HashAlgorithmsHelper.rotatingHash(mac, hash_prime);
		hits[hash] = hits[hash]+1;
		exec_processes.get(hash).submit((new Runnable() {
			@Override
			public void run() {
				String ctx = CmCtxInfo.parserCtxName(topic);
				doSpecialProcessor(ctx,payload,type,headers);
				deviceMessageDispatchRpcService.messageDispatch(ctx,payload,headers);
			}
		}));
	}
	
	public static void validateStep1(String msg){
		if(StringUtils.isEmpty(msg) || msg.length()<=8) 
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
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
		if(exec_processes != null && !exec_processes.isEmpty()){
			int index = 0;
			Iterator<ExecutorService> iter = exec_processes.iterator();
			while(iter.hasNext()){
				ExecutorService exec = iter.next();
				exec.shutdown();
				System.out.println(String.format("[%s] exec[%s]正在shutdown成功",simplename,index));
				while(true){
					System.out.println(String.format("[%s] 正在判断exec[%s]是否执行完毕",simplename,index));
					if(exec.isTerminated()){
						System.out.println(String.format("[%s] exec[%s]是否执行完毕,终止exec...",simplename,index));
						exec.shutdownNow();
						System.out.println(String.format("[%s] exec[%s]是否执行完毕,终止exec成功",simplename,index));
						break;
					}else{
						System.out.println(String.format("[%s] exec[%s]未执行完毕...",simplename,index));
						try {
							Thread.sleep(2*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				iter.remove();
				index++;
			}
		}
		
	}

	public int[] getHits() {
		return hits;
	}

	public void setHits(int[] hits) {
		this.hits = hits;
	}
	
}
