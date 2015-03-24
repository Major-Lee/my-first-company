package com.bhu.vas.business.asyn.spring.activemq.service;

import java.util.List;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.business.asyn.spring.activemq.queue.producer.DeliverMessageQueueProducer;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.CMUPWithWifiDeviceOnlinesDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceOfflineDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceSyncDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceLocationDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceOfflineDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceOnlineDTO;


public class DeliverMessageService {
	@Resource(name="deliverMessageQueueProducer")
	private DeliverMessageQueueProducer deliverMessageQueueProducer;

	public void sendPureText(String message){
		deliverMessageQueueProducer.sendPureText(message);
	}
	
	public void sendWifiDeviceOnlineActionMessage(String wifiId, long login_ts, 
			long last_login_at, boolean newWifi){
		WifiDeviceOnlineDTO dto = new WifiDeviceOnlineDTO();
		dto.setMac(wifiId);
		dto.setNewWifi(newWifi);
		dto.setLogin_ts(login_ts);
		dto.setLast_login_at(last_login_at);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendCMUPWithWifiDeviceOnlinesActionMessage(String ctx, List<WifiDeviceDTO> devices){
		CMUPWithWifiDeviceOnlinesDTO dto = new CMUPWithWifiDeviceOnlinesDTO();
		dto.setCtx(ctx);
		dto.setDevices(devices);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendWifiDeviceOfflineActionMessage(String wifiId, long last_login_at){
		WifiDeviceOfflineDTO dto = new WifiDeviceOfflineDTO();
		dto.setMac(wifiId);
		dto.setLast_login_at(last_login_at);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendHandsetDeviceOnlineActionMessage(String wifiId, String handsetId, long login_ts, 
			long last_login_at, boolean newHandset){
		HandsetDeviceOnlineDTO dto = new HandsetDeviceOnlineDTO();
		dto.setMac(handsetId);
		dto.setWifiId(wifiId);
		dto.setNewHandset(newHandset);
		dto.setLogin_ts(login_ts);
		dto.setLast_login_at(last_login_at);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendHandsetDeviceOfflineActionMessage(String wifiId, String handsetId, String uptime){
		HandsetDeviceOfflineDTO dto = new HandsetDeviceOfflineDTO();
		dto.setMac(handsetId);
		dto.setWifiId(wifiId);
		dto.setUptime(uptime);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendHandsetDeviceSyncActionMessage(String wifiId, List<HandsetDeviceDTO> dtos){
		HandsetDeviceSyncDTO dto = new HandsetDeviceSyncDTO();
		dto.setMac(wifiId);
		dto.setDtos(dtos);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendQueryDeviceLocationActionMessage(String wifiId, String lat, String lon){
		WifiDeviceLocationDTO dto = new WifiDeviceLocationDTO();
		dto.setMac(wifiId);
		dto.setLat(lat);
		dto.setLon(lon);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
}
