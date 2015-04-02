package com.bhu.jorion.zoo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.jorion.JOrion;
import com.bhu.jorion.JOrionConfig;
import com.bhu.jorion.ursids.UrsidsSession;
import com.bhu.jorion.util.SerializeUtil;
import com.bhu.jorion.util.StringHelper;

public class ZkWorker implements Watcher {
    private final static Logger LOGGER = LoggerFactory.getLogger(ZkWorker.class);
	private ZooKeeper zk;
	private Map<String, ZkUrsids> ursidsMap;
	private JOrion jorion;
	private boolean dead;
	
	
	public ZkWorker(JOrion jorion){
		ursidsMap = new ConcurrentHashMap<String, ZkUrsids>();
		this.jorion = jorion;
		dead = true;
	}
	
	private void onZkNodeCreated(WatchedEvent event){
		LOGGER.info("new node created:" + event.getPath());
		String path = event.getPath();
		readZkUrsidsFromZoo(path);
		balance();
	}
	private void onZkNodeDeleted(WatchedEvent event){
		LOGGER.info("node deleted:" + event.getPath());
		String path = event.getPath();
		String ursidsid = getUrsidsNameFromPath(path);
		if(ursidsMap.get(ursidsid) != null){
			ursidsMap.remove(ursidsid);
			balance();
		}
	}
	private void onZkNodeDataChange(WatchedEvent event){
		LOGGER.info("node data changed:" + event.getPath());
		String path = event.getPath();
		readZkUrsidsFromZoo(path);
		balance();
	}
	
	@Override
	public synchronized void process(WatchedEvent event) {
		if(event.getType() == Event.EventType.None){
			// We are are being told that the state of the
            // connection has changed
			switch (event.getState()) {
            case SyncConnected:
        		LOGGER.info("Zk Server connected");
        		dead = false;
            	syncZkStatus();
                break;
                default:
            		LOGGER.info("Zk status change to:" + event.getState());
                	break;
            case Expired:
        		LOGGER.info("Zk Server connected");
                // It's all over
                dead = true;
                break;
           }
		} else if(event.getType() == Event.EventType.NodeCreated){
			onZkNodeCreated(event);
		}else if(event.getType() == Event.EventType.NodeDeleted){
			onZkNodeDeleted(event);
		}else if(event.getType() == Event.EventType.NodeDataChanged){
			onZkNodeDataChange(event);
		}else if(event.getType() == Event.EventType.NodeChildrenChanged){
			syncZkStatus();
		}
	}
	
