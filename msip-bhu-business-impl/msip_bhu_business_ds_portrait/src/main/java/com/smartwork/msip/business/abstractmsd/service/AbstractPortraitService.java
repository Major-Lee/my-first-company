package com.smartwork.msip.business.abstractmsd.service;

import java.io.Serializable;

import javax.annotation.Resource;

import com.smartwork.msip.cores.cache.entitycache.CacheService;
import com.smartwork.msip.cores.orm.jpa.EntityDao;
import com.smartwork.msip.cores.orm.logic.identifier.Identifier;
import com.smartwork.msip.cores.orm.service.EntityCacheableService;

public abstract class AbstractPortraitService<KEY extends Serializable, MODEL extends Identifier, DAO extends EntityDao<KEY, MODEL>> extends EntityCacheableService<KEY,MODEL, DAO>{
	public AbstractPortraitService() {
		super();
	}
	
	@Resource(name = "coreCacheService")
	@Override
	public void setCacheService(CacheService cacheService) {
		super.setCacheService(cacheService);
	}
}
