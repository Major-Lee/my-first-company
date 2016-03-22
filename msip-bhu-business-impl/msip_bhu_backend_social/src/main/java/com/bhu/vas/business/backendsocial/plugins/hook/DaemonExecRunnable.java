package com.bhu.vas.business.backendsocial.plugins.hook;

import java.util.concurrent.ExecutorService;

public class DaemonExecRunnable implements Runnable{
	private ExecutorService exec;
	public DaemonExecRunnable(ExecutorService _exec){
		this.exec = _exec;
	}
	@Override
	public void run() {
		System.out.println("exec正在shutdown");
		exec.shutdown();
		System.out.println("exec正在shutdown成功");
		while(true){
			System.out.println("正在判断exec是否执行完毕");
			if(exec.isTerminated()){
				System.out.println("exec是否执行完毕,终止exec...");
				exec.shutdownNow();
				System.out.println("exec是否执行完毕,终止exec成功");
				break;
			}else{
				System.out.println("exec未执行完毕...");
				try {
					Thread.sleep(2*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		/*while(true){
			if(exec.isTerminated()){
				System.exit(0);
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
	}
}
