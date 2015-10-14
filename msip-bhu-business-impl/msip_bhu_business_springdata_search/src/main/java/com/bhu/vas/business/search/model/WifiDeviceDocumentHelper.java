package com.bhu.vas.business.search.model;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;


public class WifiDeviceDocumentHelper {
	public static WifiDeviceDocument fromWifiDevice(WifiDevice wifiDevice,List<Long> groupids){
		WifiDeviceDocument doc1 = new WifiDeviceDocument();
		doc1.setId(wifiDevice.getId());
		doc1.setSn(wifiDevice.getSn());
		doc1.setAddress(wifiDevice.getFormatted_address());
		doc1.setOnline(wifiDevice.isOnline()?Boolean.TRUE:Boolean.FALSE);
		doc1.setModuleonline(wifiDevice.isModule_online()?Boolean.TRUE:Boolean.FALSE);
		doc1.setConfigmodel(wifiDevice.getConfig_mode());
		doc1.setWorkmodel(wifiDevice.getWork_mode());
		doc1.setOrigswver(wifiDevice.getOrig_swver());
		doc1.setOrigvapmodule(wifiDevice.getOrig_vap_module());
		doc1.setDevicetype(wifiDevice.getHdtype());
		doc1.setNvd(DeviceHelper.isNewOrigSwverDevice(wifiDevice.getOrig_swver()) ? 1 : 0);
		if(StringUtils.isNotEmpty(wifiDevice.getLon()) && StringUtils.isNotEmpty(wifiDevice.getLat())){
			doc1.setGeopoint(new double[]{Double.parseDouble(wifiDevice.getLon()),Double.parseDouble(wifiDevice.getLat())});
		}
		if(groupids != null && !groupids.isEmpty())
			doc1.setGroups(ArrayHelper.toSplitString(groupids, StringHelper.WHITESPACE_STRING_GAP));
		doc1.setRegisteredat(wifiDevice.getCreated_at().getTime());
		doc1.setUpdatedat(DateTimeHelper.getDateTime());
		return doc1;
	}
	
	
	public static WifiDeviceVTO toWifiDeviceVTO(WifiDeviceDocument doc,WifiDevice wifiDevice){
		WifiDeviceVTO vto = new WifiDeviceVTO();
		if(doc != null){
			vto.setWid(doc.getId());
			vto.setOl(doc.getOnline()?1:0);
			if(doc.getModuleonline() != null){
				vto.setMol(doc.getModuleonline()?1:0);
			}else{
				vto.setMol(0);
			}
			vto.setCohc(doc.getCount());
			vto.setAdr(doc.getAddress());
			vto.setDt(doc.getDevicetype());
			vto.setOsv(doc.getOrigswver());
			vto.setOsm(doc.getOrigvapmodule());
			vto.setGids(doc.getGroups());
		}
		if(wifiDevice != null){
			vto.setOm(StringUtils.isEmpty(wifiDevice.getOem_model()) ? wifiDevice.getOrig_model() : wifiDevice.getOem_model());
			vto.setWm(wifiDevice.getWork_mode());
			vto.setCfm(wifiDevice.getConfig_mode());
			vto.setRts(wifiDevice.getLast_reged_at().getTime());
			vto.setCts(wifiDevice.getCreated_at().getTime());
			vto.setOvd(StringUtils.isEmpty(wifiDevice.getOem_vendor()) ? wifiDevice.getOrig_vendor() : wifiDevice.getOem_vendor());
			vto.setOesv(wifiDevice.getOem_swver());
			vto.setDof(StringUtils.isEmpty(wifiDevice.getRx_bytes()) ? 0 : Long.parseLong(wifiDevice.getRx_bytes()));
			vto.setUof(StringUtils.isEmpty(wifiDevice.getTx_bytes()) ? 0 : Long.parseLong(wifiDevice.getTx_bytes()));
			vto.setIpgen(wifiDevice.isIpgen());
			vto.setSn(wifiDevice.getSn());
			//如果是离线 计算离线时间
			if(vto.getOl() == 0){
				long logout_ts = wifiDevice.getLast_logout_at().getTime();
				vto.setOfts(logout_ts);
				vto.setOftd(System.currentTimeMillis() - logout_ts);
			}
		}
		return vto;
	}
}
