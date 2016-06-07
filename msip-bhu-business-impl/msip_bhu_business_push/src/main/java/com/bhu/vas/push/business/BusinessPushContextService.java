package com.bhu.vas.push.business;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.push.DeviceResetPushDTO;
import com.bhu.vas.api.dto.push.HandsetDeviceOnlinePushDTO;
import com.bhu.vas.api.dto.push.HandsetDeviceVisitorAuthorizeOnlinePushDTO;
import com.bhu.vas.api.dto.push.SharedealNotifyPushDTO;
import com.bhu.vas.api.dto.redis.DeviceMobilePresentDTO;
import com.bhu.vas.api.helper.BusinessEnumType.OrderPaymentType;
import com.bhu.vas.api.helper.BusinessEnumType.OrderUmacType;
import com.bhu.vas.api.rpc.user.dto.UserTerminalOnlineSettingDTO;
import com.bhu.vas.api.rpc.user.model.UserSettingState;
import com.bhu.vas.api.rpc.user.model.UserWifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetAliasService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;
import com.bhu.vas.push.common.context.DeviceResetContext;
import com.bhu.vas.push.common.context.HandsetOnlineContext;
import com.bhu.vas.push.common.context.SharedealNofityContext;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.DevicesSet;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.MacDictParserFilterHelper;

@Service
public class BusinessPushContextService {
	private static final Logger logger = LoggerFactory.getLogger(BusinessPushContextService.class);
	
	@Resource
	private UserSettingStateService userSettingStateService;
	
/*	@Resource
	private UserDeviceService userDeviceService;*/
	
	@Resource
	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	/**
	 * 终端上线上下文组成
	 * @param pushDto
	 * @param presentDto
	 * @return
	 */
	public HandsetOnlineContext handsetOnlineContext(HandsetDeviceOnlinePushDTO hd_push_dto, DeviceMobilePresentDTO presentDto){
		HandsetOnlineContext context = new HandsetOnlineContext();
		
		//判断是否是自己
		if(hd_push_dto.getHd_mac().equals(presentDto.getDm())){
			return context;
		}
		
		UserSettingState userSettingState = userSettingStateService.getById(hd_push_dto.getMac());
		if(userSettingState != null){
			UserTerminalOnlineSettingDTO dto = userSettingState.getUserSetting(UserTerminalOnlineSettingDTO
					.Setting_Key, UserTerminalOnlineSettingDTO.class);
			if(dto != null){
				//判断终端上线通知开关
				if(dto.isOn()){
					//验证终端上线时间段
					if(validateHandsetValidTime(dto)){
						//验证陌生终端开关
						//boolean strangerOn = validateHandsetStrangerOn(dto, hd_push_dto.isNewed(), context);
						//if(strangerOn){
							//验证终端昵称探测开关
							boolean aliasOn = validateHandsetAliasOn(dto, hd_push_dto.getMac(), hd_push_dto.getHd_mac(), context);
							if(aliasOn){
								//构建终端上下文
								builderHandsetOnlineContext(presentDto.getUid(), hd_push_dto.getMac(), hd_push_dto.getHd_mac(),
										presentDto.isMulti(), context);
							}else{
								logger.info(String.format("HandsetOnlineContext Alias stop mac[%s] hd_mac[%s]", hd_push_dto.getMac(), hd_push_dto.getHd_mac()));
							}
						//}else{
						//	logger.info(String.format("HandsetOnlineContext Stranger stop mac[%s] hd_mac[%s]", hd_push_dto.getMac(), hd_push_dto.getHd_mac()));
						//}
					}else{
						logger.info(String.format("HandsetOnlineContext Vaildtime stop mac[%s] hd_mac[%s]", hd_push_dto.getMac(), hd_push_dto.getHd_mac()));
					}
				}
			}
		}
		return context;
	}
	
