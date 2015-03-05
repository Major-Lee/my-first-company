package com.smartwork.im.extension.support.cache;

public enum CacheType{
	
	SPY_MEMCACHED(false,false), 
	ALI_MEMCACHED(false,false), 
	X_MEMCACHED(false,false), 
	TTSERVER(false,true), 
	REDISDEFAULT_STRING(false,true),
	REDISRELATION_LIST(false,true),
	REDISRELATION_SET(false,true),
	REDISRELATION_ZSET(false,true),
	REDISRELATION_HASH(false,true),
	EHCACHE(true,false), 
	LOCAL_MAP(true,false), 
	NULL_CACHE(true,false);
	
	boolean localCache;
	private CacheType(boolean isLocalCache,boolean isPersistent){
		localCache = isLocalCache;
	}
	public boolean isLocalCache() {
		return localCache;
	}
	public void setLocalCache(boolean localCache) {
		this.localCache = localCache;
	}
	
	
}
