package com.bhu.vas.business.search.model;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.helper.VapEnumType.DeviceUnitType;
import com.bhu.vas.api.helper.VapEnumType.GrayLevel;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.api.rpc.agent.model.AgentDeviceClaim;
import com.bhu.vas.api.rpc.devices.dto.DeviceVersion;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGray;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceModule;
import com.bhu.vas.api.rpc.user.model.User;
import com.smartwork.msip.cores.helper.DateTimeHelper;


public class WifiDeviceDocumentHelper {
//	public static WifiDeviceDocument fromWifiDevice(WifiDevice wifiDevice,WifiDeviceModule deviceModule,List<Long> groupids){
//		WifiDeviceDocument doc1 = new WifiDeviceDocument();
//		doc1.setId(wifiDevice.getId());
//		doc1.setSn(wifiDevice.getSn());
//		doc1.setAddress(wifiDevice.getFormatted_address());
//		doc1.setOnline(wifiDevice.isOnline()?Boolean.TRUE:Boolean.FALSE);
//		if(deviceModule != null){
//			doc1.setModuleonline(deviceModule.isModule_online()?Boolean.TRUE:Boolean.FALSE);
//			doc1.setOrigvapmodule(deviceModule.getOrig_vap_module());
//		}else{
//			doc1.setModuleonline(false);
//			doc1.setOrigvapmodule(null);
//		}
//		
//		doc1.setConfigmodel(wifiDevice.getConfig_mode());
//		doc1.setWorkmodel(wifiDevice.getWork_mode());
//		doc1.setOrigswver(wifiDevice.getOrig_swver());
//		
//		doc1.setDevicetype(wifiDevice.getHdtype());
//		
//		doc1.setNvd(DeviceHelper.isNewOrigSwverDevice(wifiDevice.getOrig_swver()) ? 1 : 0);
//		if(StringUtils.isNotEmpty(wifiDevice.getLon()) && StringUtils.isNotEmpty(wifiDevice.getLat())){
//			doc1.setGeopoint(new double[]{Double.parseDouble(wifiDevice.getLon()),Double.parseDouble(wifiDevice.getLat())});
//		}
//		if(groupids != null && !groupids.isEmpty())
//			doc1.setGroups(ArrayHelper.toSplitString(groupids, StringHelper.WHITESPACE_STRING_GAP));
//		doc1.setRegisteredat(wifiDevice.getCreated_at().getTime());
//		doc1.setUpdatedat(DateTimeHelper.getDateTime());
//		return doc1;
//	}
	
