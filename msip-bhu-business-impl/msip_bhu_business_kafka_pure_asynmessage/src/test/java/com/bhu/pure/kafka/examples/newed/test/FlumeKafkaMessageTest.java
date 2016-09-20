package com.bhu.pure.kafka.examples.newed.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.bhu.pure.kafka.assigner.Assigner;
import com.bhu.pure.kafka.assigner.TopicAssigner;
import com.bhu.pure.kafka.client.consumer.StringKafkaMessageConsumer;
import com.bhu.pure.kafka.client.consumer.callback.PollIteratorNotify;
import com.bhu.pure.kafka.client.producer.StringKafkaMessageProducer;
import com.bhu.pure.kafka.client.producer.callback.KeyValueProducerCallback;
import com.bhu.pure.kafka.subscribe.TopicSubscriber;


public class FlumeKafkaMessageTest {
	public static final String TOPIC1 = "topic1";
	public static final String TOPIC2 = "topic2";
	public static final String TOPIC3 = "topic3";
	
	public static final int PARTITION0 = 0;
	public static final int PARTITION1 = 1;
	
	public static void main(String[] args) throws Exception{
		TestConsumerSubscriberGroup();
	}
	
	
	/**
	 * subscriber方式consumer
	 * 可以实现多topic的全分区消费
	 * @throws Exception
	 */
	public static void TestConsumerSubscriberGroup() throws Exception{
		
		//consumer c2
		StringKafkaMessageConsumer consumer_c2 = new StringKafkaMessageConsumer("flume");
		List<String> topics = new ArrayList<String>();
		topics.add("flume_topic");
		consumer_c2.doSubscribeTopics(topics, new PollIteratorNotify<ConsumerRecords<String, String>>(){
			@Override
			public void notifyComming(String consumerId, ConsumerRecords<String, String> records) {
				for(ConsumerRecord<String, String> record : records){
					System.out.println(String
							.format("Received message: consumerId[%s] topic[%s] partition[%s] key[%s] value[%s] offset[%s]",
									consumerId, record.topic(), record.partition(),
									record.key(), record.value(),
									record.offset()));
				}
			}
		});
	}
	
}
