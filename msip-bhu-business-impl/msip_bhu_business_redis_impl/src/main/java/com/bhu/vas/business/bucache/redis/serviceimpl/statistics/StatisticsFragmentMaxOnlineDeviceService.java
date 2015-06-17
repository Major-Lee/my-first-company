package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.comparator.SortMapHelper;

/**
 * 周期内用户在线数量曲线展示图标接口
 * @author edmond
 *
 */
public class StatisticsFragmentMaxOnlineDeviceService extends AbstractRelationHashCache{
	
	private static class ServiceHolder{ 
		private static StatisticsFragmentMaxOnlineDeviceService instance =new StatisticsFragmentMaxOnlineDeviceService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static StatisticsFragmentMaxOnlineDeviceService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private StatisticsFragmentMaxOnlineDeviceService(){
	}
	
	private static String generateKey(String fragment,String buPrefixKey){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.FragmentDeviceOnline);
		sb.append(buPrefixKey).append(fragment);
		return sb.toString();
	}
	
	/**
	 * 当前数值需要写入当前时间对应的 日，周，月，季度，年对应的field中
	 * 日 key yyyy-MM-dd 	field  yyyy-MM-dd HH	 Value count
	 * 周 key yyyy-w		 	field  yyyy-MM-dd		 Value count
	 * 月 key yyyy-MM	 	field  yyyy-MM-dd		 Value count
	 * 季度 key yyyy-QQ	 	field  yyyy-w		 Value count
	 * 年 key yyyy	 		field  yyyy-MM		 	 Value count
	 * @param count
	 */
	public void fragmentAllSet(Date current,int count){
		if(count < 0) return;
		String count_str = String.valueOf(count);
		List<String> fragments = DateTimeExtHelper.generateServalDateFormat(current);
		initFragmentFieldAndValueWith(fragments);
		//每日数据更新
		this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey),
				fragments.get(DateTimeExtHelper.HH), count_str);
		boolean larger = false;
		//判定count值是否比redis中存储的当日中的最大值还大
		String value1 = this.hget(generateKey(fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey), 
				fragments.get(DateTimeExtHelper.YEAR_MONTH_DD));
		if(StringUtils.isEmpty(value1)){
			larger = true;
		}else{
			int rvalue = Integer.parseInt(value1);
			if(count > rvalue){
				larger = true;
			}
		}
		if(larger){
			//每周数据更新
			this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey), 
					fragments.get(DateTimeExtHelper.YEAR_MONTH_DD), count_str);
			//每月数据更新
			this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR_MONTH),BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey), 
					fragments.get(DateTimeExtHelper.YEAR_MONTH_DD), count_str);
		}
		
		String value2 = this.hget(generateKey(fragments.get(DateTimeExtHelper.YEAR_QUARTER),BusinessKeyDefine.Statistics.FragmentOnlineQuarterlySuffixKey), 
				fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK));
		if(StringUtils.isEmpty(value2) || count > Integer.parseInt(value2)){
			//每季度数据更新
			this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR_QUARTER),BusinessKeyDefine.Statistics.FragmentOnlineQuarterlySuffixKey), 
					fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK), count_str);
		}
		
		String value3 = this.hget(generateKey(fragments.get(DateTimeExtHelper.YEAR),BusinessKeyDefine.Statistics.FragmentOnlineYearlySuffixKey), 
				fragments.get(DateTimeExtHelper.YEAR_MONTH));
		if(StringUtils.isEmpty(value3) || count > Integer.parseInt(value3)){
			//每年数据更新
			this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR),BusinessKeyDefine.Statistics.FragmentOnlineYearlySuffixKey), 
					fragments.get(DateTimeExtHelper.YEAR_MONTH), count_str);
		}
	}
	
	/**
	 * 用户指定日期设定值，不比对大小
	 * @param current
	 * @param count
	 */
	public void fragmentAllReSet(Date current,int count){
		if(count < 0) return;
		String count_str = String.valueOf(count);
		List<String> fragments = DateTimeExtHelper.generateServalDateFormat(current);
		//initFragmentFieldAndValueWith(fragments);
		//每日数据更新
		//this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey),
		//		fragments.get(DateTimeExtHelper.HH), count_str);
		//每周数据更新
		this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey), 
				fragments.get(DateTimeExtHelper.YEAR_MONTH_DD), count_str);
		//每月数据更新
		this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR_MONTH),BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey), 
				fragments.get(DateTimeExtHelper.YEAR_MONTH_DD), count_str);
		

		this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR_QUARTER),BusinessKeyDefine.Statistics.FragmentOnlineQuarterlySuffixKey), 
				fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK), count_str);
		
		this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR),BusinessKeyDefine.Statistics.FragmentOnlineYearlySuffixKey), 
					fragments.get(DateTimeExtHelper.YEAR_MONTH), count_str);
	}
	
	
	public Map<String,String> fragmentGet(String fragment,String buPrefixKey){
		Map<String,String> all = this.hgetall(generateKey(fragment,buPrefixKey));
		return SortMapHelper.sortMapByKey(all);
	}
	
	public Long cleanFragment(String fragment,String buPrefixKey){
		return this.expire(generateKey(fragment,buPrefixKey), 0);
	}
	
	public Long fragmentSize(String fragment,String buPrefixKey){
		return this.hlen(generateKey(fragment,buPrefixKey));
	}
	
	public void initFragmentFieldAndValueWith(List<String> fragments){
		initFragmentFieldAndValue(fragments.get(DateTimeExtHelper.YEAR_MONTH_DD),BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey);
		initFragmentFieldAndValue(fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK),BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey);
		initFragmentFieldAndValue(fragments.get(DateTimeExtHelper.YEAR_MONTH),BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey);
		initFragmentFieldAndValue(fragments.get(DateTimeExtHelper.YEAR_QUARTER),BusinessKeyDefine.Statistics.FragmentOnlineQuarterlySuffixKey);
		initFragmentFieldAndValue(fragments.get(DateTimeExtHelper.YEAR),BusinessKeyDefine.Statistics.FragmentOnlineYearlySuffixKey);
	}
	/**
	 * 目前对 日，周，月进行初始化数据
	 * 当前数值需要写入当前时间对应的 日，周，月，季度，年对应的field中
	 * 日 key yyyy-MM-dd 	field  yyyy-MM-dd HH	 Value count
	 * 周 key yyyy-w		 	field  yyyy-MM-dd		 Value count
	 * 月 key yyyy-MM	 	field  yyyy-MM-dd		 Value count
	 * 季度 key yyyy-QQ	 	field  yyyy-w		 Value count
	 * 年 key yyyy	 		field  yyyy-MM		 	 Value count
	 * @param count
	 */
	public void initFragmentFieldAndValue(String fragment,String buPrefixKey){
		Long fragment_size = this.fragmentSize(fragment, buPrefixKey);
		if( fragment_size != null && fragment_size.longValue()>0) return;
		if(BusinessKeyDefine.Statistics.FragmentOnlineDailySuffixKey.equals(buPrefixKey)){//天
			{//初始化24个值 0~23
				Map<String,String> map = new HashMap<String,String>();
				for(int j=0;j<24;j++){
					String key = String.format("%02d", j);
					map.put(key, "0");
				}
				this.hmset(generateKey(fragment,buPrefixKey), map);
			}
		}
		
		if(BusinessKeyDefine.Statistics.FragmentOnlineWeeklySuffixKey.equals(buPrefixKey)){//周
			String[] array = fragment.split(StringHelper.MINUS_STRING_GAP);
			int year = Integer.parseInt(array[0]);
			int weeknum = Integer.parseInt(array[1]);
			String[] day_array = DateTimeExtHelper.getYearWeekAllDay(year, weeknum);
			{//初始化7个值 1~7
				Map<String,String> map = new HashMap<String,String>();
				for(String day:day_array){
					map.put(day, "0");
				}
				this.hmset(generateKey(fragment,buPrefixKey), map);
			}
		}
		
		if(BusinessKeyDefine.Statistics.FragmentOnlineMonthlySuffixKey.equals(buPrefixKey)){//周
			String[] array = fragment.split(StringHelper.MINUS_STRING_GAP);
			int year = Integer.parseInt(array[0]);
			int monthnum = Integer.parseInt(array[1]);
			String[] day_array = DateTimeExtHelper.getYearMonthAllDay(year, monthnum);
			{//初始化size个值 1~7
				Map<String,String> map = new HashMap<String,String>();
				for(String day:day_array){
					map.put(day, "0");
				}
				this.hmset(generateKey(fragment,buPrefixKey), map);
			}
		}
		//TODO:获取季度所有周初始化数据 
		if(BusinessKeyDefine.Statistics.FragmentOnlineQuarterlySuffixKey.equals(buPrefixKey)){//周
			String[] array = fragment.split(StringHelper.MINUS_STRING_GAP);
			int year = Integer.parseInt(array[0]);
			int seasonnum = Integer.parseInt(array[1]);
			String[] week_array = DateTimeExtHelper.getYearSeasonAllWeek(year, seasonnum);
			{//初始化size个值 1~7
				Map<String,String> map = new HashMap<String,String>();
				for(String week:week_array){
					map.put(week,"0");
				}
				this.hmset(generateKey(fragment,buPrefixKey), map);
			}
		}
		//TODO:以及获取年所有月的初始化数据
		if(BusinessKeyDefine.Statistics.FragmentOnlineYearlySuffixKey.equals(buPrefixKey)){//周
			String[] array = fragment.split(StringHelper.MINUS_STRING_GAP);
			int year = Integer.parseInt(array[0]);
			int monthnum = 12;//Integer.parseInt(array[1]);
			//String[] day_array = DateTimeExtHelper.getYearMonthAllDay(year, monthnum);
			{//初始化size个值 1~7
				Map<String,String> map = new HashMap<String,String>();
				for(int i=0;i<monthnum;i++){
					map.put(String.format("%s-%s",year,String.format("%02d", i+1)),"0");
				}
				this.hmset(generateKey(fragment,buPrefixKey), map);
			}
		}
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return StatisticsFragmentMaxOnlineDeviceService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.STATISTICS);
	}
	
	/*public static Map<String, String> sortMapByKey(Map<String, String> map) {  
        if (map == null || map.isEmpty()) {  
            return map;  
        }
        Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());  
        sortMap.putAll(map);  
        return sortMap;  
    } */
	
}
/*class MapKeyComparator implements Comparator<String>{  
    public int compare(String str1, String str2) {  
        return str1.compareTo(str2);  
    }  
}*/
