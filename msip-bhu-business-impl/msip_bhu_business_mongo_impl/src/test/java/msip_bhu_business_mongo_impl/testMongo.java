package msip_bhu_business_mongo_impl;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class testMongo {
	public static void test() throws UnknownHostException{
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
        
        
//        collection.insert(list);
	}
	
	public static void main(String[] args) throws UnknownHostException {
		test();
	}
}
