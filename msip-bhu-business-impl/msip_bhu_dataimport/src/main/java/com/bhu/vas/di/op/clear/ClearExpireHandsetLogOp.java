package com.bhu.vas.di.op.clear;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.dto.HandsetLogDTO;
import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.cache.relationcache.impl.jedis.RedisKeyEnum;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.EntityIterator;
import com.smartwork.msip.cores.orm.iterator.KeyBasedEntityBatchIterator;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
/**
 * 清除终端上下线数据
 * @author yetao
 *
 */
public class ClearExpireHandsetLogOp {
	/*
	 * CLUSTEREXT("redis-hosts.clusterext"),
设备上的终端上下线log     (lset)
     HPL.devMac-staMac,   list形式，value 为json字符串， 记录上线时间，下线时间, 本次上线流量总和， 15分钟内的上下线会合并

终端再某个设备上的流量总计 (hash-set)
     HPT.devMac-staMac   hashset形式，用hash filed ： trb记录总字节数

设备上的所有接入过的终端
     HPR.devMac,      hashset形式, 每个终端mac作为一个filed，value为最后一次上线或者下线的时间.

HANDSETPRESENT("redis-hosts.handsetpresent"),
终端详细信息
HEP.%05d的哈希值（设备mac-终端mac然后计算hash） dmac-hmac作为filed， filed value为json字符串，记录终端在此设备上的详细关联信息

	 */
	
	public static final long expire = 1000*3600*24*7; //7天
	public static int staBatch = 1000;
	public static final String HEP = "HEP.";
	public static final String HPL = "HPL.";
	public static final String HPT = "HPT.";
	public static final String HPR = "HPR.";
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  

	private Jedis jedis_handset;
	private Jedis jedis_ext;
	private WifiDeviceService wifiDeviceService;

	
	
	
	public WifiDeviceService getWifiDeviceService() {
		return wifiDeviceService;
	}


	public void setWifiDeviceService(WifiDeviceService wifiDeviceService) {
		this.wifiDeviceService = wifiDeviceService;
	}


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
	
	
	public ClearExpireHandsetLogOp(){
		InputStream in = null;
		Properties properties = new Properties();
	    try {
	    	in = ClearExpireHandsetLogOp.class.getResourceAsStream("/deploy/lazyloadconf/redis.properties");
			properties.load(in);
		} catch (Exception e) {
			try{
				in = ClearExpireHandsetLogOp.class.getResourceAsStream("/lazyloadconf/redis.properties");
				properties.load(in);
			}catch(Exception ex){
				System.out.println("init loading /deploy/lazyloadconf/redis.properties or  /lazyloadconf/redis.properties failed!");
				e.printStackTrace();
			}
		}
	    jedis_handset = getJedis(properties.getProperty(RedisKeyEnum.HANDSETPRESENT.getName()));
	    jedis_ext = getJedis(properties.getProperty(RedisKeyEnum.CLUSTEREXT.getName()));
	}
	
