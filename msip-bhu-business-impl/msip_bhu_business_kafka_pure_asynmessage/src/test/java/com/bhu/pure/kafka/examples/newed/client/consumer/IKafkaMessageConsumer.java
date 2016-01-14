package com.bhu.pure.kafka.examples.newed.client.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.bhu.pure.kafka.examples.newed.assigner.Assigner;
import com.bhu.pure.kafka.examples.newed.client.IKafkaMessageClient;
import com.bhu.pure.kafka.examples.newed.client.consumer.callback.PollIteratorNotify;
import com.bhu.pure.kafka.examples.newed.subscribe.Subscriber;

public interface IKafkaMessageConsumer<KEY, VALUE> extends IKafkaMessageClient{
	public static long DEFAULT_POLLSIZE = 1000;
	
	public long pollSize();
	
	public boolean doSubscribe(Subscriber subscriber, PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify);
	
	public boolean doAssgin(Assigner assigner, final PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify);
}
