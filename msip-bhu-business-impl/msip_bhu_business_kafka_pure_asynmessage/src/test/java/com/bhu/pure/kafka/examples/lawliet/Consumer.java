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
package com.bhu.pure.kafka.examples.lawliet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import kafka.utils.ShutdownableThread;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

public class Consumer extends ShutdownableThread
{
  private final KafkaConsumer<Integer, String> consumer;
  private final List<String> topics;

  public Consumer(List<String> topics)
  {
    super("KafkaConsumerExample", false);
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "DemoConsumer");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
    props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "30000");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

    consumer = new KafkaConsumer<>(props);
    this.topics = topics;
  }

  @Override
  public void doWork() {
	//doSubscribe();
	//doAssign();
	doMultiAssign();
    ConsumerRecords<Integer, String> records = consumer.poll(1000);
    for (ConsumerRecord<Integer, String> record : records) {
    	System.out.println(String.format("Received message: topic[%s] partition[%s] key[%s] value[%s] offset[%s]",record.topic(),record.partition(), record.key() ,record.value(), record.offset()));
      System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
    }
  }

  public void doSubscribe(){
	  consumer.subscribe(topics);
  }
  
  public void doAssign(){
	TopicPartition tp = new TopicPartition(topics.get(0), 1);
	consumer.assign(Collections.singletonList(tp));
  }
  
  public void doMultiAssign(){
	List<TopicPartition> tps = new ArrayList<TopicPartition>();
	TopicPartition tp1 = new TopicPartition(topics.get(0), 0);
	TopicPartition tp2 = new TopicPartition(topics.get(0), 1);
	TopicPartition tp3 = new TopicPartition(topics.get(1), 0);
	tps.add(tp1);
	tps.add(tp2);
	tps.add(tp3);
	consumer.assign(tps);
  }
  
  @Override
  public String name() {
    return null;
  }

  @Override
  public boolean isInterruptible() {
    return false;
  }
}