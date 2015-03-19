package com.bhu.vas.daemon;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.helper.CMDBuilder;
import com.bhu.vas.daemon.observer.DaemonObserverManager;
import com.smartwork.msip.cores.helper.StringHelper;

public class DaemonCheckSerialTask extends TimerTask{
	@Override
	public void run() {
		System.out.println("DaemonCheckSerialTask starting...");
		long current = System.currentTimeMillis();
    	Iterator<Map.Entry<String,SerialTask>> iter = SessionManager.getInstance().getSerialTaskmap().entrySet().iterator();
    	int i=0;
    	int unsended = 0;
    	int sended = 0;
    	int notExistMac = 0;
    	while(iter.hasNext()){
    		Map.Entry<String,SerialTask> element = iter.next();
    		String key = element.getKey();
    		SerialTask task = element.getValue();
    		if(task.canBeExecute(current)){
    			String wifi_mac = task.getMac();
    			String ctx = SessionManager.getInstance().getSession(wifi_mac);
    			if(StringUtils.isNotEmpty(ctx)){//找不到wifimac对应的ctx了
    				DaemonObserverManager.CmdDownObserver.notifyCmdDown(ctx, StringHelper.unformatMacAddress(task.getMac()), CMDBuilder.builderDeviceLocationStep2Query(task.getMac(), task.getTaskid(), task.getSerialno()));
    				SessionManager.getInstance().getSerialTaskmap().remove(key);
    				sended++;
    			}else{
    				notExistMac++;
    			}
    		}else{
    			unsended++;
    		}
    		i++;
    	}
    	System.out.println(String.format("DaemonCheckSerialTask ended! Total[%s] Sended[%s] UnSended[%s] notExistMac[%s]",i,sended,unsended,notExistMac));
	}
}
