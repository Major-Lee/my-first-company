package com.bhu.pure.kafka.examples.newed.client.consumer.callback;

public interface PollIteratorNotify<T> {
	public void notifyComming(String consumerId, T t);
}
