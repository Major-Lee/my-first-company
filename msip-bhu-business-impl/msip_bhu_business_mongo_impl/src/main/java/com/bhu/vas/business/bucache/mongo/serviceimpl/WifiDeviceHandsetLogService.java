package com.bhu.vas.business.bucache.mongo.serviceimpl;

import org.springframework.stereotype.Service;

import com.bhu.vas.business.bucache.mongo.entity.HandsetLogMongoDTO;
import com.smartwork.msip.cores.cache.relationcache.impl.mongodb.AbstarctDTOService;
import com.smartwork.msip.cores.cache.relationcache.impl.mongodb.MongoConstantDefine;


@Service
public class WifiDeviceHandsetLogService extends AbstarctDTOService<HandsetLogMongoDTO>{

    private  static class ServiceHolder{
        private static WifiDeviceHandsetLogService instance = new WifiDeviceHandsetLogService();
    }

    /**
     * 获得工厂单例
     * @return
     */
    public static WifiDeviceHandsetLogService getInstance() {
        return ServiceHolder.instance;
    }
	
	@Override
	public String dbCollection() {
		return MongoConstantDefine.Collection.UserStarFrdColl;
	}

	@Override
	public String dbName() {
		return MongoConstantDefine.DB.WhisperMongo;
	}
}
