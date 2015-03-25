package com.bhu.vas.business.bucache.redis.serviceimpl.statistics;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationHashCache;
import com.smartwork.msip.cores.helper.DateTimeExtHelper;

/**
 *  用户收到所有聊天消息信息存储空间
 *  ZSET 
 *  	key：user 
 *  	score 消息接收时间
 *  	value msgid
 *  包括	
 *  	聊天离线消息
 * @author edmond
 *
 */
public class StatisticsFragmentMaxOnlineHandsetService extends AbstractRelationHashCache{
	
	private static class ServiceHolder{ 
		private static StatisticsFragmentMaxOnlineHandsetService instance =new StatisticsFragmentMaxOnlineHandsetService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static StatisticsFragmentMaxOnlineHandsetService getInstance() { 
		return ServiceHolder.instance; 
	}
	
	private StatisticsFragmentMaxOnlineHandsetService(){
	}
	
	private static String generateKey(String fragment){//,String buPrefixKey){
		StringBuilder sb = new StringBuilder(BusinessKeyDefine.Statistics.FragmentOnline);
		sb./*append(buPrefixKey).append(StringHelper.POINT_CHAR_GAP).*/append(fragment);
		return sb.toString();
	}
	
	/**
	 * 当前数值需要写入当前时间对应的 日，周，月，季度，年对应的field中
	 * 日 key yyyy-MM-dd 	field  yyyy-MM-dd HH	 Value count
	 * 周 key yyyy-w		 	field  yyyy-MM-dd		 Value count
	 * 月 key yyyy-MM	 	field  yyyy-MM-dd		 Value count
	 * 季度 key yyyy-QQ	 	field  yyyy-MM-dd		 Value count
	 * 年 key yyyy	 		field  yyyy-MM		 	 Value count
	 * @param count
	 */
	public void fragmentAllSet(Date current,int count){
		if(count < 0) return;
		String count_str = String.valueOf(count);
		List<String> fragments = DateTimeExtHelper.generateServalDateFormat(current, false);
		//每日数据更新
		this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR_MONTH_DD)),
				fragments.get(DateTimeExtHelper.YEAR_MONTH_DD_HH), count_str);
		boolean larger = false;
		//判定count值是否比redis中存储的当日中的最大值还大
		String value1 = this.hget(generateKey(fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK)), fragments.get(DateTimeExtHelper.YEAR_MONTH_DD));
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
			this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR_WHICH_WEEK)), fragments.get(DateTimeExtHelper.YEAR_MONTH_DD), count_str);
			//每月数据更新
			this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR_MONTH)), fragments.get(DateTimeExtHelper.YEAR_MONTH_DD), count_str);
			//每季度数据更新
			this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR_QUARTER)), fragments.get(DateTimeExtHelper.YEAR_MONTH_DD), count_str);
		}
		String value2 = this.hget(generateKey(fragments.get(DateTimeExtHelper.YEAR)), fragments.get(DateTimeExtHelper.YEAR_MONTH));
		if(StringUtils.isEmpty(value2) || count > Integer.parseInt(value2)){
			//每年数据更新
			this.hset(generateKey(fragments.get(DateTimeExtHelper.YEAR)), fragments.get(DateTimeExtHelper.YEAR_MONTH), count_str);
		}
	}
	
	public Map<String,String> fragmentGet(String fragment){
		Map<String,String> all = this.hgetall(generateKey(fragment));
		return sortMapByKey(all);
	}
	
	public Long cleanFragment(String fragment){
		return this.expire(fragment, 0);
	}
	
	@Override
	public String getRedisKey() {
		return null;
	}
	
	@Override
	public String getName() {
		return StatisticsFragmentMaxOnlineHandsetService.class.getName();
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.PRESENT);
	}
	
	public static Map<String, String> sortMapByKey(Map<String, String> map) {  
        if (map == null || map.isEmpty()) {  
            return null;  
        }
        Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());  
        sortMap.putAll(map);  
        return sortMap;  
    } 
	
}
class MapKeyComparator implements Comparator<String>{  
    public int compare(String str1, String str2) {  
        return str1.compareTo(str2);  
    }  
}
