package com.bhu.pure.kafka.examples.newed.assigner;

import java.util.List;

import org.apache.kafka.common.TopicPartition;

public interface Assigner {
	public Assigner addTopicPartition(String topic, int partition);
	public List<TopicPartition> getTopicPartitions();
}