	private void batchClear(List<String> list) throws IOException{
		if(list == null || list.size() < 0)
			return;
		Pipeline pl_handset = jedis_handset.pipelined();
		Pipeline pl_ext = jedis_ext.pipelined();
		int count = 0;
		for(String key:list){
			count ++;
			String[] ar = key.split("-");
			System.out.println("removing:" + HandsetStorageService.generateKey(ar[0], ar[1]) + " field:" + key);
			pl_handset.hdel(HandsetStorageService.generateKey(ar[0], ar[1]), key);
			pl_ext.del(HPL + key);
			pl_ext.del(HPT + key);
			pl_ext.hdel(HPR + ar[0], ar[1]);
			
			if(count >= staBatch){
				pl_handset.sync();
				pl_handset.close();
				pl_handset = null;
				pl_ext.sync();
				pl_ext.close();
				pl_ext = null;
				
				pl_handset = jedis_handset.pipelined();
				pl_ext = jedis_ext.pipelined();
				count = 0;
			}
		}

		if(count > 0){
			pl_handset.sync();
			pl_handset.close();
			pl_handset = null;
			pl_ext.sync();
			pl_ext.close();
			pl_ext = null;
		}
	}
	
	
	/*
	 * 遍历每个hashset， 一次取出整个hashset集合，
	 * 遍历集合中的filed，并获取value， 根据value判断是否过期，如果过期，加入待清理列表.
	 * 调用批量清理
	 * 
	 *	终端详细信息
	 *	HEP.%05d的哈希值（设备mac-终端mac然后计算hash） dmac-hmac作为filed， filed value为json字符串，记录终端在此设备上的详细关联信息
	 */
	private void clearStartFromHEP() throws IOException{
		System.out.println("clearStartFromHEP");
		List<String> list = null;
    	Set<String> keys = jedis_handset.keys("*");
    	for(String key:keys){
    		if(!key.startsWith(HEP))
    			continue;
    		Map<String, String> all = jedis_handset.hgetAll(key);
    		list = new ArrayList<String>(all.size());
    		for(String dmac_hdmac:all.keySet()){
    			String val = all.get(dmac_hdmac);
    			HandsetDeviceDTO dto = JsonHelper.getDTO(val, HandsetDeviceDTO.class);
				System.out.println("ts:" + sdf.format(new Date(dto.getTs())));
    			if(System.currentTimeMillis() - dto.getTs() > expire){
    				System.out.println(dmac_hdmac + " expired, add to list");
    				list.add(dmac_hdmac);
    			} else {
    				System.out.println(dmac_hdmac + " using");
    			}
    		}
    		batchClear(list);
    		list.clear();
    		list = null;
    	}
   	}
	
	
	private void handleHPR(Pipeline pl, Map<String,Response<Map<String,String>>> rp) throws IOException{
		List<String> list = new ArrayList<String>(10000);
	    pl.sync();
	    for(String mac:rp.keySet()){
			Response<Map<String, String>> response = rp.get(mac);
			if(response == null){
				continue;
			} 
			Map<String, String> stas = response.get();
			if(stas == null || stas.size() <= 0)
				continue;
			for(String hdmac:stas.keySet()){
				long lastop = Long.parseLong(stas.get(hdmac).substring(1));
				System.out.println("lastop:" + sdf.format(new Date(lastop)));
				if(System.currentTimeMillis() - lastop > expire){
					System.out.println(mac + "-" + hdmac + " expire, add to list");
					list.add(mac + "-" + hdmac);
				} else {
					System.out.println(mac + "-" + hdmac + " using");
				}
			}
	    }
	    pl.close();
	    pl = null;
		rp.clear();
		
		batchClear(list);
	}
	
	

