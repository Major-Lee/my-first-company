package com.bhu.pure.kafka.assigner;

import java.util.List;

import org.apache.kafka.common.TopicPartition;

public interface Assigner {
	public Assigner addTopicPartition(String topic, int partition);
	public List<TopicPartition> getTopicPartitions();
}
