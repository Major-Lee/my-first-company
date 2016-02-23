package com.bhu.pure.kafka.client.config;

import org.apache.commons.lang.StringUtils;

import com.bhu.pure.kafka.helper.StringHelper;

public class ClientConfig {
/*	//配置生产者的具体一个topic和分区 topic1@0.1.2 或 topic1
	public static final String PRODUCER_TOPIC = "clientconfig.producer.topic";
	//配置消费者的具体多个topic和分区 topic1@0.1.2,topic2@0.1.2,topic3
	public static final String CONSUMER_TOPIC = "clientconfig.consumer.topic";
	
	public class Partition{
		//表示所有分区的值
		public static final int ALLPARTITION = -1;
	}*/
	public static final String GROUP_ID = "group.id";
	
	public static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
	//配置生产者的具体一个或多个subscribe topic
	public static final String CONSUMER_SUBSCRIBE_TOPICS = "consumer.subscribe.topics";
	
	public static String builderSubscribeTopicsWithId(String consumerId){
		if(StringUtils.isEmpty(consumerId)) return CONSUMER_SUBSCRIBE_TOPICS;
		return consumerId.concat(StringHelper.POINT_STRING_GAP).concat(CONSUMER_SUBSCRIBE_TOPICS);
	}
	
	public static String builderBootstrapServersWithId(String id){
		if(StringUtils.isEmpty(id)) return BOOTSTRAP_SERVERS;
		return id.concat(StringHelper.POINT_STRING_GAP).concat(BOOTSTRAP_SERVERS);
	}
	
	public static String builderGroupIdWithId(String consumerId){
		if(StringUtils.isEmpty(consumerId)) return GROUP_ID;
		return consumerId.concat(StringHelper.POINT_STRING_GAP).concat(GROUP_ID);
	}
}