	public void start(){
		try {
			zk = new ZooKeeper(JOrionConfig.ZOO_URL, JOrionConfig.ZOO_SESSION_TIMEOUT, this);
		} catch (IOException e) {
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
	}
	private void balance(){
		LOGGER.info("find balance target...");
//		List l = new ArrayList(ursidsMap.size());
//		Iterator it = ursidsMap.keySet().
		Object[] za = ursidsMap.values().toArray();
		Arrays.sort(za, new ZkUrsidsComparator());
		int s = 0;
		int e = za.length - 1;
		
		//数组按照接入空闲数字升序排序
		//调整重定向的规则：数组中排最前的，重定向到排最后的
		//数组节点只有接入比超过指定值以后，才能向其他节点重定向。 
		//获取每个ursids的接入比例 n = current/max
		for(int i = 0; i < za.length; i++)
			LOGGER.debug("No." + i + "  " + za[i].toString());
		
		LOGGER.debug("browsing node map...");
		while(e > s){
			//寻找第一个需要重定向的目标
			while(s < za.length - 1){
				if(((ZkUrsids)za[s]).getConnectedRate() > JOrionConfig.ZOO_BALANCE_RATE)
					break;
				s ++;
			}
			while(e > 0){
				if((((ZkUrsids)za[e]).getFreeConnections() > 0) &&
						(((ZkUrsids)za[e]).getBalanceUrl() != null || !((ZkUrsids)za[e]).getBalanceUrl().isEmpty())
						)
					break;
				e --;
			}
			LOGGER.debug("s:" + s + "      e:" + e);
			if(s < e && ((ZkUrsids)za[e]).getBalanceUrl() != null){
				LOGGER.info("balance result:" + ((ZkUrsids)za[s]).getJorionId() + "^" + ((ZkUrsids)za[s]).getId() + " will redirect to " 
						+ ((ZkUrsids)za[e]).getJorionId() + ((ZkUrsids)za[e]).getId());
				//disalbe target's redirect func
				if(((ZkUrsids)za[e]).getJorionId().equals(JOrionConfig.ZOO_NAME))
					jorion.setBalanceTarget(((ZkUrsids)za[e]).getId(), null);
				
				if(((ZkUrsids)za[s]).getJorionId().equals(JOrionConfig.ZOO_NAME))
					jorion.setBalanceTarget(((ZkUrsids)za[s]).getId(), ((ZkUrsids)za[e]).getBalanceUrl());
			}
			s++;
			e--;
		}
	}
	
	
/*	
	private void balance(){
		LOGGER.debug("find balance target...");
		if(dead){
			jorion.setBalanceTarget(null);
			return;
		}
		LOGGER.debug("browsing node map...");
		ZkUrsids min = null;
		Iterator<String> it = ursidsMap.keySet().iterator();

		while(it.hasNext()){
			ZkUrsids zu = ursidsMap.get(it.next());
			if(min == null || 
					(zu.getBalanceUrl() != null && !zu.getBalanceUrl().isEmpty() 
					&&  zu.getAllDevCount() < min.getAllDevCount()))
				min = zu;
		}
		if(min != null){
			LOGGER.debug("balance result, target:" + min.getId() + " url:" + min.getBalanceUrl());
			jorion.setBalanceTarget(min.getBalanceUrl());
		}
	}
*/	
	public void syncZkStatus(){
		if(!initRoot())
			return;
		//up
		Iterator<String> it = ursidsMap.keySet().iterator();
		while(it.hasNext()){
			ZkUrsids zu = ursidsMap.get(it.next());
			if(zu.getJorionId().equals(JOrionConfig.ZOO_NAME))
				publishZkUrsidsToZoo(zu);
			else
				it.remove();
		}
		
		//down
		List<String> pl = null;
		try{
			pl = zk.getChildren(JOrionConfig.ZOO_START_APTH, true);
		}catch(Exception e){
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
		if(pl != null && !pl.isEmpty()){
			it = pl.iterator();
			while (it.hasNext()) {
				String path = it.next();
				readZkUrsidsFromZoo(JOrionConfig.ZOO_START_APTH + "/" + path);
			}
		}
		balance();
	}
	
	
	public String getZkPath(ZkUrsids zu){
		StringBuffer sb = new StringBuffer();
		sb.append(JOrionConfig.ZOO_START_APTH);
		sb.append("/");
		sb.append(zu.getJorionId());
		sb.append("^");
		sb.append(zu.getId());
		return sb.toString();
	}
	
	private String getOrionNameFromPath(String path){
		if(!path.startsWith(JOrionConfig.ZOO_START_APTH))
				return null;
		int k = path.indexOf('^');
		if(k <= JOrionConfig.ZOO_START_APTH.length())
			return "";
		return path.substring(JOrionConfig.ZOO_START_APTH.length(), k);
	}
	private String getUrsidsNameFromPath(String path){
		if(!path.startsWith(JOrionConfig.ZOO_START_APTH))
				return null;
		int k = path.indexOf('^');
		return path.substring(k);
	}
	
	private String getUrsidsName(UrsidsSession s){
		String id = s.getId();
		int k = id.lastIndexOf('_');
		if(k < 0)
			return null;
		return id.substring(0, k);
	}
	
	private String getUrisdsNumber(UrsidsSession s){
		String id = s.getId();
		int k = id.lastIndexOf('_');
		if(k < 0)
			return null;
		return id.substring(k + 1);
	}
	
	public boolean initRoot(){
		try{
			if(zk.exists(JOrionConfig.ZOO_START_APTH, true) == null){
				zk.create(JOrionConfig.ZOO_START_APTH, "root".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		}catch(Exception e){
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void readZkUrsidsFromZoo(String path){
		try {
			if (JOrionConfig.ZOO_NAME
					.equals(getOrionNameFromPath(path))) {
				zk.exists(path, false); // remove watch for my data
			} else {
//				zk.sync(path, cb, ctx);
				byte[] bt = zk.getData(path, true, null);
				ZkUrsids zu = (ZkUrsids) SerializeUtil.unserialize(bt);
				//for safe, ignore my data
				if (JOrionConfig.ZOO_NAME.equals(zu.getJorionId())) 
					return;
				ursidsMap.put(zu.getId(), zu);
			}
		} catch (Exception e) {
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
	}

	private void publishZkUrsidsToZoo(ZkUrsids zu){
		try{
			String path = getZkPath(zu);
			if(zk.exists(path, false) == null)
				zk.create(path, SerializeUtil.serialize(zu), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			else
				zk.setData(path, SerializeUtil.serialize(zu), -1);
		}catch(Exception e){
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
	}
	
	private ZkUrsids getMyZkUrsids(UrsidsSession s){
		String name = getUrsidsName(s);
		ZkUrsids u = ursidsMap.get(name);
		if(u != null){
			if(!u.getJorionId().equals(JOrionConfig.ZOO_NAME)){
				LOGGER.error("ursids " + name + " is connected with jorion:" + u.getJorionId());
				return null;
			}
			return u;
		}
		u = new ZkUrsids(JOrionConfig.ZOO_NAME, getUrsidsName(s), s.getBalanceUrl(), s.getMaxClient(), s.getReservedConection());
		ursidsMap.put(u.getId(), u);
		return u;
	}
	
	public synchronized void refreshDevCounts(UrsidsSession s){
		ZkUrsids u = getMyZkUrsids(s);
		if(u == null){
			return;
		}
		LOGGER.debug("ursids session " + s.getId() + " process:" + getUrisdsNumber(s) + "  count:" + s.getDevCount());
		u.setDevCountOfProcess(getUrisdsNumber(s), s.getDevCount());
		if(u.getLastReportCount() == -1 || 
				Math.abs(u.getCurrentClient() - u.getLastReportCount()) >= JOrionConfig.ZOO_SYNC_STEPS){
			publishZkUrsidsToZoo(u);
			u.setLastReportCount(u.getCurrentClient());
		}
	}
	
	public synchronized void removeUrisdsSession(UrsidsSession s){
		try{
			ZkUrsids zu = getMyZkUrsids(s);
			if(zu != null){
				String path = getZkPath(zu);
				zk.delete(path, -1);
				ursidsMap.remove(zu.getId());
			}
		}catch(Exception e){
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
	}
}
