package com.bhu.vas.api.helper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.ret.setting.DeviceSettingBuilderDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingAclDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRadioDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRateControlDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;

public class DeviceHelper {
	
	/**
	 * 获取可用的vap的names
	 * @param vaps
	 * @return
	 */
	public static List<String> builderSettingVapNames(List<WifiDeviceSettingVapDTO> vaps){
		if(vaps == null || vaps.isEmpty()) return Collections.emptyList();
		List<String> vapnames = new ArrayList<String>();
		for(WifiDeviceSettingVapDTO vap : vaps){
			if(isVapEnable(vap)){
				vapnames.add(vap.getName());
			}
		}
		return vapnames;
	}
	
	/**
	 * 判断vap是否可用
	 * @param vap
	 * @return
	 */
	public static boolean isVapEnable(WifiDeviceSettingVapDTO vap){
		if(vap == null) return false;
		if(WifiDeviceSettingVapDTO.Vap_Enable.equalsIgnoreCase(vap.getEnable())){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断vap是否是访客网络
	 * @param vap
	 * @return
	 */
	public static boolean isVapGuestEnable(WifiDeviceSettingVapDTO vap){
		if(vap == null) return false;
		if(WifiDeviceSettingVapDTO.Vap_Enable.equalsIgnoreCase(vap.getGuest_en())){
			return true;
		}
		return false;
	}
	
	/**
	 * 匹配终端mac是否有限速设置
	 * @param dto 设备的配置dto
	 * @param mac 终端mac
	 * @return
	 */
	public static WifiDeviceSettingRateControlDTO matchRateControl(WifiDeviceSettingDTO dto, String mac){
		if(dto == null || StringUtils.isEmpty(mac)) return null;
		List<WifiDeviceSettingRateControlDTO> rateControls = dto.getRatecontrols();
		if(rateControls == null || rateControls.isEmpty()) return null;
		for(WifiDeviceSettingRateControlDTO rcDto : rateControls){
			if(mac.endsWith(rcDto.getMac())){
				return rcDto;
			}
		}
		return null;
	}
	
	/**
	 * 匹配设备的黑名单的block配置列表
	 * 由于设备中可能不存在黑名单列表配置 则返回空列表
	 * @param dto
	 * @return
	 */
	public static WifiDeviceSettingAclDTO matchDefaultAcl(WifiDeviceSettingDTO dto){
		if(dto != null) {
			List<WifiDeviceSettingAclDTO> acls = dto.getAcls();
			if(acls != null && !acls.isEmpty()){
				int index = acls.indexOf(new WifiDeviceSettingAclDTO(WifiDeviceSettingDTO.Default_AclName));
				if(index != -1){
					return acls.get(index);
				}
			}
		}
		return null;
	}
	
	public static final String Mode_Static_Show = "Static";
	public static final String Mode_Dhcpc_Show = "DHCP";
	public static final String Mode_Pppoe_Show = "PPPoE";
	public static final String Mode_Pppol2tp_Show = "pppol2tp";
	/**
	 * 获取设备的上网方式
	 * @param dto
	 * @return
	 */
	public static String getDeviceMode(WifiDeviceSettingDTO dto){
		if(dto == null) return null;
		String mode = dto.getMode();
		if(WifiDeviceSettingDTO.Mode_Static.equals(mode)){
			return Mode_Static_Show;
		}
		else if(WifiDeviceSettingDTO.Mode_Dhcpc.equals(mode)){
			return Mode_Dhcpc_Show;
		}
		else if(WifiDeviceSettingDTO.Mode_Pppoe.equals(mode)){
			return Mode_Pppoe_Show;
		}
		else if(WifiDeviceSettingDTO.Mode_Pppol2tp.equals(mode)){
			return Mode_Pppol2tp_Show;
		}
		return mode;
	}
	
	/**
	 * URouter设备获取radio信息 由于urouter设备是单频 只返回第一个radio
	 * @param dto
	 * @return
	 */
	public static WifiDeviceSettingRadioDTO getURouterDeviceRadio(WifiDeviceSettingDTO dto){
		if(dto == null) return null;
		List<WifiDeviceSettingRadioDTO> radio_dtos = dto.getRadios();
		if(radio_dtos == null || radio_dtos.isEmpty()) return null;
		return radio_dtos.get(0);
	}
	
	/**
	 * URouter设备获取信号强度信息 由于urouter设备是单频 只返回第一个radio的信号强度
	 * @param dto
	 * @return
	 */
	public static String getURouterDevicePower(WifiDeviceSettingDTO dto){
		WifiDeviceSettingRadioDTO radio_dto = getURouterDeviceRadio(dto);
		if(radio_dto == null) return null;
		return radio_dto.getPower();
	}
	
	/**
	 * 获取urouter设备的正常vap
	 * 由于urouter设备的最多会暴露2个vap 正常和访客
	 * 获取正常的vap时如果有多个暴露 只获取第一个
	 * @param dto
	 * @return
	 */
	public static WifiDeviceSettingVapDTO getUrouterDeviceVap(WifiDeviceSettingDTO dto){
		if(dto == null) return null;
		List<WifiDeviceSettingVapDTO> vaps = dto.getVaps();
		if(vaps == null || vaps.isEmpty()) return null;
		
		for(WifiDeviceSettingVapDTO vap : vaps){
			if(isVapEnable(vap) && !isVapGuestEnable(vap)){
				return vap;
			}
		}
		return null;
	}
	
	
	/**
	 * 获取设备的运行时长
	 * @param device_entity
	 * @return
	 */
	public static String getDeviceUptime(WifiDevice device_entity){
		String uptime = null;
		if(device_entity != null) {
			//如果设备在线 运行时长还要加上计算本次的
			if(device_entity.isOnline()){
				long last_reged_ts = device_entity.getLast_reged_at().getTime();
				long current_ts = System.currentTimeMillis();
				BigInteger bi = new BigInteger(String.valueOf(current_ts - last_reged_ts));
				uptime = bi.toString();
				if(!StringUtils.isEmpty(device_entity.getUptime())){
					uptime = (bi.add(new BigInteger(device_entity.getUptime()))).toString();
				}
			}else{
				uptime = device_entity.getUptime();
			}
		}
		
		if(StringUtils.isEmpty(uptime))
			uptime = "0";
		
		return uptime;
	}
	//新版本设备定义
	public static final String[] newOrigSwvers = new String[]{"1.2.8","1.2.9","1.2.10","1.2.11","1.2.12","1.2.13","1.2.14","1.2.15"};
	
	/**
	 * 根据设备的原始软件版本号 判断是否新版本设备
	 * @param orig_swver
	 * @return
	 */
	public static boolean isNewOrigSwverDevice(String orig_swver){
		if(StringUtils.isEmpty(orig_swver)) return false;
		
		for(String newOrigSwver : newOrigSwvers){
			if(orig_swver.contains(newOrigSwver)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 验证urouter设备的vap黑名单列表配置是否正确
	 * 1:验证黑名单列表是有包含约定名称的列表
	 * 2:验证vap是否都关联到此黑名单
	 * @param dto
	 * @return
	 */
	public static boolean validateURouterBlackList(WifiDeviceSettingDTO dto){
		if(dto != null){
			//1:验证黑名单列表是有包含约定名称的列表
			List<WifiDeviceSettingAclDTO> acl_dtos = dto.getAcls();
			if(acl_dtos != null && !acl_dtos.isEmpty()){
				if(!acl_dtos.contains(new WifiDeviceSettingAclDTO(WifiDeviceSettingDTO.Default_AclName))){
					return false;
				}
			}
			//2:验证vap是否都关联到此黑名单
			List<WifiDeviceSettingVapDTO> vap_dtos = dto.getVaps();
			if(vap_dtos != null && !vap_dtos.isEmpty()){
				for(WifiDeviceSettingVapDTO vap_dto : vap_dtos){
					if(!WifiDeviceSettingDTO.Default_AclName.equals(vap_dto.getAcl_name())
							|| !WifiDeviceSettingVapDTO.AclType_Deny.equals(vap_dto.getAcl_type())){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/*******************************    设备配置修改模板  ****************************************/
	public static final String DeviceSetting_URouterDefaultVapAclTree = "<dev><wifi><vap>%s</vap><acllist>%s</acllist></wifi></dev>";
	
	public static final String DeviceSetting_ConfigSequenceTree = "<dev><sys><config><ITEM sequence=\"%s\"/></config></sys></dev>";
	public static final String DeviceSetting_VapTree = "<dev><wifi><vap>%s</vap></wifi></dev>";
	public static final String DeviceSetting_AclTree = "<dev><wifi><acllist>%s</acllist></wifi></dev>";
	
	public static final String DeviceSetting_VapItem = "<ITEM name=\"%s\" radio=\"%s\" ssid=\"%s\" auth=\"%s\" enable=\"%s\" acl_type=\"%s\" acl_name=\"%s\" guest_en=\"%s\"/>";
	public static final String DeviceSetting_AclItem = "<ITEM name=\"%s\" macs=\"%s\" />";
	
	/**
	 * 通过配置模板和配置dto来组装配置xml
	 * @param template
	 * @param builderDto
	 * @return
	 */
	public static String builderDeviceSetting(String template, DeviceSettingBuilderDTO builderDto){
		if(StringUtils.isEmpty(template)) return null;
		if(builderDto == null) return null;
		return String.format(template, builderDto.builderProperties());
	}
	
	/**
	 * 通过配置模板和配置dtos来组装配置xml
	 * @param template
	 * @param builderDtos
	 * @return
	 */
	public static String builderDeviceSetting(String template, List<? extends DeviceSettingBuilderDTO> builderDtos){
		if(StringUtils.isEmpty(template)) return null;
		if(builderDtos == null || builderDtos.isEmpty()) return null;
		StringBuffer sb = new StringBuffer();
		for(DeviceSettingBuilderDTO builderDto : builderDtos){
			String result = builderDeviceSetting(template, builderDto);
			if(!StringUtils.isEmpty(result)){
				sb.append(result);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 构造并拼装设备配置 拼装config_sequence
	 * @param ds
	 * @param config_sequence
	 * @return
	 */
	public static String builderConfigSequence(String ds, String config_sequence){
		if(StringUtils.isEmpty(ds) || StringUtils.isEmpty(config_sequence)) return null;
		
		String sequence = String.format(DeviceSetting_ConfigSequenceTree, config_sequence);
		return ds.concat(sequence);
	}
	
	/**
	 * 构造urouter设备的默认vap黑名单列表配置
	 * @param config_sequence
	 * @param vap_dtos
	 * @param acl_dto
	 * @return
	 */
	public static String builderDSURouterDefaultVapAndAcl(WifiDeviceSettingDTO dto){
		if(dto == null || StringUtils.isEmpty(dto.getSequence())) return null;
		
		List<WifiDeviceSettingVapDTO> vap_dtos = builderDSURouterDefaultVapDtos(dto);
		WifiDeviceSettingAclDTO acl_dto = builderDSURouterDefaultAclDto(dto);
		
		String vap_string = builderDeviceSetting(DeviceSetting_VapItem, vap_dtos);
		String acl_string = builderDeviceSetting(DeviceSetting_AclItem, acl_dto);
		
		if(StringUtils.isEmpty(vap_string) || StringUtils.isEmpty(acl_string)) return null;

		String result = String.format(DeviceSetting_URouterDefaultVapAclTree, vap_string, acl_string);
		return builderConfigSequence(result, dto.getSequence());
	}
	
	
	public static List<WifiDeviceSettingVapDTO> builderDSURouterDefaultVapDtos(WifiDeviceSettingDTO dto){
		if(dto != null) {
			List<WifiDeviceSettingVapDTO> vap_dtos = dto.getVaps();
			if(vap_dtos != null && !vap_dtos.isEmpty()){
				for(WifiDeviceSettingVapDTO vap_dto : vap_dtos){
					vap_dto.setAcl_type(WifiDeviceSettingVapDTO.AclType_Deny);
					vap_dto.setAcl_name(WifiDeviceSettingDTO.Default_AclName);
				}
				return vap_dtos;
			}
		}
		return null;
	}
	
	public static WifiDeviceSettingAclDTO builderDSURouterDefaultAclDto(WifiDeviceSettingDTO dto){
		WifiDeviceSettingAclDTO current_acl_dto = matchDefaultAcl(dto);
		if(current_acl_dto != null){
			return current_acl_dto;
		}
		
		WifiDeviceSettingAclDTO result = new WifiDeviceSettingAclDTO();
		result.setName(WifiDeviceSettingDTO.Default_AclName);
		return result;
	}
}
