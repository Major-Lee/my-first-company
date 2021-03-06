package com.bhu.vas.business.ds.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetAliasService;
import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Tuple;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.WifiDeviceAlarmDTO;
import com.bhu.vas.api.dto.WifiDeviceDTO;
import com.bhu.vas.api.dto.ret.WifiDeviceStatusDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingRateControlDTO;
import com.bhu.vas.api.dto.search.WifiDeviceSearchDTO;
import com.bhu.vas.api.helper.DeviceHelper;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceAlarm;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceStatus;
import com.bhu.vas.api.vto.HandsetDeviceVTO;
import com.bhu.vas.api.vto.URouterHdVTO;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceLoginCountMDTO;
import com.smartwork.msip.cores.helper.ArithHelper;
/**
 * 用于dto和model之间的转换builder
 * @author tangzichao
 *
 */
public class BusinessModelBuilder {
	
	/*----------------------------DTO to Entity-----------------------------------*/
	
	public static WifiDevice wifiDeviceDtoToEntity(WifiDeviceDTO dto){
		WifiDevice wifi_device_entity = new WifiDevice();
		wifi_device_entity.setId(dto.getMac().toLowerCase());
		wifi_device_entity.setHdtype(dto.getHdtype());
		wifi_device_entity.setOrig_vendor(dto.getOrig_vendor());
		wifi_device_entity.setOrig_model(dto.getOrig_model());
		wifi_device_entity.setOrig_hdver(dto.getOrig_hdver());
		wifi_device_entity.setOrig_swver(dto.getOrig_swver());
		wifi_device_entity.setOem_vendor(dto.getOem_vendor());
		wifi_device_entity.setOem_model(dto.getOem_model());
		wifi_device_entity.setOem_hdver(dto.getOem_hdver());
		wifi_device_entity.setOem_swver(dto.getOem_swver());
		wifi_device_entity.setSn(dto.getSn());
		wifi_device_entity.setIp(dto.getIp());
		wifi_device_entity.setWan_ip(dto.getWan_ip());
		wifi_device_entity.setConfig_sequence(dto.getConfig_sequence());
		wifi_device_entity.setBuild_info(dto.getBuild_info());
		wifi_device_entity.setConfig_model_ver(dto.getConfig_model_ver());
		wifi_device_entity.setConfig_mode(dto.getConfig_mode());
		wifi_device_entity.setWork_mode(dto.getWork_mode());
		wifi_device_entity.setConfig_mode(dto.getConfig_mode());
		wifi_device_entity.setLast_reged_at(new Date());
		wifi_device_entity.setOnline(true);
		return wifi_device_entity;
	}
	
