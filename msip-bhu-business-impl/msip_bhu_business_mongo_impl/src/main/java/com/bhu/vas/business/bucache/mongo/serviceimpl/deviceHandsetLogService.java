package com.bhu.vas.business.bucache.mongo.serviceimpl;

import com.smartwork.msip.cores.cache.relationcache.impl.mongodb.AbstarctDTOService;
import com.smartwork.msip.cores.cache.relationcache.impl.mongodb.MongoConstantDefine;

public class deviceHandsetLogService extends AbstarctDTOService<Object>{

	
	
	
	
	
	@Override
	public String dbCollection() {
		return MongoConstantDefine.Collection.UserStarFrdColl;
	}

	@Override
	public String dbName() {
		return MongoConstantDefine.DB.WhisperMongo;
	}

}
