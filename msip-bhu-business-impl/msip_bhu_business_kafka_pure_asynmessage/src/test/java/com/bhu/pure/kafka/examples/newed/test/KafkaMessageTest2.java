package com.bhu.pure.kafka.examples.newed.test;

import java.util.Collections;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.bhu.pure.kafka.examples.newed.subscribe.TopicSubscriber;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;

public class KafkaMessageTest2 {
	public static final String TOPIC = "topic";
	
	public static void main(String[] args) {
		//consumer
//		CustomKafkaMessageConsumer consumer = new CustomKafkaMessageConsumer();
//		TopicSubscriber subscriber = new TopicSubscriber(Collections.singletonList(TOPIC));
//		consumer.doSubscribe(subscriber, new IteratorNotify<ConsumerRecords<Integer, String>>(){
//			@Override
//			public void notifyComming(ConsumerRecords<Integer, String> records) {
//				for(ConsumerRecord<Integer, String> record : records){
//					System.out.println(String
//							.format("Received message: topic[%s] partition[%s] key[%s] value[%s] offset[%s]",
//									record.topic(), record.partition(),
//									record.key(), record.value(),
//									record.offset()));
//					System.out.println("Received message: (" + record.key() + ", "
//								+ record.value() + ") at offset " + record.offset());
//				}
//			}
//		});
		//producer
		CustomKafkaMessageProducer producer = new CustomKafkaMessageProducer();
		int key = 0;
		while(true){
/*			ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(TOPIC, key, "msg"+key);
			RecordMetadata ret = producer.send(record);
			if(ret != null){
				System.out.println("successed");
			}*/
			RecordMetadata ret = producer.send(TOPIC, 3, key, "msg"+key);
			try {
				Thread.sleep(2000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			key++;
		}
	}

}
