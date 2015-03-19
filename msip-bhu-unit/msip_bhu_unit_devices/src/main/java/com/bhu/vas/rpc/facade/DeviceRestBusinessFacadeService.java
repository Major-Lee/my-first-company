package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceLoginCountMDTO;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.bhu.vas.business.ds.device.service.WifiHandsetDeviceLoginCountMService;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.Pagination;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;

/**
 * device Rest RPC组件的业务service
 * @author tangzichao
 *
 */
@Service
public class DeviceRestBusinessFacadeService {
	private final Logger logger = LoggerFactory.getLogger(DeviceRestBusinessFacadeService.class);

	@Resource
	private WifiHandsetDeviceLoginCountMService wifiHandsetDeviceLoginCountMService;
	
	@Resource
	private WifiDeviceService wifiDeviceService;
	
	/**
	 * 获取接入移动设备数量最多的wifi设备列表
	 * TODO：目前直接从mongodb中获取 后续改成后台程序定时从mongodb获取并放入指定的redis中 这边直接从redis提取数据
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<WifiDeviceMaxBusyVTO> fetchWDevicesOrderMaxHandset(int pageNo, int pageSize) {
		Pagination<WifiHandsetDeviceLoginCountMDTO> wifiDevices = wifiHandsetDeviceLoginCountMService.
				findWifiDevicesOrderMaxHandset(pageNo, pageSize);
		List<WifiHandsetDeviceLoginCountMDTO> mdtos = wifiDevices.getDatas();
		if(mdtos == null || mdtos.isEmpty()){
			return Collections.emptyList();
		}
		
		List<WifiDeviceMaxBusyVTO> vtos = new ArrayList<WifiDeviceMaxBusyVTO>();
		WifiDeviceMaxBusyVTO vto = null;
		for(WifiHandsetDeviceLoginCountMDTO mdto : mdtos){
			vto = new WifiDeviceMaxBusyVTO();
			vto.setWid(mdto.getId());
			vto.setHdc(mdto.getCount());
			vtos.add(vto);
		}
		return vtos;
	}
	
	/**
	 * 获取在线的wifi设备列表
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public TailPage<WifiDevice> fetchWDevicesOnline(int pageNo, int pageSize){
		 return wifiDeviceService.findModelByOnline(pageNo, pageSize);
	}
}
