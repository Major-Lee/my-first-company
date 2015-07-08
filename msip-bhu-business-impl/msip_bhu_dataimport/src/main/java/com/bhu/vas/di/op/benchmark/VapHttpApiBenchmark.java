package com.bhu.vas.di.op.benchmark;

import java.util.HashMap;
import java.util.Map;

import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.localunit.benchmark.BenchmarkTask;
import com.smartwork.msip.localunit.benchmark.ConcurrentBenchmark;

public class VapHttpApiBenchmark extends ConcurrentBenchmark {
	private static final String UID_Field = "uid";
	private static final String UID_Value = "5";
	private static final String MAC_Field = "mac";
	private static final String MAC_Value = "84:82:f4:19:01:10";
	
	private static final String Remote_rest_prefix = "http://vap.bhunetworks.com/bhu_api/v1/";
	private static final String Local_rest_prefix = "http://10.171.90.208:8080/msip_bhu_rest/";
	private static final int DEFAULT_THREAD_COUNT = 50;
	private static final long DEFAULT_TOTAL_COUNT = 10000;
	
	public static final String ARGUMENT_REMOTE = "REMOTE";
	public static final String ARGUMENT_LOCAL = "LOCAL";
	
	public static final String[] suffix_business_uri = {
		"sessions/validates",//header uid
		"noauth/query/fetch_device_bind_user",//获取设备绑定的用户
		"urouter/device/device_query_used_status", //header uid mac
		"urouter/device/hd_list"//终端列表
	};
	String mode;
	//int thread;
	//int total;
	int index;
	public VapHttpApiBenchmark(/*String mode,int thread,int total*/) {
		super(DEFAULT_THREAD_COUNT, DEFAULT_TOTAL_COUNT);
	}

	public VapHttpApiBenchmark(int thread,int total) {
		super(thread, total);
	}
	public static void main(String[] args) throws Exception {
		
		if(args.length != 4){
			System.out.println(" loss param!");
			return;
		}
		String mode = args[0];
		int index = Integer.parseInt(args[1]);
		int thread = Integer.parseInt(args[2]);
		int total = Integer.parseInt(args[3]);
		
		System.out.println(String.format("Api Url Request[%s]:", suffix_business_uri[index]));
		VapHttpApiBenchmark benchmark = new VapHttpApiBenchmark(thread,total);
		benchmark.setMode(mode);
		benchmark.setIndex(index);
		/*benchmark.setThread(thread);
		benchmark.setTotal(total);*/
		benchmark.execute();
	}

	@Override
	protected BenchmarkTask createTask() {
		return new VapHttpApiTask();
	}
	
	public class VapHttpApiTask extends BenchmarkTask {
		/*String mode ;
		int thread ;
		int total ;
		
		
		public VapHttpApiTask(String mode, int thread, int total) {
			super();
			this.mode = mode;
			this.thread = thread;
			this.total = total;
		}*/

		@Override
		protected void execute(int requestSequence) {
			Map<String,String> params = new HashMap<String,String>();
			params.put(UID_Field, UID_Value);
			params.put(MAC_Field, MAC_Value);
			params.put("status", "1");
			Map<String,String> headers = new HashMap<String,String>();
			headers.put(RuntimeConfiguration.Param_ATokenHeader, "OjJWVFJXAUEXT0UNCVFf");
			try {
				String url = null;//"urouter/device/device_query_used_status";
				if(ARGUMENT_REMOTE.equals(mode)){
					url = Remote_rest_prefix.concat(suffix_business_uri[index]);
				}else{
					url = Local_rest_prefix.concat(suffix_business_uri[index]);
				}
				String postUrlAsString = HttpHelper.postUrlAsString(url, 
							params, headers, StringHelper.CHATSET_UTF8);
				//System.out.println(postUrlAsString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/*public void setThread(int thread) {
		this.thread = thread;
	}

	public void setTotal(int total) {
		this.total = total;
	}*/
	
	
}
