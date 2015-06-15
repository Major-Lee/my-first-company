package com.bhu.vas.processor;

import java.util.ArrayList;
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
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;
import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.DynaQueueMessageListener;
import com.bhu.vas.processor.task.DaemonProcessesStatusTask;
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
public class BusinessDynaMsgProcessor implements DynaQueueMessageListener{
	private final Logger logger = LoggerFactory.getLogger(BusinessDynaMsgProcessor.class);
	private ExecutorService exec_dispatcher = Executors.newFixedThreadPool(1);
	private List<ExecutorService> exec_processes = new ArrayList<ExecutorService>();//Executors.newFixedThreadPool(1);
	private int[] hits;
	private int hash_prime = 50;
	private int per_threads = 1;
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
		
		for(int i=0;i<hash_prime;i++){
			exec_processes.add(Executors.newFixedThreadPool(per_threads));
		}
		hits = new int[hash_prime];
		TaskEngine.getInstance().schedule(new DaemonProcessesStatusTask(this), 5*60*1000,5*60*1000);
		QueueMsgObserverManager.DynaMsgCommingObserver.addMsgCommingListener(this);
		//初始化ActiveMQConnectionManager
		//ActiveMQConnectionManager.getInstance().initConsumerQueues();
	}

	@Override
	public void onMessage(final String ctx,final String message) {
		//logger.info(String.format("BusinessDynaMsgProcessor receive:ctx[%s] message[%s]", ctx,message));
		validateStep1(message);
		exec_dispatcher.submit((new Runnable() {
			@Override
			public void run() {
				logger.info(String.format("BusinessDynaMsgProcessor receive:ctx[%s] message[%s]", ctx,message));
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
						onProcessor(ctx,payload,type,headers);
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
	
	public void onProcessor(final String ctx,final String payload,final int type,final ParserHeader headers) {
		String mac = headers.getMac();
		int hash = HashAlgorithmsHelper.rotatingHash(mac, hash_prime);
		hits[hash] = hits[hash]+1;
		exec_processes.get(hash).submit((new Runnable() {
			@Override
			public void run() {
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
							if(cmd_opt == OperationCMD.QueryDeviceLocationNotify){
								daemonRpcService.wifiDeviceSerialTaskComming(ctx,payload, headers);
							}
						}
					}
				}
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
		if(exec_dispatcher != null){
			String simplename = this.getClass().getSimpleName();
			System.out.println(simplename+" exec正在shutdown");
			exec_dispatcher.shutdown();
			System.out.println(simplename+" exec正在shutdown成功");
			while(true){
				System.out.println(simplename+" 正在判断exec是否执行完毕");
				if(exec_dispatcher.isTerminated()){
					System.out.println(simplename+" exec是否执行完毕,终止exec...");
					exec_dispatcher.shutdownNow();
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

	public int[] getHits() {
		return hits;
	}

	public void setHits(int[] hits) {
		this.hits = hits;
	}
	
}
