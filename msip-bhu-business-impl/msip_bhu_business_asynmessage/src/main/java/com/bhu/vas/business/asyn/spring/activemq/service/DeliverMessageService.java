package com.bhu.vas.business.asyn.spring.activemq.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.asyn.spring.activemq.queue.producer.DeliverMessageQueueProducer;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceOfflineDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceOnlineDTO;

@Service
public class DeliverMessageService {
	@Resource(name="deliverMessageQueueProducer")
	private DeliverMessageQueueProducer deliverMessageQueueProducer;

	public void sendPureText(String message){
		deliverMessageQueueProducer.sendPureText(message);
	}
	
	public void sendWifiDeviceOnlineActionMessage(String wifiId, long login_ts){
		WifiDeviceOnlineDTO dto = new WifiDeviceOnlineDTO();
		dto.setMac(wifiId);
		dto.setLogin_ts(login_ts);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendWifiDeviceOfflineActionMessage(String wifiId){
		WifiDeviceOfflineDTO dto = new WifiDeviceOfflineDTO();
		dto.setMac(wifiId);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendHandsetDeviceOnlineActionMessage(String wifiId, String handsetId, long login_ts){
		HandsetDeviceOnlineDTO dto = new HandsetDeviceOnlineDTO();
		dto.setMac(handsetId);
		dto.setWifiId(wifiId);
		dto.setLogin_ts(login_ts);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
}
