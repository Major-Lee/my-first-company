package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import com.bhu.vas.api.dto.advertise.AdCommentDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationSortedSetCache;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;

public class AdvertiseCommentSortedSetService extends AbstractRelationSortedSetCache{
	
    private static class ServiceHolder{
        private static AdvertiseCommentSortedSetService instance =new AdvertiseCommentSortedSetService();
    }
    
    /**
     * 获取工厂单例
     * @return
     */
    public static AdvertiseCommentSortedSetService getInstance() {
        return ServiceHolder.instance;
    }

    private static String generateKey(String adid){
        StringBuilder sb = new StringBuilder(BusinessKeyDefine.Advertise.AdvertiseComment);
        sb.append(adid);
        return sb.toString();
    }
	
	public void AdComment(int uid ,String adid,String comment){
		AdCommentDTO dto = new AdCommentDTO();
		dto.setUid(uid);
		dto.setComment(comment);
		this.zadd(generateKey(adid),Double.parseDouble(DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern16)) ,JsonHelper.getJSONString(dto));
	}

	public void AdReply(String adid,double score,String reply){
		Set<String> result =  this.zrangeByScore(generateKey(adid), score, score);
		Iterator<String> it = result.iterator();
		String member = null;
		while(it.hasNext()){
			member = it.next();
		}
		AdCommentDTO dto = JsonHelper.getDTO(member, AdCommentDTO.class);
		dto.setReply(reply);
		this.zadd(generateKey(adid), score,JsonHelper.getJSONString(dto));
	}
	
	public Set<Tuple> fetchAdComments(String adid){
		return this.zrevrangeWithScores(generateKey(adid), 0, -1);
	}
	
	@Override
	public JedisPool getRedisPool() {
		return RedisPoolManager.getInstance().getPool(RedisKeyEnum.ADVERTISE);	
	}

	@Override
	public String getRedisKey() {
		return null;
	}

	@Override
	public String getName() {
		return AdvertiseCommentSortedSetService.class.getName();
	}
}
