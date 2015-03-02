package com.bhu.vas.business.subject.cache;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.subject.dto.SubjectListVTO;
import com.smartwork.msip.cores.cache.entitycache.Cache;
import com.smartwork.msip.cores.cache.entitycache.CacheService;
/**
 * 用于当日最热的头条列表缓存
 * 只缓存100条
 * @author lawliet
 *
 */
@Service
public class SubjectDailyBusinessCacheService {
	public static final String SubjectDaily_CachePrefix = "sdcp"; //daily subject hot 
	public static final int MaxCache_Szie = 100;//只缓存100条
	
	public int maxSubjectDailyCacheSize(){
		return MaxCache_Szie;
	}
	
	@Resource(name="coreCacheService")
	CacheService cacheService;
	Cache entityCache;
	
	@PostConstruct
	protected void init() {
		entityCache = cacheService.addCache(this.getClass().getSimpleName(),10*10000,300);//30分钟 方便测试先设置成5分钟
    }
	
	public String generateSubjectDailyCacheKeyBy(){
		return SubjectDaily_CachePrefix;
	}
	
	@SuppressWarnings("unchecked")
	public List<SubjectListVTO> getSubjectDailyByQ(){
		Object cacheObj = this.entityCache.get(generateSubjectDailyCacheKeyBy());
		if(cacheObj == null) {
			return null;
		}
		return (List<SubjectListVTO>)cacheObj;
	}
	
	public void storeSubjectDailyResult(List<SubjectListVTO> result){
		this.entityCache.remove(generateSubjectDailyCacheKeyBy());
		this.entityCache.put(generateSubjectDailyCacheKeyBy(), result);
	}
}
