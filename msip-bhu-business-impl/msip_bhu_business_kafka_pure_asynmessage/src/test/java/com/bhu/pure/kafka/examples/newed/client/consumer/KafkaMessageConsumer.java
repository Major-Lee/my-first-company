/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bhu.pure.kafka.examples.newed.client.consumer;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.pure.kafka.examples.newed.assigner.Assigner;
import com.bhu.pure.kafka.examples.newed.client.KafkaMessageClient;
import com.bhu.pure.kafka.examples.newed.client.consumer.callback.PollIteratorNotify;
import com.bhu.pure.kafka.examples.newed.subscribe.Subscriber;
import com.bhu.pure.kafka.examples.newed.subscribe.TopicPatternSubscriber;
import com.bhu.pure.kafka.examples.newed.subscribe.TopicRebalanceSubscriber;
import com.bhu.pure.kafka.examples.newed.subscribe.TopicSubscriber;

public abstract class KafkaMessageConsumer<KEY, VALUE> extends KafkaMessageClient implements IKafkaMessageConsumer<KEY, VALUE>{
	private static final Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);
	
	private KafkaConsumer<KEY, VALUE> consumer;
	//private List<TopicPartition> topicPartitions;
	
	public KafkaMessageConsumer(){
		initialize();
	}
	
	private void initialize(){
		logger.info("start consumer initialize");
		
		Properties clientProperties = loadProperties();
		if(clientProperties == null){
			throw new RuntimeException("KafkaMessageConsumer initialize failed require properties object");
		}
		consumer = new KafkaConsumer<KEY, VALUE>(clientProperties);
		
		//parseConsumerClientConfig(clientProperties);
	}
	
/*	private void parseConsumerClientConfig(Properties clientProperties){
		try{
			String consumer_topics = clientProperties.getProperty(ClientConfig.CONSUMER_TOPIC);
			if(StringUtils.isNotEmpty(consumer_topics)){
				String[] consumer_topics_array = consumer_topics.split(StringHelper.COMMA_STRING_GAP);
				int topics_length = consumer_topics_array.length;
				if(topics_length > 0){
					topicPartitions = new ArrayList<TopicPartition>();
					for(String consumer_topic : consumer_topics_array){
						String[] consumer_topic_array = consumer_topic.split(StringHelper.AT_STRING_GAP);
						int topic_length = consumer_topic_array.length;
						String topic = null;
						String partitions_text = null;
						if(topic_length > 0){
							List<Integer> partitions = new ArrayList<Integer>();
							topic = consumer_topic_array[0];
							//如果只配置topic而没有指名具体的分区, 则表示消费该topic的所有分区
							if(topic_length == 1){
								List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
								int num_partitions = partitionInfos.size();
								for(int i = 0;i<num_partitions;i++){
									partitions.add(i);
								}
							}
							//如果只配置topic并且指名具体的分区, 则表示消费该topic的指定分区
							else if(topic_length == 2){
								partitions_text = consumer_topic_array[1];
								if(StringUtils.isNotEmpty(partitions_text)){
									String[] partitions_text_array = partitions_text.split(StringHelper.POINT_STRING_GAP);
									int length = partitions_text_array.length;
									if(length > 0){
										for(String partition_text : partitions_text_array){
											partitions.add(Integer.parseInt(partition_text));
										}
									}
								}
							}
							
							if(!partitions.isEmpty()){
								for(Integer partition : partitions){
									topicPartitions.add(new TopicPartition(topic, partition));
								}
							}
						}
					}
				}
			}
			
			printTopicPartitionsLog();
		}catch(Exception ex){
			throw new RuntimeException("KafkaMessageProducer parseClientConfig failed", ex);
		}
	}
	
	private void printTopicPartitionsLog(){
		if(topicPartitions != null && !topicPartitions.isEmpty()){
			for(TopicPartition topic_partition : topicPartitions){
				logger.info(String.format("ParseConsumerClientConfig specify topicPartition [%s %s]", 
						topic_partition.topic(), topic_partition.partition()));
			}
		}
	}*/
	@Override
	public boolean doSubscribeTopics(List<String> topics, final PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify){
		return doSubscribe(new TopicSubscriber(topics), notify);
	}
	
	@Override
	public boolean doSubscribe(Subscriber subscriber, final PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify) {
		if(subscriber == null || notify == null) return false;
		
		if(subscriber instanceof TopicPatternSubscriber){
			TopicPatternSubscriber tps = (TopicPatternSubscriber)subscriber;
			consumer.subscribe(tps.getPattern(), tps.getConsumerRebalanceListener());
		}
		else if(subscriber instanceof TopicRebalanceSubscriber){
			TopicRebalanceSubscriber trs = (TopicRebalanceSubscriber)subscriber;
			consumer.subscribe(trs.getTopics(), trs.getConsumerRebalanceListener());
		}
		else if(subscriber instanceof TopicSubscriber){
			TopicSubscriber ts = (TopicSubscriber)subscriber;
			consumer.subscribe(ts.getTopics());
		}else{
			return false;
		}

		poll(notify);
		
		return true;
	}
	
	@Override
	public boolean doAssgin(Assigner assigner, final PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify){
		if(assigner == null) return false;
		
		List<TopicPartition> topicPartitions = assigner.getTopicPartitions();
		if(topicPartitions == null || topicPartitions.isEmpty()) return false;
		
		consumer.assign(topicPartitions);
		
		poll(notify);
		
		return true;
	}
	
	protected void poll(final PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify){
		Executors.newSingleThreadExecutor().submit((new Runnable() {
			@Override
			public void run() {
				while(true){
					System.out.println("start consumer poll");
					ConsumerRecords<KEY, VALUE> records = consumer.poll(pollSize());
					notify.notifyComming(records);
				}
			}
		}));
	}
	
	@Override
	public long pollSize() {
		return DEFAULT_POLLSIZE;
	}
//	public abstract Subscriber getSubscriber();
	
}