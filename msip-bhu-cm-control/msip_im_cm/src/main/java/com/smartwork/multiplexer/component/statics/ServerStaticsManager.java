package com.smartwork.multiplexer.component.statics;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.im.container.BasicModule;
import com.smartwork.im.container.IServerContainer;
import com.smartwork.im.utils.JingGlobals;
import com.smartwork.msip.cores.helper.task.TaskEngine;
import com.smartwork.multiplexer.SessionManager;

public class ServerStaticsManager extends BasicModule{
	private static final Logger logger = LoggerFactory.getLogger(ServerStaticsManager.class);
	//private IServerContainer server;
	
	private boolean printDetail = false;
	public ServerStaticsManager() {
		super("ServerStaticsManager");
		System.out.println("ServerStaticsManager 构造");
	}
	@Override
	public void initialize(IServerContainer server) {
		super.initialize(server);
		System.out.println("ServerStaticsManager 初始化");
		//this.server = server;
	}
	@Override
	public void start() throws IllegalStateException {
		super.start();
		printDetail = JingGlobals.getBooleanProperty("print.statics.detail", printDetail);
		int timegap = JingGlobals.getIntProperty("print.statics.timegap", 5*60)*1000;
		TaskEngine.getInstance().schedule(new DamenStaticsTask(), 60*1000,timegap);
		System.out.println("ServerStaticsManager 启动");
	}
	@Override
	public void stop() {
		super.stop();
	}
	@Override
	public void destroy() {
		super.destroy();
	}
	private class DamenStaticsTask extends TimerTask{
		@Override
		public void run() {
			StringBuilder sb = new StringBuilder("******************************\n");
			sb.append(SessionManager.getInstance().sessionsStat(printDetail));
			/*sb.append(server.getFwdListenManagerImpl().routingStat(printDetail));
			sb.append(server.getClusterNodeManager().routingStat(printDetail));*/
			sb.append("******************************\n");
			logger.info(sb.toString());
			//System.out.println(sb.toString());
			//System.out.println("prepare statics data of MDS Server");
			//System.out.println("prepare statics Online data of MDS Server");
			//System.out.println(server.getRoutingTableImpl().routingStat(true));
			/*Collection<RoutingInfo> values = server.getRoutingTableImpl().values();
			for(RoutingInfo info:values){
				System.out.println(info);
			}*/
			/*System.out.println("prepare statics FwdListen data of MDS Server");
			Iterator<Entry<String, FwdInfo>> iterator = server.getFwdListenManagerImpl().iterator();
			while (iterator.hasNext()) { 
				Entry<String, FwdInfo> entry = iterator.next(); 
			    //String key = entry.getKey(); 
			    FwdInfo val = entry.getValue(); 
			    System.out.println(val);
			} */
			
		}
	}
}
