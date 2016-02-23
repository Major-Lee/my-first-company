package com.bhu.pure.kafka.business.consumer;

import com.bhu.pure.kafka.client.consumer.StringKafkaMessageConsumer;

public class DeliverMessageTopicConsumer extends StringKafkaMessageConsumer{
	
	public void init(){
		super.doSubscribeTopics(new DeliverMessageTopicNotify());
	}
}
