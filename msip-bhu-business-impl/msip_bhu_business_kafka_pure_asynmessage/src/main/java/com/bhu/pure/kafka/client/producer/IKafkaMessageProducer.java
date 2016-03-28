package com.bhu.pure.kafka.client.producer;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;


public interface IKafkaMessageProducer<KEY, VALUE>{
	//public RecordMetadata send(ProducerRecord<KEY, VALUE> record);
	//public void sendAsync(ProducerRecord<KEY, VALUE> record, Callback callback);
	//public RecordMetadata send(KEY key, VALUE value);
	public RecordMetadata send(String topic, KEY key, VALUE value) throws InterruptedException, ExecutionException;
	public RecordMetadata send(String topic, Integer partition, KEY key, VALUE value) throws InterruptedException, ExecutionException;
	//public void sendAsync(KEY key, VALUE value, Callback callback);
	public void sendAsync(String topic, Integer partition, KEY key, VALUE value, Callback callback);
}
