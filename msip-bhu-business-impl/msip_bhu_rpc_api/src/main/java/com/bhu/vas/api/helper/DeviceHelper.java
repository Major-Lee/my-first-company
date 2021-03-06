package com.bhu.vas.api.helper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.ret.param.ParamVapAdDTO;
import com.bhu.vas.api.dto.ret.param.ParamVapHttp404DTO;
import com.bhu.vas.api.dto.ret.param.ParamVapHttpPortalDTO;
import com.bhu.vas.api.dto.ret.param.ParamVapHttpRedirectDTO;
import com.bhu.vas.api.dto.ret.setting.DeviceSettingBuilderDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingAclDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingInterfaceDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingLinkModeDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingMMDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRadioDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRateControlDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingUserDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapAdDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapHttp404DTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapHttpPortalDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingVapHttpRedirectDTO;
import com.bhu.vas.api.dto.ret.setting.param.RateControlParamDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.ReflectionHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.encrypt.JNIRsaHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.localunit.RandomPicker;

public class DeviceHelper {
	
	public static final int Device_Peak_Section_Type_OnlyDownload = 1;//设备只测速下行
	public static final int Device_Peak_Section_Type_OnlyUpload = 2;//设备只测速上行
	public static final int Device_Peak_Section_Type_All = 3;//设备上下行都进行测速
	
	//获取配置数据正常
	public static final int RefreashDeviceSetting_Normal = 0;
	//获取配置数据序列号比当前小 认为是恢复出厂
	public static final int RefreashDeviceSetting_RestoreFactory = 1;
	
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
	
