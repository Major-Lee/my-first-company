package com.bhu.vas.web.statistics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.StatisticsFragmentMaxOnlineHandsetService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.msip.exception.BusinessException;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseStatus;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/statistics")
public class StatisticsController extends BaseController{
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
			@RequestParam(required = true,defaultValue="4",value = "t") int type,
			@RequestParam(required = false,defaultValue="1",value = "ml") int ml
			//@RequestParam(required = false) String fragment
			) {
		if(type<0 || type>4) {
			throw new BusinessException(ResponseStatus.Forbidden,ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
		}
		if(ml<1 ) ml = 1;
		if(ml>5 ) ml = 5;
		List<Map<String,String>> result = build4Chart(type,ml);
		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(result));
	}

	private List<Map<String,String>> build4Chart(int type,int ml){
		if(type == DateTimeExtHelper.YEAR_MONTH_DD){
			return build4DailyChart(ml);
		}else if(type == DateTimeExtHelper.YEAR_WHICH_WEEK){
			return build4WeeklyChart(ml);
		}else if (type == DateTimeExtHelper.YEAR_MONTH){
			return build4MonthlyChart(ml);
		}else if (type == DateTimeExtHelper.YEAR_QUARTER){
			return build4QuarterlyChart(ml);
		}else if (type == DateTimeExtHelper.YEAR){
			return build4YearlyChart(ml);
		}
		return null;
	}
	
	private List<Map<String,String>> build4DailyChart(int ml){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(int i=0;i<24;i++){
			Map<String,String> map = new HashMap<String,String>(); 
			map.put("key"+i, String.format("%02d", i));
			result.add(map);
		}
		Date current = new Date();
		//for(int i=ml;i>=0;i--){
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateDaysAgo(current,i);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_MONTH_DD);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String keyStr = next.getKey();
				if(keyStr.length()>2) continue;
				int key = Integer.parseInt(next.getKey());//小时的key
				result.get(key).put(fragment, next.getValue());
			}
		}
		return result;
	}
	
	private List<Map<String,String>> build4WeeklyChart(int ml){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(int i=0;i<7;i++){
			Map<String,String> map = new HashMap<String,String>(); 
			map.put("key"+i+1, String.format("%02d", i+1));
			result.add(map);
		}
		Date current = new Date();
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateDaysAgo(current,i*7);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_WHICH_WEEK);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 0;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				result.get(j).put(fragment, next.getValue());
				j++;
			}
		}
		return result;
	}
	
	private List<Map<String,String>> build4MonthlyChart(int ml){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(int i=0;i<31;i++){
			Map<String,String> map = new HashMap<String,String>(); 
			map.put(String.format("key%02d", i+1), String.format("%02d", i+1));
			result.add(map);
		}
		Date current = new Date();
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateFirstDayOfMonthAgo(current,i);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_MONTH);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
			//System.out.println(fragment+"~~~~"+fragment_result+"    :"+fragment_result.size());
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 0;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				result.get(j).put(fragment, next.getValue());
				j++;
			}
		}
		return result;
	}
	
	private List<Map<String,String>> build4QuarterlyChart(int ml){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(int i=0;i<13;i++){
			Map<String,String> map = new HashMap<String,String>(); 
			map.put(String.format("key%02d", i+1), String.format("%02d", i+1));
			result.add(map);
		}
		Date current = new Date();
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateFirstDayOfMonthAgo(current,i*3);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_QUARTER);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineQuarterlySuffixKey);
			System.out.println(fragment+"~~~~"+fragment_result+"    :"+fragment_result.size());
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 0;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				result.get(j).put(fragment, next.getValue());
				j++;
			}
		}
		return result;
	}
	
	private List<Map<String,String>> build4YearlyChart(int ml){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(int i=0;i<12;i++){
			Map<String,String> map = new HashMap<String,String>(); 
			map.put(String.format("key%02d", i+1), String.format("%02d", i+1));
			result.add(map);
		}
		Date current = new Date();
		
		Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY); //设置每周的第一天为星期一  
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//每周从周一开始  
//      上面两句代码配合，才能实现，每年度的第一个周，是包含第一个星期一的那个周。  
        c.setMinimalDaysInFirstWeek(7);  //设置每周最少为7天  
		c.setTime(current);
		int year = c.get(Calendar.YEAR);
		
		for(int i=0;i<ml;i++){
			c.set(Calendar.YEAR, year-i);
			//Date current_ago = DateTimeHelper.getDateFirstDayOfMonthAgo(current,i*3);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(c.getTime(), DateTimeExtHelper.YEAR);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineYearlySuffixKey);
			System.out.println(fragment+"~~~~"+fragment_result+"    :"+fragment_result.size());
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 0;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				result.get(j).put(fragment, next.getValue());
				j++;
			}
		}
		return result;
	}
	
/*	private List<Map<String,String>> build4Chart(int type,int ml){
		if(type == DateTimeExtHelper.YEAR_MONTH_DD){
			return build4DailyChart(ml);
		}
		return null;
	}
	
	private List<Map<String,String>> build4DailyChart(int ml){
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		for(int i=0;i<24;i++){
			Map<String,String> map = new HashMap<String,String>(); 
			map.put("key"+i, String.format("%02d", i));
			result.add(map);
		}
		Date current = new Date();
		for(int i=ml;i>=0;i--){
			Date current_ago = DateTimeHelper.getDateDaysAgo(current,1);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_MONTH_DD);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment);
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				int key = Integer.parseInt(next.getKey());//小时的key
				result.get(key).put(fragment, next.getValue());
			}
		}
		return result;
	}*/
}
