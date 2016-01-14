package com.bhu.pure.kafka.examples.newed.partitioner;

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
        	return key.hashCode() % numPartitions;
        }
		return 0;
	}
}
