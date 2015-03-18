package com.bhu.vas.business.ds.device.dao;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceRelationMDTO;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.BaseMongoDAOImpl;

@Repository
public class WifiHandsetDeviceRelationMDao extends BaseMongoDAOImpl<WifiHandsetDeviceRelationMDTO>{
	@Resource(name = "mongoTemplate")
	@Override
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
}
