package com.bhu.vas.business.spark.streaming.wifistasniffer.receiver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

import com.bhu.vas.business.spark.streaming.wifistasniffer.TerminalScanStreamingDTO;
import com.smartwork.msip.cores.helper.JsonHelper;
/**
 * 模拟input数据源
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class TerminalScanReceiver extends Receiver<String>{
	  // ============= Receiver code that receives data over a socket ==============

	  public TerminalScanReceiver() {
	    super(StorageLevel.MEMORY_ONLY());
	  }

	  public void onStart() {
	    // Start the thread that receives data over a connection
	    new Thread()  {
	      @Override public void run() {
	        receive();
	      }
	    }.start();
	  }

	  public void onStop() {
	    // There is nothing much to do as the thread calling receive()
	    // is designed to stop by itself isStopped() returns false
	  }

	  /** Create a socket connection and receive data until receiver is stopped */
	  private void receive() {
			try{
				while(true){
					super.store(generateSimulateData());
					System.out.println("模拟接收数据");
					Thread.sleep(10000);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
	  }
	  
	  /**
	   * 生成模拟的input数据
	   * @return
	   */
	  private Iterator<String> generateSimulateData(){
			TerminalScanStreamingDTO dto1 = new TerminalScanStreamingDTO("aa:aa:aa:aa:aa:aa", "hh:hh:hh:hh:hh:h1");
			TerminalScanStreamingDTO dto2 = new TerminalScanStreamingDTO("aa:aa:aa:aa:aa:aa", "hh:hh:hh:hh:hh:h2");
			TerminalScanStreamingDTO dto3 = new TerminalScanStreamingDTO("bb:bb:bb:bb:bb:bb", "hh:hh:hh:hh:hh:h3");
			List<String> simulateDataList = new ArrayList<String>();
			simulateDataList.add(JsonHelper.getJSONString(dto1));
			simulateDataList.add(JsonHelper.getJSONString(dto2));
			simulateDataList.add(JsonHelper.getJSONString(dto3));
			return simulateDataList.iterator();
	  }
	  
	  public static void main(String[] args){
		  TerminalScanStreamingDTO dto1 = new TerminalScanStreamingDTO("aa:aa:aa:aa:aa:aa", "hh:hh:hh:hh:hh:h1");
		  System.out.println(JsonHelper.getJSONString(dto1));
	  }
}
