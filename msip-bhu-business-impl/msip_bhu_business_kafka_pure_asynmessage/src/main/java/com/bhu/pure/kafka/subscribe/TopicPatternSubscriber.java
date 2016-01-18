package com.bhu.pure.kafka.subscribe;

import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;

public class TopicPatternSubscriber implements Subscriber{
	private Pattern pattern;
	private ConsumerRebalanceListener consumerRebalanceListener;
	
	public TopicPatternSubscriber(Pattern pattern, ConsumerRebalanceListener consumerRebalanceListener){
		this.pattern = pattern;
		this.consumerRebalanceListener = consumerRebalanceListener;
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	public ConsumerRebalanceListener getConsumerRebalanceListener() {
		return consumerRebalanceListener;
	}
	public void setConsumerRebalanceListener(
			ConsumerRebalanceListener consumerRebalanceListener) {
		this.consumerRebalanceListener = consumerRebalanceListener;
	}
}
