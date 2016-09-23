package com.bhu.vas.pa.service.device;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.pure.kafka.client.producer.StringKafkaMessageProducer;
import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.charging.ActionBuilder.ActionMode;
import com.bhu.vas.api.dto.header.ParserHeader;
import com.bhu.vas.api.helper.RPCMessageParseHelper;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.user.facade.UserIdentityAuthFacadeService;
import com.bhu.vas.pa.dto.PaHandsetOnlineAction;
import com.smartwork.msip.cores.helper.JsonHelper;

/**
 * device RPC组件的业务service
 * @author tangzichao
 *
 */
@Service
public class WanganBusinessServiceProcessor{
	private final Logger logger = LoggerFactory.getLogger(WanganBusinessServiceProcessor.class);

	private final String KAFKA_QUEUE_NAME = "sjrz";
	
	@Resource
	private StringKafkaMessageProducer paTopicProducer;

	@Resource
	WifiDeviceService wifiDeviceService;

	@Resource
	UserIdentityAuthFacadeService userIdentityAuthFacadeService;
	
	/**
	 * 移动设备连接状态请求生成，网安终端上线消息
	 * 1:online
	 * 2:offline
	 * 3:auth
	 * @param ctx
	 * @param payload
	 */
	public void doPaProcessor(String payload, ParserHeader parserHeader) {
		try {
			logger.info("process begin:" + payload);
			List<HandsetDeviceDTO> dtos = RPCMessageParseHelper.generateDTOListFromMessage
			    (payload, HandsetDeviceDTO.class);
			
			if(dtos == null || dtos.isEmpty()) return;

			String mac = parserHeader.getMac().toLowerCase();
			HandsetDeviceDTO firstDto = dtos.get(0);
			logger.debug("action:" + firstDto.getAction());
			switch(firstDto.getAction()){
				case HandsetDeviceDTO.Action_Online:
					processHandsetOnline(mac, firstDto);
					break;
				case HandsetDeviceDTO.Action_Offline:
					processHandsetOffline(mac, firstDto);
					break;
				case HandsetDeviceDTO.Action_Authorize:
					processHandsetAuthorize(mac, firstDto);
					break;
				case HandsetDeviceDTO.Action_Update:
					processHandsetUpdate(mac, firstDto);
					break;
				case HandsetDeviceDTO.Action_Sync:
					processHandsetSync(mac, dtos);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			logger.error("handle error:" + e.getMessage());
		}
		logger.info("handle done");
	}
	
	/**
	 * 更新终端的hostname和ip地址
	 * @param ctx
	 * @param message
	 */
	private void processHandsetUpdate(String mac, HandsetDeviceDTO dto) {
		String memdtoStr = HandsetStoragePaService.getInstance().getHandset(mac, dto.getMac());
		if(StringUtils.isEmpty(memdtoStr)){
			logger.info("cant find it from redis, drop");
			return;
		}
		PaHandsetOnlineAction memdto = JsonHelper.getDTO(memdtoStr, PaHandsetOnlineAction.class);
		if(StringUtils.isNotEmpty(dto.getDhcp_name()))
			memdto.setHname(dto.getDhcp_name());
		if(StringUtils.isNotEmpty(dto.getIp()) )
			memdto.setHip(dto.getIp());
		
		String message = JsonHelper.getJSONString(memdto);
		HandsetStoragePaService.getInstance().saveHandset(mac, dto.getMac(), message);
		logger.info("save handset info: " + message);
	}
	
    /**
     * 终端上线处理
     * @param message
     */
	public void processHandsetOnline(String mac, HandsetDeviceDTO dto){
		logger.debug(String.format("processHandsetOnline for [%s][%s], vap[%s], ethernet[%s]", mac, dto.getMac(), dto.getVapname(), dto.getEthernet()));

		if(!WifiDeviceSetting.VAPNAME_WLAN3.equals(dto.getVapname()) &&
				!WifiDeviceSetting.VAPNAME_WLAN13.equals(dto.getVapname())){
			logger.debug(String.format("handset[%s] is not in a expected vap", dto.getMac()));
			return;
		}
		if(String.valueOf(true).equals(dto.getEthernet())){
			logger.debug(String.format("handset[%s] come from ethernet", dto.getMac()));
			return;
		}

		String memHandsetOnline = HandsetStoragePaService.getInstance()
		    .getHandset(mac, dto.getMac());
		
		PaHandsetOnlineAction memdto = null;
		if(StringUtils.isEmpty(memHandsetOnline)){
			memdto = PaHandsetOnlineAction.builderHandsetOnlineAction(mac, dto);
		} else {
			memdto = JsonHelper.getDTO(memHandsetOnline, PaHandsetOnlineAction.class);
			
			if(StringUtils.isNotEmpty(dto.getDhcp_name()))
				memdto.setHname(dto.getDhcp_name());
			if(StringUtils.isNotEmpty(dto.getIp()) )
				memdto.setHip(dto.getIp());
			if(StringUtils.isNotEmpty(dto.getRssi()))
				memdto.setRssi(dto.getRssi());
			if(StringUtils.isNotEmpty(dto.getSsid()))
				memdto.setSsid(dto.getSsid());
			if(StringUtils.isNotEmpty(dto.getBssid()))
				memdto.setBssid(dto.getBssid());
		}
        
		String message = JsonHelper.getJSONString(memdto);
		HandsetStoragePaService.getInstance().saveHandset(mac, dto.getMac(), message);
		logger.info("save handset info: " + message);
	}
	
	
	private void sendAuthorizeMessage(PaHandsetOnlineAction memdto, boolean online){
		if(StringUtils.isEmpty(memdto.getWan())){
			WifiDevice entity = wifiDeviceService.getById(memdto.getMac());
			memdto.setWan(entity.getIp());
			memdto.setInternet(entity.getWan_ip());
		}
		if(StringUtils.isEmpty(memdto.getVipacc())){
			userIdentityAuthFacadeService.fetchUserMobilenoByHdmac(memdto.getHmac());
		}		

		if(online){
			memdto.setAct(ActionMode.HandsetOnline.getPrefix());
			memdto.setTs(System.currentTimeMillis());
			memdto.setEnd_ts(null);
		} else { 
			memdto.setAct(ActionMode.HandsetOffline.getPrefix());
			memdto.setEnd_ts(System.currentTimeMillis());
		}
		String message = JsonHelper.getJSONString(memdto);
		HandsetStoragePaService.getInstance().saveHandset(memdto.getMac(), memdto.getHmac(), message);
		logger.info("save handset info: " + message);
		sendKafkaMessage(message, memdto.getHmac());
	}
	
	
	
    /**
     * 终端验证处理
     * @param message
     */
	private void processHandsetAuthorize(String mac, HandsetDeviceDTO dto) {
		logger.info("got authorize message: ");
		String memHandsetOnline = HandsetStoragePaService.getInstance()
			    .getHandset(mac, dto.getMac());

		PaHandsetOnlineAction memdto = JsonHelper.getDTO(memHandsetOnline, PaHandsetOnlineAction.class);
		sendAuthorizeMessage(memdto, dto.getAuthorized().equals("true"));
	}
    
	
	private void sendKafkaMessage(String message, String hmac){
		if(StringUtils.isNotEmpty(message)){
			try{
				paTopicProducer.send(KAFKA_QUEUE_NAME, 0, null, message);
				logger.info("kafka sent:" + message);
			}catch(Exception e){
				e.printStackTrace(System.out);
				logger.error("failed to send msg to kafka:" + e.getMessage());
			}
		}
	}
	/**
	 *
	 * 移动设备下线
	 */
	void processHandsetOffline(String mac, HandsetDeviceDTO dto){
		logger.debug(String.format("processHandsetOnline for [%s][%s], vap[%s], ethernet[%s]", mac, dto.getMac(), dto.getVapname(), dto.getEthernet()));

		if(!WifiDeviceSetting.VAPNAME_WLAN3.equals(dto.getVapname()) &&
				!WifiDeviceSetting.VAPNAME_WLAN13.equals(dto.getVapname())){
			logger.debug(String.format("handset[%s] is not in a expected vap", dto.getMac()));
			return;
		}
		if(String.valueOf(true).equals(dto.getEthernet())){
			logger.debug(String.format("handset[%s] come from ethernet", dto.getMac()));
			return;
		}
	}
	
	
	/**
	 * 移动设备连接状态sync
	 */
	public void processHandsetSync(String mac, List<HandsetDeviceDTO> dtos){
		for(HandsetDeviceDTO dto:dtos){
			//太复杂的逻辑不考虑，只做最常见的情况处理
			if(!WifiDeviceSetting.VAPNAME_WLAN3.equals(dto.getVapname()) &&
					!WifiDeviceSetting.VAPNAME_WLAN13.equals(dto.getVapname()))
				continue;
			if(String.valueOf(true).equals(dto.getEthernet()))
				continue;
			if(dto.getAuthorized() == null || !dto.getAuthorized().equals("true"))
				continue;

			String memHandsetOnline = HandsetStoragePaService.getInstance()
				    .getHandset(mac, dto.getMac());
			if(StringUtils.isEmpty(memHandsetOnline))
				continue;
			PaHandsetOnlineAction memdto = JsonHelper.getDTO(memHandsetOnline, PaHandsetOnlineAction.class);
			if(memdto.getEnd_ts() == null){ //记录的状态是认证离线，那么可以产生一次认证上线
				sendAuthorizeMessage(memdto, true);
			}
		}
	}
}
