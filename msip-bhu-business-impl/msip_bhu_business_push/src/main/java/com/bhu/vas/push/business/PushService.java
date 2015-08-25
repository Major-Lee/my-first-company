package com.bhu.vas.push.business;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.push.HandsetDeviceOnlinePushDTO;
import com.bhu.vas.api.dto.push.HandsetDeviceWSOnlinePushDTO;
import com.bhu.vas.api.dto.push.PushDTO;
import com.bhu.vas.api.dto.push.UserBBSsignedonPushDTO;
import com.bhu.vas.api.dto.push.WifiDeviceRebootPushDTO;
import com.bhu.vas.api.dto.push.WifiDeviceSettingChangedPushDTO;
import com.bhu.vas.api.dto.redis.DeviceMobilePresentDTO;
import com.bhu.vas.api.rpc.user.dto.UserTerminalOnlineSettingDTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.api.rpc.user.model.PushMessageConstant;
import com.bhu.vas.api.rpc.user.model.PushType;
import com.bhu.vas.api.rpc.user.model.UserSettingState;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceMobilePresentStringService;
import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserSettingStateService;
import com.bhu.vas.push.common.dto.PushMsg;
import com.bhu.vas.push.common.service.gexin.GexinPushService;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.DevicesSet;
import com.smartwork.msip.cores.plugins.dictparser.impl.mac.MacDictParserFilterHelper;

/**
 * 业务push service
 * @author tangzichao
 *
 */
@Service
public class PushService{
	private static final Logger logger = LoggerFactory.getLogger(PushService.class);
	
	@Resource
	private UserSettingStateService userSettingStateService;
	
	@Resource
	private DeviceFacadeService deviceFacadeService;
	
	//@Resource
	//private HandsetDeviceService handsetDeviceService;
	
	/**
	 * 业务逻辑发送push消息统一接口
	 * @param pushDto
	 */
	public boolean push(PushDTO pushDto){
		boolean push_ret = false;
		if(pushDto != null){
			PushType pushType = PushType.getPushTypeFromType(pushDto.getPushType());
			if(pushType != null){
				switch(pushType){
					case HandsetDeviceOnline:
						push_ret = this.pushHandsetDeviceOnline(pushDto);
						break;
					case WifiDeviceReboot:
						push_ret = this.pushWifiDeviceReboot(pushDto);
						break;
					case WifiDeviceSettingChanged:
						push_ret = this.pushWifiDeviceSettingChanged(pushDto);
						break;
					default:
						break;
				}
			}
		}
		return push_ret;
	}
	
