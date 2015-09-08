package com.bhu.vas.business.asyn.spring.activemq.service;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTerminalDTO;
import com.bhu.vas.business.asyn.spring.activemq.queue.producer.DeliverMessageQueueProducer;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.*;
import com.bhu.vas.business.asyn.spring.model.agent.AgentDeviceClaimImportDTO;


public class DeliverMessageService {
	@Resource(name="deliverMessageQueueProducer")
	private DeliverMessageQueueProducer deliverMessageQueueProducer;

	public void sendPureText(String message){
		deliverMessageQueueProducer.sendPureText(message);
	}

	public void sendWifiCmdGenMessage(int uid,int gid,boolean dependency,String mac,String opt,String subopt, String extparams,String channel,String channel_taskid){
		WifiDeviceAsynCmdGenerateDTO dto = new WifiDeviceAsynCmdGenerateDTO();
		dto.setUid(uid);
		dto.setGid(gid);
		dto.setDependency(dependency);
		dto.setMac(mac);
		dto.setOpt(opt);
		dto.setSubopt(subopt);
		dto.setExtparams(extparams);
		dto.setChannel(channel);
		dto.setChannel_taskid(channel_taskid);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	/*public void sendWifiCmdCommingNotifyMessage(String mac,long taskid,String opt,String payload){
		WifiCmdNotifyDTO dto = new WifiCmdNotifyDTO();
		dto.setMac(mac);
		dto.setTaskid(taskid);
		dto.setOpt(opt);
		dto.setPayload(payload);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}*/
	
	public void sendWifiCmdsCommingNotifyMessage(String mac,String... payloads){
		sendWifiCmdsCommingNotifyMessage(mac,Arrays.asList(payloads));
	}
	public void sendWifiCmdsCommingNotifyMessage(String mac,/*long taskid,String opt,*/List<String> payloads){
		WifiCmdsNotifyDTO dto = new WifiCmdsNotifyDTO();
		dto.setMac(mac);
		dto.setPayloads(payloads);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	/*public void sendWifiCmdsCommingNotifyMessage(String mac,int taskid,String opt,List<String> payloads){
		WifiCmdNotifyDTO dto = new WifiCmdNotifyDTO();
		dto.setMac(mac);
		dto.setTaskid(taskid);
		dto.setOpt(opt);
		dto.setPayload(payload);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}*/
	
	public void sendWifiDeviceModuleOnlineMessage(String wifiId){
		WifiDeviceModuleOnlineDTO dto = new WifiDeviceModuleOnlineDTO();
		dto.setMac(wifiId);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendWifiDeviceOnlineActionMessage(String wifiId, String join_reason, long login_ts, 
			long last_login_at, boolean newWifi,boolean needLocationQuery){
		WifiDeviceOnlineDTO dto = new WifiDeviceOnlineDTO();
		dto.setMac(wifiId);
		dto.setJoin_reason(join_reason);
		dto.setNewWifi(newWifi);
		dto.setLogin_ts(login_ts);
		dto.setLast_login_at(last_login_at);
		dto.setNeedLocationQuery(needLocationQuery);
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
	
	public void sendWifiDeviceOfflineActionMessage(String wifiId){
		WifiDeviceOfflineDTO dto = new WifiDeviceOfflineDTO();
		dto.setMac(wifiId);
//		dto.setLast_login_at(last_login_at);
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
	
	public void sendHandsetDeviceOfflineActionMessage(String wifiId, String handsetId, String uptime,
													  String rx_bytes, String tx_bytes){
		HandsetDeviceOfflineDTO dto = new HandsetDeviceOfflineDTO();
		dto.setMac(handsetId);
		dto.setWifiId(wifiId);
		dto.setUptime(uptime);
		dto.setRx_bytes(rx_bytes);
		dto.setTx_bytes(tx_bytes);
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
	
	public void sendQueryDeviceTerminalsActionMessage(String wifiId, List<WifiDeviceTerminalDTO> terminals){
		WifiDeviceTerminalNotifyDTO dto = new WifiDeviceTerminalNotifyDTO();
		dto.setMac(wifiId);
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
	
	public void sendDeviceHDRateFetchActionMessage(String wifiId){
		WifiHDRateFetchDTO dto = new WifiHDRateFetchDTO();
		dto.setMac(wifiId);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	
	public void sendQueryDeviceSpeedFetchActionMessage(String wifiId, int type, int period, int duration){
		WifiDeviceSpeedFetchDTO dto = new WifiDeviceSpeedFetchDTO();
		dto.setMac(wifiId);
		dto.setType(type);
		dto.setPeriod(period);
		dto.setDuration(duration);
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
	
	public void sendUserDeviceDestoryActionMessage(int uid, String mac){
		UserDeviceDestoryDTO dto = new UserDeviceDestoryDTO();
		dto.setUid(uid);
		dto.setMac(mac);
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
	
	/**
	 * 主动触发的设备配置修改 任务id是区间id
	 * @param mac
	 * @param payload
	 */
	public void sendActiveDeviceSettingModifyActionMessage(String mac, String payload){
		WifiDeviceSettingModifyDTO dto = new WifiDeviceSettingModifyDTO();
		dto.setMac(mac);
		dto.setPayload(payload);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	
	public void sendDeviceSettingQueryActionMessage(String mac, int refresh_status, List<String> payloads){
		WifiDeviceSettingQueryDTO dto = new WifiDeviceSettingQueryDTO();
		dto.setMac(mac);
		dto.setRefresh_status(refresh_status);
		dto.setPayloads(payloads);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	
	public void sendDeviceSettingChangedActionMessage(String mac, List<String> payloads){
		WifiDeviceSettingChangedDTO dto = new WifiDeviceSettingChangedDTO();
		dto.setMac(mac);
		dto.setPayloads(payloads);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	

	public void sendUserRegisteredActionMessage(Integer uid,String mobileno,String channel,String device,String remoteip){
		UserRegisteredDTO dto = new UserRegisteredDTO();
		dto.setUid(uid);
		dto.setMobileno(mobileno);
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
	
	public void sendDeviceModifySettingAclMacsActionMessage(Integer uid, String mac){
		DeviceModifySettingAclMacsDTO dto = new DeviceModifySettingAclMacsDTO();
		dto.setUid(uid);
		dto.setMac(mac);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}

	public void sendDeviceGroupCreateIndexMessage(String wifiIds, long gid, String type) {
		WifiDeviceGroupAsynCreateIndexDTO dto = new WifiDeviceGroupAsynCreateIndexDTO();
		dto.setWifiIds(wifiIds);
		dto.setGid(gid);
		dto.setType(type);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendUserBBSsignedonMessage(Integer uid, String dt, String d, int countrycode, String acc, String secretkey) {
		UserBBSsignedonDTO dto = new UserBBSsignedonDTO();
		dto.setUid(uid);
		dto.setDt(dt);
		dto.setD(d);
		dto.setCountrycode(countrycode);
		dto.setAcc(acc);
		dto.setSecretkey(secretkey);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}


	public void sendAgentDeviceClaimImportMessage(Integer uid, String path, String originName) {
		AgentDeviceClaimImportDTO dto = new AgentDeviceClaimImportDTO();
		dto.setUid(uid);
		dto.setPath(path);
		dto.setOriginName(originName);
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));

	}
}
