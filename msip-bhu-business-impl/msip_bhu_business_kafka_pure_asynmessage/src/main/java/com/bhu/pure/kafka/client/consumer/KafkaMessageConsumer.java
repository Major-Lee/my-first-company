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
package com.bhu.pure.kafka.client.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.internals.NoOpConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.pure.kafka.assigner.Assigner;
import com.bhu.pure.kafka.client.KafkaMessageClient;
import com.bhu.pure.kafka.client.config.ClientConfig;
import com.bhu.pure.kafka.client.consumer.callback.PollIteratorNotify;
import com.bhu.pure.kafka.helper.StringHelper;
import com.bhu.pure.kafka.subscribe.Subscriber;
import com.bhu.pure.kafka.subscribe.TopicPatternSubscriber;
import com.bhu.pure.kafka.subscribe.TopicRebalanceSubscriber;
import com.bhu.pure.kafka.subscribe.TopicSubscriber;

public abstract class KafkaMessageConsumer<KEY, VALUE> extends KafkaMessageClient implements IKafkaMessageConsumer<KEY, VALUE>{
	private static final Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final ExecutorService consumerExecutorService = Executors.newSingleThreadExecutor();
	
	private Properties consumerProperties;
	private String consumerId;
	private KafkaConsumer<KEY, VALUE> consumer;
	private List<String> subscribe_topics;
	private final AtomicBoolean subscribe_topics_changed = new AtomicBoolean(false);
	//private List<TopicPartition> topicPartitions;
	
	public KafkaMessageConsumer(){
		this(null);
	}
	
	public KafkaMessageConsumer(String consumerId){
		this.consumerId = consumerId;
		initialize();
	}
	
	private void initialize(){
		logger.info("start consumer initialize");
		
		consumerProperties = loadProperties();
		if(consumerProperties == null){
			throw new RuntimeException("KafkaMessageConsumer initialize failed require properties object");
		}
		consumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer());
		consumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer());
		loadConsumerIdProperties();
		
		consumer = new KafkaConsumer<KEY, VALUE>(consumerProperties);
		//parseConsumerClientConfig(clientProperties);
	}
	
	public void loadConsumerIdProperties(){
		//load consumer subscribe topics
		subscribe_topics = new ArrayList<String>();
		
		String consumerSubscribeTopics = consumerProperties.getProperty(ClientConfig.builderSubscribeTopicsWithId(consumerId));
		if(StringUtils.isNotEmpty(consumerSubscribeTopics)){
			String[] topics_array = consumerSubscribeTopics.split(StringHelper.COMMA_STRING_GAP);
			for(String topic : topics_array){
				if(!subscribe_topics.contains(topic)){
					subscribe_topics.add(topic);
				}
			}
		}
		
		//load consumer bootstrap.servers
		String consumerBootstrapServers = consumerProperties.getProperty(ClientConfig.builderBootstrapServersWithId(consumerId));
		if(StringUtils.isNotEmpty(consumerBootstrapServers)){
			consumerProperties.setProperty(ClientConfig.BOOTSTRAP_SERVERS, consumerBootstrapServers);
		}
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
	
	public void unsubscribe(){
		consumer.unsubscribe();
	}
	
	public boolean doSubscribePattern(String regex, final PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify){
		TopicPatternSubscriber tps = new TopicPatternSubscriber(Pattern.compile(regex), new NoOpConsumerRebalanceListener());
		return doSubscribe(tps, notify);
	}
	
	@Override
	public void addSubscribeTopic(String topic){
		if(!subscribe_topics.contains(topic)){
			subscribe_topics.add(topic);
			subscribe_topics_changed.set(true);
		}
	}
	
	@Override
	public boolean doSubscribeTopics(final PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify){
		if(subscribe_topics.isEmpty()) return false;
		return doSubscribe(new TopicSubscriber(subscribe_topics), notify);
	}
	
	@Override
	public boolean doSubscribeTopics(List<String> topics, final PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify){
		subscribe_topics.addAll(topics);
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
	
//	public boolean addSubscribeTopic(String topic){
//		consumer.wakeup();
//		
//		Set<String> current_topics = consumer.subscription();
//		current_topics.add(topic);
//
//		consumer.subscribe(new ArrayList<String>(current_topics));
//		return true;
//	}
	
	@Override
	public void shutdown(){
		closed.set(true);
		consumer.wakeup();
		consumerExecutorServiceShutdown();
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
		consumerExecutorService.submit((new Runnable() {
			@Override
			public void run() {
				try{
					while(!closed.get()){
						if(subscribe_topics_changed.get()){
							subscribeTopicsChangedNotify();
						}
						System.out.println("start consumer poll");
						ConsumerRecords<KEY, VALUE> records = consumer.poll(pollSize());
						notify.notifyComming(consumerId, records);
					}
				}catch (WakeupException e) {
					System.out.println("wakeup");
		            // Ignore exception if closing
		            if (!closed.get()) throw e;
		        }catch(Exception ex){
		        	ex.printStackTrace();
		        	consumer.close();
		        }
//				finally {
//		        	System.out.println("finally");
//		            consumer.close();
//		        }
			}
		}));
	}
	
	public void subscribeTopicsChangedNotify(){
		System.out.println("notify changed " + subscribe_topics);
		//consumer.subscribe(Collections.singletonList("topic3"));
		consumer.subscribe(subscribe_topics);
		subscribe_topics_changed.set(false);
	}
	
//	protected void validates(){
//		consumer.s
//	}
	
	public void consumerExecutorServiceShutdown(){
		Executors.newSingleThreadExecutor().submit((new Runnable() {
			@Override
			public void run() {
				while(closed.get()){
					System.out.println("exec正在shutdown");
					consumerExecutorService.shutdown();
					System.out.println("exec正在shutdown成功");
					while(true){
						System.out.println("正在判断exec是否执行完毕");
						if(consumerExecutorService.isTerminated()){
							System.out.println("exec是否执行完毕,终止exec...");
							consumerExecutorService.shutdownNow();
							System.out.println("exec是否执行完毕,终止exec成功");
							break;
						}else{
							System.out.println("exec未执行完毕...");
							try {
								Thread.sleep(2*1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}));
	}
	
	@Override
	public long pollSize() {
		return DEFAULT_POLLSIZE;
	}
	
	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
		initialize();
	}

	public abstract String keyDeserializer();
	public abstract String valueDeserializer();

}