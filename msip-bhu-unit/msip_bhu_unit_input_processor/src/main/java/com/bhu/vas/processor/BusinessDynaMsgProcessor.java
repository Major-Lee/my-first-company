package com.bhu.vas.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;
import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.DynaQueueMessageListener;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class BusinessDynaMsgProcessor implements DynaQueueMessageListener{
	private final Logger logger = LoggerFactory.getLogger(BusinessDynaMsgProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(50);
	//private static String Online_Prefix = "00000001";
	/*private static final int DeviceOffline_Prefix = 3;
	private static final int DeviceNotExist_Prefix = 4;
	private static final int Transfer_Prefix = 5;*/
	@Resource
	private IDeviceMessageDispatchRpcService deviceMessageDispatchRpcService;
	@Resource
	private IDaemonRpcService daemonRpcService;

	@PostConstruct
	public void initialize(){
		QueueMsgObserverManager.DynaMsgCommingObserver.addMsgCommingListener(this);
		//初始化ActiveMQConnectionManager
		//ActiveMQConnectionManager.getInstance().initConsumerQueues();
	}

	@Override
	public void onMessage(final String ctx,final String message) {
		validateStep1(message);
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					System.out.println("BusinessNotifyMsgProcessor receive:"+ctx+"~~~~"+message);
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
							headers = ParserHeader.builder(message.substring(8, 42),type);
							payload = message.substring(42);
							break;
						default:
							throw new UnsupportedOperationException(
									String.format( "MessageType[%s] not yet implement handler process!full ctx[%s] message[%s]",
											type,ctx,message));
					}
					deviceMessageDispatchRpcService.messageDispatch(ctx,payload,headers);
					if(ParserHeader.DeviceOffline_Prefix == type){//设备下线
						
					}
					if(ParserHeader.Transfer_Prefix == type && headers.getMt() == 0 && headers.getSt()==1){//设备上线
						
					}
					System.out.println("BusinessNotifyMsgProcessor receive type:"+type+" message:"+message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("DeliverMessageQueueConsumer", ex);
				}
			}
		}));
	}
	
	public static void validateStep1(String msg){
		if(StringUtils.isEmpty(msg) || msg.length()<=8) 
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
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
