package msip_bhu_business_mongo_impl;

import java.net.UnknownHostException;

import msip_bhu_business_mongo_entity.UserDTO;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.smartwork.msip.cores.cache.relationcache.impl.mongodb.DBObjectHelper;

public class testMongo {
	public static void test() throws UnknownHostException, IllegalArgumentException, IllegalAccessException{
		MongoClient mongoClient = new MongoClient( "192.168.66.124" , 27017);
		

		
        for (String name : mongoClient.getDatabaseNames()) {
            System.out.println("dbName: " + name);
        }
        
		DB db = mongoClient.getDB("unicorn");
        System.out.println("---------------");
        for (String name : db.getCollectionNames()) {
            System.out.println("collectionName: " + name);
        }
        
        DBCollection collection = db.getCollection("unicorn");
        System.out.println("-----collection------");
        UserDTO dto = new UserDTO();
        dto.setId("111");
        dto.setName("xiaowei");
        dto.setSex("FM");
        DBObject dbObject = DBObjectHelper.bean2DBObject(dto);
        dbObject.put("_id", "123");
        collection.insert(dbObject);
        System.out.println("--------end--------");
	}
	
	public static void main(String[] args) throws UnknownHostException, IllegalArgumentException, IllegalAccessException {
		test();
	}
}
