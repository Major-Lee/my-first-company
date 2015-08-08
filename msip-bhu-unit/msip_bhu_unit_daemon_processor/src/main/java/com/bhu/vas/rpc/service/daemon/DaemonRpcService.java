package com.bhu.vas.rpc.service.daemon;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.CmCtxInfo;
import com.bhu.vas.api.rpc.daemon.iservice.IDaemonRpcService;
import com.bhu.vas.business.asyn.normal.activemq.ActiveMQConnectionManager;
import com.bhu.vas.business.asyn.normal.activemq.ActiveMQDynamicProducer;
import com.bhu.vas.daemon.DaemonCheckTask;
import com.bhu.vas.daemon.SessionInfo;
import com.bhu.vas.daemon.SessionManager;
import com.bhu.vas.daemon.observer.DaemonObserverManager;
import com.bhu.vas.daemon.observer.listener.CmdDownListener;
import com.smartwork.msip.cores.helper.task.TaskEngine;

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
	public boolean wifiDevicesOnline(String ctx,List<String> macs) {
		logger.info(String.format("wifiDeviceOnline ctx[%s] macs[%s]",ctx,macs));
		for(String mac:macs){
			SessionManager.getInstance().addSession(mac, ctx);
			//设备上行首先发送查询地理位置指令
			//activeMQDynamicProducer.deliverMessage(CmCtxInfo.builderDownQueueName(ctx), CMDBuilder.builderDeviceLocationStep1Query(mac, RandomData.intNumber(1, 100000)));
			//activeMQDynamicProducer.deliverMessage(CmCtxInfo.builderDownQueueName(ctx), CMDBuilder.builderDeviceLocationStep1Query(mac, CMDBuilder.location_taskid_fragment.getNextSequence()));
		}
		return false;
	}
	
	@Override
	public boolean wifiDeviceOnline(String ctx,String mac) {
		logger.info(String.format("wifiDeviceOnline ctx[%s] mac[%s]",ctx,mac));
		SessionManager.getInstance().addSession(mac, ctx);
		//设备上行首先发送查询地理位置指令
		//activeMQDynamicProducer.deliverMessage(CmCtxInfo.builderDownQueueName(ctx), CMDBuilder.builderDeviceLocationStep1Query(mac, CMDBuilder.location_taskid_fragment.getNextSequence()));
		//DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, mac, CMDBuilder.builderDeviceLocationStep1Query(mac, RandomData.intNumber(1, 100000)));
		return false;
	}
	
	@Override
	public boolean wifiDeviceOffline(String ctx,String mac) {
		logger.info(String.format("wifiDeviceOffline ctx[%s] mac[%s]",ctx,mac));
		SessionInfo sessionCtx = SessionManager.getInstance().getSession(mac);
		if(sessionCtx != null && ctx.equals(sessionCtx.getCtx())){
			SessionManager.getInstance().removeSession(mac);
		}else{
			;//TODO:如何处理
		}
		return false;
	}

	@Override
	public boolean wifiDeviceCmdDown(String ctx,String mac, String cmd) {
		//logger.info(String.format("wifiDeviceCmdDown0 ctx[%s] mac[%s] cmd[%s]",ctx,mac,cmd));
		if(StringUtils.isEmpty(ctx)){
			SessionInfo sessionCtx = SessionManager.getInstance().getSession(mac);
			if(sessionCtx != null){
				ctx = sessionCtx.getCtx();
				logger.info(String.format("wifiDeviceCmdDown ctx[%s] mac[%s] cmd[%s] ctx existed",ctx,mac,cmd));
			}else{
				logger.info(String.format("wifiDeviceCmdDown ctx[%s] mac[%s] cmd[%s] ctx not existed",ctx,mac,cmd));
				return false;
			}
		}else{
			logger.info(String.format("wifiDeviceCmdDown with ctx[%s] mac[%s] cmd[%s]",ctx,mac,cmd));
		}
		activeMQDynamicProducer.deliverMessage(CmCtxInfo.builderDownQueueName(ctx), cmd);
		return true;
	}

	@Override
	public boolean wifiDeviceCmdsDown(String ctx, String mac, List<String> cmds) {
		//logger.info(String.format("wifiDeviceCmdsDown0 ctx[%s] mac[%s] cmds[%s]",ctx,mac,cmds));
		if(StringUtils.isEmpty(ctx)){
			SessionInfo sessionCtx = SessionManager.getInstance().getSession(mac);
			if(sessionCtx != null){
				ctx = sessionCtx.getCtx();
				logger.info(String.format("wifiDeviceCmdsDown1 ctx[%s] mac[%s] cmds[%s] ctx existed",ctx,mac,cmds));
			}else{
				logger.info(String.format("wifiDeviceCmdsDown2 ctx[%s] mac[%s] cmds[%s] ctx not existed",ctx,mac,cmds));
				return false;
			}
		}else{
			logger.info(String.format("wifiDeviceCmdsDown with ctx[%s] mac[%s] cmds[%s]",ctx,mac,cmds));
		}
		for(String cmd:cmds){
			activeMQDynamicProducer.deliverMessage(CmCtxInfo.builderDownQueueName(ctx), cmd);
		}
		return true;
	}
	
	@Override
	public boolean cmJoinService(CmCtxInfo info) {
		//System.out.println("cmJoinService:"+info);
		logger.info("cmJoinService:"+info);
		ActiveMQConnectionManager.getInstance().createNewProducerQueues("down", info.toString(), true);
		return true;
	}

	@Override
	public boolean cmLeave(CmCtxInfo info) {
		//System.out.println("cmLeave:"+info);
		logger.info("cmLeave:"+info);
		//清除所有此cm的设备
		SessionManager.getInstance().removeSessionByCtx(info.toString());
		//createNewConsumerQueues("up", cmInfo.toString(),true);
		return true;
	}

	/*@Override
	public boolean wifiDeviceSerialTaskComming(String ctx,String payload,ParserHeader header){
		logger.info(String.format("wifiDeviceSerialTaskComming ctx[%s] header[%s] payload[%s]",ctx,header,payload));
		try{
			QuerySerialReturnDTO retDTO = RPCMessageParseHelper.parserMessageByDom4j(payload, QuerySerialReturnDTO.class);
			if(StringUtils.isNotEmpty(retDTO.getSerial())){//如果此类消息没有serial则忽略掉
				SessionManager.getInstance().getSerialTaskmap().put(header.getMac()+retDTO.getSerial(), new SerialTask(header.getMac(),header.getTaskid(),retDTO.getSerial()));
			}
		}catch(Exception ex){
			logger.error("wifiDeviceSerialTaskComming paser payload", ex);
		}
		return true;
	}*/

	@Override
	public boolean wifiDevicesOnlineTimer() {
		logger.info("wifiDevicesOnlineTimer notify");
		TaskEngine.getInstance().schedule(new DaemonCheckTask(), 5*1000);
		return true;
	}

	/*@Override
	public boolean wifiDevicesLocationQuerySerialTimer() {
		logger.info("wifiDevicesLocationQuerySerialTimer notify");
		TaskEngine.getInstance().schedule(new DaemonCheckSerialTask(), 5*1000);
		return true;
	}*/
	
	/*public static void main(String[] argv) throws InterruptedException{
		TaskEngine.getInstance().schedule(new TimerTask(){
			@Override
			public void run() {
				System.out.println(1234);
			}
		}, 5*1000);
		
		Thread.sleep(100000);
	}*/
}
