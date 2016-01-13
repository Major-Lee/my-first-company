package com.bhu.pure.kafka.examples.newed.client.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.bhu.pure.kafka.examples.newed.client.IKafkaMessageClient;


public interface IKafkaMessageProducer<KEY, VALUE> extends IKafkaMessageClient{
	public RecordMetadata send(ProducerRecord<KEY, VALUE> record);
	public void sendAsync(ProducerRecord<KEY, VALUE> record, Callback callback);
}
