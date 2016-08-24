package msip_bhu_business_mongo_impl;


import org.junit.Test;

import com.bhu.vas.business.bucache.mongo.entity.HandsetLogMongoDTO;
import com.bhu.vas.business.bucache.mongo.serviceimpl.WifiDeviceHandsetLogService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.smartwork.msip.cores.cache.relationcache.impl.mongodb.MongoConstantDefine;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.localunit.BaseTest;

public class test extends BaseTest{
	
	@Test
	public void test001() throws Exception{
		DBObject query = new BasicDBObject(MongoConstantDefine.Defalut_Mongo_PrimaryKey, 111 );
		HandsetLogMongoDTO result = WifiDeviceHandsetLogService.getInstance().getByQuery(query);
		System.out.println(JsonHelper.getJSONString(result));
		
		HandsetLogMongoDTO dto = WifiDeviceHandsetLogService.getInstance().getById("111");
		System.out.println(JsonHelper.getJSONString(dto));
	}
}
