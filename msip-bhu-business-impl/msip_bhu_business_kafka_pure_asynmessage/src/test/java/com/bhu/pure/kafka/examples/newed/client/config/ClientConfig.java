package com.bhu.pure.kafka.examples.newed.client.config;

import org.apache.commons.lang.StringUtils;

import com.bhu.pure.kafka.examples.newed.helper.StringHelper;

public class ClientConfig {
/*	//配置生产者的具体一个topic和分区 topic1@0.1.2 或 topic1
	public static final String PRODUCER_TOPIC = "clientconfig.producer.topic";
	//配置消费者的具体多个topic和分区 topic1@0.1.2,topic2@0.1.2,topic3
	public static final String CONSUMER_TOPIC = "clientconfig.consumer.topic";
	
	public class Partition{
		//表示所有分区的值
		public static final int ALLPARTITION = -1;
	}*/
	
	//配置生产者的具体一个或多个subscribe topic
	public static final String CONSUMER_SUBSCRIBE_TOPICS = "consumer.subscribe.topics";
	
	public static String builderConsumerSubscribeTopicsWithId(String consumerId){
		if(StringUtils.isEmpty(consumerId)) return CONSUMER_SUBSCRIBE_TOPICS;
		return consumerId.concat(StringHelper.POINT_STRING_GAP).concat(CONSUMER_SUBSCRIBE_TOPICS);
	}
}
