package com.bhu.statistics.util.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bhu.statistics.util.redis.IJedisClient;
import com.bhu.statistics.util.redis.JedisClientImpl;


/**
 * redis 缓存管理
 * @author lixp
 *
 */
public class Cache{

	private static IJedisClient jedisClient;
	
	private static Cache cache=new Cache();
	
	private Cache(){
		jedisClient = new JedisClientImpl("redis_m");
	}
	
	public static Cache getInstance(){
		return cache;
	}
	
	public String get(String key) {
		return jedisClient.get(key);
	}

	
	public String set(String key, String value) {
		return jedisClient.set(key, value);
	}

	
	public Long del(String... keys) {
		return jedisClient.del(keys);
	}

	
	public Long append(String key, String str) {
		return jedisClient.append(key, str);
	}

	
	public boolean exists(String key) {
		return jedisClient.exists(key);
	}

	
	public Long setnx(String key, String value) {
		return jedisClient.setnx(key, value);
	}

	
	public String setex(String key, String value, int seconds) {
		return jedisClient.setex(key, value, seconds);
	}

	
	public Long setrange(String key, String str, int offset) {
		return jedisClient.setrange(key, str, offset);
	}

	
	public List<String> mget(String... keys) {
		return jedisClient.mget(keys);
	}

	
	public String mset(String... keysvalues) {
		return jedisClient.mset(keysvalues);
	}

	
	public Long msetnx(String... keysvalues) {
		return jedisClient.msetnx(keysvalues);
	}

	
	public String getset(String key, String value) {
		return jedisClient.getset(key, value);
	}

	
	public String getrange(String key, int startOffset, int endOffset) {
		return jedisClient.getrange(key, startOffset, endOffset);
	}

	
	public Long incr(String key) {
		return jedisClient.incr(key);
	}

	
	public Long incrBy(String key, Long integer) {
		return jedisClient.incrBy(key, integer);
	}

	
	public Long decr(String key) {
		return jedisClient.decr(key);
	}

	
	public Long decrBy(String key, Long integer) {
		return jedisClient.decrBy(key, integer);
	}

	
	public Long serlen(String key) {
		return jedisClient.serlen(key);
	}

	
	public Long hset(String key, String field, String value) {
		return jedisClient.hset(key, field, value);
	}

	
	public Long hsetnx(String key, String field, String value) {
		return jedisClient.hsetnx(key, field, value);
	}

	
	public String hmset(String key, Map<String, String> hash) {
		return jedisClient.hmset(key, hash);
	}

	
	public String hget(String key, String field) {
		return jedisClient.hget(key, field);
	}

	
	public List<String> hmget(String key, String... fields) {
		return jedisClient.hmget(key, fields);
	}

	
	public Long hincrby(String key, String field, Long value) {
		return jedisClient.hincrby(key, field, value);
	}

	
	public Boolean hexists(String key, String field) {
		return jedisClient.hexists(key, field);
	}

	
	public Long hlen(String key) {
		return jedisClient.hlen(key);
	}

	
	public Long hdel(String key, String... fields) {
		return jedisClient.hdel(key, fields);
	}

	
	public Set<String> hkeys(String key) {
		return jedisClient.hkeys(key);
	}

	
	public List<String> hvals(String key) {
		return jedisClient.hvals(key);
	}

	
	public Map<String, String> hgetall(String key) {
		return jedisClient.hgetall(key);
	}

	
	public Long lpush(String key, String... strs) {
		return jedisClient.lpush(key, strs);
	}

	
	public Long rpush(String key, String... strs) {
		return jedisClient.rpush(key, strs);
	}

	
	public String lset(String key, Long index, String value) {
		return jedisClient.lset(key, index, value);
	}

	
	public Long lrem(String key, long count, String value) {
		return jedisClient.lrem(key, count, value);
	}

	
	public String ltrim(String key, long start, long end) {
		return jedisClient.ltrim(key, start, end);
	}

	
	public String lpop(String key) {
		return jedisClient.lpop(key);
	}

	
	public String rpop(String key) {
		return jedisClient.rpop(key);
	}

	
	public String rpoplpush(String srckey, String dstkey) {
		return jedisClient.rpoplpush(srckey, dstkey);
	}

	
	public String lindex(String key, long index) {
		return jedisClient.lindex(key, index);
	}

	
	public Long llen(String key) {
		return jedisClient.llen(key);
	}

	
	public List<String> lrange(String key, long start, long end) {
		return jedisClient.lrange(key, start, end);
	}

	
	public Long sadd(String key, String... members) {
		return jedisClient.sadd(key, members);
	}

	
	public Long srem(String key, String... members) {
		return jedisClient.srem(key, members);
	}

	
	public String spop(String key) {
		return jedisClient.spop(key);
	}

	
	public Set<String> sdiff(String... keys) {
		return jedisClient.sdiff(keys);
	}

	
	public Long sdiffstore(String dstkey, String... keys) {
		return jedisClient.sdiffstore(dstkey, keys);
	}

	
	public Set<String> sinter(String... keys) {
		return jedisClient.sinter(keys);
	}

	
	public Long sinterstore(String dstkey, String... keys) {
		return jedisClient.sinterstore(dstkey, keys);
	}

	
	public Set<String> sunion(String... keys) {
		return jedisClient.sunion(keys);
	}

	
	public Long sunionstore(String dstkey, String... keys) {
		return jedisClient.sunionstore(dstkey, keys);
	}

	
	public Long smove(String srckey, String dstkey, String member) {
		return jedisClient.smove(srckey, dstkey, member);
	}

	
	public Long scard(String key) {
		return jedisClient.scard(key);
	}

	
	public Boolean sismember(String key, String member) {
		return jedisClient.sismember(key, member);
	}

	
	public String srandmember(String key) {
		return jedisClient.srandmember(key);
	}

	
	public Set<String> smembers(String key) {
		return jedisClient.smembers(key);
	}

	
	public Long zadd(String key, Map<Double, String> scoreMembers) {
		return jedisClient.zadd(key, scoreMembers);
	}

	
	public Long zadd(String key, double score, String member) {
		return jedisClient.zadd(key, score, member);
	}

	
	public Long zrem(String key, String... members) {
		return jedisClient.zrem(key, members);
	}

	
	public Double zincrby(String key, double score, String member) {
		return jedisClient.zincrby(key, score, member);
	}

	
	public Long zrank(String key, String member) {
		return jedisClient.zrank(key, member);
	}

	
	public Long zrevrank(String key, String member) {
		return jedisClient.zrevrank(key, member);
	}

	
	public Set<String> zrevrange(String key, long start, long end) {
		return jedisClient.zrevrange(key, start, end);
	}

	
	public Set<String> zrangebyscore(String key, String max, String min) {
		return jedisClient.zrangebyscore(key, max, min);
	}

	
	public Set<String> zrangeByScore(String key, double max, double min) {
		return jedisClient.zrangeByScore(key, max, min);
	}

	
	public Long zcount(String key, String min, String max) {
		return jedisClient.zcount(key, min, max);
	}

	
	public Long zcard(String key) {
		return jedisClient.zcard(key);
	}

	
	public Double zscore(String key, String member) {
		return jedisClient.zscore(key, member);
	}

	
	public Long zremrangeByRank(String key, long start, long end) {
		return jedisClient.zremrangeByRank(key, start, end);
	}

	
	public Long zremrangeByScore(String key, double start, double end) {
		return jedisClient.zremrangeByScore(key, start, end);
	}

	
	public Set<String> keys(String pattern) {
		return jedisClient.keys(pattern);
	}

	
	public String type(String key) {
		return jedisClient.type(key);
	}

}
