package com.bhu.statistics.util.redis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangsongpu
 * @date:2015-7-11 下午5:49:36
 */
public interface IJedisClient {
	
	public static final long CACHE_TIME_ONE_MINITS = 60 * 1000;
	public static final long CACHE_TIME_HALF_HOUR = 30 * CACHE_TIME_ONE_MINITS;
	public static final long CACHE_TIME_ONE_HOUR = 2 * CACHE_TIME_HALF_HOUR;
	public static final long CACHE_TIME_ONE_DAY = 24 * CACHE_TIME_ONE_HOUR;

	public String get(String key);
	public String set(String key, String value);
	public Long del(String... keys);
	
	public Long append(String key, String str);
	public boolean exists(String key);
	public Long setnx(String key, String value);
	
	public String setex(String key, String value, int seconds);
	public Long setrange(String key, String str, int offset);
	public List<String> mget(String... keys);
	public String mset(String... keysvalues);
	public Long msetnx(String... keysvalues);
	
	public String getset(String key, String value);
	public String getrange(String key, int startOffset, int endOffset);
	public Long incr(String key);
	public Long incrBy(String key, Long integer);	
	public Long decr(String key);
	public Long decrBy(String key, Long integer);
	
	public Long serlen(String key);
	public Long hset(String key, String field, String value);
	public Long hsetnx(String key, String field, String value);
	public String hmset(String key, Map<String, String> hash);
	public String hget(String key, String field);
	
	public List<String> hmget(String key, String... fields);
	public Long hincrby(String key, String field, Long value);
	
	public Boolean hexists(String key, String field);
	public Long hlen(String key);
	public Long hdel(String key, String... fields);
	
	public Set<String> hkeys(String key);
	public List<String> hvals(String key);
	public Map<String, String> hgetall(String key);
	public Long lpush(String key, String... strs);
	public Long rpush(String key, String... strs);
	public String lset(String key, Long index, String value);
	public Long lrem(String key, long count, String value);
	public String ltrim(String key, long start, long end);
	public String lpop(String key);
	public String rpop(String key);
	public String rpoplpush(String srckey, String dstkey);
	public String lindex(String key, long index);
	public Long llen(String key);
	public List<String> lrange(String key, long start, long end);
	public Long sadd(String key, String... members);
	public Long srem(String key, String... members);
	public String spop(String key);
	public Set<String> sdiff(String... keys);
	public Long sdiffstore(String dstkey, String... keys);
	public Set<String> sinter(String... keys);
	public Long sinterstore(String dstkey, String... keys);
	public Set<String> sunion(String... keys);
	public Long sunionstore(String dstkey, String... keys);
	public Long smove(String srckey, String dstkey, String member);
	public Long scard(String key);
	public Boolean sismember(String key, String member);
	public String srandmember(String key);
	public Set<String> smembers(String key);
	public Long zadd(String key, Map<Double, String> scoreMembers);
	public Long zadd(String key, double score, String member);
	public Long zrem(String key, String... members);
	public Double zincrby(String key, double score, String member);
	public Long zrank(String key, String member);
	public Long zrevrank(String key, String member);
	public Set<String> zrevrange(String key, long start, long end);
	public Set<String> zrangebyscore(String key, String max, String min);
	public Set<String> zrangeByScore(String key, double max, double min);
	public Long zcount(String key, String min, String max);
	public Long zcard(String key);
	public Double zscore(String key, String member);
	public Long zremrangeByRank(String key, long start, long end);
	public Long zremrangeByScore(String key, double start, double end);
	public Set<String> keys(String pattern);
	public String type(String key);
	public String setObject(String key,Object value);
	public <T> T getObject(String key,Class<T> c);
	public <M> String setList(String key,List<M> list);
	public <T> List<T> getList(String key,Class<T> c);
}
