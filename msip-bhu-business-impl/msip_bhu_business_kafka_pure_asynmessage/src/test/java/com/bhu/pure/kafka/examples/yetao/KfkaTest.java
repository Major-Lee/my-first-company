package com.bhu.pure.kafka.examples.yetao;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.bhu.pure.kafka.client.consumer.StringKafkaMessageConsumer;
import com.bhu.pure.kafka.client.consumer.callback.PollIteratorNotify;
import com.bhu.pure.kafka.client.producer.StringKafkaMessageProducer;


public class KfkaTest {

	public static void main(String[] args) throws Exception {
/*		
		 Properties props = new Properties();
		 props.put("bootstrap.servers", "localhost:4242");
		 props.put("acks", "all");
		 props.put("retries", 0);
		 props.put("batch.size", 16384);
		 props.put("linger.ms", 1);
		 props.put("buffer.memory", 33554432);
		 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		 Producer<String, String> producer = new KafkaProducer<String, String>(props);
		 for(int i = 0; i < 100; i++)
		     producer.send(new ProducerRecord<String, String>("my-topic", Integer.toString(i), Integer.toString(i)));

		 producer.close();
*/		 
		 TestAddSubscribeTopics();
		 
	}

	
	public static void TestAddSubscribeTopics() throws Exception{
		//consumer
//		StringKafkaMessageConsumer consumer = new StringKafkaMessageConsumer(); 
		//StringKafkaMessageConsumer consumer1 = new StringKafkaMessageConsumer("consumerId1");
//		StringKafkaMessageConsumer consumer1 = new StringKafkaMessageConsumer();
//		consumer1.setConsumerId("consumerId1");
		StringKafkaMessageConsumer consumer1 = new StringKafkaMessageConsumer("mng");
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
		StringKafkaMessageProducer producer = new StringKafkaMessageProducer("mng");
		int key = 0;
		int topic_index = 1;
		while(true){
/*			ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(TOPIC, key, "msg"+key);
			RecordMetadata ret = producer.send(record);
			if(ret != null){
				System.out.println("successed");
			}*/
			System.out.println("send message " + key);
			for(int i = 0;i<topic_index;i++){
				producer.send("topic"+i, null, String.valueOf(key), "msg"+key+"-"+System.currentTimeMillis());
			}
			
			Thread.sleep(2000l);
			key++;
			
			System.out.println("addSubscribeTopic");
			consumer1.addSubscribeTopic("topic"+topic_index);
			topic_index++;
//			
//				Thread.sleep(2000l);

		}
	}

}
