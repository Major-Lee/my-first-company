package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.StatisticsFragmentMaxOnlineDeviceService;
//import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
//import com.bhu.vas.business.bucache.redis.serviceimpl.statistics.StatisticsFragmentMaxOnlineHandsetService;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.comparator.SortMapHelper;

@Service
public class StatisticsDeviceMaxOnlineService {
	public Map<String,Object> build4Chart(int type,int ml){
		String hint = null;
		Map<String,List<Integer>> result = null;
		if(type == DateTimeExtHelper.YEAR_MONTH_DD){
			hint = "日曲线图数据";
			result = build4DailyChart(ml);
		}else if(type == DateTimeExtHelper.YEAR_WHICH_WEEK){
			hint = "周曲线图数据";
			result = build4WeeklyChart(ml);
		}else if (type == DateTimeExtHelper.YEAR_MONTH){
			hint = "月曲线图数据";
			result =  build4MonthlyChart(ml);
		}else if (type == DateTimeExtHelper.YEAR_QUARTER){
			hint = "季度曲线图数据";
			result =  build4QuarterlyChart(ml);
		}else if (type == DateTimeExtHelper.YEAR){
			hint = "年曲线图数据";
			result =  build4YearlyChart(ml);
		}else{
			result = null;
		}
		Map<String,Object> dataFull = new HashMap<String,Object>();
		dataFull.put(hint, SortMapHelper.sortMapByKey(result));
		return dataFull;
	}
	
