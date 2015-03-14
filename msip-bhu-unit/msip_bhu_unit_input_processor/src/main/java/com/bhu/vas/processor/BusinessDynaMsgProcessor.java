package com.bhu.vas.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;
import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.DynaQueueMessageListener;
import com.smartwork.msip.cores.helper.StringHelper;

@Service
public class BusinessDynaMsgProcessor implements DynaQueueMessageListener{
	private final Logger logger = LoggerFactory.getLogger(BusinessDynaMsgProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(50);
	//private static String Online_Prefix = "00000001";
	private static final int DeviceOffline_Prefix = 3;
	private static final int DeviceNotExist_Prefix = 4;
	private static final int Transfer_Prefix = 5;
	@Resource
	private IDeviceMessageDispatchRpcService deviceMessageDispatchRpcService;
	
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
					System.out.println("BusinessNotifyMsgProcessor receive:"+ctx+"~~~~"+message);
					String type = message.substring(0, 8);
					ParserHeader headers = null;
					String payload = null;
					switch(Integer.parseInt(type)){
						case DeviceOffline_Prefix://0000000362687500003e
							payload = StringHelper.formatMacAddress(payload.substring(8));
							break;
						case DeviceNotExist_Prefix:
							payload = StringHelper.formatMacAddress(payload.substring(8));
							break;
						case Transfer_Prefix:
							headers = ParserHeader.builder(message.substring(8, 42));
							payload = message.substring(42);
							break;
						default:
							throw new UnsupportedOperationException(
									String.format( "MessageType[%s] not yet implement handler process!full ctx[%s] message[%s]",
											type,ctx,message));
					}
					
					System.out.println("BusinessNotifyMsgProcessor receive type:"+type+" message:"+message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("DeliverMessageQueueConsumer", ex);
				}
			}
		}));
	}
	
/*	public static String[] parserTransferMsgHeader(String msg){
		String[] array = new String[4];
		array[0] = msg.substring(0, 12);//12字节mac
		array[1] = msg.substring(12, 22);//10字节任务id
		array[2] = msg.substring(22, 26);//设备报文主类型(4字节)
		array[3] = msg.substring(26, 34);//子类型(8字节)
		return array;
	}
	
	public static void main(String[] argv){
		//00000005
		String[] array = parserTransferMsgHeader("62687500003e0000000000000100000007");
		int i=0;
		for(String str:array){
			
			System.out.println(i==0?StringHelper.formatMacAddress(str):str);
			i++;
		}
	}*/
}
