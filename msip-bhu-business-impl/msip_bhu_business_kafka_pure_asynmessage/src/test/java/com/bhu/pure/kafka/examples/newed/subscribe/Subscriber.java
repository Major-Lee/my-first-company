package com.bhu.pure.kafka.examples.newed.subscribe;

public interface Subscriber {
	public static long DEFAULT_POLLSIZE = 1000;
	public long pollSize();
}