	/**
	 * 终端上线push
	 * @param uid
	 * @param mac 设备mac 
	 * @param hd_mac 终端mac
	 */
	public boolean pushHandsetDeviceOnline(PushDTO pushDto){
		boolean ret = false;
		try{
			DeviceMobilePresentDTO presentDto = this.getMobilePresent(pushDto.getMac());
			if(presentDto != null){
				HandsetDeviceOnlinePushDTO hd_push_dto = (HandsetDeviceOnlinePushDTO)pushDto;
				//判断是否是自己
				if(hd_push_dto.getHd_mac().equals(presentDto.getDm())){
					return false;
				}
				
				UserSettingState userSettingState = userSettingStateService.getById(pushDto.getMac());
				if(userSettingState != null){
					UserTerminalOnlineSettingDTO dto = userSettingState.getUserSetting(UserTerminalOnlineSettingDTO
							.Setting_Key, UserTerminalOnlineSettingDTO.class);
					if(dto != null){
						//判断终端上线通知开关
						if(dto.isOn()){
							if(!StringUtils.isEmpty(dto.getTimeslot())){
								//根据时间段模式 判断是否在有效的时间段内
								boolean valid_time = false;
								//正常模式
								if(UserTerminalOnlineSettingDTO.Timeslot_Mode_Normal == dto.getTimeslot_mode()){
									valid_time = DateTimeHelper.isInTime(dto.getTimeslot());
								}else if(UserTerminalOnlineSettingDTO.Timeslot_Mode_Silent == dto.getTimeslot_mode()){
									valid_time = !DateTimeHelper.isInTime(dto.getTimeslot());
								}
								
								if(valid_time){
									boolean need_push = false;
									//判断是否开启陌生终端设置
									if(dto.isStranger_on()){
										//第一次接入的终端算是陌生终端
										if(hd_push_dto.isNewed()) 
											need_push = true;
									}else{
										need_push = true;
									}
									
									if(need_push){
										PushMsg pushMsg = this.generatePushMsg(presentDto);
										if(pushMsg != null){
											//构建终端上线通知push内容
											this.builderHandsetDeviceOnlinePushMsg(pushMsg, presentDto, hd_push_dto);
											//发送push
											ret = pushNotification(pushMsg);
											if(ret){
												logger.info("PushHandsetDeviceOnline Successed " + pushMsg.toString());
											}else{
												logger.info("PushHandsetDeviceOnline Failed " + pushMsg.toString());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("PushHandsetDeviceOnline exception " + ex.getMessage(), ex);
		}
		return ret;
	}
	
	/**
	 * 终端探测出现push
	 * @param pushDto
	 * @param presentDto
	 * @return
	 */
	public boolean pushHandsetDeviceWSOnline(PushDTO pushDto, DeviceMobilePresentDTO presentDto){
		boolean ret = false;
		try{
			HandsetDeviceWSOnlinePushDTO wspush_dto = (HandsetDeviceWSOnlinePushDTO)pushDto;
			if(presentDto != null){
				PushMsg pushMsg = this.generatePushMsg(presentDto);
				if(pushMsg != null){
					String push_deviceName = StringHelper.EMPTY_STRING_GAP;
					if(!RuntimeConfiguration.isSystemTestUsers(presentDto.getUid())){
						pushMsg.setTitle(String.format(PushType.HandsetDeviceWSOnline.getTitle(), push_deviceName));
						String name = wspush_dto.getHd_mac();
						if(!StringUtils.isEmpty(wspush_dto.getN())){
							name = wspush_dto.getN();
						}else{
							//如果没有昵称 匹配mac短名称
							String scn = MacDictParserFilterHelper.prefixMactch(wspush_dto.getHd_mac(),true,false);
							if(!DevicesSet.Unknow.getScn().equals(scn)){
								name = scn;
							}
						}
						pushMsg.setText(String.format(PushType.HandsetDeviceWSOnline.getText(), name));
						pushMsg.setPaylod(JsonHelper.getJSONString(wspush_dto));
					}else{
						if(presentDto.isMulti()){
							String deviceName = deviceFacadeService.getUserDeviceName(presentDto.getUid(), wspush_dto.getMac());
							if(!StringUtils.isEmpty(deviceName)){
								push_deviceName = String.format(PushMessageConstant.Android_DeviceName, deviceName);
							}
						}
						pushMsg.setTitle(String.format(PushType.HandsetDeviceWSOnline.getTitle(), push_deviceName));
						pushMsg.setText(String.format(PushType.HandsetDeviceWSOnline.getText(), wspush_dto.getHd_mac()));
						pushMsg.setPaylod(JsonHelper.getJSONString(wspush_dto));
					}
					//发送push
					ret = pushNotification(pushMsg);
					if(ret){
						logger.info("PushHandsetDeviceWSOnline Successed " + pushMsg.toString());
					}else{
						logger.info("PushHandsetDeviceWSOnline Failed " + pushMsg.toString());
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("PushHandsetDeviceWSOnline exception " + ex.getMessage(), ex);
		}
		return ret;
	}
	
	/**
	 * 发送用户bbs登录消息
	 * @param pushDto
	 * @param presentDto
	 * @return
	 */
	public boolean pushUserBBSsignedon(PushDTO pushDto, DeviceMobilePresentDTO presentDto){
		boolean ret = false;
		try{
			UserBBSsignedonPushDTO bbs_push_dto = (UserBBSsignedonPushDTO)pushDto;
			if(presentDto != null){
				PushMsg pushMsg = this.generatePushMsg(presentDto);
				if(pushMsg != null){
					pushMsg.setPaylod(JsonHelper.getJSONString(bbs_push_dto));
					//发送push
					ret = pushAndroidTransmissionAndIosNotification(pushMsg);
					if(ret){
						logger.info("PushUserBBSsignedon Successed " + pushMsg.toString());
					}else{
						logger.info("PushUserBBSsignedon Failed " + pushMsg.toString());
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("PushUserBBSsignedon exception " + ex.getMessage(), ex);
		}
		return ret;
	}
	
	/**
	 * 用户通过指令重启设备成功push
	 * @param pushDto
	 */
	public boolean pushWifiDeviceReboot(PushDTO pushDto){
		boolean ret = false;
		try{
			WifiDeviceRebootPushDTO reboot_dto = (WifiDeviceRebootPushDTO)pushDto;
			if(WifiDeviceDTO.UserCmdRebootReason.equals(reboot_dto.getJoin_reason())){
				DeviceMobilePresentDTO presentDto = this.getMobilePresent(pushDto.getMac());
				if(presentDto != null){
					PushMsg pushMsg = this.generatePushMsg(presentDto);
					if(pushMsg != null){
						pushMsg.setPaylod(JsonHelper.getJSONString(reboot_dto));
						//发送push
						ret = pushTransmission(pushMsg);
						if(ret){
							logger.info("PushWifiDeviceReboot Successed " + pushMsg.toString());
						}else{
							logger.info("PushWifiDeviceReboot Failed " + pushMsg.toString());
						}
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("pushWifiDeviceReboot exception " + ex.getMessage(), ex);
		}
		return ret;
	}
	
	/**
	 * 服务器接收到设备变更消息后的push
	 * @param pushDto
	 */
	public boolean pushWifiDeviceSettingChanged(PushDTO pushDto){
		boolean ret = false;
		try{
			WifiDeviceSettingChangedPushDTO wds_changed_dto = (WifiDeviceSettingChangedPushDTO)pushDto;

			DeviceMobilePresentDTO presentDto = this.getMobilePresent(pushDto.getMac());
			if(presentDto != null){
				PushMsg pushMsg = this.generatePushMsg(presentDto);
				if(pushMsg != null){
					pushMsg.setPaylod(JsonHelper.getJSONString(wds_changed_dto));
					//发送push
					ret = pushTransmission(pushMsg);
					if(ret){
						logger.info("pushWifiDeviceSettingChanged Successed " + pushMsg.toString());
					}else{
						logger.info("pushWifiDeviceSettingChanged Failed " + pushMsg.toString());
					}
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("pushWifiDeviceSettingChanged exception " + ex.getMessage(), ex);
		}
		return ret;
	}
	
	/**
	 * 构建终端上线push的透传内容
	 * @param hd_push_dto
	 * @return
	 */
	public void builderHandsetDeviceOnlinePushMsg(PushMsg pushMsg, DeviceMobilePresentDTO presentDto, 
			HandsetDeviceOnlinePushDTO hd_push_dto){
		if(!RuntimeConfiguration.isSystemTestUsers(presentDto.getUid())){
			//构造payload
			String aliasName = deviceFacadeService.queryPushHandsetDeviceAliasName(hd_push_dto.getHd_mac(), hd_push_dto.getMac());
			//如果终端有别名 则不显示终端厂商短名称
			if(!StringUtils.isEmpty(aliasName)){
				hd_push_dto.setN(aliasName);
				pushMsg.setText(String.format(PushType.HandsetDeviceOnline.getText(), StringHelper.EMPTY_STRING_GAP, 
						aliasName));
			}
			//如果不存在终端别名 显示厂商短名称
			else{
				boolean exist_hostname = false;
				boolean exist_scn = false;
				String hostname = deviceFacadeService.queryPushHandsetDeviceHostname(hd_push_dto.getHd_mac(), hd_push_dto.getMac());
				hd_push_dto.setN(hostname);
				if(!StringUtils.isEmpty(hostname)){
					exist_hostname = true;
				}
				//根据设备mac匹配终端厂商
				String scn = MacDictParserFilterHelper.prefixMactch(hd_push_dto.getHd_mac(),true,false);
				logger.info(String.format("builderHandsetDeviceOnlinePushMsg scn [%s] hostname [%s]", scn, hostname));
				if(!DevicesSet.Unknow.getScn().equals(scn)){
					exist_scn = true;
					//scn = "终端";
				}
				//如果主机名和短名称都存在 显示短名称+主机名
				if(exist_hostname && exist_scn){
					//如果短名称包含 手机 字样
					if(scn.contains(PushMessageConstant.Android_Mobile_String)){
						//主机名是安卓标记的显示为安卓os
						if(PushMessageConstant.Android_Host_Name.equals(hostname)){
							hostname = PushMessageConstant.Android_OS;
						}
					}
					//如果短名称不包含 手机 字样 短名称+主机名
					else{
						//主机名是安卓标记的显示为安卓终端
						if(PushMessageConstant.Android_Host_Name.equals(hostname)){
							hostname = PushMessageConstant.Android_Terminal;
						}
					}
				}
				//如果只有主机名 只显示主机名
				else if(exist_hostname){
					if(PushMessageConstant.Android_Host_Name.equals(hostname)){
						hostname = PushMessageConstant.Android_Terminal;
					}
					scn = StringHelper.EMPTY_STRING_GAP;
				}
				//如果只有短名称 显示短名称+未知终端
				else if(exist_scn){
					hostname = PushMessageConstant.Android_Unkown_Hostname;
				}
				//如果主机名和短名称都没有 显示 未知终端
				else{
					hostname = PushMessageConstant.Android_Unkown_Hostname;
					scn = StringHelper.EMPTY_STRING_GAP;
				}
				pushMsg.setText(String.format(PushType.HandsetDeviceOnline.getText(), scn, hostname));
			}
			
			String payload = JsonHelper.getJSONString(hd_push_dto);
			pushMsg.setPaylod(payload);
			//构造title和text
			//如果用户管理多个设备 标题中添加设备名称
			String push_deviceName = StringHelper.EMPTY_STRING_GAP;
			if(presentDto.isMulti()){
				String deviceName = deviceFacadeService.getUserDeviceName(presentDto.getUid(), hd_push_dto.getMac());
				logger.info(String.format("multi device push deviceName [%s]", deviceName));
				if(!StringUtils.isEmpty(deviceName)){
					push_deviceName = String.format(PushMessageConstant.Android_DeviceName, deviceName);
				}
			}
			pushMsg.setTitle(String.format(PushType.HandsetDeviceOnline.getTitle(), hd_push_dto.isNewed() ? "陌生" : "", push_deviceName));
		}else{
			pushMsg.setTitle(String.format(PushType.HandsetDeviceOnline.getTitle(), StringHelper.EMPTY_STRING_GAP, StringHelper.EMPTY_STRING_GAP));
			pushMsg.setText(String.format(PushType.HandsetDeviceOnline.getText(), StringHelper.EMPTY_STRING_GAP, hd_push_dto.getHd_mac()));
			pushMsg.setPaylod(JsonHelper.getJSONString(hd_push_dto));
		}
	}
	
	/**
	 * 获取用户的mobile push信息数据
	 * @param mac
	 * @return
	 */
	protected DeviceMobilePresentDTO getMobilePresent(String mac){
		String present = WifiDeviceMobilePresentStringService.getInstance().getMobilePresent(mac);
		if(!StringUtils.isEmpty(present)){
			return JsonHelper.getDTO(present, DeviceMobilePresentDTO.class);
		}
		return null;
		//return new DeviceMobilePresentDTO(100017,"D","a0fa58123b6c4735159ba718cdf5d88c","O");
		//return new DeviceMobilePresentDTO(100017,"R","d52bf656bb5f70670c785e62812a2528","O");
	}
	
	/**
	 * 发送通知类型push
	 * @param pushMsg
	 */
	protected boolean pushNotification(PushMsg pushMsg){
		if(DeviceEnum.isIos(pushMsg.getD())){
			return GexinPushService.getInstance().pushNotification4ios(pushMsg);
		}else{
			return GexinPushService.getInstance().pushNotification(pushMsg);
		}
	}
	
	/**
	 * 安卓发送透传消息
	 * ios发送通知消息
	 * @param pushMsg
	 * @return
	 */
	protected boolean pushAndroidTransmissionAndIosNotification(PushMsg pushMsg){
		if(DeviceEnum.isIos(pushMsg.getD())){
			return GexinPushService.getInstance().pushNotification4ios(pushMsg);
		}else{
			return GexinPushService.getInstance().pushTransmission(pushMsg);
		}
	}
	
	/**
	 * 发送透传消息push
	 * @param pushMsg
	 * @return
	 */
	protected boolean pushTransmission(PushMsg pushMsg){
		if(DeviceEnum.isIos(pushMsg.getD())){
			return GexinPushService.getInstance().pushTransmission4ios(pushMsg);
		}else{
			return GexinPushService.getInstance().pushTransmission(pushMsg);
		}
	}
	
	/**
	 * 根据mobile push信息数据生成PushMsg对象
	 * @param mac 设备mac
	 * @return
	 */
	protected PushMsg generatePushMsg(String mac){
		DeviceMobilePresentDTO presentDto = this.getMobilePresent(mac);
		return generatePushMsg(presentDto);
	}
	
	protected PushMsg generatePushMsg(DeviceMobilePresentDTO presentDto){
		if(presentDto == null) return null;
		
		PushMsg pushMsg = new PushMsg();
		BeanUtils.copyProperties(presentDto, pushMsg);
		return pushMsg;
	}
	
}
