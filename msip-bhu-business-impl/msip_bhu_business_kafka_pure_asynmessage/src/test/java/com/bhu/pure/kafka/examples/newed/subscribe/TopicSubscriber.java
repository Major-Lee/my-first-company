package com.bhu.pure.kafka.examples.newed.subscribe;

import java.util.List;

public class TopicSubscriber implements Subscriber{
	private List<String> topics;

	public TopicSubscriber(List<String> topics){
		this.topics = topics;
	}
	
	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}
	
	@Override
	public long pollSize() {
		return DEFAULT_POLLSIZE;
	}
}
