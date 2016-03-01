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
package com.bhu.pure.kafka.examples.zero9;


import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Producer extends Thread
{
  private final KafkaProducer<Integer, String> producer;
  private final String topic;
  private final Boolean isAsync;

  public Producer(String topic, Boolean isAsync)
  {
    Properties props = new Properties();
    //props.put("bootstrap.servers", "192.168.66.123:9092");
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka.sanji.bhunetworks.com:9092,kafka.sanji.bhunetworks.com:9093,kafka.franky.bhunetworks.com:9092,kafka.franky.bhunetworks.com:9093");
    props.put("client.id", "DemoProducer");
    props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    producer = new KafkaProducer<Integer, String>(props);
    this.topic = topic;
    this.isAsync = isAsync;
  }

  public void run() {
    int messageNo = 1;
    while(true)
    {
    	System.out.println("send");
      String messageStr = "Message_" + messageNo;
      long startTime = System.currentTimeMillis();
      if (isAsync) { // Send asynchronously
        producer.send(new ProducerRecord<Integer, String>(topic,
            messageNo,
            messageStr), new DemoCallBack(startTime, messageNo, messageStr));
      } else { // Send synchronously
        try {
          producer.send(new ProducerRecord<Integer, String>(topic,
              messageNo,
              messageStr)).get();
          //System.out.println("Sent message: (" + messageNo + ", " + messageStr + ")");
        } catch (InterruptedException e) {
        	System.out.println("send fail 1");
          e.printStackTrace();
        } catch (ExecutionException e) {
        	System.out.println("send fail 2");
          e.printStackTrace();
        }
      }
      
      ++messageNo;
      try {
		sleep(1000l);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
  }
}

class DemoCallBack implements Callback {

  private long startTime;
  private int key;
  private String message;

  public DemoCallBack(long startTime, int key, String message) {
    this.startTime = startTime;
    this.key = key;
    this.message = message;
  }

  /**
   * A callback method the user can implement to provide asynchronous handling of request completion. This method will
   * be called when the record sent to the server has been acknowledged. Exactly one of the arguments will be
   * non-null.
   *
   * @param metadata  The metadata for the record that was sent (i.e. the partition and offset). Null if an error
   *                  occurred.
   * @param exception The exception thrown during processing of this record. Null if no error occurred.
   */
  public void onCompletion(RecordMetadata metadata, Exception exception) {
    long elapsedTime = System.currentTimeMillis() - startTime;
    if (metadata != null) {
      /*System.out.println(
          "message(" + key + ", " + message + ") sent to partition(" + metadata.partition() +
              "), " +
              "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");*/
    } else {
      exception.printStackTrace();
    }
  }
}
