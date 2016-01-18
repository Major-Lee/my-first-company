package com.bhu.pure.kafka.assigner;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.TopicPartition;

public class TopicAssigner implements Assigner{
	private List<TopicPartition> topicPartitions = new ArrayList<TopicPartition>();
	
	@Override
	public TopicAssigner addTopicPartition(String topic, int partition){
		if(topic != null && partition >= 0) {
			topicPartitions.add(new TopicPartition(topic, partition));
		}
		return this;
	}
	
	@Override
	public List<TopicPartition> getTopicPartitions() {
		return topicPartitions;
	}

/*	public void setTopicPartitions(List<TopicPartition> topicPartitions) {
		this.topicPartitions = topicPartitions;
	}*/
}
