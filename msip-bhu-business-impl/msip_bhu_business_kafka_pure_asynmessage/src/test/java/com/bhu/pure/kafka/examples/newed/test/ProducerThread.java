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
package com.bhu.pure.kafka.examples.newed.test;

import com.bhu.pure.kafka.client.producer.StringKafkaMessageProducer;

public class ProducerThread extends Thread {

	private StringKafkaMessageProducer producer;
	private String topic;
	
	public ProducerThread(StringKafkaMessageProducer producer, String topic) {
		this.producer = producer;
		this.topic = topic;
	}

	public void run(){
		try{
			int key = 0;
			while(true){
				producer.send(topic, key+"", "msg"+key+"-happy yetao");
				Thread.sleep(1000l);
				key++;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
