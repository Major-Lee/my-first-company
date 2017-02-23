package com.bhu.vas.business.bucache.redis.serviceimpl.advertise;

import java.util.List;

import redis.clients.jedis.JedisPool;

import com.bhu.vas.api.dto.advertise.AdvertiseCPMDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisPoolManager;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.impl.AbstractRelationListCache;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.StringHelper;

public class AdvertiseCPMListService  extends AbstractRelationListCache{

    private static class ServiceHolder{
        private static AdvertiseCPMListService instance =new AdvertiseCPMListService();
    }
    
    /**
     * 获取工厂单例
     * @return
     */
    public static AdvertiseCPMListService getInstance() {
        return ServiceHolder.instance;
    }
    
    public void AdCPMPosh(List<String> adids){
    	for(String adid : adids){
        	AdvertiseCPMDTO dto = new AdvertiseCPMDTO();
        	dto.setAdid(adid);
        	this.lpush(BusinessKeyDefine.Advertise.AdvertiseCPM, JsonHelper.getJSONString(dto));
    	}
    }
    
    public void AdCPMPosh(List<String> adids,String mac ,String umac){
    	for(String adid : adids){
        	AdvertiseCPMDTO dto = new AdvertiseCPMDTO();
        	dto.setAdid(adid);
        	if(StringHelper.isNotEmpty(mac))
            	dto.setMac(mac);
        	if(StringHelper.isNotEmpty(umac))
        		dto.setUmac(umac);
        	this.lpush(BusinessKeyDefine.Advertise.AdvertiseCPM, JsonHelper.getJSONString(dto));
    	}
    }
    
    public String AdCPMNotify(){
    	return this.rpop(BusinessKeyDefine.Advertise.AdvertiseCPM);
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
		return AdvertiseCPMListService.class.getName();
	}

}