	/**
	 * 判断终端mac是否在约定的黑名单中
	 * @param mac 终端mac
	 * @param dto
	 * @return
	 */
	public static boolean isAclMac(String mac, WifiDeviceSettingDTO dto){
		if(StringUtils.isEmpty(mac)) return false;
		
		WifiDeviceSettingAclDTO acl_dto = matchDefaultAcl(dto);
		if(acl_dto != null){
			List<String> macs = acl_dto.getMacs();
			if(macs != null && !macs.isEmpty()){
				return macs.contains(mac);
			}
		}
		return false;
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
	public static String getDeviceMode(String mode){
		if(StringUtils.isEmpty(mode)) return null;
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
	
	private static final String Unknow_Power = "-1";
	private static final String Unknow_RealChannel = "-1";
	/**
	 * URouter设备获取信号强度信息 由于urouter设备是单频 只返回第一个radio的信号强度
	 * @param dto
	 * @return
	 */
	public static String[] getURouterDevicePowerAndRealChannel(WifiDeviceSettingDTO dto){
		String[] result = new String[2];
		WifiDeviceSettingRadioDTO radio_dto = getFristDeviceRadio(dto);
		if(radio_dto == null){
			result[0] = Unknow_Power;
			result[1] = Unknow_RealChannel;
		}else{
			result[0] = StringUtils.isEmpty(radio_dto.getPower())?Unknow_Power:radio_dto.getPower();
			result[1] = StringUtils.isEmpty(radio_dto.getReal_channel())?Unknow_RealChannel:radio_dto.getReal_channel();
		}
		return result;
	}
	
	/**
	 * 返回设备配置数据中的admin的用户数据
	 * @param dto
	 * @return
	 */
	public static WifiDeviceSettingUserDTO getURouterDeviceAdminUser(WifiDeviceSettingDTO dto){
		if(dto == null) return null;
		List<WifiDeviceSettingUserDTO> users = dto.getUsers();
		if(users == null || users.isEmpty()) return null;
		WifiDeviceSettingUserDTO user_dto = new WifiDeviceSettingUserDTO();
		user_dto.setName(WifiDeviceSettingUserDTO.Admin_Name);
		int index = users.indexOf(user_dto);
		if(index != -1){
			return users.get(index);
		}
		return null;
	}
	
	/**
	 * 获取ratecontorl的indexs
	 * @param rc_list
	 * @return
	 */
	public static List<Integer> getDeviceRateControlIndex(List<WifiDeviceSettingRateControlDTO> rc_list){
		if(rc_list == null || rc_list.isEmpty()) return null;
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
/*	public static String getCurrentDeviceUptime(WifiDevice device_entity){
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
	}*/
	
	//新版本设备定义
	//public static final String[] newOrigSwvers = new String[]{"1.2.8","1.2.9","1.2.10","1.2.11","1.2.12","1.2.13","1.2.14","1.2.15"};
	public static final String NewMinOrgiSwverVersion = "1.2.8";
	/**
	 * 根据设备的原始软件版本号 判断是否新版本设备
	 * @param orig_swver
	 * @return
	 */
	public static boolean isNewOrigSwverDevice(String orig_swver){
		if(StringUtils.isEmpty(orig_swver)) return false;
		
		try{
	    	Pattern p = Pattern.compile("V(.*)(B|r)");  
	    	Matcher m = p.matcher(orig_swver);
	    	String version = null;
	    	while(m.find()){  
	    		version = m.group(1);  
	    	}
	    	if(StringUtils.isEmpty(version)) return false;
	    	int ret = StringHelper.compareVersion(version, NewMinOrgiSwverVersion);
	    	return ret >= 0 ? true : false;
		}catch(Exception ex){
			//ex.printStackTrace(System.out);
		}
		return false;
//		for(String newOrigSwver : newOrigSwvers){
//			if(orig_swver.contains(newOrigSwver)){
//				return true;
//			}
//		}
//		return false;
	}
	
	/**
	 * 比较两个设备的软件版本号
	 * 返回 1 表示 orig_swver1 大于 orig_swver2
	 * 返回 0 表示 orig_swver1 等于 orig_swver2
	 * 返回 -1 表示 orig_swver1 小于 orig_swver2
	 * @param orig_swver1
	 * @param orig_swver2
	 * @return
	 */
	public static int compareDeviceVersions(String orig_swver1, String orig_swver2){
		if(StringUtils.isEmpty(orig_swver1) || StringUtils.isEmpty(orig_swver2)) 
			throw new RuntimeException("param validate empty");
		
		String[] orig_swver1_versions = parseDeviceSwverVersion(orig_swver1);
		if(orig_swver1_versions == null) return -1;
		String[] orig_swver2_versions = parseDeviceSwverVersion(orig_swver2);
		if(orig_swver2_versions == null) return 1;
		//判断大版本号
		int top_ret = StringHelper.compareVersion(orig_swver1_versions[0], orig_swver2_versions[0]);
		//System.out.println("top ret " + top_ret);
		if(top_ret != 0) return top_ret;
		
		//判断小版本号
		int bottom_ret = StringHelper.compareVersion(orig_swver1_versions[1], orig_swver2_versions[1]);
		//System.out.println("bottom ret " + bottom_ret);
		return bottom_ret;
	}
	
	/**
	 * 解析设备的软件版本
	 * 返回数组 0 大版本号 1 小版本号
	 * @param orig_swver
	 * @return
	 */
	public static String[] parseDeviceSwverVersion(String orig_swver){
		try{
	    	Pattern p = Pattern.compile("V(.*)(B|r)");
	    	Matcher m = p.matcher(orig_swver);
	    	String top_version = null;
	    	while(m.find()){  
	    		top_version = m.group(1);  
	    	}
	    	
	    	p = Pattern.compile("Build(\\d+)");
	    	m = p.matcher(orig_swver);
	    	String bottom_version = null;
	    	while(m.find()){  
	    		bottom_version = m.group(1);  
	    	}
	    	System.out.println(top_version + "-" + bottom_version);
	    	return new String[]{top_version, bottom_version};
		}catch(Exception ex){
			
		}
		return null;
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
			//2:验证vap是否都关联到此黑名单 只判断vap为ap模式的
			List<WifiDeviceSettingVapDTO> vap_dtos = dto.getVaps();
			if(vap_dtos != null && !vap_dtos.isEmpty()){
				for(WifiDeviceSettingVapDTO vap_dto : vap_dtos){
					if(!WifiDeviceSettingVapDTO.VapMode_AP.equals(vap_dto.getMode())){
						continue;
					}
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
				WifiDeviceSettingLinkModeDTO mode = source.getMode();
				if(mode != null){
					ReflectionHelper.copyProperties(source.getMode(), target.getMode());
				}
				//合并 vaps
				List<WifiDeviceSettingVapDTO> m_vaps = mergeList(source.getVaps(), target.getVaps());
				if(m_vaps != null){
					target.setVaps(m_vaps);
				}
				//合并黑白名单 由于黑名单删除方式不是ssdel的 所以特殊处理
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

	
	public static final String DeviceSetting_Portal_Outer = "<dev>".concat(DeviceSetting_ConfigSequenceInner).concat("%s</dev>");

	public static final String DeviceSetting_VapItem = "<ITEM name=\"%s\" radio=\"%s\" ssid=\"%s\" auth=\"%s\" enable=\"%s\" acl_type=\"%s\" acl_name=\"%s\" guest_en=\"%s\"/>";
	public static final String DeviceSetting_AclItem = "<ITEM name=\"%s\" macs=\"%s\" />";
	
	
	public static final String DeviceSetting_Start_HttpAdItem 	= "<ITEM bhu_id=\"%s\" bhu_ad_url=\"%s\" bhu_enable=\"%s\" />";
	public static final String DeviceSetting_Stop_HttpAdItem 	= "<ITEM bhu_enable=\"disable\" />";
	/*
	<ITEM bhu_http404_enable="enable" bhu_http404_url="" bhu_http_redirect_enable="enable"
		    bhu_http_redirect_rule="1,20:00:00,21:00:00,http://www.src1.com,http://www.dst1.com,http://src2.com,http://dst2.com ..."
		 />*/
	//public static final String DeviceSetting_Http404Item = "<ITEM bhu_http404_enable=\"%s\" bhu_http404_url=\"http://auth.wi2o.cn/404/\" bhu_http404_codes=\"404,502\"/>";
	public static final String DeviceSetting_Start_Http404Item 		= "<ITEM bhu_http404_enable=\"%s\" bhu_http404_url=\"%s\" bhu_http404_codes=\"40*,502\"/>";
	public static final String DeviceSetting_Stop_Http404Item 		= "<ITEM bhu_http404_enable=\"disable\"/>";
	public static final String DeviceSetting_Start_HttpRedirectItem = "<ITEM bhu_http_redirect_enable=\"%s\" bhu_http_redirect_rule=\"%s\"/>";
	public static final String DeviceSetting_Stop_HttpRedirectItem 	= "<ITEM bhu_http_redirect_enable=\"disable\"/>";
	
	public static final String DeviceSetting_Start_VapItem_Begin_Fragment 		= "<ITEM ";
	public static final String DeviceSetting_Start_HttpAdItem_Inner_Fragment 	= " bhu_id=\"%s\" bhu_ad_url=\"%s\" bhu_enable=\"%s\" ";
	public static final String DeviceSetting_Start_Http404Item_Inner_Fragment 	= " bhu_http404_enable=\"%s\" bhu_http404_url=\"%s\" bhu_http404_codes=\"%s\" ";
	public static final String DeviceSetting_Start_HttpRedirectItem_Inner_Fragment = " bhu_http_redirect_enable=\"%s\" bhu_http_redirect_rule=\"%s\" ";
	public static final String DeviceSetting_Start_VapItem_End_Fragment 	= " />";
	
	//mac type opt taskid
	public static final String DeviceSetting_VapModule_VapItem_Header_Fragment 	= "00001001%s0000000000000000100000012%s%s%s";
	public static final String DeviceSetting_VapModule_VapItem_Begin_Fragment 	= "<bhu_module>";
	public static final String DeviceSetting_VapModule_VapItem_End_Fragment 	= "</bhu_module>";
	public static final String DeviceSetting_VapModule_Start_Http404Item 		= "<http404><ITEM enable=\"%s\" url=\"%s\" codes=\"%s\" ver=\"%s\"/></http404>";
	public static final String DeviceSetting_VapModule_Stop_Http404Item 		= "<http404><ITEM enable=\"disable\"/></http404>";
	public static final String DeviceSetting_VapModule_Start_HttpRedirectItem 	= "<redirect><ITEM enable=\"%s\" rule=\"%s\" ver=\"%s\" /></redirect>";
	public static final String DeviceSetting_VapModule_Stop_HttpRedirectItem 	= "<redirect><ITEM enable=\"disable\"/></redirect>";
	public static final String DeviceSetting_VapModule_Upgrade = "<upgrade><ITEM url = “” retry_count=”” retry_interval=”” /></upgrade>";
	
	public static final String DeviceSetting_VapModuleFull_Stop = "<bhu_module>"+
							    "<channel>"+
							        "<ITEM enable=\"disable\"/>"+
							    "</channel>"+
							    "<brand>"+
						        	"<ITEM enable=\"disable\"/>"+
						        "</brand>"+							    
							    "<redirect>"+
							        "<ITEM enable=\"disable\"/>"+
							    "</redirect>"+
							    "<http404>"+
							        "<ITEM enable=\"disable\"/>"+
							    "</http404>"+
							"</bhu_module>";
	
	/*public static final String VapModule_Setting_MsgType = "00000003";
	public static final String VapModule_Query_MsgType = "00000004";*/
	
	//TODO:待完善
	//public static final String DeviceSetting_HttpPortalItem = "<ITEM bhu_id=\"%s\" bhu_ad_url=\"%s\" bhu_enable=\"%s\" />";
	public static final String DeviceSetting_Start_HttpPortalItem =  	
     "<net>"+
          "<interface>"+
               "<ITEM name=\"wlan3\" enable=\"enable\" if_tx_rate=\"512\" if_rx_rate=\"512\" />"+
          "</interface>"+
          "<bridge>"+
               "<ITEM name=\"br-lan\" complete_isolate_ports=\"wlan3\" />"+
          "</bridge>"+
          "<webportal>"+
               "<setting>"+
                    "<ITEM interface=\"br-lan,wlan3\" enable=\"enable\" auth_mode=\"local\" local_mode=\"answer\" block_mode=\"route\" "
                    + "extend_memory_enable=\"enable\" guest_portal_en=\"enable\"  progressbar_duration=\"0\" get_portal_method=\"Remote Get\"  manage_server=\"disable\" "
                    + "redirect_url=\"%s\"  max_clients=\"256\" idle_timeout=\"%s\" force_timeout=\"%s\" open_resource=\"%s\" />"+
               "</setting>"+
               "<users>"+
                    "<ITEM id=\"1\" username=\"%s\" password=\"%s\" auth_mode=\"4\" share=\"enable\"/>"+
               "</users>"+
          "</webportal>"+
     "</net>"+
     "<wifi><vap><ITEM name=\"wlan3\" ssid=\"%s\" guest_en=\"enable\" isolation=\"7\" /></vap></wifi>"+
     "<sys><manage><plugin><ITEM guest=\"enable\" /></plugin></manage></sys>";

	public static final String DeviceSetting_Stop_HttpPortalItem =
     "<net>"+
          "<interface><ITEM name=\"wlan3\" enable=\"disable\" /></interface>"+
          "<webportal><setting><ITEM  enable=\"disable\"  /></setting></webportal>"+
     "</net>"+
     "<wifi>"+
          "<vap><ITEM name=\"wlan3\" guest_en=\"disable\" isolation=\"7\" /></vap>"+
     "</wifi>"+
     "<sys><manage><plugin><ITEM guest=\"disable\" /></plugin></manage></sys>";
	
	public static final String DeviceSetting_RadioItem_Power = "<ITEM name=\"%s\" power=\"%s\" />";
	public static final String DeviceSetting_RadioItem_RealChannel = "<ITEM name=\"%s\" channel=\"%s\" />";
	
	public static final String DeviceSetting_VapPasswordItem = "<ITEM name=\"%s\" ssid=\"%s\" auth=\"%s\" auth_key=\"%s\" auth_key_rsa=\"%s\"/>";
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
	
	
	/*public static String autoBuilderDSOuter(String config_sequence, String extparams, WifiDeviceSettingDTO ds_dto){
		ParamVapAdDTO pad_dto = JsonHelper.getDTO(extparams, ParamVapAdDTO.class);
		if(pad_dto == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		//WifiDeviceSettingVapAdDTO ad_dto = new WifiDeviceSettingVapAdDTO();
		String item = builderDeviceSettingItemWithDto(DeviceSetting_Start_HttpAdItem, WifiDeviceSettingVapAdDTO.fromParamVapAdDTO(pad_dto));
		return builderDeviceSettingOuter(DeviceSetting_AdOuter, config_sequence, item);
	}*/
	
	public static String builderDSHttpVapSettinStartOuter(String config_sequence, String innerPayload){
		StringBuilder payload = new StringBuilder();
		payload.append(DeviceSetting_Start_VapItem_Begin_Fragment).append(innerPayload).append(DeviceSetting_Start_VapItem_End_Fragment);
		return builderDeviceSettingOuter(DeviceSetting_AdOuter, config_sequence, payload.toString());
	}
	
	public static String builderDSHttpAdStartFragmentOuter(String extparams){
		ParamVapAdDTO pad_dto = JsonHelper.getDTO(extparams, ParamVapAdDTO.class);
		if(pad_dto == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		return builderDeviceSettingItemWithDto(DeviceSetting_Start_HttpAdItem_Inner_Fragment, WifiDeviceSettingVapAdDTO.fromParamVapAdDTO(pad_dto));
	}
	public static String builderDSHttpRedirectStartFragmentOuter(String extparams){
		ParamVapHttpRedirectDTO pad_dto = JsonHelper.getDTO(extparams, ParamVapHttpRedirectDTO.class);
		if(pad_dto == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		return  builderDeviceSettingItemWithDto(DeviceSetting_Start_HttpRedirectItem_Inner_Fragment, WifiDeviceSettingVapHttpRedirectDTO.fromParamVapAdDTO(pad_dto));
	}
	
	public static String builderDSHttp404StartFragmentOuter(String extparams){
		//WifiDeviceSettingVapHttp404DTO ad_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingVapHttp404DTO.class);
		ParamVapHttp404DTO ad_dto = JsonHelper.getDTO(extparams, ParamVapHttp404DTO.class);
		if(ad_dto == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		return builderDeviceSettingItemWithDto(DeviceSetting_Start_Http404Item_Inner_Fragment, WifiDeviceSettingVapHttp404DTO.fromParamVapAdDTO(ad_dto));
	}
	
	/**
	 * 构建广告配置数据
	 * @param config_sequence
	 * @param extparams 页面传递过来的值构建ParamVapAdDTO
	 * @param ds_dto
	 * @return throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
	 */
	public static String builderDSHttpAdStartOuter(String config_sequence, String extparams){
		ParamVapAdDTO pad_dto = JsonHelper.getDTO(extparams, ParamVapAdDTO.class);
		if(pad_dto == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		//WifiDeviceSettingVapAdDTO ad_dto = new WifiDeviceSettingVapAdDTO();
		String item = builderDeviceSettingItemWithDto(DeviceSetting_Start_HttpAdItem, WifiDeviceSettingVapAdDTO.fromParamVapAdDTO(pad_dto));
		return builderDeviceSettingOuter(DeviceSetting_AdOuter, config_sequence, item);
	}
	
	public static String builderDSHttpAdStopOuter(String config_sequence){
		return builderDeviceSettingOuter(DeviceSetting_AdOuter, config_sequence, DeviceSetting_Stop_HttpAdItem);
	}
	
	public static String builderDSHttpRedirectStartOuter(String config_sequence, String extparams){
		ParamVapHttpRedirectDTO pad_dto = JsonHelper.getDTO(extparams, ParamVapHttpRedirectDTO.class);
		//WifiDeviceSettingVapHttpRedirectDTO ad_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingVapHttpRedirectDTO.class);
		if(pad_dto == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		String item = builderDeviceSettingItemWithDto(DeviceSetting_Start_HttpRedirectItem, WifiDeviceSettingVapHttpRedirectDTO.fromParamVapAdDTO(pad_dto));
		return builderDeviceSettingOuter(DeviceSetting_AdOuter, config_sequence, item);
	}
	
	public static String builderDSHttpRedirectStopOuter(String config_sequence){
		return builderDeviceSettingOuter(DeviceSetting_AdOuter, config_sequence, DeviceSetting_Stop_HttpRedirectItem);
	}
	
	public static String builderDSHttp404StartOuter(String config_sequence, String extparams){
		//WifiDeviceSettingVapHttp404DTO ad_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingVapHttp404DTO.class);
		ParamVapHttp404DTO ad_dto = JsonHelper.getDTO(extparams, ParamVapHttp404DTO.class);
		if(ad_dto == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		String item = builderDeviceSettingItemWithDto(DeviceSetting_Start_Http404Item, WifiDeviceSettingVapHttp404DTO.fromParamVapAdDTO(ad_dto));
		return builderDeviceSettingOuter(DeviceSetting_AdOuter, config_sequence, item);
	}
	public static String builderDSHttp404StopOuter(String config_sequence){
		return builderDeviceSettingOuter(DeviceSetting_AdOuter, config_sequence, DeviceSetting_Stop_Http404Item);
	}
	
	public static String builderDSStartHttpPortalOuter(String config_sequence, String extparams){
		ParamVapHttpPortalDTO ad_dto = JsonHelper.getDTO(extparams, ParamVapHttpPortalDTO.class);
		//WifiDeviceSettingVapHttpPortalDTO ad_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingVapHttpPortalDTO.class);
		if(ad_dto == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		String item = builderDeviceSettingItemWithDto(DeviceSetting_Start_HttpPortalItem, WifiDeviceSettingVapHttpPortalDTO.fromParamVapAdDTO(ad_dto));
		return builderDeviceSettingOuter(DeviceSetting_Portal_Outer, config_sequence, item);
	}
	
	public static String builderDSStopHttpPortalOuter(String config_sequence){
		//ParamVapHttpPortalDTO ad_dto = JsonHelper.getDTO(extparams, ParamVapHttpPortalDTO.class);
		//WifiDeviceSettingVapHttpPortalDTO ad_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingVapHttpPortalDTO.class);
		//if(ad_dto == null)
		//	throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		//String item = builderDeviceSettingItemWithDto(DeviceSetting_Stop_HttpPortalItem, WifiDeviceSettingVapHttpPortalDTO.fromParamVapAdDTO(ad_dto));
		return builderDeviceSettingOuter(DeviceSetting_Portal_Outer, config_sequence, DeviceSetting_Stop_HttpPortalItem);
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
		if(radio_dto == null || StringUtils.isEmpty(radio_dto.getPower()) || 
				Integer.parseInt(radio_dto.getPower()) < 0)
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
		//String item = builderDeviceSettingItemWithDto(DeviceSetting_RadioItem_Power, radio_dto);
		String item = builderDeviceSettingItem(DeviceSetting_RadioItem_Power, 
				radio_dto.builderProperties(WifiDeviceSettingRadioDTO.MODEL_Power_Radio));
		return builderDeviceSettingOuter(DeviceSetting_RadioOuter, config_sequence, item);
	}
	
	
	private static final String[] optionalChannel4URouter = {"1","6","11"};
	public static String builderDSRealChannelOuter(String config_sequence, String extparams, WifiDeviceSettingDTO ds_dto){
		WifiDeviceSettingRadioDTO radio_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingRadioDTO.class);
		if(radio_dto == null /*|| StringUtils.isEmpty(radio_dto.getReal_channel()) || 
				Integer.parseInt(radio_dto.getReal_channel()) < 0*/)
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
			if(StringUtils.isEmpty(radio_dto.getReal_channel())){
				String old_real_channel = frist_radio_dto.getReal_channel();
				Set<String> optionals = ArrayHelper.toSet(optionalChannel4URouter);
				if(StringUtils.isNotEmpty(old_real_channel)){
					optionals.remove(old_real_channel);
				}
				radio_dto.setReal_channel(RandomPicker.pick(optionals));
			}
			radio_dto.setName(frist_radio_dto.getName());
		}
		//String item = builderDeviceSettingItemWithDto(DeviceSetting_RadioItem_RealChannel, radio_dto);
		String item = builderDeviceSettingItem(DeviceSetting_RadioItem_RealChannel, 
				radio_dto.builderProperties(WifiDeviceSettingRadioDTO.MODEL_RealChannel_Radio));
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

		String auth_key = vap_dto.getAuth_key();
		//如果没有指定vap的具体名称 则获取默认第一个非访客的vap进行修改
		if(StringUtils.isEmpty(vap_dto.getName())){
			WifiDeviceSettingVapDTO frist_vap_dto = getUrouterDeviceVap(ds_dto);
			//如果没有一个可用的vap
			if(frist_vap_dto == null) 
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_ERROR);
			vap_dto.setName(frist_vap_dto.getName());
			vap_dto.setAuth_key(auth_key);
			vap_dto.setAuth_key_rsa(JNIRsaHelper.jniRsaEncryptHexStr(auth_key));
		}
		String item = builderDeviceSettingItem(DeviceSetting_VapPasswordItem, 
				vap_dto.builderProperties(WifiDeviceSettingVapDTO.BuilderType_VapPassword));
		return builderDeviceSettingOuter(DeviceSetting_VapOuter, config_sequence, item);
	}
	
//	public static String builderDSVapGuestOuter(String config_sequence, String extparams, WifiDeviceSettingDTO ds_dto){
////		WifiDeviceSettingVapDTO vap_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingVapDTO.class);
////		if(vap_dto == null)
////			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
//		
//		String item = "<ITEM name=\"wlan2\" \"guest_en\":\"enable\" \"enable\":\"enable\"/>";
//		return builderDeviceSettingOuter(DeviceSetting_VapOuter, config_sequence, item);
//	}
	
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
			default_acl_dto = new WifiDeviceSettingAclDTO(WifiDeviceSettingDTO.Default_AclName);
		
		WifiDeviceSettingAclDTO acl_incr_dto = acl_dto_map.get(DeviceSettingAction_Incr);
		List<String> incr_macs = null;
		if(acl_incr_dto != null){
			incr_macs = acl_incr_dto.getMacs();
			if(incr_macs != null && !incr_macs.isEmpty()){
				if(!StringHelper.isValidMacs(incr_macs)){
					throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
				}	
			}
		}
			
		WifiDeviceSettingAclDTO acl_del_dto = acl_dto_map.get(DeviceSettingAction_Del);
		List<String> del_macs = null;
		if(acl_del_dto != null){
			del_macs = acl_del_dto.getMacs();
			if(del_macs != null && !del_macs.isEmpty()){
				if(!StringHelper.isValidMacs(del_macs)){
					throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
				}
			}
		}
		
		if((incr_macs == null || incr_macs.isEmpty()) && (del_macs == null || del_macs.isEmpty()))
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_MODIFY_VALIDATE_ILEGAL);
		
		Set<String> macs = new HashSet<String>();
		if(default_acl_dto.getMacs() != null){
			macs.addAll(default_acl_dto.getMacs());
		}
		if(incr_macs != null && !incr_macs.isEmpty()){
			macs.addAll(incr_macs);
		}
		if(del_macs != null && !del_macs.isEmpty()){
			macs.removeAll(del_macs);
		}
		//验证黑名单数量 一个黑名单列表最多256个
		if(WifiDeviceSettingAclDTO.Max_MemberCount < macs.size()){
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_ACLMAC_MAX_MEMBER);
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
		Map<String, List<RateControlParamDTO>> rc_dto_map = JsonHelper.getDTOMapKeyList(extparams, RateControlParamDTO.class);
		if(rc_dto_map == null || rc_dto_map.isEmpty())
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		
		StringBuffer ds = new StringBuffer();
		//此次修改产生的数量变化 增加或减少的数量
		int rc_changed_count = 0;
		//当前的限速的成员
		List<WifiDeviceSettingRateControlDTO> rc_current_dtos = ds_dto.getRatecontrols();
		
		List<RateControlParamDTO> rc_incr_dtos = rc_dto_map.get(DeviceSettingAction_Incr);
		if(rc_incr_dtos != null && !rc_incr_dtos.isEmpty()){
			List<Integer> rc_indexs = getDeviceRateControlIndex(rc_current_dtos);
			if(rc_indexs == null)
				rc_indexs = new ArrayList<Integer>();
			
			for(RateControlParamDTO rc_incr_dto : rc_incr_dtos){
				//验证限速数值是否合法
				if(StringUtils.isEmpty(rc_incr_dto.getTm_rx()) || StringUtils.isEmpty(rc_incr_dto.getTm_tx())
						|| !StringUtils.isNumeric(rc_incr_dto.getTm_rx()) || !StringUtils.isNumeric(rc_incr_dto.getTm_tx())){
					continue;
				}
					
				WifiDeviceSettingRateControlDTO match_rc_dto = matchRateControl(ds_dto, rc_incr_dto.getMac());
				//如果匹配到 说明是修改
				if(match_rc_dto != null){
					//rc_incr_dto.setIndex(match_rc_dto.getIndex());
					match_rc_dto.setTx(rc_incr_dto.getTm_rx());
					match_rc_dto.setRx(rc_incr_dto.getTm_tx());
				}
				//没匹配到 说明是新增 获取新的index
				else{
					int index = ArrayHelper.getMinOrderNumberVacant(rc_indexs);
					match_rc_dto = new WifiDeviceSettingRateControlDTO();
					match_rc_dto.setMac(rc_incr_dto.getMac());
					match_rc_dto.setIndex(String.valueOf(index));
					match_rc_dto.setTx(rc_incr_dto.getTm_rx());
					match_rc_dto.setRx(rc_incr_dto.getTm_tx());
					rc_indexs.add(index);
					rc_changed_count++;
				}
				ds.append(builderDeviceSettingItem(DeviceSetting_RatecontrolItem, match_rc_dto.builderProperties()));
			}
		}
				
		List<RateControlParamDTO> rc_del_dtos = rc_dto_map.get(DeviceSettingAction_Del);
		if(rc_del_dtos != null && !rc_del_dtos.isEmpty()){
			for(RateControlParamDTO rc_del_dto : rc_del_dtos){
				WifiDeviceSettingRateControlDTO match_rc_dto = matchRateControl(ds_dto, rc_del_dto.getMac());
				if(match_rc_dto != null){
					//rc_del_dto.setIndex(match_rc_dto.getIndex());
					ds.append(builderDeviceSettingItem(DeviceSetting_RemoveRatecontrolItem, match_rc_dto.
							builderProperties(WifiDeviceSettingRateControlDTO.BuilderType_RemoveRC)));
					rc_changed_count--;
				}
			}
		}
				
		if(ds.length() == 0){
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_MODIFY_VALIDATE_ILEGAL);
		}
		
		//验证指定mac限速最多不能超过128个
		int rc_current_count = 0;
		if(rc_current_dtos != null)
			rc_current_count = rc_current_dtos.size();
		
		if(WifiDeviceSettingRateControlDTO.Max_MemberCount < (rc_current_count + rc_changed_count)){
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_RATECONTROL_MAX_MEMBER);
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
	public static String builderDSAdminPasswordOuter(String config_sequence, String extparams)
			throws Exception {
		WifiDeviceSettingUserDTO user_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingUserDTO.class);
		if(user_dto == null || StringUtils.isEmpty(user_dto.getPassword())){
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		}
//		if(StringUtils.isEmpty(user_dto.getOldpassword()) || StringUtils.isEmpty(user_dto.getPassword())){
//			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
//		}

//		user_dto.setOldpassword(RSAHelper.encryptToAp(user_dto.getOldpassword(), RuntimeConfiguration.BHUDeviceRSAPublicKey));
		//user_dto.setPassword(RSAHelper.encryptToAp(user_dto.getPassword(), RuntimeConfiguration.BHUDeviceRSAPublicKey));
		user_dto.setPassword(JNIRsaHelper.jniRsaEncryptHexStr(user_dto.getPassword()));
		
		String item = builderDeviceSettingItem(DeviceSetting_AdminPasswordItem, user_dto.builderProperties());
		return builderDeviceSettingOuter(DeviceSetting_AdminPasswordOuter, config_sequence, item);
	}

	/**
	 * 修改上网方式配置()
	 * @param config_sequence
	 * @param extparams
	 * @return
	 */
	public static String builderDSLinkModeOuter(String config_sequence, String extparams) throws Exception {
		if(!StringUtils.isEmpty(config_sequence) && !StringUtils.isEmpty(extparams)){
			WifiDeviceSettingLinkModeDTO linkModelDTO = JsonHelper.getDTO(extparams, WifiDeviceSettingLinkModeDTO.class);
			if (linkModelDTO != null) {
				if (WifiDeviceSettingDTO.Mode_Pppoe.equals(linkModelDTO.getModel())){
					//linkModelDTO.setPassword_rsa(RSAHelper.encryptToAp(linkModelDTO.getPassword_rsa(), RuntimeConfiguration.BHUDeviceRSAPublicKey));
					linkModelDTO.setPassword_rsa(JNIRsaHelper.jniRsaEncryptHexStr(linkModelDTO.getPassword_rsa()));
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
		Map<String, List<WifiDeviceSettingMMDTO>> mm_dto_map = JsonHelper.getDTOMapKeyList(extparams, WifiDeviceSettingMMDTO.class);
		if(mm_dto_map == null || mm_dto_map.isEmpty())
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);

		StringBuffer ds = new StringBuffer();


		//todo(bluesand): 新增别名
		List<WifiDeviceSettingMMDTO> mm_incr_dtos = mm_dto_map.get(DeviceSettingAction_Incr);
		if(mm_incr_dtos != null && !mm_incr_dtos.isEmpty()){
			for(WifiDeviceSettingMMDTO mm_incr_dto : mm_incr_dtos){
				if(StringUtils.isEmpty(mm_incr_dto.getMac()) || StringUtils.isEmpty(mm_incr_dto.getName())){
					throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
				}
				if (mm_incr_dto.getName().getBytes("utf8").length > 32) {
					throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_LENGTH_ILEGAL);
				}
				ds.append(builderDeviceSettingItem(DeviceSetting_MMItem, mm_incr_dto.builderProperties()));
			}
		}



		List<WifiDeviceSettingMMDTO> mm_del_dtos = mm_dto_map.get(DeviceSettingAction_Del);
		if(mm_del_dtos != null && !mm_del_dtos.isEmpty()){
			for(WifiDeviceSettingMMDTO mm_del_dto : mm_del_dtos){
				if(StringUtils.isEmpty(mm_del_dto.getMac())){
					throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
				}
				//如果存在才可以删除
				if(matchMacManagement(ds_dto, mm_del_dto.getMac()) != null){
					mm_del_dto.setSsdel(DeviceSettingBuilderDTO.Removed);
					ds.append(builderDeviceSettingItem(DeviceSetting_RemoveMMItem, mm_del_dto.
							builderProperties(WifiDeviceSettingMMDTO.BuilderType_RemoveMM)));
				}
			}
		}

		if(ds.length() == 0){
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_MODIFY_VALIDATE_ILEGAL);
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
		
		System.out.println(StringUtils.isNumeric(""));
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


		String extparams = "{\"ssid\":\"urouter_admin\",\"auth\":\"WPA/WPA2-PSK\",\"auth_key\":\"12345678999\"}";
		WifiDeviceSettingVapDTO vap_dto = JsonHelper.getDTO(extparams, WifiDeviceSettingVapDTO.class);
		if(vap_dto == null)
			throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);

		String auth_key = vap_dto.getAuth_key();
		//如果没有指定vap的具体名称 则获取默认第一个非访客的vap进行修改
		if(StringUtils.isEmpty(vap_dto.getName())){

			String old = "{\"name\":\"wlan0\",\"radio\":\"wifi0\",\"ssid\":\"urouter_admin\",\"auth\":\"ttt\",\"enable\":\"enable\",\"acl_type\":\"deny\",\"acl_name\":\"blackList\",\"guest_en\":\"disable\",\"auth_key\":null,\"auth_key_rsa\":\"14e61a755a682c17d24fb11844864c4bee67861fbd226493f9230734b14b75945b28e072cb7c0587f87094f19452786059b5140fcdc9faf1e08f3fe4597ebdef\"}";

			WifiDeviceSettingVapDTO frist_vap_dto = JsonHelper.getDTO(old, WifiDeviceSettingVapDTO.class);
			//如果没有一个可用的vap
			if(frist_vap_dto == null)
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_ERROR);
			vap_dto.setName(frist_vap_dto.getName());
			vap_dto.setAuth_key(auth_key);
			vap_dto.setAuth_key_rsa("1234567890");
		}
		String item = builderDeviceSettingItem(DeviceSetting_VapPasswordItem,
				vap_dto.builderProperties(WifiDeviceSettingVapDTO.BuilderType_VapPassword));


		System.out.println(item);
		
		
		System.out.println(isNewOrigSwverDevice("AP104P06V1.2.12r2"));
		
		parseDeviceSwverVersion("AP106P06V1.2.15BuildYt");
		compareDeviceVersions("AP106P06V1.2.16Build8057", "AP106P06V1.2.15Build8057");
	}

}
