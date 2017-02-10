package helper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.msip.cores.helper.HttpHelper;

public class GomeCallbackHelper {
	private final Logger logger = LoggerFactory.getLogger(GomeCallbackHelper.class);

	public static void notify(String mac, int online){
    	Map<String, String> pm = new HashMap<String, String>();
    	pm.put("appId", "bhuID");
    	pm.put("timestamp", "20170119191623");
    	pm.put("nonce",  "1");
    	pm.put("sign", "");
    	String body = "{\"gid\":\"1\",\"online\":\"1\",\"time\":\"2017-01-19 19:18:18\",\"deviceId\":\"84:82:f4:23:07:28\"}";
    	System.out.println("response:" + HttpHelper.postWithBody("http://testcloud.gomesmart.com:5000/gome/data/report", pm, null, body));

	}
}
