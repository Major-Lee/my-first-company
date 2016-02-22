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
package com.bhu.pure.kafka.client.producer;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.pure.kafka.client.KafkaMessageClient;
import com.bhu.pure.kafka.client.config.ClientConfig;

public abstract class KafkaMessageProducer<KEY, VALUE> extends KafkaMessageClient implements IKafkaMessageProducer<KEY, VALUE>{
	private static final Logger logger = LoggerFactory.getLogger(KafkaMessageProducer.class);
	
	private KafkaProducer<KEY, VALUE> producer;
	private String producerId;
	private Properties producerProperties;
	//properties set
	//private TopicPartition producerTopicPartition;
	
	public KafkaMessageProducer() {
		this(null);
	}
	
	public KafkaMessageProducer(String producerId) {
		this.producerId = producerId;
		initialize();
	}
	
	public void initialize(){
		logger.info("KafkaMessageProducer start initialize");
		
		producerProperties = loadProperties();
		if(producerProperties == null){
			throw new RuntimeException("KafkaMessageProducer initialize failed require properties object");
		}
		loadProducerIdProperties();
		
		producer = new KafkaProducer<KEY, VALUE>(producerProperties);
		
		//parseProducerClientConfig(clientProperties);
	}
	
	public void loadProducerIdProperties(){
		//load consumer bootstrap.servers
		String consumerBootstrapServers = producerProperties.getProperty(ClientConfig.builderBootstrapServersWithId(producerId));
		if(StringUtils.isNotEmpty(consumerBootstrapServers)){
			producerProperties.setProperty(ClientConfig.BOOTSTRAP_SERVERS, consumerBootstrapServers);
		}
	}
/*	public void parseProducerClientConfig(Properties clientProperties){
		try{
			String producer_topic = clientProperties.getProperty(ClientConfig.PRODUCER_TOPIC);
			if(StringUtils.isNotEmpty(producer_topic)){
				String[] producer_topic_array = producer_topic.split(StringHelper.AT_STRING_GAP);
				int length = producer_topic_array.length;
				String topic = null;
				int partition = ClientConfig.Partition.ALLPARTITION;
				if(length > 0){
					topic = producer_topic_array[0];
					if(length == 2){
						partition = Integer.parseInt(producer_topic_array[1]);
					}
					producerTopicPartition = new TopicPartition(topic, partition);
				}
			}
		}catch(Exception ex){
			throw new RuntimeException("KafkaMessageProducer parseClientConfig failed", ex);
		}
	}*/
	
/*	@Override
	public RecordMetadata send(KEY key, VALUE value){
		if(producerTopicPartition == null){
			throw new RuntimeException("KafkaMessageProducer send failed require producerTopicPartition object");
		}
		return send(producerTopicPartition.topic(), producerTopicPartition.partition(), key, value);
	}*/
	@Override
	public RecordMetadata send(String topic, KEY key, VALUE value){
		return send(topic, null, key, value);
	}
	
	@Override
	public RecordMetadata send(String topic, Integer partition, KEY key, VALUE value){
		try {
			return producer.send(new ProducerRecord<KEY, VALUE>(topic, partition, key, value)).get();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
/*	@Override
	public void sendAsync(KEY key, VALUE value, Callback callback){
		if(producerTopicPartition == null){
			throw new RuntimeException("KafkaMessageProducer sendAsync failed require producerTopicPartition object");
		}
		producer.send(new ProducerRecord<KEY, VALUE>(producerTopicPartition.topic(), producerTopicPartition.partition(), 
				key, value), callback);
	}*/
	
	@Override
	public void sendAsync(String topic, Integer partition, KEY key, VALUE value, Callback callback){
		producer.send(new ProducerRecord<KEY, VALUE>(topic, partition, key, value), callback);
	}

	public String getProducerId() {
		return producerId;
	}

	public void setProducerId(String producerId) {
		this.producerId = producerId;
		initialize();
	}
	
/*	@Override
	public RecordMetadata send(ProducerRecord<KEY, VALUE> record){
		try {
			return producer.send(record).get();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void sendAsync(ProducerRecord<KEY, VALUE> record, Callback callback){
		producer.send(record, callback);
	}*/
	
	
}