	/*private Map<String,Map<String,String>> build4Chart(int type,int ml){
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
	}*/
	private Map<String,List<Integer>> build4DailyChart(int ml){
		Map<String,List<Integer>> result = new HashMap<String,List<Integer>>();
		Date current = new Date();
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateDaysAgo(current,i);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_MONTH_DD);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineDeviceService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
			if(fragment_result.isEmpty()) continue;
			List<Integer> dataarray = new ArrayList<Integer>();
			{//过滤掉当天的超过当前实际点的数据
				if(i==0){
					Calendar c = Calendar.getInstance();
					c.setTime(current);
					int hour = c.get(Calendar.HOUR_OF_DAY);
					String current_hour = String.format("%02d", hour);
					Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
					while(iter.hasNext()){
						Entry<String, String> next = iter.next();
						String po = next.getKey();
						if(po.compareTo(current_hour)>0){
							iter.remove();
						}else{
							dataarray.add(Integer.parseInt(next.getValue()));
						}
					}
				}else{
					Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
					while(iter.hasNext()){
						Entry<String, String> next = iter.next();
						dataarray.add(Integer.parseInt(next.getValue()));
					}
				}
			}
			result.put(fragment, dataarray);
		}
		return result;
	}
	private Map<String,List<Integer>> build4WeeklyChart(int ml){
		Map<String,List<Integer>> result = new HashMap<String,List<Integer>>();
		Date current = new Date();
		String current_day = DateTimeHelper.formatDate(current, DateTimeHelper.FormatPattern5);
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateDaysAgo(current,i*7);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_WHICH_WEEK);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineDeviceService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
			if(fragment_result.isEmpty()) continue;
			List<Integer> dataarray = new ArrayList<Integer>();
			//Map<String,String> fragment_tmp_result = new HashMap<String,String>(); 
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			//int j = 1;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				if(i == 0 && po.compareTo(current_day)>0){
					;
				}else{
					dataarray.add(Integer.parseInt(va));
					//fragment_tmp_result.put(String.format("第%02d天", j), va);
				}
				//j++;
			}
			result.put(fragment, dataarray);//SortMapHelper.sortMapByKey(fragment_tmp_result));
		}
		return result;
	}
	
	private Map<String,List<Integer>> build4MonthlyChart(int ml){
		Map<String,List<Integer>> result = new HashMap<String,List<Integer>>();
		Date current = new Date();
		String current_day = DateTimeHelper.formatDate(current, DateTimeHelper.FormatPattern5);
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateFirstDayOfMonthAgo(current,i);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_MONTH);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineDeviceService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
			if(fragment_result.isEmpty()) continue;
			List<Integer> dataarray = new ArrayList<Integer>();
			//Map<String,String> fragment_tmp_result = new HashMap<String,String>(); 
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			//int j = 1;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				if(i == 0 && po.compareTo(current_day)>0){
					;
				}else{
					dataarray.add(Integer.parseInt(va));
					//fragment_tmp_result.put(String.format("第%02d天", j), va);
				}
				//j++;
			}
			
			result.put(fragment, dataarray);//SortMapHelper.sortMapByKey(fragment_tmp_result));
		}
		return result;
	}
	
	private Map<String,List<Integer>> build4QuarterlyChart(int ml){
		Map<String,List<Integer>> result = new HashMap<String,List<Integer>>();
		Date current = new Date();
		String current_week = DateTimeExtHelper.getDateWeekFormat(current);
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateFirstDayOfMonthAgo(current,i*3);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_QUARTER);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineDeviceService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineQuarterlySuffixKey);
			if(fragment_result.isEmpty()) continue;
			List<Integer> dataarray = new ArrayList<Integer>();
			//Map<String,String> fragment_tmp_result = new HashMap<String,String>(); 
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			//int j = 1;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				if(i == 0 && po.compareTo(current_week)>0){
					;
				}else{
					dataarray.add(Integer.parseInt(va));
					//fragment_tmp_result.put(String.format("第%02d周", j), va);
				}
				//j++;
			}
			result.put(fragment, dataarray);//SortMapHelper.sortMapByKey(fragment_tmp_result));
		}
		return result;
	}
	
	private Map<String,List<Integer>> build4YearlyChart(int ml){
		Map<String,List<Integer>> result = new HashMap<String,List<Integer>>();
		Date current = new Date();
		String current_month = DateTimeHelper.formatDate(current, DateTimeHelper.FormatPattern11);
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
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineDeviceService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineYearlySuffixKey);
			if(fragment_result.isEmpty()) continue;
			List<Integer> dataarray = new ArrayList<Integer>();
			//Map<String,String> fragment_tmp_result = new HashMap<String,String>(); 
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			//int j = 1;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				if(i == 0 && po.compareTo(current_month)>0){
					;
				}else{
					dataarray.add(Integer.parseInt(va));
					//fragment_tmp_result.put(String.format("第%02d月", j), va);
				}
				//j++;
				//fragment_tmp_result.put(String.format("第%02d月", j), va);
				//j++;
			}
			result.put(fragment, dataarray);//SortMapHelper.sortMapByKey(fragment_tmp_result));
		}
		return result;
	}
	/*private Map<String,Map<String,String>> build4DailyChart(int ml){
		Map<String,Map<String,String>> result = new HashMap<String,Map<String,String>>();
		Date current = new Date();
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateDaysAgo(current,i);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_MONTH_DD);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
			if(fragment_result.isEmpty()) continue;
			{//过滤掉当天的超过当前实际点的数据
				if(i==0){
					Calendar c = Calendar.getInstance();
					c.setTime(current);
					int hour = c.get(Calendar.HOUR_OF_DAY);
					String current_hour = String.format("%02d", hour);
					Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
					while(iter.hasNext()){
						Entry<String, String> next = iter.next();
						String po = next.getKey();
						if(po.compareTo(current_hour)>0){
							iter.remove();
						}
					}
				}
			}
			result.put(fragment, fragment_result);
		}
		return result;
	}*/
	
	/*private Map<String,Map<String,String>> build4WeeklyChart(int ml){
		Map<String,Map<String,String>> result = new HashMap<String,Map<String,String>>();
		Date current = new Date();
		String current_day = DateTimeHelper.formatDate(current, DateTimeHelper.FormatPattern5);
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateDaysAgo(current,i*7);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_WHICH_WEEK);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
			if(fragment_result.isEmpty()) continue;
			Map<String,String> fragment_tmp_result = new HashMap<String,String>(); 
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 1;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				if(i == 0 && po.compareTo(current_day)>0){
					;
				}else{
					fragment_tmp_result.put(String.format("第%02d天", j), va);
				}
				j++;
			}
			result.put(fragment, SortMapHelper.sortMapByKey(fragment_tmp_result));
		}
		return result;
	}*/
	
	/*private Map<String,Map<String,String>> build4MonthlyChart(int ml){
		Map<String,Map<String,String>> result = new HashMap<String,Map<String,String>>();
		Date current = new Date();
		String current_day = DateTimeHelper.formatDate(current, DateTimeHelper.FormatPattern5);
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateFirstDayOfMonthAgo(current,i);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_MONTH);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
			if(fragment_result.isEmpty()) continue;
			
			Map<String,String> fragment_tmp_result = new HashMap<String,String>(); 
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 1;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				if(i == 0 && po.compareTo(current_day)>0){
					;
				}else{
					fragment_tmp_result.put(String.format("第%02d天", j), va);
				}
				j++;
			}
			
			result.put(fragment, SortMapHelper.sortMapByKey(fragment_tmp_result));
		}
		return result;
	}
	
	private Map<String,Map<String,String>> build4QuarterlyChart(int ml){
		Map<String,Map<String,String>> result = new HashMap<String,Map<String,String>>();
		Date current = new Date();
		String current_week = DateTimeExtHelper.getDateWeekFormat(current);
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateFirstDayOfMonthAgo(current,i*3);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_QUARTER);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineQuarterlySuffixKey);
			if(fragment_result.isEmpty()) continue;
			
			Map<String,String> fragment_tmp_result = new HashMap<String,String>(); 
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 1;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				if(i == 0 && po.compareTo(current_week)>0){
					;
				}else{
					fragment_tmp_result.put(String.format("第%02d周", j), va);
				}
				j++;
			}
			result.put(fragment, SortMapHelper.sortMapByKey(fragment_tmp_result));
		}
		return result;
	}
	
	private Map<String,Map<String,String>> build4YearlyChart(int ml){
		Map<String,Map<String,String>> result = new HashMap<String,Map<String,String>>();
		Date current = new Date();
		String current_month = DateTimeHelper.formatDate(current, DateTimeHelper.FormatPattern11);
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
			if(fragment_result.isEmpty()) continue;
			
			Map<String,String> fragment_tmp_result = new HashMap<String,String>(); 
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 1;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				if(i == 0 && po.compareTo(current_month)>0){
					;
				}else{
					fragment_tmp_result.put(String.format("第%02d月", j), va);
				}
				j++;
				//fragment_tmp_result.put(String.format("第%02d月", j), va);
				//j++;
			}
			result.put(fragment, SortMapHelper.sortMapByKey(fragment_tmp_result));
		}
		return result;
	}*/
	
	/*
	private Map<String,Object> build4Chart(int type,int ml){
		String hint = null;
		Map<String,List<String>> result = null;
		if(type == DateTimeExtHelper.YEAR_MONTH_DD){
			hint = "日曲线图数据";
			result = build4DailyChart(ml);
		}else if(type == DateTimeExtHelper.YEAR_WHICH_WEEK){
			hint = "周曲线图数据";
			result = build4WeeklyChart(ml);
		}else if (type == DateTimeExtHelper.YEAR_MONTH){
			hint = "月曲线图数据";
			result =  build4MonthlyChart(ml);
		}else if (type == DateTimeExtHelper.YEAR_QUARTER){
			hint = "季度曲线图数据";
			result =  build4QuarterlyChart(ml);
		}else if (type == DateTimeExtHelper.YEAR){
			hint = "年曲线图数据";
			result =  build4YearlyChart(ml);
		}else{
			result = null;
		}
		Map<String,Object> dataFull = new HashMap<String,Object>();
		dataFull.put(hint, result);
		return dataFull;
	}
	
	private Map<String,List<String>> build4DailyChart(int ml){
		Map<String,List<String>> result = new HashMap<String,List<String>>();
		Date current = new Date();
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateDaysAgo(current,i);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_MONTH_DD);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
			if(fragment_result.isEmpty()) continue;
			List<String> elements = new ArrayList<String>();
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				elements.add(new PointDTO(po,va).toString());
			}
			result.put(fragment, elements);
		}
		return result;
	}
	
	private Map<String,List<String>> build4WeeklyChart(int ml){
		Map<String,List<String>> result = new HashMap<String,List<String>>();
		Date current = new Date();
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateDaysAgo(current,i*7);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_WHICH_WEEK);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
			if(fragment_result.isEmpty()) continue;
			List<String> elements = new ArrayList<String>();
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 1;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				elements.add(new PointDTO(po,va).toString());//String.format("%02d", j)
				j++;
			}
			result.put(fragment, elements);
		}
		return result;
	}
	
	private Map<String,List<String>> build4MonthlyChart(int ml){
		Map<String,List<String>> result = new HashMap<String,List<String>>();
		Date current = new Date();
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateFirstDayOfMonthAgo(current,i);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_MONTH);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
			if(fragment_result.isEmpty()) continue;
			List<String> elements = new ArrayList<String>();
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 1;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				elements.add(new PointDTO(po,va).toString());
				j++;
			}
			result.put(fragment, elements);
		}
		return result;
	}
	
	private Map<String,List<String>> build4QuarterlyChart(int ml){
		Map<String,List<String>> result = new HashMap<String,List<String>>();
		Date current = new Date();
		for(int i=0;i<ml;i++){
			Date current_ago = DateTimeHelper.getDateFirstDayOfMonthAgo(current,i*3);
			String fragment = DateTimeExtHelper.generateCertainDateFormat(current_ago, DateTimeExtHelper.YEAR_QUARTER);
			Map<String,String> fragment_result = StatisticsFragmentMaxOnlineHandsetService.getInstance().fragmentGet(fragment,BusinessKeyDefine.Statistics.FragmentOnlineQuarterlySuffixKey);
			if(fragment_result.isEmpty()) continue;
			List<String> elements = new ArrayList<String>();
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 1;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				elements.add(new PointDTO(po,va).toString());
				j++;
			}
			result.put(fragment, elements);
		}
		return result;
	}
	
	private Map<String,List<String>> build4YearlyChart(int ml){
		Map<String,List<String>> result = new HashMap<String,List<String>>();
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
			if(fragment_result.isEmpty()) continue;
			List<String> elements = new ArrayList<String>();
			Iterator<Entry<String, String>> iter = fragment_result.entrySet().iterator();
			int j = 1;
			while(iter.hasNext()){
				Entry<String, String> next = iter.next();
				String po = next.getKey();
				String va = next.getValue();
				elements.add(new PointDTO(po,va).toString());
				j++;
			}
			result.put(fragment, elements);
		}
		return result;
	}*/
	
	/*
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
	}*/
	
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
