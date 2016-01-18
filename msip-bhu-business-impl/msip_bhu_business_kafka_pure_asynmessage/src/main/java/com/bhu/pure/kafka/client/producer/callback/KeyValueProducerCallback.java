package com.bhu.pure.kafka.client.producer.callback;

import org.apache.kafka.clients.producer.Callback;

public abstract class KeyValueProducerCallback<KEY, VALUE> implements Callback{
	private KEY key;
	private VALUE value;
	
	public KeyValueProducerCallback(KEY key, VALUE value) {
		this.key = key;
		this.value = value;
	}
	
	public KEY getKey() {
		return key;
	}

	public void setKey(KEY key) {
		this.key = key;
	}

	public VALUE getValue() {
		return value;
	}

	public void setValue(VALUE value) {
		this.value = value;
	}

}
