package com.bhu.vas.business.device;



import java.util.Date;

import com.bhu.vas.api.rpc.devicegroup.model.WifiDeviceBackendTask;
import com.bhu.vas.business.ds.devicegroup.service.WifiDeviceBackendTaskService;
public class WifiDevcieBackendTaskThread implements Runnable{
		
		private WifiDeviceBackendTaskService wifiDeviceBackendTaskService;
			
		public Long taskId;
		public Long taskTotal;
		public WifiDevcieBackendTaskThread(WifiDeviceBackendTaskService wifiDeviceBackendTaskService) {
			this.wifiDeviceBackendTaskService = wifiDeviceBackendTaskService;
		}
		
		public Long createTask(Long gid,Long total) throws Exception{
			WifiDeviceBackendTask bean = new WifiDeviceBackendTask();
			System.out.println(wifiDeviceBackendTaskService);
			System.out.println(total+","+gid);
			bean.setGid(gid);
			bean.setTotal(total);
			bean.setStarted_at(new Date());
			bean = wifiDeviceBackendTaskService.insert(bean);
			System.out.println("~~~~~~~~~~~~~~~~~~~createTask Bean:"+bean);
			taskId = bean.getId();
			taskTotal = bean.getTotal();
			System.out.println("Task:"+taskId+"...创建成功");
			
			return  taskId;
		}
		
		@Override
		public void run() {
			System.out.println("Run taskId:"+taskId);
			WifiDeviceBackendTask bean =wifiDeviceBackendTaskService.getById(taskId);
			System.out.println("~~~~~~~~~~~~~~~~~~~~bean"+bean);
			bean.setState(WifiDeviceBackendTask.State_Doing);
			wifiDeviceBackendTaskService.update(bean);
			System.out.println("Task:"+taskId+".....Doing");
			
			for(int i=1; i<=taskTotal;i++){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					
				}
				bean.setCurrent(bean.getCurrent()+1);
				System.out.println("TaskId:"+taskId+"执行至: ..."+bean.getCurrent()+"条");
				if(bean.getCurrent()%10 == 0){
					wifiDeviceBackendTaskService.update(bean);
					System.out.println("Task:"+this.taskId+"....正在更新");
				}
			}
			bean.setState(WifiDeviceBackendTask.State_Completed);
			wifiDeviceBackendTaskService.update(bean);
			System.out.println("Task:"+taskId+"，状态:"+bean.getState());
		}
}
