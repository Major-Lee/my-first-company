package com.bhu.pure.kafka.examples.newed.test;

import java.util.Collections;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.bhu.pure.kafka.examples.newed.assigner.Assigner;
import com.bhu.pure.kafka.examples.newed.assigner.TopicAssigner;
import com.bhu.pure.kafka.examples.newed.client.consumer.StringKafkaMessageConsumer;
import com.bhu.pure.kafka.examples.newed.client.consumer.callback.PollIteratorNotify;
import com.bhu.pure.kafka.examples.newed.client.producer.StringKafkaMessageProducer;
import com.bhu.pure.kafka.examples.newed.client.producer.callback.KeyValueProducerCallback;
import com.bhu.pure.kafka.examples.newed.subscribe.TopicSubscriber;

public class KafkaMessageTest {
	public static final String TOPIC1 = "topic1";
	public static final String TOPIC2 = "topic2";
	public static final String TOPIC3 = "topic3";
	
	public static final int PARTITION0 = 0;
	public static final int PARTITION1 = 1;
	
	public static void main(String[] args) throws Exception{
//		TestConsumerSubscriber();
//		TestConsumerAssgin();
//		TestProducerAsync();
//		TestDTO();
		TestStringKafkaMessage();
	}
	
	/**
	 * assgin方式consumer
	 * 可以实现多topic 指定分区的消费
	 * @throws Exception
	 */
	public static void TestConsumerAssgin() throws Exception{
		//consumer
		SimpleKafkaMessageConsumer consumer = new SimpleKafkaMessageConsumer();
		Assigner assigner = new TopicAssigner();
		assigner.addTopicPartition(TOPIC1, PARTITION0);
		assigner.addTopicPartition(TOPIC1, PARTITION1);
		assigner.addTopicPartition(TOPIC2, PARTITION0);
		consumer.doAssgin(assigner, new PollIteratorNotify<ConsumerRecords<Integer, String>>(){
			@Override
			public void notifyComming(ConsumerRecords<Integer, String> records) {
				for(ConsumerRecord<Integer, String> record : records){
					System.out.println(String
							.format("Received message: topic[%s] partition[%s] key[%s] value[%s] offset[%s]",
									record.topic(), record.partition(),
									record.key(), record.value(),
									record.offset()));
					System.out.println("Received message: (" + record.key() + ", "
								+ record.value() + ") at offset " + record.offset());
				}
			}
		});
		
		Thread.sleep(2000l);
		//producer
		SimpleKafkaMessageProducer producer = new SimpleKafkaMessageProducer();
		int key = 0;
		while(true){
/*			ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(TOPIC, key, "msg"+key);
			RecordMetadata ret = producer.send(record);
			if(ret != null){
				System.out.println("successed");
			}*/
			RecordMetadata ret1 = producer.send(TOPIC1, null, key, "msg"+key);
			RecordMetadata ret2 = producer.send(TOPIC2, null, key, "msg"+key);
			Thread.sleep(2000l);
			key++;
		}
	}

	
	/**
	 * subscriber方式consumer
	 * 可以实现多topic的全分区消费
	 * @throws Exception
	 */
	public static void TestConsumerSubscriber() throws Exception{
		//consumer
		SimpleKafkaMessageConsumer consumer = new SimpleKafkaMessageConsumer();
		TopicSubscriber subscriber = new TopicSubscriber(Collections.singletonList(TOPIC1));
		consumer.doSubscribe(subscriber, new PollIteratorNotify<ConsumerRecords<Integer, String>>(){
			@Override
			public void notifyComming(ConsumerRecords<Integer, String> records) {
				for(ConsumerRecord<Integer, String> record : records){
					System.out.println(String
							.format("Received message: topic[%s] partition[%s] key[%s] value[%s] offset[%s]",
									record.topic(), record.partition(),
									record.key(), record.value(),
									record.offset()));
					System.out.println("Received message: (" + record.key() + ", "
								+ record.value() + ") at offset " + record.offset());
				}
			}
		});
		
		Thread.sleep(2000l);
		//producer
		SimpleKafkaMessageProducer producer = new SimpleKafkaMessageProducer();
		int key = 0;
		while(true){
/*			ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(TOPIC, key, "msg"+key);
			RecordMetadata ret = producer.send(record);
			if(ret != null){
				System.out.println("successed");
			}*/
			RecordMetadata ret = producer.send(TOPIC1, null, key, "msg"+key);
			Thread.sleep(2000l);
			key++;
		}
	}

