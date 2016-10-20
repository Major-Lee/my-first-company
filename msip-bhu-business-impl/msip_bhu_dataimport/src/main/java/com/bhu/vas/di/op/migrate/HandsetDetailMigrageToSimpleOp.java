package com.bhu.vas.di.op.migrate;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.elasticsearch.common.lang3.StringUtils;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.HandsetDeviceOldDTO;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.helper.JsonHelper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
/**
 * redis中的终端详情，存储格式简化（把旧格式转化为新格式)
 * @author yetao
 *
 */
public class HandsetDetailMigrageToSimpleOp {
	/*
终端详细信息
HEP.%05d的哈希值（设备mac-终端mac然后计算hash） dmac-hmac作为filed， filed value为json字符串，记录终端在此设备上的详细关联信息

	 */
	
	public static int staBatch = 1000;
	public static final String HEP = "HEP.";
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  

	private Jedis jedis_handset;

	private static Jedis getJedis(String initStr){
		final String[] hostAndPort = initStr.split(":");
        if (null != hostAndPort && hostAndPort.length >= 2) {
            String host = hostAndPort[0];
            int port = 6379;
            try {
                port = Integer.parseInt(hostAndPort[1]);
            } catch (final NumberFormatException nfe) {
            }
            return new Jedis(host, port);
        }
        return null;
	}
	
	public HandsetDetailMigrageToSimpleOp(){
		InputStream in = null;
		Properties properties = new Properties();
	    try {
	    	in = HandsetDetailMigrageToSimpleOp.class.getResourceAsStream("/deploy/lazyloadconf/redis.properties");
			properties.load(in);
		} catch (Exception e) {
			try{
				in = HandsetDetailMigrageToSimpleOp.class.getResourceAsStream("/lazyloadconf/redis.properties");
				properties.load(in);
			}catch(Exception ex){
				System.out.println("init loading /deploy/lazyloadconf/redis.properties or  /lazyloadconf/redis.properties failed!");
				e.printStackTrace();
			}
		}
	    jedis_handset = getJedis(properties.getProperty(RedisKeyEnum.HANDSETPRESENT.getName()));
	}
	
	
	
	private void batchMigrate(String key, Map<String, String> m) throws IOException{
		Pipeline pl = jedis_handset.pipelined();
		int count = 0;
		for(String field:m.keySet()){
			String value = m.get(field);
			if(StringUtils.isEmpty(value)){
				pl.hdel(key, field);
				count ++;
			} else {
				if(value.indexOf("action") > 0){
					//old format
					HandsetDeviceOldDTO old = JsonHelper.getDTO(value, HandsetDeviceOldDTO.class);
					HandsetDeviceDTO dto = new HandsetDeviceDTO();
					try{
						BeanUtils.copyProperties(dto, old);
						value = JsonHelper.getJSONString(dto,false);;
						pl.hset(key,  field, value);
						count ++;
					}catch(Exception e){
						e.printStackTrace();
					}
				} else {
					//already new format
				}
			}
			
			if(count >= staBatch){
				pl.sync();
				pl.close();
				pl = null;
				
				pl = jedis_handset.pipelined();
				count = 0;
			}
		}

		if(count > 0){
			pl.sync();
			pl.close();
			pl = null;
		}
	}
	
	
	/*
	 *	终端详细信息
	 *	HEP.%05d的哈希值（设备mac-终端mac然后计算hash） dmac-hmac作为filed， filed value为json字符串，记录终端在此设备上的详细关联信息
	 */
	private void doMigrate() throws IOException{
		System.out.println("clearStartFromHEP");
    	Set<String> keys = jedis_handset.keys("*");
    	for(String key:keys){
    		if(!key.startsWith(HEP))
    			continue;
    		try{
    			Map<String, String> all = jedis_handset.hgetAll(key);
    			batchMigrate(key, all);
    		}catch(Exception e){
        		System.out.println("error while handle key : " + key);
    		}
    	}
   	}
	

	public static void main(String[] argv) throws Exception{

		long t0 = System.currentTimeMillis();
		System.out.println("Start:" + t0);
				HandsetDetailMigrageToSimpleOp op = new HandsetDetailMigrageToSimpleOp();
		op.doMigrate();
		System.out.println(System.currentTimeMillis());
		System.exit(0);
	}
}