	/**
	 * 创建上过线的设备的索引数据
	 * @param wifiDevice
	 * @param deviceModule
	 * @param agentDeviceClaim
	 * @param wifiDeviceGray
	 * @param bindUser
	 * @param bindUserDNick 用户绑定的设备的昵称
	 * @param agentUser
	 * @param hoc
	 * @return
	 */
	public static WifiDeviceDocument fromNormalWifiDevice(WifiDevice wifiDevice, WifiDeviceModule deviceModule,
			AgentDeviceClaim agentDeviceClaim, WifiDeviceGray wifiDeviceGray, User bindUser, String bindUserDNick, 
			User agentUser, int hoc){
		if(wifiDevice == null) return null;
		
		WifiDeviceDocument doc = new WifiDeviceDocument();
		if(wifiDevice != null){
			doc.setId(wifiDevice.getId());
			doc.setD_mac(wifiDevice.getId());
			doc.setD_sn(wifiDevice.getSn());
			doc.setD_origswver(wifiDevice.getOrig_swver());
			doc.setD_workmodel(wifiDevice.getWork_mode());
			doc.setD_configmodel(wifiDevice.getConfig_mode());
			doc.setD_type(wifiDevice.getHdtype());
			if(StringUtils.isNotEmpty(wifiDevice.getLon()) && StringUtils.isNotEmpty(wifiDevice.getLat())){
				doc.setD_geopoint(new double[]{Double.parseDouble(wifiDevice.getLon()), 
						Double.parseDouble(wifiDevice.getLat())});
			}
			doc.setD_address(wifiDevice.getFormatted_address());
			doc.setD_online(wifiDevice.isOnline() ? WifiDeviceDocumentEnumType.OnlineEnum.Online.getType()
					: WifiDeviceDocumentEnumType.OnlineEnum.Offline.getType());
			if(wifiDevice.getLast_reged_at() != null){
				doc.setD_lastregedat(wifiDevice.getLast_reged_at().getTime());
			}
			if(wifiDevice.getLast_logout_at() != null){
				doc.setD_lastlogoutat(wifiDevice.getLast_logout_at().getTime());
			}
			if(wifiDevice.getCreated_at() != null){
				doc.setD_createdat(wifiDevice.getCreated_at().getTime());
			}
			DeviceVersion parser = DeviceVersion.parser(wifiDevice.getOrig_swver());
			if(parser != null){
				String dut = parser.getDut();
				doc.setD_dut(dut);
				DeviceUnitType deviceUnitType = VapEnumType.DeviceUnitType.fromHdType(dut, wifiDevice.getHdtype());
				if(deviceUnitType != null){
					doc.setD_type_sname(deviceUnitType.getSname());
				}
			}
			
			/*if(DeviceUnitType.isSocHdType(wifiDevice.getHdtype())){
				doc.setD_dut(DeviceVersion.DUT_soc);
			}else if(DeviceUnitType.isURouterHdType(wifiDevice.getHdtype())){
				doc.setD_dut(DeviceVersion.DUT_uRouter);
			}else{
				doc.setD_dut(DeviceVersion.DUT_CWifi);
			}*/
			if(!StringUtils.isEmpty(wifiDevice.getUptime())){
				doc.setD_uptime(wifiDevice.getUptime());
			}
		}

		if(deviceModule != null){
			String orig_vap_module = deviceModule.getOrig_vap_module();
			if(!StringUtils.isEmpty(orig_vap_module)){
				doc.setD_origvapmodule(orig_vap_module);
				doc.setO_operate(WifiDeviceDocumentEnumType.OperateEnum.Operate.getType());
			}
			doc.setD_monline(deviceModule.isModule_online() ? WifiDeviceDocumentEnumType.MOnlineEnum.MOnline.getType() 
					: WifiDeviceDocumentEnumType.MOnlineEnum.MOffline.getType());
		}
		
		if(agentDeviceClaim != null){
			if(agentDeviceClaim.getImport_id() > 0){
				doc.setO_batch(String.valueOf(agentDeviceClaim.getImport_id()));
			}
		}
		
		if(wifiDeviceGray != null){
			doc.setO_graylevel(String.valueOf(wifiDeviceGray.getGl()));
		}else{
			doc.setO_graylevel(String.valueOf(GrayLevel.Other.getIndex()));
		}
		//TODO:doc.setO_template(o_template);
		if(bindUser != null){
			doc.setU_binded(WifiDeviceDocumentEnumType.UBindedEnum.UBinded.getType());
			doc.setU_id(String.valueOf(bindUser.getId()));
			doc.setU_nick(bindUser.getNick());
			doc.setU_mno(bindUser.getMobileno());
			doc.setU_mcc(String.valueOf(bindUser.getCountrycode()));
			doc.setU_type(String.valueOf(bindUser.getUtype()));
			doc.setU_dnick(bindUserDNick);
		}
		
		if(agentUser != null){
			doc.setA_id(String.valueOf(agentUser.getId()));
			doc.setA_nick(agentUser.getNick());
			doc.setA_org(agentUser.getOrg());
		}
		doc.setD_hoc(hoc);
		doc.setUpdatedat(DateTimeHelper.getDateTime());
		return doc;
	}
	
