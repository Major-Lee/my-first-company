package com.bhu.vas.push.business;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.push.HandsetDeviceOnlinePushDTO;
import com.bhu.vas.api.dto.push.HandsetDeviceVisitorAuthorizeOnlinePushDTO;
import com.bhu.vas.api.dto.push.PushDTO;
import com.bhu.vas.api.dto.redis.DeviceMobilePresentDTO;
import com.bhu.vas.api.rpc.user.dto.UserTerminalOnlineSettingDTO;
import com.bhu.vas.api.rpc.user.model.UserDevice;
import com.bhu.vas.api.rpc.user.model.UserSettingState;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetAliasService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserDeviceService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;
import com.bhu.vas.push.common.context.HandsetOnlineContext;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.DevicesSet;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.MacDictParserFilterHelper;

@Service
public class BusinessPushContextService {
	private static final Logger logger = LoggerFactory.getLogger(BusinessPushContextService.class);
	
	@Resource
	private UserSettingStateService userSettingStateService;
	
	@Resource
	private UserDeviceService userDeviceService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	/**
	 * 终端上线上下文组成
	 * @param pushDto
	 * @param presentDto
	 * @return
	 */
	public HandsetOnlineContext handsetOnlineContext(PushDTO pushDto, DeviceMobilePresentDTO presentDto){
		HandsetOnlineContext context = new HandsetOnlineContext();
		
		HandsetDeviceOnlinePushDTO hd_push_dto = (HandsetDeviceOnlinePushDTO)pushDto;
		//判断是否是自己
		if(hd_push_dto.getHd_mac().equals(presentDto.getDm())){
			return context;
		}
		
		UserSettingState userSettingState = userSettingStateService.getById(pushDto.getMac());
		if(userSettingState != null){
			UserTerminalOnlineSettingDTO dto = userSettingState.getUserSetting(UserTerminalOnlineSettingDTO
					.Setting_Key, UserTerminalOnlineSettingDTO.class);
			if(dto != null){
				//判断终端上线通知开关
				if(dto.isOn()){
					//验证终端上线时间段
					if(validateHandsetValidTime(dto)){
						//验证陌生终端开关
						boolean strangerOn = validateHandsetStrangerOn(dto, hd_push_dto.isNewed(), context);
						if(strangerOn){
							//验证终端昵称探测开关
							boolean aliasOn = validateHandsetAliasOn(dto, hd_push_dto.getMac(), hd_push_dto.getHd_mac(), context);
							if(aliasOn){
								//构建终端上下文
								builderHandsetOnlineContext(presentDto.getUid(), hd_push_dto.getMac(), hd_push_dto.getHd_mac(),
										presentDto.isMulti(), context);
							}
						}
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
	public HandsetOnlineContext handsetOnlineGuestContext(PushDTO pushDto, DeviceMobilePresentDTO presentDto){
		HandsetOnlineContext context = new HandsetOnlineContext();
		
		HandsetDeviceVisitorAuthorizeOnlinePushDTO hd_push_dto = (HandsetDeviceVisitorAuthorizeOnlinePushDTO) pushDto;
		//判断是否是自己
/*		if(hd_push_dto.getHd_mac().equals(presentDto.getDm())){
			return context;
		}*/
		
		UserSettingState userSettingState = userSettingStateService.getById(pushDto.getMac());
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
	protected boolean validateHandsetStrangerOn(UserTerminalOnlineSettingDTO dto, boolean newedHandset, 
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
	}
	
	/**
	 * 获取终端别名
	 * @param mac
	 * @return
	 */
	protected String getHandsetAliasName(String mac, String hd_mac){
		List<UserDevice> bindDevices = userDeviceService.fetchBindDevicesUsers(mac);
		if (!bindDevices.isEmpty()) {
			return WifiDeviceHandsetAliasService.getInstance().hgetHandsetAlias(bindDevices.get(0).
					getUid(), hd_mac);
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
		if(!RuntimeConfiguration.isSystemTestUsers(uid)){
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
				
				//3) 如果别名和主机名都不存在 则终端显示名为mac地址
				if(StringUtils.isEmpty(context.getHandsetName())){
					context.setHandsetName(hd_mac);
				}
			}
			
			//2:组装厂家名称
			String manufactor = MacDictParserFilterHelper.prefixMactch(hd_mac,true,false);
			if(!DevicesSet.Unknow.getScn().equals(manufactor)){
				context.setManufactor(manufactor);
			}
			
			//3:组装设备信息
			if(multi_devices){
				String deviceName = deviceFacadeService.getUserDeviceName(uid, mac);
				if(!StringUtils.isEmpty(deviceName)){
					context.setDeviceInfo(String.format(HandsetOnlineContext.Device_Info_Template, deviceName));
				}
			}
		}else{
			context.setHandsetName(hd_mac);
		}
	}
	
}
