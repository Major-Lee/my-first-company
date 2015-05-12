package com.bhu.vas.api.helper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bhu.vas.api.dto.ret.setting.*;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.ReflectionHelper;
import com.smartwork.msip.cores.helper.encrypt.RSAHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

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
		if(WifiDeviceSettingVapDTO.Enable.equalsIgnoreCase(vap.getEnable())){
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
		if(WifiDeviceSettingVapDTO.Enable.equalsIgnoreCase(vap.getGuest_en())){
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
	 * 判断终端mac是否有别名
	 * @param dto
	 * @param mac
	 * @return
	 */
	public static WifiDeviceSettingMMDTO matchMacManagement(WifiDeviceSettingDTO dto, String mac){
		if(dto == null || StringUtils.isEmpty(mac)) return null;
		List<WifiDeviceSettingMMDTO> mms = dto.getMms();
		if(mms == null || mms.isEmpty()) return null;
		int index = mms.indexOf(new WifiDeviceSettingMMDTO(mac));
		if(index != -1){
			return mms.get(index);
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
	
	/**
	 * 根据acl的名称查找
	 * @param name
	 * @param dto
	 * @return
	 */
	public static WifiDeviceSettingAclDTO getAclByName(String name, WifiDeviceSettingDTO dto){
		if(StringUtils.isEmpty(name)) return null;
		
		if(dto != null) {
			List<WifiDeviceSettingAclDTO> acls = dto.getAcls();
			if(acls != null && !acls.isEmpty()){
				int index = acls.indexOf(new WifiDeviceSettingAclDTO(name));
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
	 * 获取终端在此设备上的别名
	 * @param mac
	 * @param dto
	 * @return
	 */
	public static String getHandsetDeviceAlias(String mac, WifiDeviceSettingDTO dto){
		if(dto == null) return null;
		List<WifiDeviceSettingMMDTO> mm_dtos = dto.getMms();
		if(mm_dtos == null || mm_dtos.isEmpty()) return null;
		int index = mm_dtos.indexOf(new WifiDeviceSettingMMDTO(mac));
		if(index != -1){
			return mm_dtos.get(index).getName();
		}
		//TODO:如果没有别名 返回hostname
		return null;
	}
	
	/**
	 * 判断radio name是否存在
	 * @param name
	 * @param dto
	 * @return
	 */
	public static boolean isExistRadioName(String name, WifiDeviceSettingDTO dto){
		if(dto == null) return false;
		List<WifiDeviceSettingRadioDTO> radio_dtos = dto.getRadios();
		if(radio_dtos == null || radio_dtos.isEmpty()) return false;
		return radio_dtos.contains(new WifiDeviceSettingRadioDTO(name));
	}
	
	/**
	 * 判断vapname是否是访客网络
	 * @param vapname
	 * @param dto
	 * @return
	 */
	public static boolean isGuest(String vapname, WifiDeviceSettingDTO dto){
		if(dto == null) return false;
		List<WifiDeviceSettingVapDTO> vap_dtos = dto.getVaps();
		if(vap_dtos == null || vap_dtos.isEmpty()) return false;
		int index = vap_dtos.indexOf(new WifiDeviceSettingVapDTO(vapname));
		if(index == -1) return false;
		if(WifiDeviceSettingVapDTO.Enable.equals(vap_dtos.get(index).getGuest_en())){
			return true;
		}
		return false;
	}
	
	/**
	 * 只返回第一个radio
	 * @param dto
	 * @return
	 */
	public static WifiDeviceSettingRadioDTO getFristDeviceRadio(WifiDeviceSettingDTO dto){
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
		WifiDeviceSettingRadioDTO radio_dto = getFristDeviceRadio(dto);
		if(radio_dto == null) return null;
		return radio_dto.getPower();
	}
	
	/**
	 * 获取ratecontorl的indexs
	 * @param rc_list
	 * @return
	 */
	public static List<Integer> getDeviceRateControlIndex(List<WifiDeviceSettingRateControlDTO> rc_list){
		if(rc_list == null || rc_list.isEmpty()) return Collections.emptyList();
		List<Integer> indexs = new ArrayList<Integer>();
		for(WifiDeviceSettingRateControlDTO rc_dto : rc_list){
			indexs.add(Integer.parseInt(rc_dto.getIndex()));
		}
		return indexs;
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
	 * 获取设备的总运行时长
	 * @param device_entity
	 * @return
	 */
	public static String getTotalDeviceUptime(WifiDevice device_entity){
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
	
	/**
	 * 获取设备的本次运行时长
	 * @param device_entity
	 * @return
	 */
	public static String getCurrentDeviceUptime(WifiDevice device_entity){
		String uptime = null;
		if(device_entity != null) {
			//如果设备在线 
			if(device_entity.isOnline()){
				long last_reged_ts = device_entity.getLast_reged_at().getTime();
				long current_ts = System.currentTimeMillis();
				BigInteger bi = new BigInteger(String.valueOf(current_ts - last_reged_ts));
				return bi.toString();
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
	
//	/**
//	 * 修改设备的配置序列号
//	 * @param dto
//	 * @param config_sequence
//	 * @return
//	 */
//	public static boolean modifyDSConfigSequence(WifiDeviceSettingDTO dto, String config_sequence){
//		if(dto != null){
//			dto.setSequence(config_sequence);
//			return true;
//		}
//		return false;
//	}
	
	/**
	 * 获取设备的配置序列号
	 * @param dto
	 * @return
	 */
	public static String getConfigSequence(WifiDeviceSettingDTO dto){
		if(dto != null){
			return dto.getSequence();
		}
		return null;
	}
	
	/**
	 * 当前配置与修改配置上下文进行合并
	 * @param source
	 * @param target
	 */
	public static void mergeDS(WifiDeviceSettingDTO source, WifiDeviceSettingDTO target){
		try{
			if(source != null && target != null){
				//合并 radio 多频设备会有多个
				List<WifiDeviceSettingRadioDTO> m_radios = mergeList(source.getRadios(), target.getRadios());
				if(m_radios != null){
					target.setRadios(m_radios);
				}
				//合并 wan
				if(!StringUtils.isEmpty(source.getMode())){
					target.setMode(source.getMode());
				}
				//合并 vaps
				List<WifiDeviceSettingVapDTO> m_vaps = mergeList(source.getVaps(), target.getVaps());
				if(m_vaps != null){
					target.setVaps(m_vaps);
				}
				//合并黑白名单
				List<WifiDeviceSettingAclDTO> m_acls = mergeList(source.getAcls(), target.getAcls());
				if(m_acls != null){
					target.setAcls(m_acls);
				}
				//合并接口速率控制
				List<WifiDeviceSettingInterfaceDTO> m_interfaces = mergeList(source.getInterfaces(), target.getInterfaces());
				if(m_interfaces != null){
					target.setInterfaces(m_interfaces);
				}
				//合并终端速率控制
				List<WifiDeviceSettingRateControlDTO> m_ratecontrols = mergeList(source.getRatecontrols(), target.getRatecontrols());
				if(m_ratecontrols != null){
					target.setRatecontrols(m_ratecontrols);
				}
				//合并终端别名
				List<WifiDeviceSettingMMDTO> m_mms = mergeList(source.getMms(), target.getMms());
				if(m_mms != null){
					target.setMms(m_mms);
				}
				//合并广告
				if(source.getAd() != null){
					ReflectionHelper.copyProperties(source.getAd(), target.getAd());
				}
				//合并管理员用户列表
				List<WifiDeviceSettingUserDTO> m_users = mergeList(source.getUsers(), target.getUsers());
				if(m_users != null){
					target.setUsers(m_users);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static <T extends DeviceSettingBuilderDTO> List<T> mergeList(List<T> source, List<T> target) throws Exception{
		if(source == null) return null;
		//如果当前为空 则直接覆盖
		if(target == null || target.isEmpty()){
			return source;
		}else{
			for(T source_item : source){
				int index = target.indexOf(source_item);
				if(source_item.beRemoved()){
					target.remove(source_item);
				}else{
					if(index != -1){
						ReflectionHelper.copyProperties(source_item, target.get(index));
					}else{
						target.add(source_item);
					}
				}
			}
			return target;
		}
	}
	
	/*******************************    设备配置修改模板  ****************************************/
	
	public static final String DeviceSetting_ConfigSequenceOuter = "<dev><sys><config><ITEM sequence=\"%s\"/></config></sys></dev>";
	public static final String DeviceSetting_ConfigSequenceInner = "<sys><config><ITEM sequence=\"%s\"/></config></sys>";
	
	public static final String DeviceSetting_URouterDefaultVapAclOuter = "<dev>".concat(DeviceSetting_ConfigSequenceInner).concat("<wifi><vap>%s</vap><acllist>%s</acllist></wifi></dev>");
	
	public static final String DeviceSetting_VapOuter = "<dev>".concat(DeviceSetting_ConfigSequenceInner).concat("<wifi><vap>%s</vap></wifi></dev>");
	public static final String DeviceSetting_AclOuter = "<dev>".concat(DeviceSetting_ConfigSequenceInner).concat("<wifi><acllist>%s</acllist></wifi></dev>");
	public static final String DeviceSetting_AdOuter = "<dev>".concat(DeviceSetting_ConfigSequenceInner).concat("<net><ad>%s</ad></net></dev>");
	public static final String DeviceSetting_RadioOuter = "<dev>".concat(DeviceSetting_ConfigSequenceInner).concat("<wifi><radio>%s</radio></wifi></dev>");
	public static final String DeviceSetting_RatecontrolOuter = "<dev>".concat(DeviceSetting_ConfigSequenceInner).concat("<net><rate_control>%s</rate_control></net></dev>");
	public static final String DeviceSetting_AdminPasswordOuter = "<dev>".concat(DeviceSetting_ConfigSequenceInner).concat("<sys><users>%s</users></sys></dev>");

	public static final String DeviceSetting_MMOuter = "<dev>".concat(DeviceSetting_ConfigSequenceInner).concat("<net><mac_management>%s</mac_management></net></dev>");
	public static final String DeviceSetting_LinkModeOuter ="<dev>".concat(DeviceSetting_ConfigSequenceInner).concat("<mod><basic><wan>%s</wan></basic></mod></dev>");


	public static final String DeviceSetting_VapItem = "<ITEM name=\"%s\" radio=\"%s\" ssid=\"%s\" auth=\"%s\" enable=\"%s\" acl_type=\"%s\" acl_name=\"%s\" guest_en=\"%s\"/>";
	public static final String DeviceSetting_AclItem = "<ITEM name=\"%s\" macs=\"%s\" />";
	public static final String DeviceSetting_AdItem = "<ITEM bhu_id=\"%s\" bhu_ad_url=\"%s\" bhu_enable=\"%s\" />";
	public static final String DeviceSetting_RadioItem = "<ITEM name=\"%s\" power=\"%s\" />";
	public static final String DeviceSetting_VapPasswordItem = "<ITEM name=\"%s\" ssid=\"%s\" auth=\"%s\" auth_key=\"%s\" />";
	public static final String DeviceSetting_RatecontrolItem = "<ITEM mac=\"%s\" tx=\"%s\" rx=\"%s\" index=\"%s\"/>";
	public static final String DeviceSetting_AdminPasswordItem = "<ITEM password_rsa=\"%s\" name=\"admin\" />";
	public static final String DeviceSetting_MMItem = "<ITEM mac=\"%s\" name=\"%s\" />";
	
	public static final String DeviceSetting_RemoveRatecontrolItem = "<ITEM index=\"%s\" ssdel=\"1\" mac=\"%s\"/>";
	public static final String DeviceSetting_RemoveMMItem = "<ITEM mac=\"%s\" ssdel=\"1\" />";

	public static final String DeviceSetting_LinkModelPPPOEItem = "<ITEM mode=\"%s\" username=\"%s\" password_rsa=\"%s\" link_mode=\"%s\" idle=\"%s\"/>";
	public static final String DeviceSetting_LinkModelStaticItem = "<ITEM mode=\"%s\" ip=\"%s\" netmask=\"%s\" gateway=\"%s\" dns=\"%s\"/>";
	public static final String DeviceSetting_LinkModelDHCPCItem =  "<ITEM mode=\"%s\"/>";
	
	/**
	 * 通过配置模板和配置dto来组装配置xml
	 * @param template
	 * @param builderDto
	 * @return
	 */
	public static String builderDeviceSettingItemWithDto(String template_item, DeviceSettingBuilderDTO builderDto){
		if(StringUtils.isEmpty(template_item)) return null;
		if(builderDto == null) return null;
		return builderDeviceSettingItem(template_item, builderDto.builderProperties());
	}
	
	public static String builderDeviceSettingItem(String template_item, Object[] properties){
		if(StringUtils.isEmpty(template_item)) return null;
		if(properties == null) return null;
		return String.format(template_item, properties);
	}
	
	/**
	 * 通过配置模板和配置dtos来组装配置xml
	 * @param template
	 * @param builderDtos
	 * @return
	 */
	public static String builderDeviceSettingItemsWithDto(String template_item, List<? extends DeviceSettingBuilderDTO> builderDtos){
		if(StringUtils.isEmpty(template_item)) return null;
		if(builderDtos == null || builderDtos.isEmpty()) return null;
		StringBuffer sb = new StringBuffer();
		for(DeviceSettingBuilderDTO builderDto : builderDtos){
			String result = builderDeviceSettingItemWithDto(template_item, builderDto);
			if(!StringUtils.isEmpty(result)){
				sb.append(result);
			}
		}
		return sb.toString();
	}
	
	public static String builderDeviceSettingItems(String template_item, List<Object[]> properties_list){
		if(StringUtils.isEmpty(template_item)) return null;
		if(properties_list == null || properties_list.isEmpty()) return null;
		StringBuffer sb = new StringBuffer();
		for(Object[] properties : properties_list){
			String result = builderDeviceSettingItem(template_item, properties);
			if(!StringUtils.isEmpty(result)){
				sb.append(result);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 构建包含前后缀的配置数据
	 * @param template_tree
	 * @param payload_item
	 * @return
	 */
	public static String builderDeviceSettingOuter(String template_tree, Object... payload_items){
		if(StringUtils.isEmpty(template_tree)) return null;
		if(payload_items == null || payload_items.length == 0) return null;
		return String.format(template_tree, payload_items);
	}
	
	/**
	 * 构造并拼装设备配置 拼装config_sequence
	 * @param ds
	 * @param config_sequence
	 * @return
	 */
	public static String builderConfigSequence(String ds, String config_sequence){
		if(StringUtils.isEmpty(ds) || StringUtils.isEmpty(config_sequence)) return null;
		
		String sequence = String.format(DeviceSetting_ConfigSequenceOuter, config_sequence);
		return sequence.concat(ds);
	}
	
	/*******************************    设备具体业务配置修改  ****************************************/
	
	public static final String DeviceSettingAction_Incr = "incr";
	public static final String DeviceSettingAction_Del = "del";
	
	/**
	 * 构造urouter设备的默认vap黑名单列表配置
	 * @param dto
	 * @return
	 */
	public static String builderDSURouterDefaultVapAndAcl(WifiDeviceSettingDTO dto){
		if(dto == null || StringUtils.isEmpty(dto.getSequence())) return null;
		
		String vap_string = builderDSURouterDefaultVapItems(dto);
		String acl_string = builderDSURouterDefaultAclItem(dto);
		
		if(StringUtils.isEmpty(vap_string) || StringUtils.isEmpty(acl_string)) return null;

		return builderDeviceSettingOuter(DeviceSetting_URouterDefaultVapAclOuter, dto.getSequence(), 
				vap_string, acl_string);
		//return builderConfigSequence(payload, dto.getSequence());
	}
	
	/**
	 * 构建urouter设备的默认vap的黑名单关联
	 * @param dto
	 * @return
	 */
	public static String builderDSURouterDefaultVapItems(WifiDeviceSettingDTO dto){
		if(dto != null) {
			List<WifiDeviceSettingVapDTO> vap_dtos = dto.getVaps();
			if(vap_dtos != null && !vap_dtos.isEmpty()){
				for(WifiDeviceSettingVapDTO vap_dto : vap_dtos){
					vap_dto.setAcl_type(WifiDeviceSettingVapDTO.AclType_Deny);
					vap_dto.setAcl_name(WifiDeviceSettingDTO.Default_AclName);
				}
				return builderDeviceSettingItemsWithDto(DeviceSetting_VapItem, vap_dtos);
			}
		}
		return null;
	}
	/**
	 * 构建urouter设备的默认的黑名单列表
	 * @param dto
	 * @return
	 */
	public static String builderDSURouterDefaultAclItem(WifiDeviceSettingDTO dto){
		WifiDeviceSettingAclDTO current_acl_dto = matchDefaultAcl(dto);
		if(current_acl_dto != null){
			return builderDeviceSettingItemWithDto(DeviceSetting_AclItem, current_acl_dto);
		}
		
		WifiDeviceSettingAclDTO result = new WifiDeviceSettingAclDTO();
		result.setName(WifiDeviceSettingDTO.Default_AclName);
		return builderDeviceSettingItemWithDto(DeviceSetting_AclItem, result);
	}
	
	/**
	 * 构建广告配置数据
	 * @param config_sequence
	 * @param extparams
	 * @param ds_dto
	 * @return throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
	 */
	public static String builderDSAdOuter(String config_sequence, String extparams, WifiDeviceSettingDTO ds_dto){
		WifiDeviceSettingAdDTO ad_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingAdDTO.class);
		if(ad_dto == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		
		String item = builderDeviceSettingItemWithDto(DeviceSetting_AdItem, ad_dto);
		return builderDeviceSettingOuter(DeviceSetting_AdOuter, config_sequence, item);
	}
	
	/**
	 * 构建信号强度配置数据
	 * @param config_sequence
	 * @param extparams
	 * @param ds_dto
	 * @return 
	 */
	public static String builderDSPowerOuter(String config_sequence, String extparams, WifiDeviceSettingDTO ds_dto){
		WifiDeviceSettingRadioDTO radio_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingRadioDTO.class);
		if(radio_dto == null || StringUtils.isEmpty(radio_dto.getPower()))
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		
		if(!StringUtils.isEmpty(radio_dto.getName())){
			//如果radio名称不存在 则返回null
			if(!isExistRadioName(radio_dto.getName(), ds_dto)){
				throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
			}
		}else{
			//如果没有指定radio的具体名称 则获取默认第一个radio进行修改
			WifiDeviceSettingRadioDTO frist_radio_dto = getFristDeviceRadio(ds_dto);
			//如果没有一个可用的radio
			if(frist_radio_dto == null) 
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_ERROR);
			
			radio_dto.setName(frist_radio_dto.getName());
		}
		String item = builderDeviceSettingItemWithDto(DeviceSetting_RadioItem, radio_dto);
		return builderDeviceSettingOuter(DeviceSetting_RadioOuter, config_sequence, item);
	}
	
	/**
	 * 构建vap密码修改配置
	 * @param config_sequence
	 * @param extparams
	 * @param ds_dto
	 * @return 
	 */
	public static String builderDSVapPasswordOuter(String config_sequence, String extparams, WifiDeviceSettingDTO ds_dto){
		WifiDeviceSettingVapDTO vap_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingVapDTO.class);
		if(vap_dto == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		
		//如果没有指定vap的具体名称 则获取默认第一个非访客的vap进行修改
		if(StringUtils.isEmpty(vap_dto.getName())){
			WifiDeviceSettingVapDTO frist_vap_dto = getUrouterDeviceVap(ds_dto);
			//如果没有一个可用的vap
			if(frist_vap_dto == null) 
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_ERROR);
			
			vap_dto.setName(frist_vap_dto.getName());
		}
		String item = builderDeviceSettingItem(DeviceSetting_VapPasswordItem, 
				vap_dto.builderProperties(WifiDeviceSettingVapDTO.BuilderType_VapPassword));
		return builderDeviceSettingOuter(DeviceSetting_VapOuter, config_sequence, item);
	}
	
	/**
	 * 构建黑名单列表名单修改配置
	 * @param config_sequence
	 * @param extparams
	 * @param ds_dto
	 * @return 
	 */
	public static String builderDSAclMacsOuter(String config_sequence, String extparams, WifiDeviceSettingDTO ds_dto){
		Map<String, WifiDeviceSettingAclDTO> acl_dto_map = JsonHelper.getDTOMapKeyDto(extparams, WifiDeviceSettingAclDTO.class);
		if(acl_dto_map == null || acl_dto_map.isEmpty())
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		
		WifiDeviceSettingAclDTO default_acl_dto = matchDefaultAcl(ds_dto);
		if(default_acl_dto == null)
			default_acl_dto = new WifiDeviceSettingAclDTO();
		
		Set<String> macs = new HashSet<String>();
		if(default_acl_dto.getMacs() != null){
			macs.addAll(default_acl_dto.getMacs());
		}
			
		WifiDeviceSettingAclDTO acl_incr_dto = acl_dto_map.get(DeviceSettingAction_Incr);
		if(acl_incr_dto != null && acl_incr_dto.getMacs() != null){
			macs.addAll(acl_incr_dto.getMacs());
		}
			
		WifiDeviceSettingAclDTO acl_del_dto = acl_dto_map.get(DeviceSettingAction_Del);
		if(acl_del_dto != null && acl_del_dto.getMacs() != null){
			macs.removeAll(acl_del_dto.getMacs());
		}
		default_acl_dto.setMacs(new ArrayList<String>(macs));
			
		String item = builderDeviceSettingItem(DeviceSetting_AclItem, default_acl_dto.builderProperties());
		return builderDeviceSettingOuter(DeviceSetting_AclOuter, config_sequence, item);
	}
	//<dev><sys><config><ITEM sequence=\""+config_sequence+"\"/></config></sys><net><rate_control><ITEM mac=\"aa:aa:aa:aa:bb:49\" tx=\"80\" rx=\"40\" index=\"aa\"/></rate_control></net></dev>
	/**
	 * 构建流量控制的修改配置
	 * @param config_sequence
	 * @param extparams
	 * @param ds_dto
	 * @return 
	 */
	public static String builderDSRateControlOuter(String config_sequence, String extparams, WifiDeviceSettingDTO ds_dto){
		Map<String, List<WifiDeviceSettingRateControlDTO>> rc_dto_map = JsonHelper.getDTOMapKeyList(extparams, WifiDeviceSettingRateControlDTO.class);
		if(rc_dto_map == null || rc_dto_map.isEmpty())
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		
		StringBuffer ds = new StringBuffer();
		
		List<Integer> rc_indexs = getDeviceRateControlIndex(ds_dto.getRatecontrols());
		List<WifiDeviceSettingRateControlDTO> rc_incr_dtos = rc_dto_map.get(DeviceSettingAction_Incr);
		if(rc_incr_dtos != null && !rc_incr_dtos.isEmpty()){
			for(WifiDeviceSettingRateControlDTO rc_incr_dto : rc_incr_dtos){
				WifiDeviceSettingRateControlDTO match_rc_dto = matchRateControl(ds_dto, rc_incr_dto.getMac());
				//如果匹配到 说明是修改
				if(match_rc_dto != null){
					rc_incr_dto.setIndex(match_rc_dto.getIndex());
				}
				//没匹配到 说明是新增 获取新的index
				else{
					int index = ArrayHelper.getMinOrderNumberVacant(rc_indexs);
					rc_incr_dto.setIndex(String.valueOf(index));
					rc_indexs.add(index);
				}
				ds.append(builderDeviceSettingItem(DeviceSetting_RatecontrolItem, rc_incr_dto.builderProperties()));
			}
		}
				
		List<WifiDeviceSettingRateControlDTO> rc_del_dtos = rc_dto_map.get(DeviceSettingAction_Del);
		if(rc_del_dtos != null && !rc_del_dtos.isEmpty()){
			for(WifiDeviceSettingRateControlDTO rc_del_dto : rc_del_dtos){
				WifiDeviceSettingRateControlDTO match_rc_dto = matchRateControl(ds_dto, rc_del_dto.getMac());
				if(match_rc_dto != null){
					rc_del_dto.setIndex(match_rc_dto.getIndex());
					ds.append(builderDeviceSettingItem(DeviceSetting_RemoveRatecontrolItem, rc_del_dto.
							builderProperties(WifiDeviceSettingRateControlDTO.BuilderType_RemoveRC)));
				}
			}
		}
				
		if(ds.length() == 0){
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		}
		return builderDeviceSettingOuter(DeviceSetting_RatecontrolOuter, config_sequence, ds.toString());
	}
	
	/**
	 * 构建admin管理密码修改配置
     *
     * 目前设备端不要求传原始密码
     *
	 * @param config_sequence
	 * @param extparams
	 * @param ds_dto
	 * @return
	 * @throws Exception 
	 */
	public static String builderDSAdminPasswordOuter(String config_sequence, String extparams, WifiDeviceSettingDTO ds_dto)
			throws Exception {
		WifiDeviceSettingUserDTO user_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingUserDTO.class);
		if(user_dto == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		}
//		if(StringUtils.isEmpty(user_dto.getOldpassword()) || StringUtils.isEmpty(user_dto.getPassword())){
//			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
//		}

//		user_dto.setOldpassword(RSAHelper.encryptToAp(user_dto.getOldpassword(), RuntimeConfiguration.BHUDeviceRSAPublicKey));
		user_dto.setPassword(RSAHelper.encryptToAp(user_dto.getPassword(), RuntimeConfiguration.BHUDeviceRSAPublicKey));
		String item = builderDeviceSettingItem(DeviceSetting_AdminPasswordItem, user_dto.builderProperties());
		return builderDeviceSettingOuter(DeviceSetting_AdminPasswordOuter, config_sequence, item);
	}

	/**
	 * 修改上网方式配置()
	 * @param config_sequence
	 * @param extparams
	 * @return
	 */
	public static String builderDSLinkModeOuter(String config_sequence, String extparams, WifiDeviceSettingDTO ds_dto) throws Exception {
		if(!StringUtils.isEmpty(config_sequence) && !StringUtils.isEmpty(extparams)){
			WifiDeviceSettingLinkModeDTO linkModelDTO = JsonHelper.getDTO(extparams, WifiDeviceSettingLinkModeDTO.class);
			if (linkModelDTO != null) {
				if (WifiDeviceSettingDTO.Mode_Pppoe.equals(linkModelDTO.getModel())){
					linkModelDTO.setPassword_rsa(RSAHelper.encryptToAp(linkModelDTO.getPassword_rsa(), RuntimeConfiguration.BHUDeviceRSAPublicKey));
					linkModelDTO.setLink_mode("auto");
					linkModelDTO.setIdle("60");
					String item = builderDeviceSettingItem(DeviceSetting_LinkModelPPPOEItem,
							linkModelDTO.builderProperties(WifiDeviceSettingLinkModeDTO.MODEL_PPPOE_TYPE));
					return builderDeviceSettingOuter(DeviceSetting_LinkModeOuter, config_sequence, item);
				} else if (WifiDeviceSettingDTO.Mode_Static.equals(linkModelDTO.getModel())){
					String item = builderDeviceSettingItem(DeviceSetting_LinkModelStaticItem,
							linkModelDTO.builderProperties(WifiDeviceSettingLinkModeDTO.MODEL_STATIC_TYPE));
					return builderDeviceSettingOuter(DeviceSetting_LinkModeOuter, config_sequence, item);
				} else if(WifiDeviceSettingDTO.Mode_Dhcpc.equals(linkModelDTO.getModel())) {
					String item = builderDeviceSettingItem(DeviceSetting_LinkModelDHCPCItem,
							linkModelDTO.builderProperties(WifiDeviceSettingLinkModeDTO.MODEL_DHCPC_TYPE));
					return builderDeviceSettingOuter(DeviceSetting_LinkModeOuter, config_sequence, item);
				}
			}
		}
        return null;
	}
	
	/**
	 * 修改终端别名配置
	 * @param config_sequence
	 * @param extparams
	 * @param ds_dto
	 * @return
	 * @throws Exception
	 */
	public static String builderDSHDAliasOuter(String config_sequence, String extparams, WifiDeviceSettingDTO ds_dto)
			throws Exception {
		List<WifiDeviceSettingMMDTO> mm_dtos = JsonHelper.getDTOList(extparams, WifiDeviceSettingMMDTO.class);
		if(mm_dtos == null || mm_dtos.isEmpty()){
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		}
		
		StringBuffer ds = new StringBuffer();
		
		for(WifiDeviceSettingMMDTO mm_dto : mm_dtos){
			if(StringUtils.isEmpty(mm_dto.getMac())){
				throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
			}
			//如果name不为空 说明为新增或修改别名
			if(!StringUtils.isEmpty(mm_dto.getName())){
				ds.append(builderDeviceSettingItem(DeviceSetting_MMItem, mm_dto.builderProperties()));
			}
			//如果name为空 说明是删除
			else{
				//如果存在才可以删除
				if(matchMacManagement(ds_dto, mm_dto.getMac()) != null){
					mm_dto.setSsdel(DeviceSettingBuilderDTO.Removed);
					ds.append(builderDeviceSettingItem(DeviceSetting_RemoveMMItem, mm_dto.
							builderProperties(WifiDeviceSettingMMDTO.BuilderType_RemoveMM)));
				}
			}
		}
		if(ds.length() == 0){
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		}
		return builderDeviceSettingOuter(DeviceSetting_MMOuter, config_sequence, ds.toString());
	}
	
	
	public static void main(String[] args){
//		WifiDeviceSettingVapDTO v1 = new WifiDeviceSettingVapDTO();
//		v1.setName("v1");
//		v1.setSsid("a11");
//		WifiDeviceSettingVapDTO v2 = new WifiDeviceSettingVapDTO();
//		v2.setName("v2");
//		v2.setSsid("a12");
//		WifiDeviceSettingVapDTO v3 = new WifiDeviceSettingVapDTO();
//		v3.setName("v3");
//		v3.setSsid("a13");
//		
//		List<WifiDeviceSettingVapDTO> vaps_target = new ArrayList<WifiDeviceSettingVapDTO>();
//		vaps_target.add(v1);
//		vaps_target.add(v2);
//		vaps_target.add(v3);
//		WifiDeviceSettingDTO target = new WifiDeviceSettingDTO();
//		target.setVaps(vaps_target);
//		
//		WifiDeviceSettingVapDTO v4 = new WifiDeviceSettingVapDTO();
//		v4.setName("v1");
//		v4.setSsid("a14");
//		v4.setAcl_type("tt");
//		
//		try {
//			ReflectionHelper.copyProperties(v3, v4);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println(v4.getName() + "=" + v4.getSsid() + "=" + v4.getAcl_type());
		
		List<WifiDeviceSettingVapDTO> vap_dtos = new ArrayList<WifiDeviceSettingVapDTO>();
		vap_dtos.add(new WifiDeviceSettingVapDTO("1"));
		vap_dtos.add(new WifiDeviceSettingVapDTO("2"));
		int index = vap_dtos.indexOf(new WifiDeviceSettingVapDTO("2"));
		System.out.println(index);
//		List<WifiDeviceSettingVapDTO> vaps_source = new ArrayList<WifiDeviceSettingVapDTO>();
//		vaps_source.add(v4);
//		
//		WifiDeviceSettingDTO source = new WifiDeviceSettingDTO();
//		source.setVaps(vaps_source);
//		
//		mergeDS(source, target);
//		
//		for(WifiDeviceSettingVapDTO vap : target.getVaps()){
//			System.out.println(vap.getName() + "=" + vap.getSsid());
//		}
	}

}
