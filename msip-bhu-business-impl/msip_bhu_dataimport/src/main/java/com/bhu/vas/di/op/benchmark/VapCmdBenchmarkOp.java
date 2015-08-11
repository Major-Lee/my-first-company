package com.bhu.vas.di.op.benchmark;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetStorageFacadeService;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.iterator.IteratorNotify;


public class VapCmdBenchmarkOp{
	//每个线程执行多少个设备的指令下发
	public static int EveThread_MacCounts = 100;
	//一共多少个线程
	public static int Thread_Counts = 100;
	//每个线程循环多少次
	public static int EveThread_LoopCounts = 10;
	public static IDaemonRpcService daemonRpcService = null;
	
	public static void main(String[] args) throws Exception {
		if(args != null){
			if(args.length == 3){
				EveThread_MacCounts = Integer.parseInt(args[0]);
				Thread_Counts = Integer.parseInt(args[1]);
				EveThread_LoopCounts = Integer.parseInt(args[2]);
			}
		}
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/op/benchmark/vapCmdBenchmarkCtx.xml");
		daemonRpcService = (IDaemonRpcService)ctx.getBean("daemonRpcService");
		
/*		List<String> macs1 = new ArrayList<String>();
		macs1.add("44:44:44:44:44:44");
		
		List<String> macs2 = new ArrayList<String>();
		macs2.add("55:55:55:55:55:55");
		
		List<List<String>> macs = new ArrayList<List<String>>();
		macs.add(macs1);
		macs.add(macs2);
		
		VapCmdBenchmark benchmark1 = new VapCmdBenchmark(2,EveThread_LoopCounts);
		benchmark1.setSplits_macs(macs);
		benchmark1.setRpcService(daemonRpcService);
		benchmark1.execute();*/
		
//		VapCmdBenchmark benchmark2 = new VapCmdBenchmark(2,EveThread_LoopCounts);
//		benchmark2.setMacs(macs2);
//		benchmark2.setRpcService(daemonRpcService);
//		benchmark2.execute();
		
		final List<String> allOnlineMacs = new ArrayList<String>();
		
		HandsetStorageFacadeService.iteratorAll(new IteratorNotify<Map<String,String>>(){
			@Override
			public void notifyComming(Map<String, String> t) {
				Iterator<Entry<String, String>> iter = t.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String, String> next = iter.next();
					String value = next.getValue();//value
					if(StringUtils.isNotEmpty(value)){
						if(value.contains(HandsetDeviceDTO.Action_Online)){
							HandsetDeviceDTO dto = JsonHelper.getDTO(value, HandsetDeviceDTO.class);
							allOnlineMacs.add(dto.getMac());
						}
					}
				}
			}
		});
		
		List<List<String>> eveThreadMacsList = ArrayHelper.splitList(allOnlineMacs, EveThread_MacCounts);
		try{
			VapCmdBenchmark benchmark = new VapCmdBenchmark();
			benchmark.setSplits_macs(eveThreadMacsList);
			benchmark.setRpcService(daemonRpcService);
			benchmark.execute();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
}
