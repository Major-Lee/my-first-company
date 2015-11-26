package com.smartwork.async.messagequeue.kafka.parser.iface;

import java.util.List;
import java.util.Map;

public interface IMessageHandler<T> {
	public void handler(String topic,Map<Integer, List<T>> value);
}
