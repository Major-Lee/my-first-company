package com.bhu.vas.business.task;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.business.ds.task.facade.TaskFacadeService;
import com.smartwork.msip.localunit.BaseTest;
import com.smartwork.msip.localunit.RandomData;
import com.smartwork.msip.localunit.RandomPicker;

public class WifiTaskTest extends BaseTest{

	@Resource
	private TaskFacadeService taskFacadeService;
	
	static String[] letters = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	private static List<String> device_macs = new ArrayList<String>();
	static{
		device_macs.add("34:36:3b:d0:4b:ac");
		device_macs.add("72:00:08:75:ef:c0");
		device_macs.add("72:00:08:75:ef:c1");
		device_macs.add("06:36:3b:d0:4b:ac");
		device_macs.add("8a:48:9b:16:97:0f");
		device_macs.add("36:36:3b:0d:18:00");
		device_macs.add("C8:9C:DC:E6:77:E6");
		device_macs.add("D4:BE:D9:E3:8F:EB");
		device_macs.add("8a:48:9b:16:07:0f");
		device_macs.add("8a:48:9b:16:17:1f");
		device_macs.add("8a:48:9b:16:27:2f");
		device_macs.add("8a:48:9b:16:37:3f");
		device_macs.add("8a:48:9b:16:47:4f");
		device_macs.add("8a:48:9b:16:57:5f");
		device_macs.add("8a:48:9b:16:67:6f");
		device_macs.add("8a:48:9b:16:77:7f");
		device_macs.add("8a:48:9b:16:87:8f");
		device_macs.add("8a:48:9b:16:97:9f");
		device_macs.add("8a:48:1b:16:07:7f");
		device_macs.add("8a:48:2b:16:17:6f");
		device_macs.add("8a:48:3b:16:27:5f");
	}
	
	public void testBatchTaskComming(){
    	System.out.println(Integer.MAX_VALUE);
    	for(int i=0;i<100;i++){
    		WifiDeviceDownTask downtask = new WifiDeviceDownTask();
    		if(RandomData.flag()){
    			downtask.setChannel("diylv");//RandomPicker.randString(letters,5));
        		downtask.setChannel_taskid(446);//RandomData.intNumber(10));
    		}
    		downtask.setMac(RandomPicker.pick(device_macs));
    		downtask.setPayload("how are u!");
    		int ret = taskFacadeService.taskComming(downtask);
    		switch(ret){
    			case TaskFacadeService.Task_Illegal:
    				System.out.println(String.format("无效或非法的任务"));
    				break;
    			case TaskFacadeService.Task_Already_Exist:
    				System.out.println(String.format("任务已经存在"));
    				break;
    			case TaskFacadeService.Task_Already_Completed:
    				System.out.println(String.format("任务已经完成"));
    				break;
    			case TaskFacadeService.Task_Startup_OK:
    				System.out.println(String.format("任务入库成功 taskid[%s]",downtask.getId()));
    				break;
    			
    		}
    	}
    	
	}	
	static String[] states = {WifiDeviceDownTask.State_Failed,WifiDeviceDownTask.State_Pending,WifiDeviceDownTask.State_Completed,WifiDeviceDownTask.State_Timeout};
	static String[] responses = {"1234","5678","2345","4567"};
	@Test
	public void taskCallbackTest(){
		for(int i=0;i<100;i++){
			taskFacadeService.taskExecuteCallback(i, RandomPicker.pick(states),RandomPicker.pick(responses));
		}
		
	}
	
}