	/**
	 * 访客上线上下文组成
	 * @param pushDto
	 * @param presentDto
	 * @return
	 */
	public HandsetOnlineContext handsetOnlineGuestContext(HandsetDeviceVisitorAuthorizeOnlinePushDTO hd_push_dto, DeviceMobilePresentDTO presentDto){
		HandsetOnlineContext context = new HandsetOnlineContext();
		
		//判断是否是自己
		if(hd_push_dto.getHd_mac().equals(presentDto.getDm())){
			return context;
		}
		
		UserSettingState userSettingState = userSettingStateService.getById(hd_push_dto.getMac());
		if(userSettingState != null){
			UserTerminalOnlineSettingDTO dto = userSettingState.getUserSetting(UserTerminalOnlineSettingDTO
					.Setting_Key, UserTerminalOnlineSettingDTO.class);
			if(dto != null){
				//判断终端上线通知开关
				if(dto.isOn()){
					//验证终端上线时间段
					if(validateHandsetValidTime(dto)){
						//获取终端别名
						String alias = getHandsetAliasName(hd_push_dto.getMac(), hd_push_dto.getHd_mac());
						if(!StringUtils.isEmpty(alias)){
							context.setHandsetName(alias);
						}
						//构建访客上下文
						builderHandsetOnlineContext(presentDto.getUid(), hd_push_dto.getMac(), hd_push_dto.getHd_mac(),
								presentDto.isMulti(), context);
					}else{
						logger.info(String.format("HandsetOnlineGuestContext Vaildtime stop mac[%s] hd_mac[%s]", hd_push_dto.getMac(), hd_push_dto.getHd_mac()));
					}
				}
			}
		}
		return context;
	}
	/**
	 * 验证终端上线时间段
	 * @param dto
	 * @return
	 */
	protected boolean validateHandsetValidTime(UserTerminalOnlineSettingDTO dto){
		//根据时间段模式 判断是否在有效的时间段内
		boolean valid_time = false;
		try{
			if(!StringUtils.isEmpty(dto.getTimeslot())){
				//正常模式
				if(UserTerminalOnlineSettingDTO.Timeslot_Mode_Normal == dto.getTimeslot_mode()){
					valid_time = DateTimeHelper.isInTime(dto.getTimeslot());
				}else if(UserTerminalOnlineSettingDTO.Timeslot_Mode_Silent == dto.getTimeslot_mode()){
					valid_time = !DateTimeHelper.isInTime(dto.getTimeslot());
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return valid_time;
	}
	/**
	 * 验证陌生终端开关
	 * @param dto
	 * @param newedHandset
	 * @param context
	 * @return
	 */
/*	protected boolean validateHandsetStrangerOn(UserTerminalOnlineSettingDTO dto, boolean newedHandset, 
			HandsetOnlineContext context){
		//第一次接入的终端算是陌生终端
		if(newedHandset) {
			context.setStrange(HandsetOnlineContext.Android_Stranger);
			return true;
		}
		
		if(dto.isStranger_on()) {
			return false;
		}
		else{
			return true;
		}
	}*/
	
	/**
	 * 获取终端别名
	 * @param mac
	 * @return
	 */
	protected String getHandsetAliasName(String mac, String hd_mac){
/*		List<UserDevice> bindDevices = userDeviceService.fetchBindDevicesUsers(mac);
		if (!bindDevices.isEmpty()) {
			return WifiDeviceHandsetAliasService.getInstance().hgetHandsetAlias(bindDevices.get(0).
					getUid(), hd_mac);
		}*/
		Integer uid = userWifiDeviceFacadeService.findUidById(mac);
		if(uid != null){
			return WifiDeviceHandsetAliasService.getInstance().hgetHandsetAlias(uid, hd_mac);
		}
		return null;
	}
	/**
	 * 验证终端昵称探测开关
	 * @param dto
	 * @param mac 设备mac
	 * @param hd_mac 终端mac
	 * @param context
	 * @return
	 */
	protected boolean validateHandsetAliasOn(UserTerminalOnlineSettingDTO dto, String mac, String hd_mac, 
			HandsetOnlineContext context){
		
/*		List<UserDevice> bindDevices = userDeviceService.fetchBindDevicesUsers(mac);
		if (!bindDevices.isEmpty()) {
			String alias = WifiDeviceHandsetAliasService.getInstance().hgetHandsetAlias(bindDevices.get(0).
					getUid(), hd_mac);
			context.setHandsetName(alias);
		}*/
		String alias = getHandsetAliasName(mac, hd_mac);
		if(!StringUtils.isEmpty(alias)){
			context.setHandsetName(alias);
		}
		//验证终端昵称探测开关
		if (dto.isAlias_on()) { //开启陌生人终端和昵称
			if(!StringUtils.isEmpty(context.getHandsetName())) {
				return true;
			}
		}else{
			return true;
		}
		return false;
	}
	/**
	 * 组装终端上线context
	 * @param uid
	 * @param mac 设备mac
	 * @param hd_mac 终端mac
	 * @param multi_devices 用户是否管理多台设备
	 * @param context
	 */
	protected void builderHandsetOnlineContext(int uid, String mac, String hd_mac, boolean multi_devices, 
			HandsetOnlineContext context){
		context.setVaild(true);
		if(!BusinessRuntimeConfiguration.isSystemTestUsers(uid)){
			//组装text
			//1:组装终端显示名
			//1) 如果终端存在别名 终端显示名为别名
			if(StringUtils.isEmpty(context.getHandsetName())){
				//2)如果不存在别名,终端显示名为主机名
				HandsetDeviceDTO handset = HandsetStorageFacadeService.handset(hd_mac);
				if(handset != null){
					String hostname = handset.getDhcp_name();
					if(!StringUtils.isEmpty(hostname)){
						//如果终端主机名显示为"android-", 则终端显示名为 安卓终端
						if(hostname.toLowerCase().startsWith(HandsetOnlineContext.Android_Host_Name_Match)){
							context.setHandsetName(HandsetOnlineContext.Android_Terminal);
						}else{
							//按照macbook的截取字符串方式缩略终端主机名
							context.setHandsetName(StringHelper.chopMiddleString(hostname, 16, 
									StringHelper.ELLIPSIS_STRING_GAP));
						}
					}
				}
				
				//3) 如果别名和主机名都不存在 则终端显示名为未知终端
/*				if(StringUtils.isEmpty(context.getHandsetName())){
					//context.setHandsetName(hd_mac);
					context.setHandsetName(HandsetOnlineContext.Android_Terminal_Unkown);
				}*/
			}
			//如果别名和主机名都不存在 则显示厂商名称
			if(StringUtils.isEmpty(context.getHandsetName())){
				//2:组装厂家名称
				String manufactor = MacDictParserFilterHelper.prefixMactch(hd_mac,true,false);
				if(!DevicesSet.Unknow.getScn().equals(manufactor)){
					context.setHandsetName(manufactor);
				}else{
					//如果厂商名称也不存在 则终端显示名为未知终端
					context.setHandsetName(HandsetOnlineContext.Android_Terminal_Unkown);
				}
			}

/*			//2:组装厂家名称
			String manufactor = MacDictParserFilterHelper.prefixMactch(hd_mac,true,false);
			if(!DevicesSet.Unknow.getScn().equals(manufactor)){
				context.setManufactor(manufactor);
			}*/
			
			
			//3:组装设备信息
			if(multi_devices){
/*				String deviceName = deviceFacadeService.getUserDeviceName(uid, mac);
				if(!StringUtils.isEmpty(deviceName)){
					context.setDeviceInfo(String.format(HandsetOnlineContext.Device_Info_Template, deviceName));
				}*/
				UserWifiDevice userWifiDevice = userWifiDeviceFacadeService.findUserWifiDeviceById(mac, uid);
				if(userWifiDevice != null){
					if(StringUtils.isNotEmpty(userWifiDevice.getDevice_name())){
						context.setDeviceInfo(String.format(HandsetOnlineContext.Device_Info_Template, userWifiDevice.getDevice_name()));
					}
				}
			}
		}else{
			context.setHandsetName(hd_mac);
		}
	}
	
	/**
	 * 打赏分成上下文组成
	 * @param pushDto
	 * @param presentDto
	 * @return
	 */
	public SharedealNofityContext sharedealNotifyContext(SharedealNotifyPushDTO sharedeal_push_dto){
		SharedealNofityContext context = new SharedealNofityContext();
		
		OrderPaymentType payment_type = OrderPaymentType.fromKey(sharedeal_push_dto.getPayment_type());
		if(payment_type == null){
			payment_type = OrderPaymentType.Weixin;
		}
		context.setPayment_type_name(payment_type.getDesc());
		context.setCash(sharedeal_push_dto.getCash());
		
		String umac_mf = MacDictParserFilterHelper.prefixMactch(sharedeal_push_dto.getHd_mac(),true,false);
		if(StringUtils.isNotEmpty(umac_mf) && !umac_mf.equals(DevicesSet.Unknow.getScn())){
			context.setUmac_mf(umac_mf);
		}
		
		OrderUmacType umac_type = OrderUmacType.fromKey(sharedeal_push_dto.getUmac_type());
		if(umac_type == null){
			umac_type = OrderUmacType.Terminal;
		}
		context.setUmac_type_desc(umac_type.getDesc());
		return context;
	}
	
	
	/**
	 * Reset解绑上下文组成
	 * @param pushDto
	 * @param presentDto
	 * @return
	 */
	public DeviceResetContext deviceResetContext(Integer uid, DeviceResetPushDTO deviceResetPushDto){
		DeviceResetContext context = new DeviceResetContext();
		
		//String deviceName = deviceFacadeService.getUserDeviceName(uid, deviceResetPushDto.getMac());
		String deviceName = null;
		UserWifiDevice userWifiDevice = userWifiDeviceFacadeService.findUserWifiDeviceById(deviceResetPushDto.getMac(), uid);
		if(userWifiDevice != null){
			deviceName = userWifiDevice.getDevice_name();
		}
		if(StringUtils.isNotEmpty(deviceName)){
			context.setDeviceInfo(deviceName);
		}else{
			context.setDeviceInfo(deviceResetPushDto.getMac());
		}
		return context;
	}
	
}
