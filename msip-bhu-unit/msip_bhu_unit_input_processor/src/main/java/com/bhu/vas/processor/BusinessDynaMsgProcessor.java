package com.bhu.vas.processor;

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
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;
import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.DynaQueueMessageListener;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class BusinessDynaMsgProcessor implements DynaQueueMessageListener{
	private final Logger logger = LoggerFactory.getLogger(BusinessDynaMsgProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(100);
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
		logger.info("BusinessDynaMsgProcessor initialize...");
		QueueMsgObserverManager.DynaMsgCommingObserver.addMsgCommingListener(this);
		//初始化ActiveMQConnectionManager
		//ActiveMQConnectionManager.getInstance().initConsumerQueues();
	}

	@Override
	public void onMessage(final String ctx,final String message) {
		logger.info(String.format("BusinessDynaMsgProcessor receive:ctx[%s] message[%s]", ctx,message));
		validateStep1(message);
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					//System.out.println(String.format("BusinessNotifyMsgProcessor receive:ctx[%s] message[%s]", ctx,message));
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
					if(headers != null){
						if(ParserHeader.DeviceOffline_Prefix == type || ParserHeader.DeviceNotExist_Prefix == type){//设备下线||设备不存在
							daemonRpcService.wifiDeviceOffline(ctx, headers.getMac());
						}
						if(ParserHeader.Transfer_Prefix == type){
							if(headers.getMt() == 0 && headers.getSt()==1){//设备上线
								daemonRpcService.wifiDeviceOnline(ctx, headers.getMac());
							}
							if(headers.getMt() == 1 && headers.getSt()==2){//CMD xml返回串
								OperationCMD cmd_opt = OperationCMD.getOperationCMDFromNo(headers.getOpt());
								if(cmd_opt != null){
									if(cmd_opt == OperationCMD.QueryDeviceLocationS1){
										daemonRpcService.wifiDeviceSerialTaskComming(ctx,payload, headers);
									}
								}
								//daemonRpcService.wifiDeviceOnline(ctx, headers.getMac());
								/*if(CMDBuilder.wasLocationQueryTaskid(headers.getTaskid())){//任务查询反馈消息
									//QuerySerialReturnDTO retDTO = RPCMessageParseHelper.parserMessageByDom4j(payload, QuerySerialReturnDTO.class);
									daemonRpcService.wifiDeviceSerialTaskComming(ctx,payload, headers);//, retDTO);
								}*/
							}
						}
						deviceMessageDispatchRpcService.messageDispatch(ctx,payload,headers);
					}
					//System.out.println("BusinessNotifyMsgProcessor receive type:"+type+" message:"+message);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("BusinessDynaMsgProcessor", ex);
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