	public static WifiDeviceAlarm wifiDeviceAlarmDtoToEntity(WifiDeviceAlarmDTO dto){
		WifiDeviceAlarm wifi_device_alarm_entity = new WifiDeviceAlarm();
		wifi_device_alarm_entity.setName(dto.getName());
		wifi_device_alarm_entity.setTrapname(dto.getTrapname());
		wifi_device_alarm_entity.setSerial_number(dto.getSerial_number());
		wifi_device_alarm_entity.setNe_name(dto.getNe_name());
		wifi_device_alarm_entity.setMac_addr(dto.getMac_addr().toLowerCase());
		wifi_device_alarm_entity.setAlarm_level(dto.getAlarm_level());
		wifi_device_alarm_entity.setAlarm_type(dto.getAlarm_type());
		wifi_device_alarm_entity.setAlarm_cause(dto.getAlarm_cause());
		wifi_device_alarm_entity.setAlarm_reason(dto.getAlarm_reason());
		wifi_device_alarm_entity.setAlarm_event_time(dto.getAlarm_event_time());
		wifi_device_alarm_entity.setAlarm_status(dto.getAlarm_status());
		wifi_device_alarm_entity.setAlarm_title(dto.getAlarm_title());
		wifi_device_alarm_entity.setAlarm_content(dto.getAlarm_content());
		wifi_device_alarm_entity.setAlarm_serial_id(dto.getAlarm_serial_id());
		return wifi_device_alarm_entity;
	}
	
/*	public static HandsetDevice handsetDeviceDtoToEntity(HandsetDeviceDTO dto){
		HandsetDevice handset_device_entity = new HandsetDevice();
		handset_device_entity.setId(dto.getMac().toLowerCase());
		handset_device_entity.setPhy_tx_rate(dto.getPhy_tx_rate());
		handset_device_entity.setPhy_rx_rate(dto.getPhy_rx_rate());
		handset_device_entity.setData_tx_rate(dto.getData_tx_rate());
		handset_device_entity.setData_rx_rate(dto.getData_rx_rate());
		handset_device_entity.setPhy_rate(dto.getPhy_rate());
		handset_device_entity.setTx_power(dto.getTx_power());
		handset_device_entity.setRx_chain_num(dto.getRx_chain_num());
		handset_device_entity.setRssi(dto.getRssi());
		handset_device_entity.setSnr(dto.getSnr());
		handset_device_entity.setIdle(dto.getIdle());
		handset_device_entity.setState(dto.getState());
		handset_device_entity.setUptime(dto.getUptime());
		handset_device_entity.setRx_pkts(dto.getRx_pkts());
		handset_device_entity.setRx_bytes(dto.getRx_bytes());
		handset_device_entity.setTx_pkts(dto.getTx_pkts());
		handset_device_entity.setTx_bytes(dto.getTx_bytes());
		handset_device_entity.setRx_unicast(dto.getRx_unicast());
		handset_device_entity.setTx_assoc(dto.getTx_assoc());
		handset_device_entity.setSsid(dto.getSsid());
		handset_device_entity.setBssid(dto.getBssid().toLowerCase());
		handset_device_entity.setLocation(dto.getLocation());
		handset_device_entity.setChannel(dto.getChannel());
		handset_device_entity.setLast_login_at(new Date());
		handset_device_entity.setHostname(dto.getDhcp_name());
		handset_device_entity.setVapname(dto.getVapname());
		//handset_device_entity.setLast_wifi_id(dto.getBssid().toLowerCase());
		handset_device_entity.setOnline(true);
		return handset_device_entity;
	}*/
	
	/*public static HandsetDeviceDTO fromHandsetDevice(HandsetDevice hDevice){
		HandsetDeviceDTO result = new HandsetDeviceDTO();
		result.setMac(hDevice.getId());
		result.setAction(hDevice.isOnline()?HandsetDeviceDTO.Action_Online:HandsetDeviceDTO.Action_Offline);
		result.setPhy_tx_rate(hDevice.getPhy_tx_rate());
		result.setPhy_rx_rate(hDevice.getPhy_rx_rate());
		result.setData_tx_rate(hDevice.getData_tx_rate());
		result.setData_rx_rate(hDevice.getData_rx_rate());
		result.setPhy_rate(hDevice.getPhy_rate());
		result.setTx_power(hDevice.getTx_power());
		result.setRx_chain_num(hDevice.getRx_chain_num());
		result.setRssi(hDevice.getRssi());
		result.setSnr(hDevice.getSnr());
		result.setIdle(hDevice.getIdle());
		result.setState(hDevice.getState());
		result.setUptime(hDevice.getUptime());
		result.setRx_pkts(hDevice.getRx_pkts());
		result.setRx_bytes(hDevice.getRx_bytes());
		result.setTx_pkts(hDevice.getTx_pkts());
		result.setTx_bytes(hDevice.getTx_bytes());
		result.setRx_unicast(hDevice.getRx_unicast());
		result.setTx_assoc(hDevice.getTx_assoc());
		result.setSsid(hDevice.getSsid());
		result.setBssid(hDevice.getBssid().toLowerCase());
		result.setLocation(hDevice.getLocation());
		result.setChannel(hDevice.getChannel());
		result.setTs(hDevice.getLast_login_at().getTime());
		//result.setLast_login_at(new Date());
		result.setDhcp_name(hDevice.getHostname());
		result.setVapname(hDevice.getVapname());
		result.setLast_wifi_id(hDevice.getLast_wifi_id());
		//handset_device_entity.setLast_wifi_id(dto.getBssid().toLowerCase());
		//handset_device_entity.setOnline(true);
		return result;
	}*/
	
