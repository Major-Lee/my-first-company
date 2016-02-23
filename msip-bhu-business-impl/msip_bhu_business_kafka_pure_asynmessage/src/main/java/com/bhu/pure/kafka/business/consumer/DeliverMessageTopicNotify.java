package com.bhu.pure.kafka.business.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.bhu.pure.kafka.client.consumer.callback.PollIteratorNotify;

public class DeliverMessageTopicNotify implements PollIteratorNotify<ConsumerRecords<String, String>>{

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

}
