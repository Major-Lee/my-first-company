package com.bhu.vas.business.device.dao;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.bhu.vas.business.device.mdto.WifiHandsetDeviceLoginCountMDTO;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.BaseMongoDAOImpl;

@Repository
public class WifiHandsetDeviceLoginCountMDao extends BaseMongoDAOImpl<WifiHandsetDeviceLoginCountMDTO>{
	@Resource(name = "mongoTemplate")
	@Override
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
}