	public static WifiDeviceStatus wifiDeviceStatusDtoToEntity(WifiDeviceStatusDTO dto){
		WifiDeviceStatus wifi_device_status_entity = new WifiDeviceStatus();
		wifi_device_status_entity.setCurrent_cpu_usage(Integer.parseInt(dto.getCurrent_cpu_usage()));
		wifi_device_status_entity.setAverage_cpu_usage(Integer.parseInt(dto.getAverage_cpu_usage()));
		wifi_device_status_entity.setMax_cpu_usage(Integer.parseInt(dto.getMax_cpu_usage()));
		wifi_device_status_entity.setCurrent_mem_usage(Integer.parseInt(dto.getCurrent_mem_usage()));
		wifi_device_status_entity.setAverage_mem_usage(Integer.parseInt(dto.getAverage_mem_usage()));
		wifi_device_status_entity.setMax_mem_usage(Integer.parseInt(dto.getMax_mem_usage()));
		return wifi_device_status_entity;
	}
	
	public static WifiDeviceVTO toWifiDeviceVTO(WifiDeviceSearchDTO searchDto, WifiDevice entity){
		WifiDeviceVTO vto = new WifiDeviceVTO();
		if(searchDto != null){
			vto.setWid(searchDto.getId());
			vto.setOl(searchDto.getOnline());
			vto.setCohc(searchDto.getCount());
			vto.setAdr(searchDto.getAddress());
			vto.setDt(searchDto.getDevicetype());
			vto.setOsv(searchDto.getOrigswver());
			vto.setGids(searchDto.getGroups());
		}
		if(entity != null){
			vto.setOm(StringUtils.isEmpty(entity.getOem_model()) ? entity.getOrig_model() : entity.getOem_model());
			vto.setWm(entity.getWork_mode());
			vto.setCfm(entity.getConfig_mode());
			vto.setRts(entity.getLast_reged_at().getTime());
			vto.setCts(entity.getCreated_at().getTime());
			vto.setOvd(StringUtils.isEmpty(entity.getOem_vendor()) ? entity.getOrig_vendor() : entity.getOem_vendor());
			vto.setOesv(entity.getOem_swver());
			vto.setDof(StringUtils.isEmpty(entity.getRx_bytes()) ? 0 : Long.parseLong(entity.getRx_bytes()));
			vto.setUof(StringUtils.isEmpty(entity.getTx_bytes()) ? 0 : Long.parseLong(entity.getTx_bytes()));
			vto.setIpgen(entity.isIpgen());
			vto.setSn(entity.getSn());
			//如果是离线 计算离线时间
			if(vto.getOl() == 0){
				long logout_ts = entity.getLast_logout_at().getTime();
				vto.setOfts(logout_ts);
				vto.setOftd(System.currentTimeMillis() - logout_ts);
			}
		}
		return vto;
	}
	
