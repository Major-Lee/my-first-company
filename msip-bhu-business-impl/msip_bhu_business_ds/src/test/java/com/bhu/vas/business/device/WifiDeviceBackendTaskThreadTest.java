package com.bhu.vas.business.device;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.business.ds.devicegroup.service.WifiDeviceBackendTaskService;
import com.smartwork.msip.localunit.BaseTest;
public class WifiDeviceBackendTaskThreadTest extends BaseTest{
	@Resource
	WifiDeviceBackendTaskService wifiDeviceBackendTaskService;

	private static ExecutorService exec = Executors.newFixedThreadPool(5);
	
	@Test
	public void test001() throws Exception{
		
		
		System.out.println("service" + wifiDeviceBackendTaskService);
		WifiDevcieBackendTaskThread task1 = new WifiDevcieBackendTaskThread(wifiDeviceBackendTaskService);
		WifiDevcieBackendTaskThread task2 = new WifiDevcieBackendTaskThread(wifiDeviceBackendTaskService);
		WifiDevcieBackendTaskThread task3 = new WifiDevcieBackendTaskThread(wifiDeviceBackendTaskService);
		WifiDevcieBackendTaskThread task4 = new WifiDevcieBackendTaskThread(wifiDeviceBackendTaskService);
		WifiDevcieBackendTaskThread task5 = new WifiDevcieBackendTaskThread(wifiDeviceBackendTaskService);
		
		task1.createTask(200L,10L);
		task2.createTask(200L,20L);
		task3.createTask(200L,30L);
		task4.createTask(200L,40L);
		task5.createTask(200L,50L);
		
		exec.execute(task1);
		exec.execute(task2);
		exec.execute(task3);
		exec.execute(task4);
		exec.execute(task5);
		
//		while (!Thread.interrupted()) {
//			try {
//				TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
//			} catch (InterruptedException e) {
//				// 终结循环
//				System.out.println("shutdown|shutdown|shutdown");
//			}
//		}
		shutdownAndAwaitTermination(exec);
	}
	
	void shutdownAndAwaitTermination(ExecutorService pool) {
		  pool.shutdown();
		  try {
		    if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
		      pool.shutdownNow(); 
		      if (!pool.awaitTermination(60, TimeUnit.SECONDS))
		         System.err.println("Pool did not terminate");
		    }
		  } catch (InterruptedException ie) {
		    pool.shutdownNow();
		    Thread.currentThread().interrupt();
		}
	}
}	
