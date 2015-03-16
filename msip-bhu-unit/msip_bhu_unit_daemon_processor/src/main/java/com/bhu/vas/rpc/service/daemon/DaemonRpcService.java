package com.bhu.vas.rpc.service.daemon;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.business.mq.activemq.ActiveMQDynamicProducer;
import com.bhu.vas.daemon.SessionManager;
import com.bhu.vas.daemon.observer.DaemonObserverManager;
import com.bhu.vas.daemon.observer.listener.CmdDownListener;

/**
 * 去除掉token存储在db中？只使用redis会比较好？
 * @author Edmond
 *
 */
@Service("daemonRpcService")
public class DaemonRpcService implements IDaemonRpcService,CmdDownListener {
	//private final Logger logger = LoggerFactory.getLogger(DaemonRpcService.class);

	@Resource
	private ActiveMQDynamicProducer activeMQDynamicProducer;
	
	
	@PostConstruct
	public void initialize(){
		DaemonObserverManager.CmdDownObserver.addCmdDownListener(this);
	}
	
	@Override
	public boolean wifiDeviceOnline(String ctx,String mac) {
		System.out.println(String.format("wifiDeviceOnline ctx[%s] mac[%s]",ctx,mac));
		SessionManager.getInstance().addSession(mac, ctx);
		return false;
	}
	
	@Override
	public boolean wifiDeviceOffline(String ctx,String mac) {
		System.out.println(String.format("wifiDeviceOffline ctx[%s] mac[%s]",ctx,mac));
		SessionManager.getInstance().removeSession(mac);
		return false;
	}

	@Override
	public boolean wifiDeviceCmdDown(String ctx,String mac, String cmd) {
		System.out.println(String.format("wifiDeviceCmdDown ctx[%s] mac[%s] cmd[%s]",ctx,mac,cmd));
		activeMQDynamicProducer.deliverMessage(CmCtxInfo.builderDownQueueName(ctx), cmd);
		return false;
	}

	@Override
	public boolean cmJoinService(CmCtxInfo info) {
		System.out.println("cmJoinService:"+info);
		return false;
	}

	@Override
	public boolean cmLeave(CmCtxInfo info) {
		System.out.println("cmLeave:"+info);
		return false;
	}
}
