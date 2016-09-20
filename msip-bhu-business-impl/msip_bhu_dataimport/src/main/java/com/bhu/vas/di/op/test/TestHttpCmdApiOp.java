package com.bhu.vas.di.op.test;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.smartwork.msip.cores.helper.HttpHelper;
/**
 * 测试redis rpush
 * @author lawliet
 *
 */
public class TestHttpCmdApiOp {
	private static ThreadPoolExecutor monitor = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);
	private static ThreadPoolExecutor exec = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
	private static long startTs = 0;
	private static int Thread_Exec_Count = 1;
	
	private static Map<String, String> api_params = null;
	
	public static void main(String[] argv) throws IOException, ParseException{
		
		try{
			if(argv.length != 1){
				System.out.println("argv params is valid");
				System.exit(1);
			}
			Thread_Exec_Count = Integer.parseInt(argv[0]);
			
			api_params = new HashMap<String, String>();
			api_params.put("sk", "PzdfTFJSUEBHG0dcWFcLew==");
			api_params.put("mac", "84:82:f4:09:54:80");
			api_params.put("opt", "201");
			api_params.put("subopt", "00");
			api_params.put("extparams", "<cmd><ITEM cmd=\"webportal_user_status\" arg=\"auth_notice\" mac=\"38:bc:1a:2f:7e:79\" idle_timeout=\"300\" force_timeout=\"10\" ></ITEM></cmd>");
			
			System.out.println("Start TestHttpCmdApiOp Executor Thread");
			startTs = System.currentTimeMillis();
			
			exec();
			monitor();
			
			System.out.println("finish TestHttpCmdApiOp Executor Thread");
			
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}finally{
			
		}
		//System.exit(1);
	}
	
	public static void monitor(){
		monitor.submit((new Runnable() {
			@Override
			public void run() {
				try{
					while(true){
						//System.out.println(exec.getActiveCount());
						if(exec.getActiveCount() == 0){
							//if(exec.isTerminated()){
							System.out.println(String.format("Monitor Executor Thread finish ts [%s]ms", (System.currentTimeMillis() - startTs)));
							System.exit(1);
							//}
						}
						Thread.sleep(10l);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}));
	}
	
	public static void exec(){
		for(int i = 0;i<10;i++){
			exec.submit((new Runnable() {
				@Override
				public void run() {
					for(int i = 0;i<Thread_Exec_Count;i++){
						try{
							String response = HttpHelper.postUrlAsString("http://vap.bhunetworks.com/bhu_api/v1/dashboard/cmd/generate", 
									api_params);
							//System.out.println(response);
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
			}));
		}

	}
}
