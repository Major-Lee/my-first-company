package com.bhu.pure.kafka.examples.newed.test;

import java.util.Collections;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.bhu.pure.kafka.assigner.Assigner;
import com.bhu.pure.kafka.assigner.TopicAssigner;
import com.bhu.pure.kafka.client.consumer.StringKafkaMessageConsumer;
import com.bhu.pure.kafka.client.consumer.callback.PollIteratorNotify;
import com.bhu.pure.kafka.client.producer.StringKafkaMessageProducer;
import com.bhu.pure.kafka.client.producer.callback.KeyValueProducerCallback;
import com.bhu.pure.kafka.subscribe.TopicSubscriber;


public class KafkaMessageTest {
	public static final String TOPIC1 = "topic1";
	public static final String TOPIC2 = "topic2";
	public static final String TOPIC3 = "topic3";
	
	public static final int PARTITION0 = 0;
	public static final int PARTITION1 = 1;
	
	public static void main(String[] args) throws Exception{
		//TestConsumerSubscriber();
//		TestConsumerAssgin();
//		TestProducerAsync();
//		TestDTO();
//		TestStringKafkaMessage();
//		TestAddSubscribeTopics();
		TestConsumerSubscriberGroup();
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
			public void notifyComming(String consumerId, ConsumerRecords<Integer, String> records) {
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
		StringKafkaMessageConsumer consumer = new StringKafkaMessageConsumer();
		TopicSubscriber subscriber = new TopicSubscriber(Collections.singletonList("t12"));
		consumer.doSubscribe(subscriber, new PollIteratorNotify<ConsumerRecords<String, String>>(){
			@Override
			public void notifyComming(String consumerId, ConsumerRecords<String, String> records) {
				for(ConsumerRecord<String, String> record : records){
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
		StringKafkaMessageProducer producer = new StringKafkaMessageProducer();
		int key = 0;
		while(true){
/*			ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(TOPIC, key, "msg"+key);
			RecordMetadata ret = producer.send(record);
			if(ret != null){
				System.out.println("successed");
			}*/
			System.out.println("send message " + key);
			RecordMetadata ret = producer.send("t12", null, key+"", "msg"+key);
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
			public void notifyComming(String consumerId, ConsumerRecords<String, TestDTO> records) {
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
//		StringKafkaMessageConsumer consumer = new StringKafkaMessageConsumer();
		new StringKafkaMessageConsumer().doSubscribeTopics(Collections.singletonList(TOPIC1), new PollIteratorNotify<ConsumerRecords<String, String>>(){
			@Override
			public void notifyComming(String consumerId, ConsumerRecords<String, String> records) {
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
	
	public static void TestAddSubscribeTopics() throws Exception{
		//consumer
//		StringKafkaMessageConsumer consumer = new StringKafkaMessageConsumer(); 
		//StringKafkaMessageConsumer consumer1 = new StringKafkaMessageConsumer("consumerId1");
//		StringKafkaMessageConsumer consumer1 = new StringKafkaMessageConsumer();
//		consumer1.setConsumerId("consumerId1");
		StringKafkaMessageConsumer consumer1 = new StringKafkaMessageConsumer();
		consumer1.doSubscribeTopics(new PollIteratorNotify<ConsumerRecords<String, String>>(){
//		StringKafkaMessageConsumer.getInstance().doSubscribePattern("topic.*", new PollIteratorNotify<ConsumerRecords<String, String>>(){	
			@Override
			public void notifyComming(String consumerId, ConsumerRecords<String, String> records) {
				for(ConsumerRecord<String, String> record : records){
					System.out.println(String
							.format("Received message: topic[%s] partition[%s] key[%s] value[%s] "
									+ "offset[%s] consumerId[%s]",
									record.topic(), record.partition(),
									record.key(), record.value(),
									record.offset(), consumerId));
				}
			}
		});
		
		Thread.sleep(2000l);
		
		//producer
/*		StringKafkaMessageProducer producer = new StringKafkaMessageProducer();
		int key = 0;
		while(true){
			ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(TOPIC, key, "msg"+key);
			RecordMetadata ret = producer.send(record);
			if(ret != null){
				System.out.println("successed");
			}
			System.out.println("send message " + key);
			RecordMetadata ret = producer.send("t11", null, key+"", "msg"+key);
			Thread.sleep(2000l);
			key++;
		}*/
		//producer
		StringKafkaMessageProducer producer = new StringKafkaMessageProducer();
		int key = 0;
		int topic_index = 1;
		while(true){
//			ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(TOPIC, key, "msg"+key);
//			RecordMetadata ret = producer.send(record);
//			if(ret != null){
//				System.out.println("successed");
//			}
			System.out.println("send message " + key);
			for(int i = 0;i<topic_index;i++){
				RecordMetadata ret = producer.send("tt"+i, null, String.valueOf(key), "msg"+key+"-"+System.currentTimeMillis());
			}
			
			Thread.sleep(2000l);
			key++;
			
			System.out.println("addSubscribeTopic");
			consumer1.addSubscribeTopic("tt"+topic_index);
			topic_index++;
//			
//				Thread.sleep(2000l);

		}
	}
	
	/**
	 * subscriber方式consumer
	 * 可以实现多topic的全分区消费
	 * @throws Exception
	 */
	public static void TestConsumerSubscriberGroup() throws Exception{
		//consumer c1
/*		StringKafkaMessageConsumer consumer_c1 = new StringKafkaMessageConsumer("c1");
		consumer_c1.doSubscribeTopics(new PollIteratorNotify<ConsumerRecords<String, String>>(){
			@Override
			public void notifyComming(String consumerId, ConsumerRecords<String, String> records) {
				for(ConsumerRecord<String, String> record : records){
					System.out.println(String
							.format("Received message: consumerId[%s] topic[%s] partition[%s] key[%s] value[%s] offset[%s]",
									consumerId, record.topic(), record.partition(),
									record.key(), record.value(),
									record.offset()));
				}
			}
		});
		
		//consumer c2
		StringKafkaMessageConsumer consumer_c2 = new StringKafkaMessageConsumer("c2");
		consumer_c2.doSubscribeTopics(new PollIteratorNotify<ConsumerRecords<String, String>>(){
			@Override
			public void notifyComming(String consumerId, ConsumerRecords<String, String> records) {
				for(ConsumerRecord<String, String> record : records){
					System.out.println(String
							.format("Received message: consumerId[%s] topic[%s] partition[%s] key[%s] value[%s] offset[%s]",
									consumerId, record.topic(), record.partition(),
									record.key(), record.value(),
									record.offset()));
				}
			}
		});*/
		
		Thread.sleep(2000l);
		//producer
		StringKafkaMessageProducer producer1 = new StringKafkaMessageProducer("biz");
		StringKafkaMessageProducer producer2 = new StringKafkaMessageProducer("mng");
		int key = 0;
		//while(true){
/*			ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(TOPIC, key, "msg"+key);
			RecordMetadata ret = producer.send(record);
			if(ret != null){
				System.out.println("successed");
			}*/
			System.out.println("send message " + key);
//			producer.send("up_ursids1_0", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("down_ursids1_1", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("down_ursids2_0", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("down_ursids2_1", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("down_ursids3_0", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("down_ursids3_1", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("down_ursids4_0", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("down_ursids4_1", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("down_ursidsdebugB_0", null, key+"", "msg"+key+"-happy yetao");
			
//			producer.send("up_ursids1_0", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("up_ursids1_1", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("up_ursids2_0", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("up_ursids2_1", null, key+"", "msg"+key+"-happy yetao");
			//producer1.send("up_ursids3_0", "8482f423070c", "000000058482f423070c000000000000000000000000001<join_req><ITEM orig_vendor=\"BHU\" hdtype=\"H106\" orig_model=\"uRouter\" orig_hdver=\"Z01\" orig_swver=\"AP106P06V1.5.2Build9396_TU\" oem_vendor=\"BHU\" oem_model=\"Urouter\" oem_hdver=\"Z01\" oem_swver=\"AP106P06V1.5.2Build9396_TU\" sn=\"BN207DE100080AA\" mac=\"84:82:f4:23:07:0c\" ip=\"192.168.66.161\" build_info=\"2016-02-19-13:31 Revision: 9396\" config_model_ver=\"V3\" config_mode=\"basic\" work_mode=\"router-ap\" config_sequence=\"60\" join_reason=\"3\" wan_ip=\"192.168.66.161\" /></join_req>");
			producer1.sendAsync("up_ursidsdebug_0", null, "8482f423070c", "000000058482f423070c000000000000000000000000001<join_req><ITEM orig_vendor=\"BHU\" hdtype=\"H106\" orig_model=\"uRouter\" orig_hdver=\"Z01\" orig_swver=\"AP106P06V1.5.2Build9396_TU\" oem_vendor=\"BHU\" oem_model=\"Urouter\" oem_hdver=\"Z01\" oem_swver=\"AP106P06V1.5.2Build9396_TU\" sn=\"BN207DE100080AA\" mac=\"84:82:f4:23:07:0c\" ip=\"192.168.66.161\" build_info=\"2016-02-19-13:31 Revision: 9396\" config_model_ver=\"V3\" config_mode=\"basic\" work_mode=\"router-ap\" config_sequence=\"60\" join_reason=\"3\" wan_ip=\"192.168.66.161\" /></join_req>", null);
			//producer1.send("up_ursids3_0", key+"","msg"+key+"-happy yetao");
			//producer2.send("mng_queue", key+"", "msg"+key+"-happy yetao");
//			producer.send("up_ursids3_1", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("up_ursids4_0", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("up_ursids4_1", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("up_ursidsdebugB_0", null, key+"", "msg"+key+"-happy yetao");
			
//			producer.send("mng_queue", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("up_ursidsdebug_0", null, key+"", "msg"+key+"-happy yetao");

//			RecordMetadata ret1 = producer.send("tdeliver", null, key+"", "msg"+key);
//			RecordMetadata ret2 = producer.send("tcm1", null, key+"", "msg"+key);
			
//			producer.send("down_ursids9_0", null, key+"", "msg"+key+"-happy yetao");
//			producer.send("down_ursids9_1", null, key+"", "msg"+key+"-happy yetao");
			Thread.sleep(2000l);
			System.out.println("send message end " + key);
			key++;
		//}
	}
	
}
