package com.bhu.pure.kafka.examples.newed.client.consumer;

import com.bhu.pure.kafka.examples.newed.client.consumer.KafkaMessageConsumer;

public class StringKafkaMessageConsumer extends KafkaMessageConsumer<String, String>{

	@Override
	public long pollSize() {
		return DEFAULT_POLLSIZE;
	}

}
