package com.bhu.statistics.util.redis;

import java.util.HashMap;
import java.util.Map;

import com.bhu.statistics.util.ConfigManager;
import com.bhu.statistics.util.Logger;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author zhangsongpu
 * @date:2015-7-10 下午6:25:28
 */
public class JedisClientPool {

	private static Logger log = new Logger(JedisClientPool.class);

	public static JedisClientPool instance = null;

	private Map<String, JedisPool> jpoolMap = new HashMap<String, JedisPool>();

	public static synchronized JedisClientPool getInstance() {
		if (instance == null) {
			instance = new JedisClientPool("redis");
		}
		return instance;
	}

	private JedisClientPool(String propertyFile) {
		int maxActive = ConfigManager.instance().getIntProperty(propertyFile, "redis.server.maxActive");
		int maxIdle = ConfigManager.instance().getIntProperty(propertyFile, "redis.server.maxIdle");
		long maxWait = ConfigManager.instance().getIntProperty(propertyFile, "redis.server.MaxWait");
		int timeOut = ConfigManager.instance().getIntProperty(propertyFile, "redis.server.timeout");
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxActive(maxActive);
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMaxWait(maxWait);
		jedisPoolConfig.setTestOnBorrow(true);
		
		String host = ConfigManager.instance().getProperty(propertyFile, "redis.master.server.ip");
		int port = ConfigManager.instance().getIntProperty(propertyFile, "redis.master.server.port");
		String masterPwd = ConfigManager.instance().getProperty(propertyFile,"redis.master.server.pwd");
		JedisPool jedisPool_master = new JedisPool(jedisPoolConfig, host, port, timeOut,masterPwd);
		jpoolMap.put("redis_m", jedisPool_master);
		
		String _host = ConfigManager.instance().getProperty(propertyFile, "redis.slave.server.ip");
		int _port = ConfigManager.instance().getIntProperty(propertyFile, "redis.slave.server.port");
		String slavePwd = ConfigManager.instance().getProperty(propertyFile,"redis.slave.server.pwd");
		JedisPool jedisPool_slave = new JedisPool(jedisPoolConfig, _host, _port, timeOut,slavePwd);
		jpoolMap.put("redis_s", jedisPool_slave);
	}

	protected JedisClientPool() {
	}

	public Map<String, JedisPool> getJpoolMap() {
		return jpoolMap;
	}

	public void setJpoolMap(Map<String, JedisPool> jpoolMap) {
		this.jpoolMap = jpoolMap;
	}

}
