package com.bhu.vas.rpc.asyncprocessor.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Service;

import com.bhu.vas.rpc.asyncprocessor.dto.AsyncIndexDTO;

@Service
public class AsyncIndexProcessorQueue {
	private static ConcurrentLinkedQueue<AsyncIndexDTO> queue = new ConcurrentLinkedQueue<AsyncIndexDTO>();
	
	public AsyncIndexDTO poll(){
		return queue.poll();
	}
	
	public void addQueue(AsyncIndexDTO item){
		queue.add(item);
	}
}
