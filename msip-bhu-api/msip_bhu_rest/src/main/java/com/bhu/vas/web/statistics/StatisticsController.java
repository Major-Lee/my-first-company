package com.bhu.vas.web.statistics;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.StatisticsFragmentMaxOnlineHandsetService;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {
	/**
	 * 获取最繁忙的TOP5wifi设备
	 * @param request
	 * @param response
	 * 	public static final int YEAR = 0;
		public static final int YEAR_QUARTER = 1;
		public static final int YEAR_MONTH = 2;
		public static final int YEAR_WHICH_WEEK = 3;
		public static final int YEAR_MONTH_DD = 4;
	 */
	@ResponseBody()
	@RequestMapping(value="/fetch_online_handset",method={RequestMethod.GET,RequestMethod.POST})
	public void fetch_max_busy_devices(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false,defaultValue="4",value = "t") int type,
			@RequestParam(required = false) String fragment
			) {
		if(StringUtils.isEmpty(fragment)){
			fragment = DateTimeExtHelper.generateCertainDateFormat(new Date(), type);
		}
		Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(fragment_result));
	}
}
