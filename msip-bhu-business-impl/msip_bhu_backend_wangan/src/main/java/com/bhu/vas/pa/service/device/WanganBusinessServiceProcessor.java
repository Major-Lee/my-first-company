package com.bhu.vas.pa.service.device;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.pure.kafka.business.observer.KafkaMsgObserverManager;
import com.bhu.pure.kafka.business.observer.listener.DynaMessageListener;
import com.bhu.pure.kafka.client.producer.StringKafkaMessageProducer;
import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.charging.ActionBuilder;
import com.bhu.vas.api.dto.charging.ActionBuilder.ActionMode;
import com.bhu.vas.api.dto.charging.HandsetAuthorizeAction;
import com.bhu.vas.api.dto.commdity.internal.useragent.OrderUserAgentDTO;
import com.bhu.vas.api.dto.handset.HandsetOfflineAction;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.business.bucache.redis.serviceimpl.commdity.UserOrderDetailsHashService;
import com.bhu.vas.pa.dto.PaHandsetOnlineAction;
import com.bhu.vas.pa.log.WriterThread;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * device RPC组件的业务service
 * @author tangzichao
 *
 */
@Service
public class WanganBusinessServiceProcessor implements DynaMessageListener {
	private final Logger logger = LoggerFactory.getLogger(WanganBusinessServiceProcessor.class);

	
	@Resource
	private StringKafkaMessageProducer wanganMessageTopicProducer;
	
	
	@PostConstruct
	public void initialize(){
		logger.info("WanganBusinessServiceProcessor initialize...");
		KafkaMsgObserverManager.DynaMsgCommingObserver.addMsgCommingListener(this);
	}
	
	/**
	 * 移动设备连接状态请求生成，网安终端上线消息
	 * 1:online
	 * 2:offline
	 * 3:auth
	 * @param ctx
	 * @param payload
	 * modified by PengYu Zhang for handset storage
	 */
	public void doWangAnProcessor(String ctx, String payload, ParserHeader parserHeader) {
		logger.info("..............wangan processor..............");
		try {
			if(parserHeader != null && OperationCMD.DeviceCmdPassThrough.getNo().equals(parserHeader.getOpt())){
				logger.info(String.format("ctx[%s] mac[%s] paylod[%s]", ctx,parserHeader.getMac(),payload));
			}
			List<HandsetDeviceDTO> dtos = RPCMessageParseHelper.generateDTOListFromMessage
			    (payload, HandsetDeviceDTO.class);
			
			if(dtos == null || dtos.isEmpty()) return;
			
			for (HandsetDeviceDTO dto : dtos) {
				logger.info("do WangAn Processor" + dto.getAction());
				dto.setLast_wifi_id(parserHeader.getMac().toLowerCase());
				dto.setTs(System.currentTimeMillis());
			}
			
			HandsetDeviceDTO fristDto = dtos.get(0);
			if(HandsetDeviceDTO.Action_Online.equals(fristDto.getAction())){
				logger.info("do WangAn Processor" + fristDto.getAction());
				
				PaHandsetOnlineAction onlineAction = PaHandsetOnlineAction.builderHandsetOnlineAction
				    (fristDto.getMac(), parserHeader.getMac().toLowerCase(), fristDto.getDhcp_name(), fristDto.getIp(),
					 fristDto.getVapname(), fristDto.getBssid(), fristDto.getRssi(), fristDto.getSnr(),
					 fristDto.getAuthorized(), fristDto.getEthernet(), System.currentTimeMillis(), fristDto.getSsid());
				
				String onlineActionStr = JsonHelper.getJSONString(onlineAction);
				logger.info("do WangAn Processor device online msg " + onlineActionStr);
				
				processHandsetOnline(ctx, onlineActionStr);
				
			}
			else if(HandsetDeviceDTO.Action_Offline.equals(fristDto.getAction())){
				
				com.bhu.vas.api.dto.charging.HandsetOfflineAction offlineAction = ActionBuilder.builderHandsetOfflineAction
				    (fristDto.getMac(), parserHeader.getMac(), fristDto.getUptime(), fristDto.getVapname(), 
				     fristDto.getBssid(), fristDto.getRssi(), fristDto.getSnr(), 
					 fristDto.getAuthorized(), fristDto.getEthernet(), Long.parseLong(fristDto.getTx_bytes()),
					 Long.parseLong(fristDto.getRx_bytes()), System.currentTimeMillis());
				
				String offlineActionStr = JsonHelper.getJSONString(offlineAction);
                handsetDeviceOffline(ctx, offlineActionStr);
			}
			else if(HandsetDeviceDTO.Action_Authorize.equals(fristDto.getAction())){
				logger.info("do WangAn Processor "+ fristDto.getAction());
				
				HandsetAuthorizeAction authorizeAction = ActionBuilder.builderHandsetAuthorizeAction
					(fristDto.getMac(), parserHeader.getMac().toLowerCase(), fristDto.getVapname(), 
					 fristDto.getAuthorized(), System.currentTimeMillis());
				
				String authorizeActionStr = JsonHelper.getJSONString(authorizeAction);
				logger.info("do WangAn Processor device Authorize msg " + authorizeActionStr);
				
				logger.info(String.format("wanganDown " + HandsetDeviceDTO.Action_Authorize +" with topic ctx[%s] mac[%s] ", 
		                    CmCtxInfo.builderDownQueueName(ctx), ctx, fristDto.getMac()));
				
				processHandsetAuthorize(ctx, authorizeActionStr);
				
			} 
			else if(HandsetDeviceDTO.Action_Sync.equals(fristDto.getAction())){
			    
				List<PaHandsetOnlineAction> onlineActions = new ArrayList<>();
				for (HandsetDeviceDTO dto : dtos) {
					PaHandsetOnlineAction onlineAction = PaHandsetOnlineAction.builderHandsetOnlineAction
					    (dto.getMac(), parserHeader.getMac().toLowerCase(), dto.getDhcp_name(), dto.getIp(),
			    		 dto.getVapname(), dto.getBssid(), dto.getRssi(), dto.getSnr(),
			    		 dto.getAuthorized(), dto.getEthernet(), System.currentTimeMillis(), dto.getSsid());
					
					onlineActions.add(onlineAction);
				}
				handsetDeviceSync(ctx, parserHeader.getMac(), onlineActions);
			}
			else if(HandsetDeviceDTO.Action_Update.equals(fristDto.getAction())){
				com.bhu.vas.api.dto.charging.HandsetUpdateAction onlineAction = ActionBuilder.builderHandsetUpdateAction
				    (fristDto.getMac(), parserHeader.getMac(), fristDto.getIp(), fristDto.getDhcp_name(), fristDto.getTs());
				
				String updateActionStr = JsonHelper.getJSONString(onlineAction);
				handsetDeviceUpdate(ctx, updateActionStr);
			}
		} catch (Exception e) {
			System.out.println("doWangAnProcessor error .....");
		}
	}
	
