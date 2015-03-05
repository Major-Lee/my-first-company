package com.smartwork.multiplexer.spi.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.im.extension.support.cache.CacheType;
import com.smartwork.im.extension.support.cache.IEntityCache;
import com.smartwork.im.net.Session;

/**
 * 一个默认的本地Cache的实现，线程安全
 * @author Edmond Lee
 *
 */
public class SessionCacheImpl implements IEntityCache<String,Session>{
	private static final Logger logger = LoggerFactory.getLogger(SessionCacheImpl.class);
	
	/**
	 * 具体内容存放的地方
	 */
	Map<String, Session>[] caches;
	Set<String> keys;
	/**
	 * 内部cache的个数，根据key的hash对module取模来定位到具体的某一个内部的Map，
	 * 减小阻塞情况发生。
	 */
	private int moduleSize = 10;
	
	public SessionCacheImpl(){
		init();
	}
	
	public SessionCacheImpl(int moduleSize){
		this.moduleSize = moduleSize;
		init();
	}
	
	
	@SuppressWarnings("unchecked")
	public void init(){
		caches = new ConcurrentHashMap[moduleSize];
		
		for(int i = 0 ; i < moduleSize ;i ++)
			caches[i] = new ConcurrentHashMap<String, Session>();
		
		keys = new HashSet<String>();
	}
	
	public boolean clear()
	{
		if (caches != null){
			for(Map<String, Session> cache : caches){
				cache.clear();
			}
		}
		if(keys != null)
			keys.clear();
		return true;
	}


	public boolean containsKey(String key){
		return getCache(key).containsKey(key);
	}


	public Session get(String key){
		return getCache(key).get(key);
	}
	
	public String sessionsStat(boolean printDetail){
		StringBuilder sb = new StringBuilder("prepare statics Online Routing data of MDS CM Server\n");
		int alldstotal = 0;
		
		for(Map<String, Session> cache : caches){
			Iterator<Entry<String, Session>> iterDe =cache.entrySet().iterator();
			while(iterDe.hasNext()){
				Entry<String, Session> entry = iterDe.next();
				sb.append(entry.getValue().infos()).append("\n");
				alldstotal++;
			}
		}
		
		sb.append("All Online Routing data Total:").append(alldstotal).append("\n");
		//sb.append("******************************\n");
		return sb.toString();
	}
	
	
	public Collection<Session> values(){
		//checkAll();
		Collection<Session> values = new ArrayList<Session>();
		for(Map<String, Session> cache : caches){
			values.addAll(cache.values());	
		}
		return values;
	}
	
	public Set<String> keySet(){
		return keys;
		/*if(expirySupport){
			checkAll();
			return expiryCache.keySet();
		}else{
			throw new RuntimeException("can not fecth cache keySet when expirySupport["+expirySupport+"]");
		}*/
	}
	
	/*public Set<String> keySet4destination(String destination){
		Set<String> result = destinationcache.get(destination);
		if(result == null || result.isEmpty()) return Collections.emptySet();
		return new HashSet<String>(result);
	}*/
	
	public int size(){
		if(keys != null) return keys.size();
		return 0;
		/*if(expirySupport){
			checkAll();
			return expiryCache.size();
		}else{
			throw new RuntimeException("can not fecth cache size when expirySupport["+expirySupport+"]");
		}*/
	}

	public Session put(String key, Session info){
		Session result = getCache(key).put(key, info);
		keys.add(key);
		/*Set<String> destinationKeys = destinationcache.get(info.getSigned_server());
		if(destinationKeys == null){
			destinationKeys = new HashSet<String>();
			destinationKeys.add(key);
			destinationcache.put(info.getSigned_server(), destinationKeys);
		}else{
			destinationKeys.add(key);
		}*/
/*		if(expirySupport){
			expiryCache.put(key,(long)-1);
		}*/
		
		return result;
	}

	public Session remove(String key)
	{
		Session result = getCache(key).remove(key);
		keys.remove(key);
		return result;
	}


	public void destroy() {
		try{
			clear();
		}catch(Exception ex){
			logger.error("destory",ex);
		}
	}

	@Override
	public CacheType getType() {
		return CacheType.LOCAL_MAP;
	}

	/*public Map<String, Session>[] getRoutingHashCaches(){
		return caches;
	}*/
	
	private Map<String, Session> getCache(String key){
		long hashCode = (long)key.hashCode();
		if (hashCode < 0)
			hashCode = -hashCode;
		int moudleNum = (int)hashCode % moduleSize;
		return caches[moudleNum];
	}
	
	/*private void checkValidate(String key){
		if(expirySupport){
			if (expiryCache.get(key) != null && expiryCache.get(key) != -1 && new Date(expiryCache.get(key)).before(new Date())){
				getCache(key).remove(key);
				expiryCache.remove(key);
			}
		}
	}
	
	private void checkAll(){
		if(expirySupport){
			Iterator<String> iter = expiryCache.keySet().iterator();
			while(iter.hasNext()){
				String key =  iter.next();
				checkValidate(key);
			}
		}
	}*/
	
	/*public String routingStat(boolean printDetail){
		StringBuilder sb = new StringBuilder("prepare statics Online Routing data of MDS Server\n");
		int alldstotal = 0;
		Iterator<Entry<String, Set<String>>> iterDe = destinationcache.entrySet().iterator();
		while(iterDe.hasNext()){
			Entry<String, Set<String>> entry = iterDe.next();
			int total = entry.getValue().size();
			sb.append("Node Server:").append(entry.getKey()).append("	Total:").append(total).append("\n");
			if(printDetail){//打印详细信息
				for(String user:entry.getValue()){
					sb.append(this.get(user)).append("\n");
				}
			}
			alldstotal +=total;
		}
		sb.append("All Node Server Total:").append(alldstotal).append("\n");
		//sb.append("******************************\n");
		return sb.toString();
	}*/
	
	@Override
	public Session put(String key, Session value, Date expiry) {
		throw new UnsupportedOperationException("method put(String key, Session value, Date expiry)");
	}

	@Override
	public Session put(String key, Session value, int TTL) {
		throw new UnsupportedOperationException("method put(String key, Session value, int TTL)");
	}
}
