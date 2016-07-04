package com.bhu.pure.kafka.partitioner;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

/**
 * 按照hash求余
 * 如果key为null 默认放到0分区
 * @author tangzichao
 *
 */
public class HashPartitioner implements Partitioner {

	// @Override
	// public int partition(Object key, int numPartitions) {
	// if(key == null) return 0;
	// return key.hashCode() % numPartitions;
	// }

	@Override
	public void configure(Map<String, ?> configs) {
	}

	@Override
	public void close() {

	}

	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, 
			byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        if (keyBytes != null) {
        	//return Math.abs(key.hashCode() % numPartitions);
        	return rotatingHash(String.valueOf(key), numPartitions);
        }
		return 0;
	}
	
	/**
	 * 旋转hash 保证正数
	 * 
	 * @param key 输入字符串
	 * @param prime 质数
	 * @return hash值
	 */
	public int rotatingHash(String key, int prime) {
		int hash, i;
		for (hash = key.length(), i = 0; i < key.length(); ++i)
			hash = (hash << 4) ^ (hash >> 28) ^ key.charAt(i);
		int value = (hash % prime);
		if(value < 0){
			return Math.abs(value);
		}
		return value;
	}
}