	/**
	 * producer Async 发送
	 * producer内部会自动进行本地消息缓存队列堆积，达到一定数量程度或时间会打包发送，会造成消息延迟推送，提高吞吐量
	 * 以下参数可以控制
		# 异步模式下缓冲数据的最大时间。例如设置为100则会集合100ms内的消息后发送，这样会提高吞吐量，但是会增加消息发送的延时
		queue.buffering.max.ms = 5000
		# 异步模式下缓冲的最大消息数，同上
		queue.buffering.max.messages = 10000
		# 异步模式下，消息进入队列的等待时间。若是设置为0，则消息不等待，如果进入不了队列，则直接被抛弃
		queue.enqueue.timeout.ms = -1
		# 异步模式下，每次发送的消息数，当queue.buffering.max.messages或queue.buffering.max.ms满足条件之一时producer会触发发送。
		batch.num.messages=200
	 * @throws Exception
	 */
	public static void TestProducerAsync() throws Exception {
		int key = 0;
		String value = "msg";
		SimpleKafkaMessageProducer producer = new SimpleKafkaMessageProducer();
		producer.sendAsync(TOPIC1, null, key, value, new KeyValueProducerCallback<Integer, String>(key, value) {
				@Override
				public void onCompletion(RecordMetadata metadata, Exception ex) {
					if (metadata != null) {
						System.out.println("message(" + this.getKey()+ ", " + this.getValue()
									+ ") sent to partition("+ metadata.partition() + "), " + "offset("
									+ metadata.offset() + ")");
					} else {
						ex.printStackTrace();
					}
				}
		});
		Thread.currentThread().join();
	}
	
	public static void TestDTO() throws Exception{
		//consumer
		DTOKafkaMessageConsumer consumer = new DTOKafkaMessageConsumer();
		TopicSubscriber subscriber = new TopicSubscriber(Collections.singletonList(TOPIC1));
		consumer.doSubscribe(subscriber, new PollIteratorNotify<ConsumerRecords<String, TestDTO>>(){
			@Override
			public void notifyComming(ConsumerRecords<String, TestDTO> records) {
				for(ConsumerRecord<String, TestDTO> record : records){
					System.out.println(String
							.format("Received message: topic[%s] partition[%s] key[%s] value[%s] value_name[%s] "
									+ "value_desc[%s] offset[%s]",
									record.topic(), record.partition(),
									record.key(), record.value(), record.value().getName(), record.value().getDesc(),
									record.offset()));
				}
			}
		});
		
		Thread.sleep(2000l);
		//producer
		DTOKafkaMessageProducer producer = new DTOKafkaMessageProducer();
		int key = 0;
		while(true){
/*			ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(TOPIC, key, "msg"+key);
			RecordMetadata ret = producer.send(record);
			if(ret != null){
				System.out.println("successed");
			}*/
			TestDTO dto = new TestDTO();
			dto.setName("name"+key);
			dto.setDesc("desc"+key);
			
			RecordMetadata ret = producer.send(TOPIC1, null, String.valueOf(key), dto);
			Thread.sleep(2000l);
			key++;
		}
	}
	
	public static void TestStringKafkaMessage() throws Exception{
		//consumer
		StringKafkaMessageConsumer consumer = new StringKafkaMessageConsumer();
		consumer.doSubscribeTopics(Collections.singletonList(TOPIC1), new PollIteratorNotify<ConsumerRecords<String, String>>(){
			@Override
			public void notifyComming(ConsumerRecords<String, String> records) {
				for(ConsumerRecord<String, String> record : records){
					System.out.println(String
							.format("Received message: topic[%s] partition[%s] key[%s] value[%s] "
									+ "offset[%s]",
									record.topic(), record.partition(),
									record.key(), record.value(),
									record.offset()));
				}
			}
		});
		
		Thread.sleep(2000l);
		//producer
		StringKafkaMessageProducer producer = new StringKafkaMessageProducer();
		int key = 0;
		while(true){
/*			ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(TOPIC, key, "msg"+key);
			RecordMetadata ret = producer.send(record);
			if(ret != null){
				System.out.println("successed");
			}*/
			
			RecordMetadata ret = producer.send(TOPIC1, null, String.valueOf(key), "msg"+key);
			Thread.sleep(2000l);
			key++;
		}
	}
	
}