	public static WifiDeviceMaxBusyVTO toWifiDeviceMaxBusyVTO(WifiHandsetDeviceLoginCountMDTO mdto, WifiDevice entity){
		WifiDeviceMaxBusyVTO vto = new WifiDeviceMaxBusyVTO();
		if(mdto != null){
			vto.setWid(mdto.getId());
			vto.setHdc(mdto.getCount());
		}
		if(entity != null){
			vto.setOl(entity.isOnline() ? 1 : 0);
			long count = WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(entity.getId());
			vto.setCohc(count);
			vto.setAdr(entity.getFormatted_address());
			vto.setOm(StringUtils.isEmpty(entity.getOem_model()) ? entity.getOrig_model() : entity.getOem_model());
			vto.setWm(entity.getWork_mode());
			vto.setCfm(entity.getConfig_mode());
			vto.setRts(entity.getLast_reged_at().getTime());
			vto.setCts(entity.getCreated_at().getTime());
			vto.setDt(entity.getHdtype());
			vto.setOvd(StringUtils.isEmpty(entity.getOem_vendor()) ? entity.getOrig_vendor() : entity.getOem_vendor());
			vto.setOsv(entity.getOrig_swver());
			vto.setOesv(entity.getOem_swver());
			vto.setDof(StringUtils.isEmpty(entity.getRx_bytes()) ? 0 : Long.parseLong(entity.getRx_bytes()));
			vto.setUof(StringUtils.isEmpty(entity.getTx_bytes()) ? 0 : Long.parseLong(entity.getTx_bytes()));
			vto.setIpgen(entity.isIpgen());
			vto.setSn(entity.getSn());
			//如果是离线 计算离线时间
			if(vto.getOl() == 0){
				long logout_ts = entity.getLast_logout_at().getTime();
				vto.setOfts(logout_ts);
				vto.setOftd(System.currentTimeMillis() - logout_ts);
			}
		}
		return vto;
	}
	
/*	public static URouterHdVTO toURouterHdVTO(String hd_mac, boolean online, HandsetDevice hd_entity,
			WifiDeviceSettingDTO setting_dto){
		URouterHdVTO vto = new URouterHdVTO();
		vto.setHd_mac(hd_mac);
		vto.setOnline(online);
		vto.setN(DeviceHelper.getHandsetDeviceAlias(hd_mac, setting_dto));
		//Data_rx_limit 设备发送终端的限速 kbps 转换成 bps
		WifiDeviceSettingRateControlDTO rc = DeviceHelper.matchRateControl(
				setting_dto, hd_mac);
		if(rc != null){
			//vto.setTx_limit(ArithHelper.unitConversionDoKbpsTobps(mark_entity.getData_rx_limit()));
			if(!StringUtils.isEmpty(rc.getRx()))
				vto.setTx_limit(ArithHelper.unitConversionDoKbpsTobps(rc.getRx()));
			if(!StringUtils.isEmpty(rc.getTx()))
				vto.setRx_limit(ArithHelper.unitConversionDoKbpsTobps(rc.getTx()));
		}
		
		if(hd_entity != null){
			if(StringUtils.isEmpty(vto.getN())){
				vto.setN(hd_entity.getHostname());
			}
			//Data_rx_rate是设备接收终端的速率 反过来就是终端的上行速率 bps
			vto.setTx_rate(hd_entity.getData_rx_rate());
			//Data_tx_rate是设备发送终端的速率 反过来就是终端的下行速率 bps
			vto.setRx_rate(hd_entity.getData_tx_rate());
			if(!StringUtils.isEmpty(hd_entity.getVapname()))
				vto.setGuest(DeviceHelper.isGuest(hd_entity.getVapname(), setting_dto));
		}
		return vto;
	}*/
	