	/**
	 * 遍历数据库中所有设备，找到上线过的设备
	 * 从设备的接入终端hashset中，判断终端是否过期，如果过期，则加入待清理列表
	 * 调用批量清理
	 * 
	 * 设备上的所有接入过的终端
     * HPR.devMac,      hashset形式, 每个终端mac作为一个filed，value为最后一次上线或者下线的时间.
	 */
	private void clearStartFromHPR() throws IOException{
		System.out.println("clearStartFromHPR");
		int batchStep = 20;
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ");
		mc.setOrderByClause("id desc");
		mc.setPageNumber(1);
		mc.setPageSize(200);
    	EntityIterator<String, WifiDevice> it = new KeyBasedEntityBatchIterator<String, WifiDevice>(String.class, WifiDevice.class, wifiDeviceService.getEntityDao(), mc);
    	List<String> list = new ArrayList<String>(20000);
		while(it.hasNext()){
			List<WifiDevice> devlist = it.next();
			for(WifiDevice dev:devlist){
				if(dev.getFirst_reged_at() == null)
					continue;
				list.add(dev.getId());
			}
		}
		if(list.size() <= 0)
			return;
		Pipeline pl = jedis_ext.pipelined();
	    Map<String,Response<Map<String,String>>> rp = new HashMap<String, Response<Map<String, String>>>(batchStep);
	    int macCount = 0;
	    for(String mac:list){
	    	macCount ++;
	    	rp.put(mac, pl.hgetAll(HPR + mac));
	    	
	    	if(macCount >= batchStep){
	    		handleHPR(pl, rp);
	    	    rp = new HashMap<String, Response<Map<String, String>>>(batchStep);
	    		pl = jedis_ext.pipelined();
	    		macCount = 0;
	    	}
	    	
	    }
	    if(macCount > 0){
    		handleHPR(pl, rp);
	    }
	}
	
	
	private void _ClearHistoryLog(Pipeline pl, Map<String, Response<List<String>>> stalogrp) throws IOException{
    	pl.sync();
		pl.close();
		pl = null;

		System.out.println("pipe synced");
		
		Pipeline plw = jedis_ext.pipelined();
		
	    for(String stakey:stalogrp.keySet()){
	    	System.out.println("checking list for " + stakey);
	    	Response<List<String>> rplist = stalogrp.get(stakey);
	    	if(rplist == null || rplist.get() == null || rplist.get().size() == 0){
				System.out.println(String.format("key[%s] no data, need to remove it", stakey));
					plw.del(stakey);
				continue;
	    	}

	    	System.out.println("stakey:" + stakey);
	    	int logcount = 0;
			List<String> logs = rplist.get();
			for(int i = logs.size(); i > 0; i --){
				HandsetLogDTO hlog = JsonHelper.getDTO(logs.get(i - 1), HandsetLogDTO.class);
				System.out.println(String.format("Log[%d], online[%s], offline[%s]", i, 
						sdf.format(new Date(hlog.getO())), sdf.format(new Date(hlog.getF()))));
				logcount ++;
				if(System.currentTimeMillis() - hlog.getO() > expire){
					System.out.println(String.format("key [%s] contains expired data, try to trim", stakey));
						plw.ltrim(stakey, -(logcount - 1), -1);
					break;
				}
			}
	    }
		
		plw.sync();
		plw.close();
		plw = null;

	}
	private void batchClearHandSetHistorLog(List<String> list) throws IOException{
		Pipeline pl = jedis_ext.pipelined();
	    Map<String, Response<List<String>>> stalogrp = new HashMap<String, Response<List<String>>>(staBatch);

		int count = 0;
		for(String key:list){
			count ++;
			stalogrp.put(HPL+key, pl.lrange(HPL+key, 0, -1));
			System.out.println("add " + HPL + key + " to pipe");
			if(count >= staBatch){
				_ClearHistoryLog(pl, stalogrp);
				stalogrp.clear();
				pl = jedis_ext.pipelined();
				count = 0;
			}
		}
		if(count > 0){
			_ClearHistoryLog(pl, stalogrp);
			stalogrp.clear();
		}
	}
	
	private void clearHandsetHistoryLog() throws IOException{
		System.out.println("clearHandsetHistoryLog");
		List<String> list = null;
    	Set<String> keys = jedis_handset.keys("*");
    	for(String key:keys){
    		if(!key.startsWith(HEP))
    			continue;
    		Map<String, String> all = jedis_handset.hgetAll(key);
    		list = new ArrayList<String>(all.size());
    		for(String dmac_hdmac:all.keySet()){
    			list.add(dmac_hdmac);
    		}
    		batchClearHandSetHistorLog(list);
    		list.clear();
    		list = null;
    	}
	}
	
	public void doClear() throws IOException{
		String[] CONFIG = {"/com/bhu/vas/di/business/dataimport/dataImportCtx.xml"};
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONFIG, ClearExpireHandsetLogOp.class);
		ctx.start();
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");

		setWifiDeviceService(wifiDeviceService);
		
		try{
			clearStartFromHEP();
			clearStartFromHPR();
			clearHandsetHistoryLog();
		}catch(Exception e){
			e.printStackTrace();
		}
		ctx.stop();
	}
	
	
	public void doCount(){
		System.out.println("clearStartFromHEP");
		long total = 0;
    	Set<String> keys = jedis_handset.keys("*");
    	for(String key:keys){
    		if(!key.startsWith(HEP))
    			continue;
    		Long ct  = jedis_handset.hlen(key);
    		System.out.println(key + " : " + ct);
    		total += ct;
    	}
		System.out.println("total : " + total);
	}

	public static void main(String[] argv) throws Exception{

		long t0 = System.currentTimeMillis();
		ClearExpireHandsetLogOp op = new ClearExpireHandsetLogOp();
		op.doClear();
		//op.doCount();
		System.out.println(System.currentTimeMillis());
		System.exit(0);
	}
}
