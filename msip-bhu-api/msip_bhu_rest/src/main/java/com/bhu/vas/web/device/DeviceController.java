package com.bhu.vas.web.device;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.redis.RegionCountDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.vto.HandsetDeviceVTO;
import com.bhu.vas.api.vto.StatisticsGeneralVTO;
import com.bhu.vas.api.vto.WifiDeviceMaxBusyVTO;
import com.bhu.vas.api.vto.WifiDeviceRecentVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
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
	 * 获取根据keyword查询地点获取wifi设备分页
	 * 排序以当前在线和当前在线移动设备数量
	 * @param request
	 * @param response
	 * @param pageNo
	 * @param pageSize
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_wifidevices_by_keyword",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_wifidevices_by_keyword(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false, value = "q") String keyword,
			@RequestParam(required = false, defaultValue="北京市", value = "region") String region,
			@RequestParam(required = false, value = "excepts") String excepts,
			@RequestParam(required = false, defaultValue="1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue="5", value = "ps") int pageSize) {
		
		TailPage<WifiDeviceVTO> vtos_page = deviceRestRpcService.fetchWDevicesByKeyword(keyword, region, 
				excepts, pageNo, pageSize);
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
	
	/**
	 * 获取wifi设备地域分布饼图
	 * @param request
	 * @param response
	 * @param regions 地域名称 逗号分割
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_region_count",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_region_count(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String regions) {
		
		List<RegionCountDTO> dtos = deviceRestRpcService.fetchWDeviceRegionCount(regions);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dtos));
	}
	
	/**
	 * 获取最近30天内接入的wifi设备列表
	 * @param request
	 * @param response
	 * @param pageNo
	 * @param pageSize
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_wifidevices_by_registerat",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_wifidevices_by_registerat(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false, defaultValue="1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue="5", value = "ps") int pageSize) {
		
		TailPage<WifiDeviceRecentVTO> result = deviceRestRpcService.fetchRecentWDevice(pageNo, pageSize);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
	}
	
	/**
	 * 根据wifi设备的id获取在线移动设备列表
	 * @param request
	 * @param response
	 * @param wifiId
	 * @param pageNo
	 * @param pageSize
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_handsetdevices_online",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_handsetdevices_online(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true, value = "wid") String wifiId,
			@RequestParam(required = false, defaultValue="1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue="5", value = "ps") int pageSize) {
		
		TailPage<HandsetDeviceVTO> result = deviceRestRpcService.fetchHDevicesOnline(wifiId, pageNo, pageSize);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
	}
}
