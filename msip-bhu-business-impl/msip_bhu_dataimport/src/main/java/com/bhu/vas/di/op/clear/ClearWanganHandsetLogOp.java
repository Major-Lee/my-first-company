package com.bhu.vas.di.op.clear;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.lang3.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
/**
 * 清除终端上下线数据
 * @author yetao
 *
 */
public class ClearWanganHandsetLogOp {
	
	public static final long expire = 1000*3600*24*7; //7天
	private long del = 0;
	private long devs = 0;
	private long statotal = 0;
	private long devWithLog = 0;
	private long trim = 0;

	

	
	public void doClear() throws Exception{
		Jedis jedis = new Jedis("10.170.245.254", 6383);
    	int batchCount = 1000;
    	String prefixKey = "PA.";
	    Map<String,Response<String>> rp = new HashMap<String, Response<String>>(batchCount);
    	int matched = 0;
    	int total = 0;
	    Pipeline pipeline = null;
	    
	    do{
		    pipeline = jedis.pipelined(); 
		    for(int i = 0; i < batchCount; i ++){
	    		rp.put(String.valueOf(i), pipeline.randomKey());
		    }
		    pipeline.sync();
		    pipeline.close();
		    pipeline = null;
		    
		    matched = 0;
		    pipeline = jedis.pipelined(); 
		    for(int i = 0; i < batchCount; i ++){
		    	Response<String> response = rp.get(String.valueOf(i));
		    	String key = response.get();
		    	if(!StringUtils.isEmpty(key) && key.startsWith(prefixKey)){
		    		matched ++;
		    		pipeline.expire(key, 0);
				    System.out.println("clean:" + key);
		    	}
		    }
		    rp.clear();
		    System.out.println("matched:" + matched);
		    pipeline.sync();
		    pipeline.close();
		    pipeline = null;
		    total += matched;
	    }while(matched > 0);
	    System.out.println("total:" + total);
	}

	
	public void doCount(){
		Jedis jedis = new Jedis("10.170.245.254", 6383);
		Set<String> keys = jedis.keys("*");
		long total = 0;
		for(String key:keys){
			long ct = jedis.hlen(key);
			System.out.println("key:" + key + " ct:" + ct);
			total += ct;
		}
		System.out.println("count:" + total);
		return;
	}
	
	public static void main(String[] argv) throws Exception{
		ClearWanganHandsetLogOp op = new ClearWanganHandsetLogOp();
//		op.doClear();
		op.doCount();
		System.exit(0);
	}
}