	/**
	 * 更新终端的hostname和ip地址
	 * @param ctx
	 * @param message
	 */
	private void handsetDeviceUpdate(String ctx, String message) {
		PaHandsetOnlineAction dto = JsonHelper.getDTO(message, PaHandsetOnlineAction.class);
		if(dto == null) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(ctx))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		
		String memdtoStr = HandsetStoragePaService.getInstance().getAuthOnline(dto.getMac(), dto.getHmac());
		if(StringUtils.isEmpty(memdtoStr)){
			logger.info("cant find it from redis, drop");
			return;
		}
		PaHandsetOnlineAction memdto = JsonHelper.getDTO(memdtoStr, PaHandsetOnlineAction.class);
		//handset.setAction(HandsetDeviceDTO.Action_Online);
		memdto.setHname(dto.getHname());
		memdto.setHip(dto.getHip());
		memdto.setMac(dto.getMac());
		
		message = JsonHelper.getJSONString(dto);
		HandsetStoragePaService.getInstance().saveAuthOnline(memdto.getMac(), memdto.getHmac(), message);
		
		try {
			wanganMessageTopicProducer.send(CmCtxInfo.builderUpQueueName(ctx), dto.getHmac(), message);
		} catch (Exception e) {
			logger.error("processHandsetAuthorize error", e);
		} 
	}
	
    /**
     * 终端上线处理
     * @param message
     */
	public void processHandsetOnline(String ctx, String message){
		PaHandsetOnlineAction dto = JsonHelper.getDTO(message, PaHandsetOnlineAction.class);
		
		logger.info("do WangAn Processor" + dto.getMac());
		
		String memHandsetOnline = HandsetStoragePaService.getInstance()
		    .getAuthOnline(dto.getMac(), dto.getHmac());
		
		PaHandsetOnlineAction memdto = null;
		if(StringUtils.isEmpty(memHandsetOnline)){
			memdto = dto;
		} else {
			memdto = JsonHelper.getDTO(memHandsetOnline, PaHandsetOnlineAction.class);
			
			if(StringUtils.isEmpty(memdto.getHname()))
				memdto.setHname(dto.getHname());
			
			if(StringUtils.isEmpty(memdto.getHip()))
				memdto.setHip(dto.getHip());
			
			if(StringUtils.isEmpty(memdto.getRssi()))
				memdto.setRssi(dto.getRssi());
		}
        
		message = JsonHelper.getJSONString(memdto);
		HandsetStoragePaService.getInstance().saveAuthOnline(dto.getMac(), dto.getHmac(), message);
		
		try {
			wanganMessageTopicProducer.send(CmCtxInfo.builderUpQueueName(ctx), dto.getHmac(), message);
		} catch (Exception e) {
			logger.error("processHandsetAuthorize error", e);
		} 
		logger.info("do WangAn store CacheResult"+message);
	}
	
    /**
     * 终端验证处理
     * @param message
     */
	private void processHandsetAuthorize(String ctx, String message) {
		logger.info("do WangAn Processor  Authorize is true" + message);
		PaHandsetOnlineAction dto = JsonHelper.getDTO(message, PaHandsetOnlineAction.class);

		String memdtoStr = HandsetStoragePaService.getInstance().getAuthOnline(dto.getMac(), dto.getHmac());
		if(StringUtils.isEmpty(memdtoStr)){
			logger.info("cant find it from redis, drop");
			return;
		}

		logger.info("do WangAn Authorize handsetOnline" + memdtoStr);
		PaHandsetOnlineAction memdto = JsonHelper.getDTO(memdtoStr, PaHandsetOnlineAction.class);
		if(StringUtils.isEmpty(memdto.getHip()) || 
		   StringUtils.isEmpty(memdto.getHname()) || 
		   StringUtils.isEmpty(memdto.getRssi())) {
			
			if(StringUtils.isEmpty(memdto.getHip()))
				memdto.setHip(dto.getHip());
			
			if(StringUtils.isEmpty(memdto.getHname()))
				memdto.setHname(dto.getHname());
			
			if(StringUtils.isEmpty(memdto.getRssi()))
				memdto.setRssi(dto.getRssi());
		}
		
		String newAddFields = UserOrderDetailsHashService.getInstance().fetchUserOrderDetail
			(dto.getMac(), dto.getHmac());
		
		logger.info("do WangAn authoize newAddFields" + newAddFields);
		if(StringUtils.isNotEmpty(newAddFields)){
			OrderUserAgentDTO addMsg = JsonHelper.getDTO(newAddFields, OrderUserAgentDTO.class);
			//2016-07-22 fixed 数据库wan_id 和终端ip写反了
			memdto.setWan(addMsg.getIp());
			memdto.setInternet(addMsg.getWan_ip());
			//2016-07-22 fixed 数据库wan_id 和终端ip写反了
			int vipType = addMsg.getType();
			switch (vipType) {
			case 0:
				memdto.setViptype("WX");
				break;
			case 10:
				memdto.setViptype("DX");
				memdto.setVipacc(addMsg.getUmac_mobileno());
				break;
			default:
				break;
			}
		}
		
		String act = "";
		if(dto.getAuthorized() != null && dto.getAuthorized().equals("true")){
			act = ActionMode.HandsetOnline.getPrefix();
			memdto.setAct(act);
			memdto.setTs(System.currentTimeMillis());
			
			message =  JsonHelper.getJSONString(memdto);
			
			HandsetStoragePaService.getInstance().saveAuthOnline(dto.getMac(), dto.getHmac(), message);
			try {
				wanganMessageTopicProducer.send(CmCtxInfo.builderUpQueueName(ctx), dto.getHmac(), message);
			} catch (Exception e) {
				logger.error("processHandsetAuthorize error", e);
			} 
			logger.info("do WangAn store CacheResult " + message);
		} else { 
			HandsetOfflineAction offdto = new HandsetOfflineAction();
			act = ActionMode.HandsetOffline.getPrefix();
			logger.info("handle offline ");
			offdto.setAct(act);
			offdto.setAuthorized(memdto.getAuthorized());
			offdto.setBssid(memdto.getBssid());
			offdto.setHip(memdto.getHip());
			offdto.setHmac(memdto.getHmac());
			offdto.setHname(memdto.getHname());
			offdto.setInternet(memdto.getInternet());
			offdto.setMac(memdto.getMac());
			offdto.setRssi(memdto.getRssi());
			// 从redis中记录的ts获取上线时间
			offdto.setTs(memdto.getTs()); 
			offdto.setVapname(memdto.getVapname());
			offdto.setVipacc(memdto.getVipacc());
			offdto.setViptype(memdto.getViptype());
			offdto.setWan(memdto.getWan());
			offdto.setEnd_ts(System.currentTimeMillis());
			message =  JsonHelper.getJSONString(offdto);
		}
		String curTime =WriterThread.getCurrentTime();
		WriterThread.writeLog(curTime + " - " + act + message);
	}
    
	/**
	 *
	 * 访客网络移动设备下线
	 * 1:更新移动设备的online状态为false
	 *
	 *
	 * 移动设备下线
	 * 1:更新移动设备的online状态为false
	 * 2:wifi设备对应handset在线列表redis移除
	 * 3:统计增量 移动设备的daily访问时长增量 (backend)
	 * @param ctx
	 * @param dto
	 * modified by Edmond Lee for handset storage
	 */
	void handsetDeviceOffline(String ctx, String message){
		if (StringUtils.isEmpty(message)) {
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		}
        
		PaHandsetOnlineAction dto = JsonHelper.getDTO(message, PaHandsetOnlineAction.class);
		if (dto == null) {
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		}
		
		//1:更新移动设备的online状态为false
		String memHandsetOnline = HandsetStoragePaService.getInstance()
			    .getAuthOnline(dto.getMac(), dto.getHmac());
		if(StringUtils.isNotEmpty(memHandsetOnline)) {
			PaHandsetOnlineAction memdto = JsonHelper.getDTO(memHandsetOnline, PaHandsetOnlineAction.class);
			dto.setVapname(memdto.getVapname());
			if (memdto.getHname() != null) {
				dto.setHname(memdto.getHname());
			}
			dto.setHip(memdto.getHip() == null ? "0.0.0.0" : memdto.getHip());
		}
        
		message = JsonHelper.getJSONString(dto);
		HandsetStoragePaService.getInstance().saveAuthOnline(dto.getMac(), dto.getHmac(), message);
		
		try {
			wanganMessageTopicProducer.send(CmCtxInfo.builderUpQueueName(ctx), dto.getHmac(), message);
		} catch (Exception e) {
			logger.error("handsetDeviceOffline error", e);
		} 
	}
	
	
	/**
	 * 移动设备连接状态sync
	 * 1:清除wifi设备对应handset在线列表redis 并重新写入 (backend)
	 * 2:移动设备基础信息更新 (backend)
	 * 3:统计增量 移动设备的daily新增用户或活跃用户增量(backend)
	 * 4:统计增量 移动设备的daily启动次数增量(backend)
	 * 	a:如果移动设备目前不在线或者不存在移动设备数据，则执行设备上线相同操作
	 * 		1:移动设备连接wifi设备的接入记录(非流水) (backend)
	 * 		2:移动设备连接wifi设备的流水log (backend)
	 * 		3:wifi设备接入移动设备的接入数量 (backend)
	 * 5. 终端
	 * @param ctx
	 * @param dto
	 */
	public void handsetDeviceSync(String ctx, String mac, List<PaHandsetOnlineAction> onlineActions){
		if(StringUtils.isEmpty(mac) || StringUtils.isEmpty(ctx)) {
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		}
			List<String> hmacs = new ArrayList<String>();
			for (PaHandsetOnlineAction onlineAction : onlineActions) {
				hmacs.add(onlineAction.getHmac());
			}
		
		    List<PaHandsetOnlineAction> memOnlineActions = HandsetStoragePaService.getInstance().gets(mac, hmacs);
			for(PaHandsetOnlineAction onlineAction : onlineActions){
				if(onlineAction != null){
					for (PaHandsetOnlineAction memOnlineAction : memOnlineActions) {
						if (onlineAction.getHmac().equalsIgnoreCase(memOnlineAction.getHmac())) {
							onlineAction.setHname(memOnlineAction.getHname());
							onlineAction.setHip(memOnlineAction.getHip() == null || memOnlineAction.getHip().isEmpty() ? "0.0.0.0" : memOnlineAction.getHip());
							//onlineAction.set(handset.getData_tx_rate() == null || handset.getData_tx_rate().isEmpty() ? 0 + "" : handset.getData_tx_rate());
						}
					}
				}
			}
			
			List<PaHandsetOnlineAction> paHandsetOnlineActions = JsonHelper.getDTOList
				(JsonHelper.getJSONString(onlineActions), PaHandsetOnlineAction.class);
			
			HandsetStoragePaService.getInstance().handsetsComming(paHandsetOnlineActions);
			
			for (PaHandsetOnlineAction onlineAction : paHandsetOnlineActions) {
				try {
					wanganMessageTopicProducer.send(CmCtxInfo.builderUpQueueName(ctx), onlineAction.getHmac(), 
							                        JsonHelper.getJSONString(onlineAction));
				} catch (Exception e) {
					logger.error("handsetDeviceOffline error", e);
				}
			}
	}
	
	public void cmupWithWifiDeviceOnlines(String ctx, List<WifiDeviceDTO> dtos) {
		
	}
  
	@Override
	public void onMessage(final String topic,final int partition,final String key,final String message, 
			              final long offset,final String consumerId) {
		int type = Integer.parseInt(message.substring(0, 8));
		ParserHeader headers = ParserHeader.builder(message.substring(8, ParserHeader.Cmd_Header_Length), type);
	
		if (headers != null) {
			String payload = message.substring(ParserHeader.Cmd_Vap_Header_Length);
			doWangAnProcessor(topic, payload, headers);
		}
	}
	
}
