package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Tuple;

import com.bhu.vas.api.dto.ret.setting.WifiDeviceSettingDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceSetting;
import com.bhu.vas.api.vto.URouterEnterVTO;
import com.bhu.vas.api.vto.URouterHdVTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.devices.WifiDeviceHandsetPresentSortedSetService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiDeviceSettingService;
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
	
	public List<URouterHdVTO> urouterHdOnlineList(Integer uid, String wifiId, int start, int size){
		Set<Tuple> online_presents = WifiDeviceHandsetPresentSortedSetService.getInstance().
				fetchOnlinePresents(wifiId, start, size);
		if(online_presents.isEmpty()){
			return Collections.emptyList();
		}
		List<URouterHdVTO> vtos = new ArrayList<URouterHdVTO>();
		return vtos;
	}
}
