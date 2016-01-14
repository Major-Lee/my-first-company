package com.bhu.pure.kafka.examples.newed.client.config;

public class ClientConfig {
	//配置生产者的具体一个topic和分区 topic1@0.1.2 或 topic1
	public static final String PRODUCER_TOPIC = "clientconfig.producer.topic";
	//配置消费者的具体多个topic和分区 topic1@0.1.2,topic2@0.1.2,topic3
	public static final String CONSUMER_TOPIC = "clientconfig.consumer.topic";
	
	public class Partition{
		//表示所有分区的值
		public static final int ALLPARTITION = -1;
	}
}
