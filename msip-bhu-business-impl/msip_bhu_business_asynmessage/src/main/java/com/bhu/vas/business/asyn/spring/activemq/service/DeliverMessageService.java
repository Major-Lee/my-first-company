package com.bhu.vas.business.asyn.spring.activemq.service;

import java.util.List;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTerminalDTO;
import com.bhu.vas.business.asyn.spring.activemq.queue.producer.DeliverMessageQueueProducer;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.CMUPWithWifiDeviceOnlinesDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceOfflineDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceSyncDTO;
import com.bhu.vas.business.asyn.spring.model.UserCaptchaCodeFetchDTO;
import com.bhu.vas.business.asyn.spring.model.UserDeviceRegisterDTO;
import com.bhu.vas.business.asyn.spring.model.UserRegisteredDTO;
import com.bhu.vas.business.asyn.spring.model.UserResetPwdDTO;
import com.bhu.vas.business.asyn.spring.model.UserSignedonDTO;
import com.bhu.vas.business.asyn.spring.model.WifiCmdNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceLocationDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceOfflineDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceSettingModifyDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceTerminalNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.WifiRealtimeRateFetchDTO;


public class DeliverMessageService {
	@Resource(name="deliverMessageQueueProducer")
	private DeliverMessageQueueProducer deliverMessageQueueProducer;

	public void sendPureText(String message){
		deliverMessageQueueProducer.sendPureText(message);
	}

	public void sendWifiCmdCommingNotifyMessage(String mac,int taskid,String opt,String payload){
		WifiCmdNotifyDTO dto = new WifiCmdNotifyDTO();
		dto.setMac(mac);
		dto.setTaskid(taskid);
		dto.setOpt(opt);
		dto.setPayload(payload);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
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
	
//	public void sendQueryDeviceSettingActionMessage(String wifiId, List<String> vapnames){
//		WifiDeviceSettingNotifyDTO dto = new WifiDeviceSettingNotifyDTO();
//		dto.setMac(wifiId);
//		dto.setVapnames(vapnames);
//		dto.setTs(System.currentTimeMillis());
//		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
//	}
	
	public void sendQueryDeviceTerminalsActionMessage(String wifiId, String ssid, String bssid, 
			List<WifiDeviceTerminalDTO> terminals){
		WifiDeviceTerminalNotifyDTO dto = new WifiDeviceTerminalNotifyDTO();
		dto.setMac(wifiId);
		dto.setSsid(ssid);
		dto.setBssid(bssid);
		dto.setTerminals(terminals);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendDeviceRealtimeRateFetchActionMessage(String wifiId){
		WifiRealtimeRateFetchDTO dto = new WifiRealtimeRateFetchDTO();
		dto.setMac(wifiId);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	
	public void sendUserSignedonActionMessage(int uid,String remoteip,String d){
		UserSignedonDTO dto = new UserSignedonDTO();
		dto.setUid(uid);
		dto.setRemoteip(remoteip);
		dto.setD(d);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	
	public void sendUserDeviceRegisterActionMessage(int uid, String mac){
		UserDeviceRegisterDTO dto = new UserDeviceRegisterDTO();
		dto.setUid(uid);
		dto.setMac(mac);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	
	public void sendDeviceSettingModifyActionMessage(Integer uid, String mac, String payload){
		WifiDeviceSettingModifyDTO dto = new WifiDeviceSettingModifyDTO();
		dto.setMac(mac);
		dto.setPayload(payload);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	

	public void sendUserRegisteredActionMessage(Integer uid,String channel,String device,String remoteip){
		UserRegisteredDTO dto = new UserRegisteredDTO();
		dto.setUid(uid);
		dto.setChannel(channel);
		//dto.setInviteuid(inviteuid);
		//dto.setInvitetoken(invitetoken);
		dto.setRemoteip(remoteip);
		dto.setD(device);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	
	public void sendUserResetPwdActionMessage(int uid,String token,String acc,String ip){
		UserResetPwdDTO dto = new UserResetPwdDTO();
		dto.setUid(uid);
		dto.setToken(token);
		dto.setAcc(acc);
		dto.setIp(ip);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	
	public void sendUserCaptchaCodeFetchActionMessage(int countrycode,String acc,String captcha){
		UserCaptchaCodeFetchDTO dto = new UserCaptchaCodeFetchDTO();
		dto.setCountrycode(countrycode);
		dto.setAcc(acc);
		dto.setCaptcha(captcha);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, 0, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	
}
