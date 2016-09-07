package com.bhu.vas.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
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
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceMessageDispatchRpcService;
import com.bhu.vas.api.rpc.tag.model.TagGroupHandsetDetail;
import com.bhu.vas.business.ds.tag.facade.TagGroupFacadeService;
import com.bhu.vas.processor.bulogs.DynamicLogWriter;
import com.bhu.vas.processor.task.DaemonProcessesStatusTask;
import com.smartwork.msip.business.logger.BusinessDefinedLogger;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.HashAlgorithmsHelper;
import com.smartwork.msip.cores.helper.task.TaskEngine;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class BusinessDynaMsgProcessor implements DynaMessageListener{
	private final Logger logger = LoggerFactory.getLogger(BusinessDynaMsgProcessor.class);
	private ExecutorService exec_dispatcher = null;//Executors.newFixedThreadPool(1);
	private List<ExecutorService> exec_processes = new ArrayList<ExecutorService>();//Executors.newFixedThreadPool(1);
	private int[] hits;
	private int hash_prime = 50;

//	@Resource
//	private IDeviceMessageDispatchRpcService deviceMessageDispatchRpcService;
	
	@Resource
	private TagGroupFacadeService tagGroupFacadeService;
	
	@PostConstruct
	public void initialize(){
		logger.info("BusinessDynaMsgProcessor initialize...");
		exec_dispatcher = ExecObserverManager.buildSingleThreadExecutor
	        (this.getClass(), "DynaMsg dispatcher消息处理");
		
		for(int i=0;i<hash_prime;i++){
			ExecutorService exec_process = ExecObserverManager.buildSingleThreadExecutor
			    (this.getClass(), "DynaMsg process消息处理".concat(String.valueOf(i)));
			
			exec_processes.add(exec_process);//Executors.newFixedThreadPool(per_threads));
		}
		
		hits = new int[hash_prime];
		TaskEngine.getInstance().schedule(new DaemonProcessesStatusTask(this), 
				                          30 * 60 * 1000, 60 * 60 * 1000);
		KafkaMsgObserverManager.DynaMsgCommingObserver.addMsgCommingListener(this);
	}

	@Override
	public void onMessage(final String topic, final int partition, final String key,
			              final String message, final long offset, final String consumerId) {

		validateStep1(message);
		exec_dispatcher.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info(String.format("Dyna Received message: topic[%s] partition[%s] key[%s] "
							                + "message[%s] offset[%s] consumerId[%s]",
							                  topic, partition, key, message, offset, consumerId));					
					int type = Integer.parseInt(message.substring(0, 8));
					ParserHeader headers = null;
					String payload = null;
					switch(type){
						case ParserHeader.DeviceOffline_Prefix://0000000362687500003e
//							headers = ParserHeader.builder(null, type);
//							payload = StringHelper.formatMacAddress(message.substring(8));
//							headers.setMac(payload);
							break;
						case ParserHeader.DeviceNotExist_Prefix:
//						    headers = ParserHeader.builder(null, type);
//							payload = StringHelper.formatMacAddress(message.substring(8));
//							headers.setMac(payload);
							break;
						case ParserHeader.Transfer_Prefix:
							headers = ParserHeader.builder(message.substring
									                          (8, ParserHeader.Cmd_Header_Length), type);
							if(headers.getMt() == ParserHeader.Transfer_mtype_1 && 
							   headers.getSt() == ParserHeader.Transfer_stype_12){
								//新版本增值模块上报的指令
								String vap_header = message.substring(ParserHeader.Cmd_Header_Length, 
										                              ParserHeader.Cmd_Vap_Header_Length);
								headers.append(vap_header);
								payload = message.substring(ParserHeader.Cmd_Vap_Header_Length);
								//System.out.printf("~~~~~~~~~~ctx[%s] ParserHeader[%s] playload[%s]",ctx, headers,payload);
							}else{
								payload = message.substring(ParserHeader.Cmd_Header_Length);
							}
							break;
						default:
							throw new UnsupportedOperationException
							    (String.format("MessageType[%s] not yet implement handler "
							    		     + "process!full topic[%s] message[%s]", 
							    		       type, topic, message));
					}
					if(headers != null){
						onProcessor(topic, payload, type, headers);
					}
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("BusinessDynaMsgProcessor", ex);
				}
			}
		}));
	}
	
	private void doSpecialProcessor(final String ctx, final String payload, final int type,
			                       final ParserHeader headers){
		//System.out.println(String.format("ctx[%s] type[%s] paylod[%s]", ctx,type,payload));
		if(headers != null && OperationCMD.DeviceCmdPassThrough.getNo().equals(headers.getOpt())){
			BusinessDefinedLogger.doInfoLog(String.format("ctx[%s] mac[%s] paylod[%s]", 
					                                      ctx, headers.getMac(), payload));
		}
		switch(type){
			case ParserHeader.Transfer_Prefix:
			    // 终端上下线
				if(headers.getMt() == ParserHeader.Transfer_mtype_1 && headers.getSt()==7){
					List<HandsetDeviceDTO> dtos = RPCMessageParseHelper.generateDTOListFromMessage
					    (payload, HandsetDeviceDTO.class);
					if(dtos != null && !dtos.isEmpty()){
						HandsetDeviceDTO fristDto = dtos.get(0);
						if(HandsetDeviceDTO.Action_Online.equals(fristDto.getAction())){
							DynamicLogWriter.doLogger
							    (headers.getMac(), ActionBuilder.toJsonHasPrefix
							    		               (ActionBuilder.builderHandsetOnlineAction(fristDto.getMac(), headers.getMac(),
													    fristDto.getDhcp_name(), fristDto.getIp(),
													    fristDto.getVapname(),fristDto.getBssid(),
													    fristDto.getRssi(),fristDto.getSnr(),fristDto.getAuthorized(),fristDto.getEthernet(),
													    System.currentTimeMillis())));
						}
						else if(HandsetDeviceDTO.Action_Offline.equals(fristDto.getAction())){
							DynamicLogWriter.doLogger(headers.getMac(), 
									ActionBuilder.toJsonHasPrefix(
											ActionBuilder.builderHandsetOfflineAction(fristDto.getMac(),headers.getMac(),
													fristDto.getUptime(),
													fristDto.getVapname(),fristDto.getBssid(),
													fristDto.getRssi(),fristDto.getSnr(),fristDto.getAuthorized(),fristDto.getEthernet(),
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
							DynamicLogWriter.doLogger(headers.getMac(), 
									ActionBuilder.toJsonHasPrefix(
											ActionBuilder.builderHandsetAuthorizeAction(fristDto.getMac(),headers.getMac(),
													fristDto.getVapname() ,fristDto.getAuthorized(), System.currentTimeMillis())));
						
						}
					}
				}
			break;
		}
	}
	
	private void onProcessor(final String topic, final String payload, final int type, 
			                 final ParserHeader headers) {
		String mac = headers.getMac();
		if(mac.startsWith(BusinessRuntimeConfiguration.DeviceTesting_Mac_Prefix)) return;
		int hash = HashAlgorithmsHelper.rotatingHash(mac, hash_prime);
		hits[hash] = hits[hash]+1;
		exec_processes.get(hash).submit((new Runnable() {
			@Override
			public void run() {
				try{
					String ctx = CmCtxInfo.parserCtxName(topic);
					doSpecialProcessor(ctx,payload,type,headers);
//					deviceMessageDispatchRpcService.messageDispatch(ctx,payload,headers);
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("BusinessDynaMsgProcessor onProcessor", ex);
				}
			}
		}));
	}
	
	public static void validateStep1(String msg){
		if(StringUtils.isEmpty(msg) || msg.length()<=8) 
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
	}

	public int[] getHits() {
		return hits;
	}

	public void setHits(int[] hits) {
		this.hits = hits;
	}
	
}
