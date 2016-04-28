package com.bhu.vas.business.asyn.spring.activemq.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.bhu.vas.api.dto.DownCmds;
import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceTerminalDTO;
import com.bhu.vas.business.asyn.spring.activemq.queue.producer.DeliverMessageQueueProducer;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.model.CMUPWithWifiDeviceOnlinesDTO;
import com.bhu.vas.business.asyn.spring.model.DeviceModifySettingAclMacsDTO;
import com.bhu.vas.business.asyn.spring.model.DeviceModifySettingAliasDTO;
import com.bhu.vas.business.asyn.spring.model.DeviceModifySettingVapDTO;
import com.bhu.vas.business.asyn.spring.model.DeviceSearchResultExportFileDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceOfflineDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceSyncDTO;
import com.bhu.vas.business.asyn.spring.model.HandsetDeviceVisitorAuthorizeOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.OrderSearchResultExportFileDTO;
import com.bhu.vas.business.asyn.spring.model.UserBBSsignedonDTO;
import com.bhu.vas.business.asyn.spring.model.UserCaptchaCodeFetchDTO;
import com.bhu.vas.business.asyn.spring.model.UserDeviceDestoryDTO;
import com.bhu.vas.business.asyn.spring.model.UserDeviceForceBindDTO;
import com.bhu.vas.business.asyn.spring.model.UserDeviceRegisterDTO;
import com.bhu.vas.business.asyn.spring.model.UserDeviceSharedNetworkApplyDTO;
import com.bhu.vas.business.asyn.spring.model.UserPortalUpdateDTO;
import com.bhu.vas.business.asyn.spring.model.UserRegisteredDTO;
import com.bhu.vas.business.asyn.spring.model.UserResetPwdDTO;
import com.bhu.vas.business.asyn.spring.model.UserSignedonDTO;
import com.bhu.vas.business.asyn.spring.model.WifiCmdsNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceAsynCmdGenerateDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceGroupAsynCreateIndexDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceLocationDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceModuleOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceOfflineDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceOnlineDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceSettingChangedDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceSettingModifyDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceSettingQueryDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceSpeedFetchDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceTerminalNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDeviceUsedStatusDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDevicesGrayChangedDTO;
import com.bhu.vas.business.asyn.spring.model.WifiDevicesModuleStyleChangedDTO;
import com.bhu.vas.business.asyn.spring.model.WifiHDRateFetchDTO;
import com.bhu.vas.business.asyn.spring.model.WifiMultiCmdsNotifyDTO;
import com.bhu.vas.business.asyn.spring.model.WifiRealtimeRateFetchDTO;
import com.bhu.vas.business.asyn.spring.model.agent.AgentDeviceClaimImportDTO;
import com.bhu.vas.business.asyn.spring.model.agent.AgentDeviceClaimUpdateDTO;


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
	
	public void sendWifiMultiCmdsCommingNotifyMessage(int uid,List<DownCmds> downCmds){
		WifiMultiCmdsNotifyDTO dto = new WifiMultiCmdsNotifyDTO();
		dto.setUid(uid);
		dto.setDownCmds(downCmds);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendWifiDeviceModuleOnlineMessage(String wifiId,String orig_vap_module){
		WifiDeviceModuleOnlineDTO dto = new WifiDeviceModuleOnlineDTO();
		dto.setMac(wifiId);
		dto.setOrig_vap_module(orig_vap_module);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendWifiDeviceOnlineActionMessage(String wifiId, String join_reason, long login_ts, 
			long last_login_at, boolean newWifi,boolean wanIpChanged,boolean needLocationQuery,
			String o_wmode,String n_wmode/*boolean workModeChanged*/
			){
		WifiDeviceOnlineDTO dto = new WifiDeviceOnlineDTO();
		dto.setMac(wifiId);
		dto.setJoin_reason(join_reason);
		dto.setNewWifi(newWifi);
		dto.setLogin_ts(login_ts);
		dto.setLast_login_at(last_login_at);
		dto.setNeedLocationQuery(needLocationQuery);
		dto.setWanIpChanged(wanIpChanged);
		//dto.setWorkModeChanged(workModeChanged);
		dto.setO_wmode(o_wmode);
		dto.setN_wmode(n_wmode);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendWifiDeviceUsedStatusActionMessage(String ctx,String mac, String response, long taskid){
		WifiDeviceUsedStatusDTO dto = new WifiDeviceUsedStatusDTO();
		dto.setCtx(ctx);
		dto.setMac(mac);
		dto.setResponse(response);
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
			long last_login_at, boolean newHandset,boolean isNew4This){
		HandsetDeviceOnlineDTO dto = new HandsetDeviceOnlineDTO();
		dto.setMac(handsetId);
		dto.setWifiId(wifiId);
		dto.setNewHandset(newHandset);
		dto.setNh4t(isNew4This);
		dto.setLogin_ts(login_ts);
		dto.setLast_login_at(last_login_at);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}

	public void sendHandsetDeviceVisitorAuthorizeOnlineMessage(String wifiId, String handsetId, long login_ts) {
		HandsetDeviceVisitorAuthorizeOnlineDTO dto = new HandsetDeviceVisitorAuthorizeOnlineDTO();
		dto.setMac(handsetId);
		dto.setWifiId(wifiId);
		dto.setLogin_ts(login_ts);
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
	}
	
	public void sendUserSingleDeviceSharedNetworkApplyActionMessage(int uid,String snk_type,String template, String mac,boolean onlyindexupdate,char dtoType){
		List<String> dmacs = new ArrayList<String>();
		dmacs.add(mac);
		this.sendUserDeviceSharedNetworkApplyActionMessage(uid, snk_type,template, dmacs, onlyindexupdate, dtoType);
		/*UserDeviceSharedNetworkApplyDTO dto = new UserDeviceSharedNetworkApplyDTO();
		dto.setUid(uid);
		dto.setSnk_type(snk_type);
		dto.setMacs(dmacs);
		dto.setOnlyindexupdate(onlyindexupdate);
		dto.setDtoType(dtoType);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));*/
	}
	public void sendUserDeviceSharedNetworkApplyActionMessage(int uid,String snk_type,String template, List<String> dmacs,boolean onlyindexupdate,char dtoType){
		UserDeviceSharedNetworkApplyDTO dto = new UserDeviceSharedNetworkApplyDTO();
		dto.setUid(uid);
		dto.setSnk_type(snk_type);
		dto.setTemplate(template);
		dto.setMacs(dmacs);
		dto.setOnlyindexupdate(onlyindexupdate);
		dto.setDtoType(dtoType);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendDeviceHDRateFetchActionMessage(String wifiId){
		WifiHDRateFetchDTO dto = new WifiHDRateFetchDTO();
		dto.setMac(wifiId);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendQueryDeviceSpeedFetchActionMessage(String wifiId, int type, int period, int duration){
		WifiDeviceSpeedFetchDTO dto = new WifiDeviceSpeedFetchDTO();
		dto.setMac(wifiId);
		dto.setType(type);
		dto.setPeriod(period);
		dto.setDuration(duration);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
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
	
	public void sendUserDeviceForceBindActionMessage(int uid, int old_uid, String mac, String orig_swver){
		UserDeviceForceBindDTO dto = new UserDeviceForceBindDTO();
		dto.setUid(uid);
		dto.setOld_uid(old_uid);
		dto.setMac(mac);
		dto.setOrig_swver(orig_swver);
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
	
	public void sendDeviceSettingQueryActionMessage(String mac,boolean uRouter, int refresh_status, List<String> payloads){
		WifiDeviceSettingQueryDTO dto = new WifiDeviceSettingQueryDTO();
		dto.setMac(mac);
		dto.setDeviceURouter(uRouter);
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

	public void sendDeviceModifySettingAaliasActionMessage(Integer uid, String mac, String content){
		DeviceModifySettingAliasDTO dto = new DeviceModifySettingAliasDTO();
		dto.setUid(uid);
		dto.setMac(mac);
		dto.setContent(content);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	
	public void sendDeviceModifySettingVapActionMessage(Integer uid, String mac){
		DeviceModifySettingVapDTO dto = new DeviceModifySettingVapDTO();
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


	public void sendAgentDeviceClaimImportMessage(Integer uid, Long logId, String inputPath, String outputPath, String originName) {
		AgentDeviceClaimImportDTO dto = new AgentDeviceClaimImportDTO();
		dto.setUid(uid);
		dto.setLogId(logId);
		dto.setInputPath(inputPath);
		dto.setOutputPath(outputPath);
		dto.setOriginName(originName);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));

	}

	public void sendAgentDeviceClaimUpdateMessage(Integer uid, Long logId) {
		AgentDeviceClaimUpdateDTO dto = new AgentDeviceClaimUpdateDTO();
		dto.setUid(uid);
		dto.setLogId(logId);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));

	}
	
	public void sendDevicesGrayChangedNotifyMessage(Integer uid,String dut,int gl,List<String> macs){
		WifiDevicesGrayChangedDTO dto = new WifiDevicesGrayChangedDTO();
		dto.setDut(dut);
		dto.setGl(gl);
		dto.setUid(uid);
		dto.setMacs(macs);
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	public void sendDevicesModuleStyleChangedNotifyMessage(Integer uid,String style,String... macs){
		WifiDevicesModuleStyleChangedDTO dto = new WifiDevicesModuleStyleChangedDTO();
		dto.setUid(uid);
		dto.setStyle(style);
		dto.setMacs(Arrays.asList(macs));
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendWifiDeviceSearchResultExportFileMessage(int uid, String message, String exportFilePath){
		DeviceSearchResultExportFileDTO dto = new DeviceSearchResultExportFileDTO();
		dto.setUid(uid);
		dto.setMessage(message);
		dto.setExportFilePath(exportFilePath);
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	
	public void sendOrderSearchResultExportFileMessage(int uid, String message, int messagetype, String exportFilePath, String start_date, String end_date){
		OrderSearchResultExportFileDTO dto = new OrderSearchResultExportFileDTO();
		dto.setUid(uid);
		dto.setMessage(message);
		dto.setMessagetype(messagetype);
		dto.setExportFilePath(exportFilePath);
		dto.setStart_date(start_date);
		dto.setEnd_date(end_date);
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//DeliverMessage message = DeliverMessageFactoryBuilder.buildDeliverMessage(type, uid, ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
		//deliverMessageQueueProducer.send(message);
	}
	
	public void sendPortalUpdateUserChangedActionMessage(int uid,String nick,String mobileno,String avatar){
		UserPortalUpdateDTO dto = new UserPortalUpdateDTO();
		dto.setUid(uid);
		dto.setType(UserPortalUpdateDTO.PortalUpdate_User);
		dto.setNick(nick);
		dto.setMobileno(mobileno);
		dto.setAvatar(avatar);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
	
	public void sendPortalUpdateRateChangedActionMessage(int uid,String snk,String tpl,int users_rate){
		UserPortalUpdateDTO dto = new UserPortalUpdateDTO();
		dto.setUid(uid);
		dto.setType(UserPortalUpdateDTO.PortalUpdate_SNK);
		dto.setSnk(snk);
		dto.setTpl(tpl);
		dto.setUsers_rate(users_rate);
		dto.setTs(System.currentTimeMillis());
		deliverMessageQueueProducer.sendPureText(ActionMessageFactoryBuilder.toJsonHasPrefix(dto));
	}
}
