package com.bhu.vas.business.ds.device.dao;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.bhu.vas.business.ds.device.mdto.WifiHandsetDeviceRelationMDTO;
import com.smartwork.msip.cores.cache.relationcache.impl.springmongo.BaseMongoDAOImpl;

@Repository
public class WifiHandsetDeviceRelationMDao extends BaseMongoDAOImpl<WifiHandsetDeviceRelationMDTO>{

	public static final String M_ID = "_id";
	public static final String M_WIFIID = "wifiId";
	public static final String M_HANDSETID = "handsetId";
	public static final String M_LAST_LOGIN_AT = "last_login_at";
	public static final String M_TOTAL_RX_BYTES = "total_rx_bytes";
	public static final String M_RX_BYTES = "rx_bytes";
	public static final String M_LOGS = "logs";
	public static final String M_LOGS_TYPE_LOGIN = "login";
	public static final String M_LOGS_TYPE_LOGOUT = "logout";

	@Resource(name = "mongoTemplate")
	@Override
	protected void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
}
