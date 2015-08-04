package com.bhu.vas.business.backendonline.asyncprocessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.business.asyn.spring.builder.ActionMessageFactoryBuilder;
import com.bhu.vas.business.asyn.spring.builder.ActionMessageType;
import com.bhu.vas.business.backendonline.asyncprocessor.service.AsyncMsgHandleService;
import com.bhu.vas.business.backendonline.asyncprocessor.service.iservice.IMsgHandlerService;
import com.bhu.vas.business.observer.QueueMsgObserverManager;
import com.bhu.vas.business.observer.listener.SpringQueueMessageListener;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class AsyncMsgBackendProcessor implements SpringQueueMessageListener{
	private final Logger logger = LoggerFactory.getLogger(AsyncMsgBackendProcessor.class);
	private ExecutorService exec = Executors.newFixedThreadPool(100);
	
	@Resource
	private AsyncMsgHandleService asyncMsgHandleService;
	
	@Resource
	private IMsgHandlerService wifiDeviceGroupServiceHandler;
	
	@PostConstruct
	public void initialize() {
		logger.info("AnsyncMsgBackendProcessor initialize...");
		QueueMsgObserverManager.SpringQueueMessageObserver.addSpringQueueMessageListener(this);
	}
	
	@Override
	public void onMessage(final String messagejsonHasPrefix){
		//logger.info(String.format("AnsyncMsgBackendProcessor receive message[%s]", messagejsonHasPrefix));
		exec.submit((new Runnable() {
			@Override
			public void run() {
				try{
					String message = ActionMessageFactoryBuilder.determineActionMessage(messagejsonHasPrefix);
					ActionMessageType type = ActionMessageFactoryBuilder.determineActionType(messagejsonHasPrefix);
					if(type == null){
						throwUnsupportedOperationException(type, messagejsonHasPrefix);
					}
					switch(type){
						case WifiDeviceOnline:
							asyncMsgHandleService.wifiDeviceOnlineHandle(message);
							break;
						case WifiDeviceOffline:
							asyncMsgHandleService.wifiDeviceOfflineHandle(message);
							break;
						case HandsetDeviceOnline:
							asyncMsgHandleService.handsetDeviceOnlineHandle(message);
							break;
						case HandsetDeviceOffline:
							asyncMsgHandleService.handsetDeviceOfflineHandle(message);
							break;
//						case HandsetDeviceSync:
//							asyncMsgHandleService.handsetDeviceSyncHandle(message);
//							break;
						case WifiDeviceLocation:
							asyncMsgHandleService.wifiDeviceLocationHandle(message);
							break;
						case CMUPWithWifiDeviceOnlines:
							asyncMsgHandleService.cmupWithWifiDeviceOnlinesHandle(message);
							break;
//						case WifiDeviceSettingModify:
//							asyncMsgHandleService.wifiDeviceSettingModify(message);
//							break;
						case WifiDeviceSettingChanged:
							asyncMsgHandleService.wifiDeviceSettingChanged(message);
							break;
//						case WifiDeviceTerminalNotify:
//							asyncMsgHandleService.WifiDeviceTerminalNotify(message);
//							break;
						case WifiDeviceRealtimeRateFetch:
							asyncMsgHandleService.wifiDeviceRealtimeRateFetch(message);
							break;		
						case WifiDeviceSpeedFetch:
							asyncMsgHandleService.wifiDevicePeakRateFetch(message);
							break;	
						case WifiDeviceHDRateFetch:
							asyncMsgHandleService.wifiDeviceHDRateFetch(message);
							break;
						case WifiCmdDownNotify:
							asyncMsgHandleService.wifiCmdDownNotifyHandle(message);
							break;
						case DeviceModifySettingAclMacs:
							asyncMsgHandleService.deviceModifySettingAclMacs(message);
							break;
						case USERFETCHCAPTCHACODE:
							asyncMsgHandleService.sendCaptchaCodeNotifyHandle(message);
							break;
						case USERSIGNEDON:
							asyncMsgHandleService.userSignedon(message);
							break;
						case USERREGISTERED:
							asyncMsgHandleService.userSignedon(message);
							break;
						case USERDEVICEREGISTER:
							asyncMsgHandleService.userDeviceRegister(message);
							break;
						case USERDEVICEDESTORY:
							asyncMsgHandleService.userDeviceDestory(message);
							break;							
						case WifiDeviceAsyncCMDGen:
							wifiDeviceGroupServiceHandler.process(message);
							break;
						case WifiDeviceGroupCreateIndex:
							wifiDeviceGroupServiceHandler.createDeviceGroupIndex(message);
						default:
							throwUnsupportedOperationException(type, messagejsonHasPrefix);
					}
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("AnsyncMsgProcessor", ex);
				}
			}
		}));
	}
	
	public void throwUnsupportedOperationException(ActionMessageType type, String messagejsonHasPrefix){
		throw new UnsupportedOperationException(
				String.format("ActionMessageType[%s] not yet implement handler processfull message[%s]",
						type,messagejsonHasPrefix));
	}
}
