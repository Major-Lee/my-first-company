package com.bhu.vas.web.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.vto.StatisticsGeneralVTO;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/device")
public class DeviceController {
	
	@Resource
	private IDeviceRestRpcService deviceRestRpcService;
	
	/**
	 * 获取最繁忙的TOP5wifi设备
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_max_busy_wifidevices",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_max_busy_devices(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false, defaultValue="1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue="5", value = "ps") int pageSize) {
		
		List<WifiDeviceMaxBusyVTO> vtos = deviceRestRpcService.fetchWDevicesOrderMaxHandset(pageNo, pageSize);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));

	}
	
	/**
	 * 获取wifi设备的在线列表
	 * @param request
	 * @param response
	 * @param pageNo
	 * @param pageSize
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_wifidevices_online_list",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_wifidevices_online_list(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false, defaultValue="1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue="5", value = "ps") int pageSize) {
		
		TailPage<WifiDevice> wifiDevice_entity_page = deviceRestRpcService.fetchWDevicesOnline(pageNo, pageSize);
		List<WifiDevice> items = wifiDevice_entity_page.getItems();
		List<WifiDeviceVTO> vtos = null;
		if(items != null && !items.isEmpty()){
			vtos = new ArrayList<WifiDeviceVTO>();
			WifiDeviceVTO vto = null;
			for(WifiDevice wifiDevice : items){
				vto = new WifiDeviceVTO();
				vto.setWid(wifiDevice.getId());
				vto.setWdt(wifiDevice.getOrig_vendor());
				vto.setOl(true);
				vtos.add(vto);
			}
		}else{
			vtos = Collections.emptyList();
		}
		TailPage<WifiDeviceVTO> vtos_page = new CommonPage<WifiDeviceVTO>(wifiDevice_entity_page.getPageNumber(), 
				wifiDevice_entity_page.getPageSize(), wifiDevice_entity_page.getTotalItemsCount(), vtos);
		
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos_page));

	}
	/**
	 * 获取统计通用数据展示
		页面中统计数据体现：
		a、总设备数、总用户数、在线设备数、在线用户数、总接入次数、总用户访问时长
		b、今日新增、活跃用户、接入次数|人均、新用户占比、平均时长、活跃率
		c、昨日新增、活跃用户、接入次数|人均、新用户占比、平均时长、活跃率
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_statistics_general",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_statistics_general(
			HttpServletRequest request,
			HttpServletResponse response) {
		
		StatisticsGeneralVTO vto = deviceRestRpcService.fetchStatisticsGeneral();
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
	}
	
}
