package com.bhu.vas.di.op.benchmark;

import java.util.ArrayList;
import java.util.List;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.api.rpc.daemon.helper.DaemonHelper;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.smartwork.msip.localunit.RandomPicker;
import com.smartwork.msip.localunit.benchmark.BenchmarkTask;
import com.smartwork.msip.localunit.benchmark.ConcurrentBenchmark;

public class VapCmdBenchmark extends ConcurrentBenchmark {
	private static final int DEFAULT_THREAD_COUNT = 1;
	private static final int DEFAULT_TOTAL_COUNT = 2;
	
	private List<List<String>> splits_macs = null;
	
	private IDaemonRpcService rpcService = null;
	
	public VapCmdBenchmark(/*String mode,int thread,int total*/) {
		super(DEFAULT_THREAD_COUNT, DEFAULT_TOTAL_COUNT);
	}

	public VapCmdBenchmark(int thread,int total) {
		super(thread, total);
	}
	public static void main(String[] args) throws Exception {
//		List<String> macs = new ArrayList<String>();
//		macs.add("44:44:44:44:44:44");
//		//System.out.println(String.format("Api Url Request[%s]:", suffix_business_uri[index]));
//		VapCmdBenchmark benchmark = new VapCmdBenchmark(DEFAULT_THREAD_COUNT,DEFAULT_TOTAL_COUNT);
//		benchmark.setSplits_macs(splits_macs);
//		benchmark.setRpcService(rpcService);
		/*benchmark.setThread(thread);
		benchmark.setTotal(total);*/
//		benchmark.execute();
	}

	@Override
	protected BenchmarkTask createTask() {
		return new VapCmdTask();
	}
	
	public class VapCmdTask extends BenchmarkTask {
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
			System.out.println(String.format("VapCmdTask Thread[%s]:", requestSequence));
			if(splits_macs == null || splits_macs.isEmpty()) return;
			if(rpcService == null) return;
			try{
				List<String> random_macs = RandomPicker.pick(splits_macs);
				for(String mac : random_macs){
					System.out.println(String.format("VapCmdTask Request[%s]:", mac));
					DaemonHelper.daemonCmdsDown(mac, builderCmdPayloads(mac), rpcService);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}

	public List<String> builderCmdPayloads(String mac){
		ArrayList<String> payloads = new ArrayList<String>();
		//获取配置指令
		payloads.add(CMDBuilder.builderDeviceSettingQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence()));
		//获取地理位置
		payloads.add(CMDBuilder.builderDeviceLocationNotifyQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence()));
		//修改信号强度
		String config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><wifi><radio><ITEM name=\"wifi0\" power=\"20\" /></radio></wifi></dev>";
		payloads.add(CMDBuilder.builderDeviceSettingModify(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
		//修改admin管理密码
		config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><sys><users><ITEM password_rsa=\"8143b8e16ebff24b330ca46bd7b358d265431a323f05120d723d73bc4fab2f373fe7088d9e054698c53122161ba11cbfd5df7412afffda396d567f51299f12be\" name=\"admin\" /></users></sys></dev>";
		payloads.add(CMDBuilder.builderDeviceSettingModify(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
		//广告注入开启
		config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><net><ad><ITEM  bhu_id=\"1000000\" bhu_ad_url=\"http://auth.wi2o.cn/ad/ad.js\" bhu_enable=\"enable\"  /></ad></net></dev>";
		payloads.add(CMDBuilder.builderDeviceSettingModify(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
		//重定向开启
		config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><net><ad><ITEM bhu_http_redirect_enable=\"enable\" bhu_http_redirect_rule=\"20,8:00:00,22:00:00,http://www.sina.com.cn,http://www.bhunetworks.com,http://www.chinaren.com,http://www.bhunetworks.com\"/></ad></net></dev>";
		payloads.add(CMDBuilder.builderDeviceSettingModify(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
		//404跳转开启
		config_payload = "<dev><sys><config><ITEM sequence=\"-1\"/></config></sys><net><ad><ITEM bhu_http404_enable=\"enable\" bhu_http404_url=\"http://vap.bhunetworks.com/vap/rw404?bid=10002\" bhu_http404_codes=\"40*,502\"/></ad></net></dev>";
		payloads.add(CMDBuilder.builderDeviceSettingModify(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), config_payload));
		//访客网络开启
		payloads.add(CMDBuilder.builderCMD4HttpPortalResourceUpdate(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), 
				"{\"style\":\"style000\"}"));
		//查询终端速率
		payloads.add(CMDBuilder.builderDeviceTerminalsQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), 2, 10));
		//查询设备网速
		payloads.add(CMDBuilder.builderDeviceSpeedNotifyQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), 2, 10, "", ""));
		//查询设备实时速率
		payloads.add(CMDBuilder.builderDeviceRateNotifyQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), "wan", 2, 10));
		//查询设备dhcpc信息
		payloads.add(CMDBuilder.builderDhcpcStatusQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence(), "eth1"));
		//查询设备使用情况
		payloads.add(CMDBuilder.builderDeviceUsedStatusQuery(mac, CMDBuilder.auto_taskid_fragment.getNextSequence()));
		
		return payloads;
	}

	public void setSplits_macs(List<List<String>> splits_macs) {
		this.splits_macs = splits_macs;
	}

	public void setRpcService(IDaemonRpcService rpcService) {
		this.rpcService = rpcService;
	}
}
