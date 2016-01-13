package com.bhu.pure.kafka.examples.newed.subscribe;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;

public class TopicRebalanceSubscriber extends TopicSubscriber{
	
	private ConsumerRebalanceListener consumerRebalanceListener;
	
	public TopicRebalanceSubscriber(List<String> topics) {
		super(topics);
	}

	public TopicRebalanceSubscriber(List<String> topics, ConsumerRebalanceListener consumerRebalanceListener) {
		super(topics);
		this.consumerRebalanceListener = consumerRebalanceListener;
	}

	public ConsumerRebalanceListener getConsumerRebalanceListener() {
		return consumerRebalanceListener;
	}

	public void setConsumerRebalanceListener(
			ConsumerRebalanceListener consumerRebalanceListener) {
		this.consumerRebalanceListener = consumerRebalanceListener;
	}
}
