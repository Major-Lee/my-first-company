package com.bhu.pure.kafka.client.consumer;

import org.apache.kafka.common.serialization.StringDeserializer;

public class StringKafkaMessageConsumer extends KafkaMessageConsumer<String, String>{
	
	public StringKafkaMessageConsumer(){
		
	}
	
	public StringKafkaMessageConsumer(String consumerId){
		super(consumerId);
	}
	
	@Override
	public long pollSize() {
		return DEFAULT_POLLSIZE;
	}

	@Override
	public String keyDeserializer() {
		return StringDeserializer.class.getName();
	}

	@Override
	public String valueDeserializer() {
		return StringDeserializer.class.getName();
	}
}
