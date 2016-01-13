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

import java.util.Properties;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.bhu.pure.kafka.examples.newed.subscribe.Subscriber;
import com.bhu.pure.kafka.examples.newed.subscribe.TopicPatternSubscriber;
import com.bhu.pure.kafka.examples.newed.subscribe.TopicRebalanceSubscriber;
import com.bhu.pure.kafka.examples.newed.subscribe.TopicSubscriber;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

public abstract class KafkaMessageConsumer<KEY, VALUE> implements IKafkaMessageConsumer<KEY, VALUE>{
	
	private KafkaConsumer<KEY, VALUE> consumer;

	public KafkaMessageConsumer(){
		initialize();
	}
	
	public void initialize(){
		System.out.println("start consumer initialize");
		Properties consumerProperties = getClientProperties();
		if(consumerProperties == null){
			throw new RuntimeException("KafkaMessageConsumer initialize failed require properties object");
		}
		consumer = new KafkaConsumer<KEY, VALUE>(consumerProperties);
	}
/*	public KafkaMessageConsumer(String topic) {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
//		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
//				"org.apache.kafka.common.serialization.IntegerDeserializer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
//		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
//				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());		

		consumer = new KafkaConsumer<>(props);
	}*/

/*	@Override
	public void doWork() {
		consumer.subscribe(pattern, listener);.subscribe(Collections.singletonList(this.topic));
		ConsumerRecords<Integer, String> records = consumer.poll(1000);
		for (ConsumerRecord<Integer, String> record : records) {
			System.out
					.println(String
							.format("Received message: topic[%s] partition[%s] key[%s] value[%s] offset[%s]",
									record.topic(), record.partition(),
									record.key(), record.value(),
									record.offset()));
			System.out.println("Received message: (" + record.key() + ", "
					+ record.value() + ") at offset " + record.offset());
		}
	}*/

	@Override
	public boolean doSubscribe(Subscriber subscriber, final IteratorNotify<ConsumerRecords<KEY, VALUE>> notify) {
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
		
		Executors.newSingleThreadExecutor().submit((new Runnable() {
			@Override
			public void run() {
				while(true){
					System.out.println("start consumer poll");
					ConsumerRecords<KEY, VALUE> records = consumer.poll(1000);
					notify.notifyComming(records);
				}
			}
		}));
		
		return true;
	}
	
//	public abstract Subscriber getSubscriber();
	
}