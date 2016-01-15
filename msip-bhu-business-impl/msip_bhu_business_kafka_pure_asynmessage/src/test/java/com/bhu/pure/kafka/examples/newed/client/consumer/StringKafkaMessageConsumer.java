package com.bhu.pure.kafka.examples.newed.client.consumer;


public class StringKafkaMessageConsumer extends KafkaMessageConsumer<String, String>{

	private static class ServiceHolder{ 
		private static StringKafkaMessageConsumer instance =new StringKafkaMessageConsumer(); 
	}

	public static StringKafkaMessageConsumer getInstance() { 
		return ServiceHolder.instance; 
	}

	
	@Override
	public long pollSize() {
		return DEFAULT_POLLSIZE;
	}

}
