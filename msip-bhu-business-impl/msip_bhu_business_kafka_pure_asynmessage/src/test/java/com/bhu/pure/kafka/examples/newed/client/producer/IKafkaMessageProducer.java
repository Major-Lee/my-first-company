package com.bhu.pure.kafka.examples.newed.client.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;


public interface IKafkaMessageProducer<KEY, VALUE>{
	//public RecordMetadata send(ProducerRecord<KEY, VALUE> record);
	//public void sendAsync(ProducerRecord<KEY, VALUE> record, Callback callback);
	//public RecordMetadata send(KEY key, VALUE value);
	public RecordMetadata send(String topic, Integer partition, KEY key, VALUE value);
	//public void sendAsync(KEY key, VALUE value, Callback callback);
	public void sendAsync(String topic, Integer partition, KEY key, VALUE value, Callback callback);
}
