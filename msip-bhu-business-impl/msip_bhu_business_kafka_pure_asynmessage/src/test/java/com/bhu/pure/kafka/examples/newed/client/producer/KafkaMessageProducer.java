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
package com.bhu.pure.kafka.examples.newed.client.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public abstract class KafkaMessageProducer<KEY, VALUE> implements IKafkaMessageProducer<KEY, VALUE>{
	
	private KafkaProducer<KEY, VALUE> producer;

	public KafkaMessageProducer() {
		initialize();
	}
/*	public KafkaMessageProducer() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("client.id", "DemoProducer");
		props.put("key.serializer",
				"org.apache.kafka.common.serialization.IntegerSerializer");
		props.put("value.serializer",
				"org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<Integer, String>(props);
		this.topic = topic;
		this.isAsync = isAsync;
	}*/
	
	public void initialize(){
		System.out.println("start producer initialize");
		Properties clientProperties = getClientProperties();
		if(clientProperties == null){
			throw new RuntimeException("KafkaMessageProducer initialize failed require properties object");
		}
		producer = new KafkaProducer<KEY, VALUE>(clientProperties);
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
	
	@Override
	public void sendAsync(String topic, Integer partition, KEY key, VALUE value, Callback callback){
		producer.send(new ProducerRecord<KEY, VALUE>(topic, partition, key, value), callback);
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
