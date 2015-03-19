package com.bhu.vas.rpc.service.daemon;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;



/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.business.asyn.normal.activemq.ActiveMQConnectionManager;
import com.bhu.vas.business.asyn.normal.activemq.ActiveMQDynamicProducer;
import com.bhu.vas.daemon.CMDBuilder;
import com.bhu.vas.daemon.SessionManager;
import com.bhu.vas.daemon.observer.DaemonObserverManager;
import com.bhu.vas.daemon.observer.listener.CmdDownListener;
import com.smartwork.msip.localunit.RandomData;

/**
 * @author Edmond
 *
 */
@Service("daemonRpcService")
public class DaemonRpcService implements IDaemonRpcService,CmdDownListener {
	private final Logger logger = LoggerFactory.getLogger(DaemonRpcService.class);

	@Resource
	private ActiveMQDynamicProducer activeMQDynamicProducer;
	
	@PostConstruct
	public void initialize(){
		DaemonObserverManager.CmdDownObserver.addCmdDownListener(this);
	}
	
	@Override
	public boolean wifiDeviceOnline(String ctx,String mac) {
		//System.out.println(String.format("wifiDeviceOnline ctx[%s] mac[%s]",ctx,mac));
		logger.info("info"+String.format("wifiDeviceOnline ctx[%s] mac[%s]",ctx,mac));
		SessionManager.getInstance().addSession(mac, ctx);
		//设备上行首先发送查询地理位置指令
		activeMQDynamicProducer.deliverMessage(CmCtxInfo.builderDownQueueName(ctx), CMDBuilder.builderDeviceLocationStep1Query(mac, RandomData.intNumber(1, 100000)));
		//DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, mac, CMDBuilder.builderDeviceLocationStep1Query(mac, RandomData.intNumber(1, 100000)));
		return false;
	}
	
	@Override
	public boolean wifiDeviceOffline(String ctx,String mac) {
		//System.out.println(String.format("wifiDeviceOffline ctx[%s] mac[%s]",ctx,mac));
		logger.info("info"+String.format("wifiDeviceOffline ctx[%s] mac[%s]",ctx,mac));
		SessionManager.getInstance().removeSession(mac);
		return false;
	}

	@Override
	public boolean wifiDeviceCmdDown(String ctx,String mac, String cmd) {
		//System.out.println(String.format("wifiDeviceCmdDown ctx[%s] mac[%s] cmd[%s]",ctx,mac,cmd));
		logger.info("info"+String.format("wifiDeviceCmdDown ctx[%s] mac[%s] cmd[%s]",ctx,mac,cmd));
		activeMQDynamicProducer.deliverMessage(CmCtxInfo.builderDownQueueName(ctx), cmd);
		return false;
	}

	@Override
	public boolean cmJoinService(CmCtxInfo info) {
		//System.out.println("cmJoinService:"+info);
		logger.info("info"+"cmJoinService:"+info);
		ActiveMQConnectionManager.getInstance().createNewProducerQueues("down", info.toString(), true);
		return false;
	}

	@Override
	public boolean cmLeave(CmCtxInfo info) {
		//System.out.println("cmLeave:"+info);
		logger.info("info"+"cmLeave:"+info);
		//createNewConsumerQueues("up", cmInfo.toString(),true);
		return false;
	}
}
