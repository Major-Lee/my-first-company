package com.bhu.vas.di.op.benchmark;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;


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
		
		daemonRpcService.wifiDevicesSimulateCmdTimer();
	}
}
