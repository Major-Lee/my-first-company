package com.bhu.pure.kafka.client.consumer;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.bhu.pure.kafka.assigner.Assigner;
import com.bhu.pure.kafka.client.consumer.callback.PollIteratorNotify;
import com.bhu.pure.kafka.subscribe.Subscriber;

public interface IKafkaMessageConsumer<KEY, VALUE>{
	public static long DEFAULT_POLLSIZE = 1000;
	
	public long pollSize();
	
	public void addSubscribeTopic(String topic);
	
	public boolean doSubscribeTopics(final PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify);
	
	public boolean doSubscribeTopics(List<String> topics, final PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify);
	
	public boolean doSubscribe(Subscriber subscriber, PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify);
	
	public boolean doAssgin(Assigner assigner, final PollIteratorNotify<ConsumerRecords<KEY, VALUE>> notify);
	
	public void shutdown();
}