	public static URouterHdVTO toURouterHdVTO(int uid, String hd_mac, boolean online, HandsetDeviceDTO hd_entity,
			WifiDeviceSettingDTO setting_dto){
		URouterHdVTO vto = new URouterHdVTO();
		vto.setHd_mac(hd_mac);
		vto.setOnline(online);
//		vto.setN(DeviceHelper.getHandsetDeviceAlias(hd_mac, setting_dto));
		vto.setN(getHandsetDeviceAlias(uid, hd_mac));
		//Data_rx_limit 设备发送终端的限速 kbps 转换成 bps
		WifiDeviceSettingRateControlDTO rc = DeviceHelper.matchRateControl(
				setting_dto, hd_mac);
		if(rc != null){
			//vto.setTx_limit(ArithHelper.unitConversionDoKbpsTobps(mark_entity.getData_rx_limit()));
			if(!StringUtils.isEmpty(rc.getRx()))
				vto.setTx_limit(ArithHelper.unitConversionDoKbpsTobps(rc.getRx()));
			if(!StringUtils.isEmpty(rc.getTx()))
				vto.setRx_limit(ArithHelper.unitConversionDoKbpsTobps(rc.getTx()));
		}
		
		if(hd_entity != null){
			if(StringUtils.isEmpty(vto.getN())){
				vto.setN(hd_entity.getDhcp_name());
			}
			//Data_rx_rate是设备接收终端的速率 反过来就是终端的上行速率 bps
			vto.setTx_rate(hd_entity.getData_rx_rate());
			//Data_tx_rate是设备发送终端的速率 反过来就是终端的下行速率 bps
			vto.setRx_rate(hd_entity.getData_tx_rate());
			if(!StringUtils.isEmpty(hd_entity.getVapname()))
				vto.setGuest(DeviceHelper.isGuest(hd_entity.getVapname(), setting_dto));

			vto.setRx_bytes(hd_entity.getTx_bytes());
			vto.setTx_bytes(hd_entity.getRx_bytes());
		}
		return vto;
	}

	private static String getHandsetDeviceAlias(int uid, String hd_mac){
		return WifiDeviceHandsetAliasService.getInstance().hgetHandsetAlias(uid, hd_mac);
	}


	
	public static HandsetDeviceVTO toHandsetDeviceVTO(String mac, String hd_mac, boolean online, 
			HandsetDeviceDTO hd_entity){
		HandsetDeviceVTO vto = new HandsetDeviceVTO();
		vto.setWid(mac);
		vto.setHid(hd_mac);
		vto.setOl(online ? 1 : 0);
		if(hd_entity != null){
			if(mac.equals(hd_entity.getLast_wifi_id())){
				vto.setTs(hd_entity.getTs());
			}
		}
		return vto;
	}
	
	/*public static HandsetDeviceVTO toHandsetDeviceVTO(String mac, String hd_mac, boolean online, 
			HandsetDevice hd_entity){
		HandsetDeviceVTO vto = new HandsetDeviceVTO();
		vto.setWid(mac);
		vto.setHid(hd_mac);
		vto.setOl(online ? 1 : 0);
		if(hd_entity != null){
			if(mac.equals(hd_entity.getLast_wifi_id())){
				vto.setTs(hd_entity.getLast_login_at().getTime());
			}
		}
		return vto;
	}*/
	
/*	public static List<WifiHandsetDeviceMarkPK> toWifiHandsetDeviceMarkPKs(String mac, List<String> hd_macs){
		if(hd_macs == null || hd_macs.isEmpty()) return Collections.emptyList();
		
		List<WifiHandsetDeviceMarkPK> pks = new ArrayList<WifiHandsetDeviceMarkPK>();
		for(String hd_mac : hd_macs){
			pks.add(new WifiHandsetDeviceMarkPK(mac, hd_mac));
		}
		return pks;
	}*/
	
	public static List<String> toWifiDeviceIds(List<WifiDeviceSearchDTO> search_dtos){
		if(search_dtos == null || search_dtos.isEmpty()) return Collections.emptyList();
		
		List<String> ids = new ArrayList<String>();
		for(WifiDeviceSearchDTO search_dto : search_dtos){
			ids.add(search_dto.getId());
		}
		return ids;
	}
	
	public static List<String> toElementsList(Set<Tuple> tupes){
		if(tupes == null || tupes.isEmpty()) return Collections.emptyList();
		
		List<String> ids = new ArrayList<String>();
		for(Tuple tuple : tupes){
			ids.add(tuple.getElement());
		}
		return ids;
	}
	
	public static String[] toElementsArray(Set<Tuple> tuples){
		if(tuples == null || tuples.isEmpty()) return null;
		
		String[] rets = new String[tuples.size()];
		int cursor = 0;
		for(Tuple tuple : tuples){
			rets[cursor] = tuple.getElement();
			cursor++;
		}
		return rets;
	}
	
}