	/**
	 * 创建在导入的确认的设备数据
	 * @param agentDeviceClaim
	 * @return
	 */
	@Deprecated
	public static WifiDeviceDocument fromClaimWifiDevice(AgentDeviceClaim agentDeviceClaim){
		if(agentDeviceClaim == null) return null;
		
		WifiDeviceDocument doc = new WifiDeviceDocument();
		if(agentDeviceClaim != null){
			doc.setId(agentDeviceClaim.getMac());
			doc.setD_mac(agentDeviceClaim.getMac());
			doc.setD_sn(agentDeviceClaim.getId());
			//doc.setD_type(agentDeviceClaim.getHdtype());
			doc.setD_online(WifiDeviceDocumentEnumType.OnlineEnum.NeverOnline.getType());
			doc.setD_monline(WifiDeviceDocumentEnumType.MOnlineEnum.MNeverOnline.getType());
			
			String[] parserHdtypes = VapEnumType.DeviceUnitType.parserIndex(agentDeviceClaim.getHdtype());
			if(parserHdtypes != null && parserHdtypes.length == 2){
				String dut = parserHdtypes[0];
				String hdtype = parserHdtypes[1];
				if(!StringUtils.isEmpty(hdtype)){
					doc.setD_type(hdtype);
					DeviceUnitType deviceUnitType = VapEnumType.DeviceUnitType.fromHdType(dut, hdtype);
					if(deviceUnitType != null){
						doc.setD_type_sname(deviceUnitType.getSname());
					}
				}
			}
			
			if(agentDeviceClaim.getSold_at() != null){
				doc.setD_createdat(agentDeviceClaim.getSold_at().getTime());
			}
			
			if(agentDeviceClaim.getImport_id() > 0){
				doc.setO_batch(String.valueOf(agentDeviceClaim.getImport_id()));
			}
		}
		doc.setD_hoc(0);
		doc.setUpdatedat(DateTimeHelper.getDateTime());
		return doc;
	}
	
//	public static WifiDeviceVTO toWifiDeviceVTO(WifiDeviceDocument doc,WifiDevice wifiDevice){
//		WifiDeviceVTO vto = new WifiDeviceVTO();
//		if(doc != null){
//			vto.setWid(doc.getId());
//			vto.setOl(doc.getOnline()?1:0);
//			if(doc.getModuleonline() != null){
//				vto.setMol(doc.getModuleonline()?1:0);
//			}else{
//				vto.setMol(0);
//			}
//			vto.setCohc(doc.getCount());
//			vto.setAdr(doc.getAddress());
//			vto.setDt(doc.getDevicetype());
//			vto.setOsv(doc.getOrigswver());
//			vto.setOsm(doc.getOrigvapmodule());
//			vto.setGids(doc.getGroups());
//		}
//		if(wifiDevice != null){
//			vto.setOm(StringUtils.isEmpty(wifiDevice.getOem_model()) ? wifiDevice.getOrig_model() : wifiDevice.getOem_model());
//			vto.setWm(wifiDevice.getWork_mode());
//			vto.setCfm(wifiDevice.getConfig_mode());
//			vto.setRts(wifiDevice.getLast_reged_at().getTime());
//			vto.setCts(wifiDevice.getCreated_at().getTime());
//			vto.setOvd(StringUtils.isEmpty(wifiDevice.getOem_vendor()) ? wifiDevice.getOrig_vendor() : wifiDevice.getOem_vendor());
//			vto.setOesv(wifiDevice.getOem_swver());
//			vto.setDof(StringUtils.isEmpty(wifiDevice.getRx_bytes()) ? 0 : Long.parseLong(wifiDevice.getRx_bytes()));
//			vto.setUof(StringUtils.isEmpty(wifiDevice.getTx_bytes()) ? 0 : Long.parseLong(wifiDevice.getTx_bytes()));
//			vto.setIpgen(wifiDevice.isIpgen());
//			vto.setSn(wifiDevice.getSn());
//			//如果是离线 计算离线时间
//			if(vto.getOl() == 0){
//				long logout_ts = wifiDevice.getLast_logout_at().getTime();
//				vto.setOfts(logout_ts);
//				vto.setOftd(System.currentTimeMillis() - logout_ts);
//			}
//		}
//		return vto;
//	}
}
