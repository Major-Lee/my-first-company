package com.bhu.pure.kafka.client.producer;



public class StringKafkaMessageProducer extends KafkaMessageProducer<String, String>{
	
	private static class ServiceHolder{ 
		private static StringKafkaMessageProducer instance =new StringKafkaMessageProducer(); 
	}

	public static StringKafkaMessageProducer getInstance() { 
		return ServiceHolder.instance; 
	}

}
