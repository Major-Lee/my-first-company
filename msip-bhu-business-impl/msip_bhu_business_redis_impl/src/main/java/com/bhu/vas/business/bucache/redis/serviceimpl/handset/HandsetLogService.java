package com.bhu.vas.business.bucache.redis.serviceimpl.handset;

import java.util.List;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.HandsetLogDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationListCache;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 终端上下线记录列表
 * 用户存储终端上下线的记录
 * 当前上线和最近的上线时间如果差距在15分钟内，则合并
 * key 为 dmac-hmac值的hashcode，代表每个设备的每个终端都存在有一个列表
 * 承载业务：
 * 		终端的上下线列表
 * 		上下线区间内的的累计流量
 * 		终端是否在是新终端（相对设备来说是否是第一次登录）
 * @author Edmond Lee
 *
 */
public class HandsetLogService extends AbstractRelationListCache{
	private static class ServiceHolder{ 
		private static HandsetLogService instance =new HandsetLogService(); 
	}
	/**
	 * 获取工厂单例
	 * @return
	 */
	public static HandsetLogService getInstance() { 
		return ServiceHolder.instance; 
	}
	private HandsetLogService(){
	}
	
	private String generateKey(String dmac,String hmac){
		StringBuilder sb = new StringBuilder();
		sb.append(BusinessKeyDefine.HandsetPresent.PresentLogKey)
			.append(dmac).append(StringHelper.MINUS_CHAR_GAP).append(hmac);
		return sb.toString();
	}
	
	private static long Merge_When_In_GAP = 15*60*1000;
	
	/**
	 * 日志数据记录
	 * 规则：
	 * 	1、如果是上线消息
	 * 		a、前一条数据缺失，直接生成带有上线ts的log记录并录入
	 * 		b、前一条数据存在，
	 * 			i、如果前一条数据完整，则构建新的上线ts的log记录并录入
	 * 			ii、如果上一条数据不完整，则上一条数据下线数据补齐到当前ts，更新，并构建新的上线ts的log记录并录入
	 *  2、如果是下线消息
	 *  	a、前一条数据缺失，直接生成带有上线（当前点前15分钟）和下线ts的log记录并录入
	 *  	b、前一条数据存在
	 *  		i、如果前一条数据完整，则合并当前下线时间，并且判定前二条数据的下线时间和当前数据的上线时间时间差距，决定是否合并
	 *  		ii、如果上一条数据不完整，则更新下线时间ts，并更新
	 * @param action true 上线 false 下线
	 * @param dmac 设备mac
	 * @param hmac 终端mac
	 * @param rtb  总得流量
	 * @param ts   时间
	 */
	public void hansetLogComming(boolean action,String dmac,String hmac,long rtb,long ts){
		String key = generateKey(dmac,hmac);
		if(action){//online
			HandsetLogDTO[] previous = previousHandsetLog(key,1);
			HandsetLogDTO current = null;
			if(previous != null && !previous[0].wasComplete()){//前一条数据不为空并且为数据是不完整的
				previous[0].setF(ts);
				this.lset(key, -1, JsonHelper.getJSONString(previous[0]));
			}
			current = HandsetLogDTO.buildOnline(ts);
			this.rpush(key, JsonHelper.getJSONString(current));
			/*if(previous == null || previous.wasComplete()){//没有前一条数据 或者前一条数据是完整的
				current = HandsetLogDTO.buildOnline(ts);
			}else{
				previous.setF(ts);
				current = HandsetLogDTO.buildOnline(ts);
			}*/
		}else{//offline
			
		}
	}
	
	/**
	 * 取列表中的最后几个元素
	 * @param key
	 * @param result 几个 result > 0 因为如果result==0则是取所有记录
	 * @return
	 */
	private HandsetLogDTO[] previousHandsetLog(String key,int result){
		if(result <= 0) return null;
		List<String> lrange = this.lrange(key, -result, -1);
		if(lrange == null || lrange.isEmpty()) return null;
		else{
			int rangesize = lrange.size();
			HandsetLogDTO[] ret = new HandsetLogDTO[result];
			for(int i = result-1;i<=0;i--){
				int fetchIndex = rangesize-1;
				if(fetchIndex < 0) break;
				ret[i] = JsonHelper.getDTO(lrange.get(fetchIndex), HandsetLogDTO.class);
				rangesize--;
			}
			return ret;
		}
	}
	
