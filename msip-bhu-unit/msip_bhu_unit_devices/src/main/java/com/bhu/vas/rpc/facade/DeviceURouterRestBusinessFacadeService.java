package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Tuple;

import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.rpc.devices.model.WifiHandsetDeviceMark;
import com.bhu.vas.api.rpc.devices.model.WifiHandsetDeviceMarkPK;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdVTO;
import com.bhu.vas.api.vto.URouterRealtimeRateVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.WifiDeviceRealtimeRateStatisticsHashService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceMarkService;
import com.smartwork.msip.exception.RpcBusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * device urouter Rest RPC组件的业务service
 * @author tangzichao
 *
 */
@Service
public class DeviceURouterRestBusinessFacadeService {
	//private final Logger logger = LoggerFactory.getLogger(DeviceRestBusinessFacadeService.class);
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	@Resource
	private WifiDeviceSettingService wifiDeviceSettingService;
	
	@Resource
	private WifiHandsetDeviceMarkService wifiHandsetDeviceMarkService;
	
	/**
	 * urouter 入口界面数据
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public URouterEnterVTO urouterEnter(Integer uid, String wifiId){
		WifiDeviceSetting entity = wifiDeviceSettingService.getById(wifiId);
		if(entity == null) {
			throw new RpcBusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_SETTING_NOTEXIST.code());
		}
		WifiDeviceSettingDTO dto = entity.getInnerModel();
		URouterEnterVTO vto = new URouterEnterVTO();
		if(!StringUtils.isEmpty(dto.getPower())){
			vto.setPower(Integer.parseInt(dto.getPower()));
			//vto.setPower_type(power_type);
		}
		vto.setOhd_count(WifiDeviceHandsetPresentSortedSetService.getInstance().presentOnlineSize(wifiId));
		//vto.setWd_rate(wd_rate);
		return vto;
	}
	
	public static final int HDList_Online_Status = 1;//获取在线终端列表
	public static final int HDList_Offline_Status = 2;//获取离线终端列表
	/**
	 * 获取urouter设备在线终端列表
	 * @param uid
	 * @param wifiId
	 * @param start
	 * @param size
	 * @return
	 */
	public List<URouterHdVTO> urouterHdList(Integer uid, String wifiId, int status, int start, int size){
		Set<Tuple> presents = null;
		switch(status){
			case HDList_Online_Status:
				presents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOnlinePresentWithScores(wifiId, start, size);
				break;
			case HDList_Offline_Status:
				presents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchOfflinePresentWithScores(wifiId, start, size);
				break;
			default:
				presents = WifiDeviceHandsetPresentSortedSetService.getInstance().fetchPresents(wifiId, start, size);
		}

		if(!presents.isEmpty()){
			List<WifiHandsetDeviceMarkPK> mark_pks = new ArrayList<WifiHandsetDeviceMarkPK>();
			for(Tuple tuple : presents){
				mark_pks.add(new WifiHandsetDeviceMarkPK(wifiId, tuple.getElement()));
			}
			List<WifiHandsetDeviceMark> mark_entitys = wifiHandsetDeviceMarkService.findByIds(mark_pks, true, true);
			if(!mark_entitys.isEmpty()){
				List<URouterHdVTO> vtos = new ArrayList<URouterHdVTO>();
				int cursor = 0;
				WifiHandsetDeviceMark mark_entity = null;
				for(Tuple tuple : presents){
					URouterHdVTO vto = new URouterHdVTO();
					vto.setHd_mac(tuple.getElement());
					vto.setOnline(WifiDeviceHandsetPresentSortedSetService.getInstance().isOnline(tuple.getScore()));
					mark_entity = mark_entitys.get(cursor);
					if(mark_entity != null){
						vto.setN(mark_entity.getHd_name());
						vto.setTx_limit(mark_entity.getData_tx_limit());
						vto.setRx_limit(mark_entity.getData_rx_limit());
						vto.setTx_rate(mark_entity.getData_tx_rate());
						vto.setRx_rate(mark_entity.getData_rx_rate());
					}
					vtos.add(vto);
					cursor++;
				}
				return vtos;
			}
		}
		return Collections.emptyList();
	}
	
	/**
	 * 获取设备的实时速率
	 * @param uid
	 * @param wifiId
	 * @return
	 */
	public URouterRealtimeRateVTO urouterRealtimeRate(Integer uid, String wifiId){
		URouterRealtimeRateVTO vto = new URouterRealtimeRateVTO();
		Map<String, String> rate_map = WifiDeviceRealtimeRateStatisticsHashService.getInstance().getRate(wifiId);
		if(rate_map != null){
			BeanUtils.copyProperties(rate_map, vto);
		}
		return vto;
	}
}