	public void test(String dmac,String hmac){
		String key = generateKey(dmac,hmac);
		for(int i=0;i<100;i++){
			this.rpush(key, String.valueOf(i));
		}
		long count = this.llen(key);
		System.out.println("count:"+count);
		List<String> lrange = this.lrange(key, 0, -1);
		for(String l:lrange){
			System.out.println("element:"+l);
		}
		System.out.println("----------1");
		List<String> lrange1 = this.lrange(key, -0, -1);
		for(String l:lrange1){
			System.out.println("element:"+l);
		}
		this.lset(key, -1, String.valueOf(1000));
		System.out.println("----------2");
		lrange = this.lrange(key, 0, -1);
		for(String l:lrange){
			System.out.println("element:"+l);
		}
		
		System.out.println("----------");
		lrange1 = this.lrange(key, -1, -1);
		for(String l:lrange1){
			System.out.println("element:"+l);
		}
	}
	public void testClear(String dmac,String hmac){
		String key = generateKey(dmac,hmac);
		this.expire(key, 0);
	
	}
	
	public static void main(String[] argv){
		String dmac = "aaaaa";
		String hmac = "bbbbb";
		HandsetLogService.getInstance().test(dmac, hmac);
		HandsetLogService.getInstance().testClear(dmac, hmac);
	}
	
	/*public void userTickerMusicLove_lpush_pipeline_samevalue(Set<String> uids,String value){
		if(uids == null || uids.isEmpty()) return;
		Set<String> idKeys = new HashSet<String>();
		for(String uid:uids){
			idKeys.add(generateTickerMusicLoveKey(uid));
		}
		this.lpush_pipeline_samevalue(idKeys, value);
	}
	
	public void userTickerMusicLove_rpush_pipeline_diffvalue(String uid, List<String> values){
		if(values == null || values.isEmpty()) return;
		this.rpush_pipeline_diffvalue(generateTickerMusicLoveKey(uid), values);
	}
	
	public long userTickerMusicLoveSize(String uid){
		//System.out.println("tiker key:"+generateTickerKey(uid));
		return this.llen(generateTickerMusicLoveKey(uid));
	}
	
	public List<String> userTickerMusicLoveLrange(String uid,int start,int size){
		//System.out.println("tiker key:"+generateTickerKey(uid));
		return this.lrange(generateTickerMusicLoveKey(uid), start, start+size-1);
	}
	
	public List<String> userTickerMusicLoveAll(String uid){
		long size = this.userTickerMusicLoveSize(uid);
		return this.userTickerMusicLoveLrange(uid, 0, (int)size);
	}
	
	public long deleteTickerMusicLoveBy(String uid){
		return this.del(generateTickerMusicLoveKey(uid));
	}
	
	public String userTickerMusicLoveTrim(String uid){
		return this.ltrim(generateTickerMusicLoveKey(uid), 0, 200);
	}
	
	public long userTickerMusicLoveRPush(String uid,String value){
		return this.rpush(generateTickerMusicLoveKey(uid), value);
	}*/
	
	
	/**
	 * touid @ fromuid后的ticker merge 功能
	 * 只merge fromuid中 uid=fromuid的ticker
	 * @param touid
	 * @param fromuid
	 * @param desc true 倒序 时间大-小
	 * 			   false正序 时间小-大
	 *//*
	public void mergeTicker(String touid,String fromuid, final boolean desc){
		List<String> tofeeds = this.lrange(generateTickerMusicLoveKey(touid), 0,200);
		List<String> fromfeeds = this.lrange(generateTickerMusicLoveKey(fromuid), 0,200);
		List<Ticker> tikers = new ArrayList<Ticker>();
		for(String json:tofeeds){
			tikers.add(TickerFactoryBuilder.fromJson(json));
		}
		for(String json:fromfeeds){
			Ticker ticker = TickerFactoryBuilder.fromJson(json);
			if(ticker.getUid() == Integer.parseInt(fromuid))
				tikers.add(TickerFactoryBuilder.fromJson(json));
		}
		
		Collections.sort(tikers, new Comparator<Ticker>() {
            public int compare(Ticker a, Ticker b) {
            	Ticker d1=(Ticker)a;
            	Ticker d2=(Ticker)b;
            	int flag = 0;
            	if(desc){//倒序 大-小
            		if(d1.getTs()>d2.getTs()) flag = -1;
            		else if(d1.getTs()<d2.getTs()) flag = 1;
            		else flag = 0;
            	}else{//正序 小-大
            		if(d1.getTs()>d2.getTs()) flag = 1;
            		else if(d1.getTs()<d2.getTs()) flag = -1;
            		else flag = 0;
            	}
        		//int flag=d1.getSorter().compareTo(d2.getSorter());
        		return flag; 
            }
        });
		//System.out.println("--------------touid"+touid+" currentfeeds"+tikers.size());
		this.deleteTickerMusicLoveBy(touid);
		for(Ticker ticker:tikers){
			this.rpush(generateTickerMusicLoveKey(touid), TickerFactoryBuilder.toJson(ticker));
			//System.out.println("--------------touid"+touid+" ticker"+TickerFactoryBuilder.toJson(ticker));
		}
	}*/

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return HandsetLogService.class.getName();
	}

	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.CLUSTEREXT);
	}
}
