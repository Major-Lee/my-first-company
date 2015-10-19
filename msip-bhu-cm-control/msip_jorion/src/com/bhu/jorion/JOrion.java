package com.bhu.jorion;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.log4j.PropertyConfigurator;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtree.json.JSONReader;
import org.stringtree.json.JSONWriter;

import com.bhu.jorion.message.DevHeader;
import com.bhu.jorion.message.UrsidsMessage;
import com.bhu.jorion.mq.MqWorker;
import com.bhu.jorion.ursids.UrsidsSession;
import com.bhu.jorion.ursids.UrsidsWorker;
import com.bhu.jorion.util.StringHelper;
import com.bhu.jorion.zoo.ZkWorker;
import com.sun.jdmk.comm.AuthInfo;
import com.sun.jdmk.comm.HtmlAdaptorServer;

public class JOrion implements JOrionMBean{
    private final static Logger LOGGER = LoggerFactory.getLogger(JOrion.class);
    public static final CharsetDecoder charsetDecoder = (Charset.forName("UTF-8" /*"ISO-8859-1"*/)).newDecoder();
	private static final String SESSION_KEY = "ursids_id";
	public static final String SESSION_ID = "session_id";
	private UrsidsWorker ursidsWorker;
	private MqWorker mqWorker;
	private ZkWorker zkWorker;
	private Map<String, UrsidsSession> ursidsSessionMap;
//	private String balanceTarget;
	
	public String getStatus(){
		StringBuffer sb = new StringBuffer();
		synchronized(this){
			Iterator<String> it = ursidsSessionMap.keySet().iterator();
			while(it.hasNext()){
				UrsidsSession s = ursidsSessionMap.get(it.next());
				StringBuffer sb2 = new StringBuffer();
				sb.append("Ursids:" + s.getId());
				sb.append(" joined devs:" + s.getDevCount());
				sb.append(" blocked devs:" + s.getTaskDetail(sb2));
				sb.append(" blocking tasks:\n");
				sb.append(sb2);
				sb.append("\n\n\n");
			}
		}
		return sb.toString();
	}
	public void reloadLogConfiguration(){
		PropertyConfigurator.configure(ClassLoader.getSystemResource("log4j.properties"));
	}

	public UrsidsWorker getUrsidsWorker() {
		return ursidsWorker;
	}
	public void setUrsidsWorker(UrsidsWorker ursids) {
		this.ursidsWorker = ursids;
	}
	public MqWorker getMqWorker() {
		return mqWorker;
	}
	public void setMqWorker(MqWorker mq) {
		this.mqWorker = mq;
	}
	public JOrion(){
		ursidsWorker = new UrsidsWorker(this);
		mqWorker = new MqWorker(this);
		zkWorker = new ZkWorker(this);
		ursidsSessionMap = new ConcurrentHashMap<String, UrsidsSession>();
//		balanceTarget = "";
	}
/*	
	public synchronized String getBalanceTarget() {
		return balanceTarget;
	}

	public  void setBalanceTarget(String url) {
		String en = "enable";
		if(url == null || url.isEmpty()){
			en = "disable";
		}
		JSONWriter writer = new JSONWriter();
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", "balancer");
		m.put("enable", en);
		if(en.equals("enable")){
			Map<String, String> pm = new HashMap<String, String>();
			pm.put("addr", url);
			m.put("param", pm);
		}
		String rstr = writer.write(m);

		UrsidsMessage redirect = UrsidsMessage.composeUrsidsMessage(0, 1, rstr.getBytes());
		redirect.setMqMessage(false);

		synchronized(this){
			this.balanceTarget = url;
			try {
				Iterator<String> it = ursidsSession.keySet().iterator();
				while(it.hasNext()){
					UrsidsSession s = ursidsSession.get(it.next());
					ursids.sendMessage(s.getSession(), redirect);
				}
			} catch (Exception e) {
				LOGGER.error(StringHelper.getStackTrace(e));
				e.printStackTrace();
			}
		}
	}
*/	
	
	public  void setBalanceTarget(String name, String url) {
		LOGGER.info("setting balance for [" + name + "] url[" + url +"]");
		String en = "enable";
		if(url == null || url.isEmpty()){
			en = "disable";
		}
		JSONWriter writer = new JSONWriter();
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("name", "balancer");
		m.put("enable", en);
		if(en.equals("enable")){
			Map<String, String> pm = new HashMap<String, String>();
			pm.put("addr", url);
			m.put("param", pm);
		}
		String rstr = writer.write(m);
		
		m.clear();
		m.put("name", "balancer");
		m.put("enable", en);
		String strEmpty = writer.write(m);

		UrsidsMessage redirect = UrsidsMessage.composeUrsidsMessage(0, 1, rstr.getBytes());
		redirect.setMqMessage(false);

		UrsidsMessage redirectEmpty = UrsidsMessage.composeUrsidsMessage(0, 1, strEmpty.getBytes());
		redirectEmpty.setMqMessage(false);

		synchronized(this){
			try {
				Iterator<String> it = ursidsSessionMap.keySet().iterator();
				while(it.hasNext()){
					UrsidsSession s = ursidsSessionMap.get(it.next());
					if(s.getName().equals(name)){
						if((s.getRedirectUrl() == null && url == null)
								|| (s.getRedirectUrl() != null && s.getRedirectUrl().equals(url)))
							continue;
						ursidsWorker.sendMessage(s.getSession(), redirect);
						s.setRedirectUrl(url);
					} else {
						//if I'm the redirect target, then clear my target
						if(url != null && !url.isEmpty() && s.getBalanceUrl() != null){
							if(s.getBalanceUrl().equals(url)){
								ursidsWorker.sendMessage(s.getSession(), redirectEmpty);
								s.setRedirectUrl(null);
							}
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error(StringHelper.getStackTrace(e));
				e.printStackTrace();
			}
		}
	}

	public void onUrsidsNotifyMsg(IoSession is, UrsidsMessage msg){
		String id = (String)is.getAttribute(SESSION_KEY);
		UrsidsSession s = null;
		LOGGER.info("got notify mesg from:" + id);
		try{
			s = ursidsSessionMap.get(id);
		}catch(NullPointerException e){
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
		if(s == null){
			LOGGER.error("ursids session not found:" + id);
			return;
		}
		StringBuffer sb = new StringBuffer();
		IoBuffer ib = IoBuffer.wrap(msg.getBody());
		ib.order(ByteOrder.BIG_ENDIAN);
		ib.rewind();
		int type = ib.getUnsignedShort();
		String mac = StringHelper.byteToHexString(ib, 6).toLowerCase();
		LOGGER.info("mac:" + mac + " got notify, type:" + type);
		if(type == 1 || type == 3){
			sb.append((type == 1)?JOrionConfig.MSG_DEV_OFFLINE:JOrionConfig.MSG_DEV_NOTEXIST);
			sb.append(mac);
			mqWorker.publishBusiness(id, sb.toString());
			s.clearDeviceTasks(mac);
			if(type == 1){
				zkWorker.refreshDevCounts(s);
			}
		} else if(type == 2){
			//task execute result
			long taskid = ib.getUnsignedInt();
			int result = ib.getUnsignedShort();
			int rev = ib.getUnsignedShort();
			if(taskid != 0){
				if(result != 0){
					s.removeTask(mac, rev, taskid);
					pushUrsidsMessage(s, mac);
				}
			}
		} else if(type == 4){
			//notice connection number
			long count = ib.getUnsignedInt();
			s.setDevCount(count);
			LOGGER.debug("ursids session" + id + " current connection:" + count);
			zkWorker.refreshDevCounts(s);
		}
	}
	
	public void sendXmlApply(UrsidsSession s, PendingTask t) throws JMSException{
		LOGGER.info("sending xml reply");
		String xml = t.getMessage().getText().substring(45);
		byte[] xmlbytes = xml.getBytes();
		String mac = t.getMessage().getText().substring(8, 20).toLowerCase();
		int mtype = Integer.parseInt(t.getMessage().getText().substring(33, 37));
		int stype = Integer.parseInt(t.getMessage().getText().substring(37, 45));
		IoBuffer ib = IoBuffer.allocate(xmlbytes.length + 1 + DevHeader.DEV_HDR_LEN);
		DevHeader dev_hdr = new DevHeader();
		dev_hdr.setVer(0);
		dev_hdr.setMtype(mtype);
		dev_hdr.setStype(stype);
		dev_hdr.setSequence(DevHeader.getNextSendSequence());
		dev_hdr.setLength(xmlbytes.length + 1);
		dev_hdr.store(ib);
		ib.put(xmlbytes);
		ib.put((byte)0);
			
		UrsidsMessage xml_rsp = UrsidsMessage.composeUrsidsMessage(1, 3, mac, t.getRev(), t.getTaskid(), ib.array());
		try {
			t.setStatus(PendingTask.STATUS_SENDING);
			xml_rsp.setMqMessage(true);
			ursidsWorker.sendMessage(s.getSession(), xml_rsp);
		} catch (Exception e) {
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
		
	}
	public void sendJoinReply(UrsidsSession s, String mac){
		//need to send join_rsp
		LOGGER.info("Sending join reply for mac " + mac);
		String devrsp = "<join_rsp><ITEM result=\"ok\" /></join_rsp>";
		byte[] xmlbytes = devrsp.getBytes();
		IoBuffer ib = IoBuffer.allocate(xmlbytes.length + 1 + DevHeader.DEV_HDR_LEN);
		DevHeader dev_hdr = new DevHeader();
		dev_hdr.setVer(0);
		dev_hdr.setMtype(0);
		dev_hdr.setStype(2);
		dev_hdr.setSequence(DevHeader.getNextSendSequence());
		dev_hdr.setLength(xmlbytes.length + 1);
		dev_hdr.store(ib);
		ib.put(xmlbytes);
		ib.put((byte)0);
			
		UrsidsMessage join_rsp = UrsidsMessage.composeUrsidsMessage(1, 3, mac, 0, 0, ib.array());
		try {
			join_rsp.setMqMessage(false);
			ursidsWorker.sendMessage(s.getSession(), join_rsp);
			zkWorker.refreshDevCounts(s);
		} catch (Exception e) {
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
	}
	public void sendRedirect(UrsidsSession s, String mac, String url){
		LOGGER.info("Sending redirect reply for mac " + mac + " redirect to " + url);
		int pos = url.indexOf(':');
		String port = "8273";
		String addr = url;
		if(pos > 0){
			port = url.substring(pos + 1);
			addr = url.substring(0, pos);
		}
		String devrsp = "<redirect><ITEM addr=\"" + addr + "\" port=\"" + port + "\" /></redirect>";
		byte[] xmlbytes = devrsp.getBytes();
		IoBuffer ib = IoBuffer.allocate(xmlbytes.length + 1 + DevHeader.DEV_HDR_LEN);
		DevHeader dev_hdr = new DevHeader();
		dev_hdr.setVer(0);
		dev_hdr.setMtype(0);
		dev_hdr.setStype(3);
		dev_hdr.setSequence(DevHeader.getNextSendSequence());
		dev_hdr.setLength(xmlbytes.length + 1);
		dev_hdr.store(ib);
		ib.put(xmlbytes);
		ib.put((byte)0);
			
		UrsidsMessage redirect_rsp = UrsidsMessage.composeUrsidsMessage(1, 3, mac, 0, 0, ib.array());
		try {
			redirect_rsp.setMqMessage(false);
			ursidsWorker.sendMessage(s.getSession(), redirect_rsp);
		} catch (Exception e) {
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
	}
	
	public void onUrsidsForwardDevMsg(IoSession is, UrsidsMessage msg) throws UnsupportedEncodingException, CharacterCodingException{
		LOGGER.info("forwarding msg to mq...");
//		long start = System.currentTimeMillis();
		String id = (String)is.getAttribute(SESSION_KEY);
		UrsidsSession s = null;
		try{
			s = ursidsSessionMap.get(id);
		}catch(NullPointerException e){
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
		if(s == null){
			LOGGER.error("ursids session not found:" + id);
			return;
		}
		
		StringBuffer sb = new StringBuffer();
		IoBuffer ib = IoBuffer.wrap(msg.getBody());
		ib.order(ByteOrder.BIG_ENDIAN);
		sb.append(JOrionConfig.MSG_DEV_MSG_FORWARD);
		int rev = ib.getUnsignedShort(); //reserved
		String mac = StringHelper.byteToHexString(ib, 6).toLowerCase();
		long taskid = ib.getUnsignedInt();
		if(taskid != 0){
			s.removeTask(mac, rev, taskid);
		}
		LOGGER.debug("mac:" + mac + "  taskid:" + taskid + " rev:" + rev);
		DevHeader header = new DevHeader(ib);
		if(header.getMtype() == 0 && header.getStype() == 1){
			String redirect = s.getRedirectUrl();
			if(redirect != null && !redirect.isEmpty()){
				sendRedirect(s, mac, redirect);
				s.clearDeviceTasks(mac);
				return;
			}
			sendJoinReply(s, mac);
			s.clearDeviceTasks(mac);
		}
		sb.append(mac);
		sb.append(String.format("%1$03d", rev));
		sb.append(String.format("%1$010d", taskid));
		sb.append(String.format("%1$04d", header.getMtype()));
		sb.append(String.format("%1$08d", header.getStype()));
		sb.append(ib.getString(charsetDecoder));
		mqWorker.publishBusiness(id, sb.toString());			
		pushUrsidsMessage(s, mac);
//		LOGGER.debug("total cost:" + (System.currentTimeMillis() - start));
	}
	
	public void onUrisdsLeave(IoSession is){
		LOGGER.info(" ursids offline");
		UrsidsSession s = null;
		String id = (String)is.getAttribute(SESSION_KEY);
		if(id == null)
			return;
		synchronized(this){
			try{
				s = ursidsSessionMap.get(id);
			}catch(NullPointerException e){
				LOGGER.error(StringHelper.getStackTrace(e));
				e.printStackTrace();
			}
			if(s == null)
				return;
			LOGGER.info(" id " + id);
			UUID uid = (UUID)is.getAttribute(SESSION_ID);
			if(uid != null && !uid.equals(s.getSessionId())){
				LOGGER.error("can't close this ursids session, maybe it's reconnectd!");
				return;
			}
			s.setSession(null);
			s.setJoinedFlag(false);
			StringBuffer sb = new StringBuffer();
			JSONWriter writer = new JSONWriter();
			Map<String, String> m = new ConcurrentHashMap<String, String>();
			int pos = id.lastIndexOf("_");
			sb.append(JOrionConfig.MSG_URSIDS_OFFLINE);
			m.put("name", id.substring(0, pos));
			m.put("process_seq", id.substring(pos + 1));
			m.put("mq_host", JOrionConfig.MQ_BUSINESS_HOST);
			m.put("mq_port", JOrionConfig.MQ_BUSINESS_PORT);
			sb.append(writer.write(m));
			mqWorker.publishManagementMessage(sb.toString());
			mqWorker.ursidsLeave(id);
			s.clearAllDevs();
			ursidsSessionMap.remove(id);
		}		
		zkWorker.removeUrisdsSession(s);
	}
	
	public void onUrsidsJoin(IoSession is, UrsidsMessage msg) throws UnsupportedEncodingException {
		//ursids online
		LOGGER.info(" ursids online ");
		UrsidsSession s = null;
		String content = new String(msg.getBody(), "UTF-8");//"ISO-8859-1");
		Map m = (Map)(new JSONReader()).read(content);
		String name = (String)m.get("name");
		String seq = (String)m.get("process_seq");
		String id = name + "_" + seq;
		
		m.put("mq_host", JOrionConfig.MQ_BUSINESS_HOST);
		m.put("mq_port", JOrionConfig.MQ_BUSINESS_PORT);
		
		String new_content = (new JSONWriter()).write(m);

		is.setAttribute(SESSION_KEY, id);
		synchronized(this){
			try{
				s = ursidsSessionMap.get(id);
				s.setSession(is);
			}catch(NullPointerException e){
			}
			if(s == null){
				s = new UrsidsSession(is, name, id);
				ursidsSessionMap.put(id, s);
				LOGGER.info(" create new ursids for id " + id);
			}
			s.setSessionId((UUID)is.getAttribute(SESSION_ID));
			String state = (String)m.get("state");
			Long last_frag = (Long)m.get("last_frag");
			if(s.getJoinedFlag() == false && last_frag == 1)
				s.setJoinedFlag(true);
			s.setBalanceUrl((String)m.get("balance_url"));
			if(s.getJoinedFlag()){
				String str = (String)m.get("max_client");
				if(str != null)
					s.setMaxClient(Long.parseLong(str));
				str = (String)m.get("current_client");
				if(str != null)
					s.setDevCount(Long.parseLong(str));
				str = (String)m.get("reserved_connection");
				if(str != null)
					s.setReservedConection(Long.parseLong(str));
			}
			if(state.equals("init") && s.getJoinedFlag()){
				s.clearAllDevs();
			}
			if(mqWorker.ursidsJoin(id)){
				StringBuffer sb = new StringBuffer();
				sb.append(JOrionConfig.MSG_URSIDS_ONLINE);
				sb.append(new_content);
				mqWorker.publishManagementMessage(sb.toString());
			} else {
				LOGGER.error("Ursids join failed!");
				s.clearAllDevs();
				ursidsSessionMap.remove(id);
				is.close(true);
				return;
			}
		}
		zkWorker.refreshDevCounts(s);
	}
	
	
	/*
	public void onUrsidsMessageSent(IoSession is, UrsidsMessage msg){
		if(!msg.isMqMessage()){ //maybe it's join reply
			LOGGER.error("not a mq message:" + msg.getMsgid()) ;
			return;
		}
		String id = (String)is.getAttribute(SESSION_KEY);
		if(id == null){
			LOGGER.error("no session id found, msgid:" + msg.getMsgid()) ;
			return;
		}
		UrsidsSession s = null;
		try{
			s = ursidsSession.get(id);
		}catch(NullPointerException e){
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
		if(s == null){
			LOGGER.error("ursids session not found:" + id);
			return;
		}
		if(msg.getMac() == null || msg.getMac().isEmpty()){
			LOGGER.error("mac empty, msgid:" + msg.getMsgid()) ;
			return;
		}
		//remove current message
		PendingTask p = s.getNextTask(msg.getMac());
		if(p == null){
			LOGGER.error("no pending task, msgid:" + msg.getMsgid()) ;
			return;
		}
		if(p.getTaskid() != 0){
			LOGGER.error("pending task id is not zero, msgid:" + msg.getMsgid()) ;
			return;
		}
		if(p.getStatus() != PendingTask.STATUS_SENDING){
			LOGGER.debug("the message in queue head has a wrong stauts,mac:" + msg.getMac());
			return;
		}
		s.removeTask(msg.getMac(), p.getTaskid());
		//push next message
		pushUrsidsMessage(s, msg.getMac());
	}
	*/
	
	public void onUrsidsMessage(IoSession is, UrsidsMessage msg){
    	try {
    		if(msg.getHeader().getMtype() == 0){
    			if(msg.getHeader().getStype() == 2){
    				onUrsidsJoin(is, msg);
    			} else {
    				LOGGER.error("unsupported message:" + msg.getHeader().toString());
    			}
    		} else if(msg.getHeader().getMtype() == 1){
    			switch(msg.getHeader().getStype()){
    			case 1:
    				onUrsidsNotifyMsg(is, msg);
    				break;
    			case 2:
    				onUrsidsForwardDevMsg(is, msg);
    				break;
				default:
    				LOGGER.error("unsupported message:" + msg.getHeader().toString());
					break;
    			}
    		}
		} catch (Exception e) {
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
	}
	

	public void pushUrsidsMessage(UrsidsSession s, String mac){
		do{
			PendingTask p = s.getNextTask(mac);
			if(p == null)
				return;
			LOGGER.debug("Msg in queue head, mac:" + mac + " taskid:"+ p.getTaskid() + " rev:" + p.getRev());
			if(p.getStatus() != PendingTask.STATUS_PENDING){
				LOGGER.debug("previous message is waiting for response from dev");
				return;
			}
			try {
				sendXmlApply(s, p);
				if(p.getTaskid() != 0)
					return;
				s.removeTask(mac, p.getRev(), p.getTaskid());
			} catch (JMSException e) {
				LOGGER.error(StringHelper.getStackTrace(e));
				e.printStackTrace();
				return;
			}
		}while(true);
	}
	
	public void onMqMessage(String id, Message msg){
		TextMessage m = (TextMessage)msg;
		UrsidsSession s = null;
		try{
			s = ursidsSessionMap.get(id);
		}catch(NullPointerException e){
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
		if(s == null){
			LOGGER.error("ursids session not found:" + id);
			return;
		}
		try {
			if(!s.getJoinedFlag()){
				LOGGER.debug("ursids session offline:" + id);
				return;
			}
			String text = m.getText();
			if(text.length() < 45){
				LOGGER.error("Wrong Message length, drop message");
				return;
			}
			String type = text.substring(0, 8);
			if(type.equals(JOrionConfig.MSG_DEV_XML_APPLY)){
				String mac = text.substring(8, 20).toLowerCase();
				int	rev = Integer.parseInt(text.substring(20, 23));
				long taskid = Long.parseLong(text.substring(23, 33));
				s.addTask(mac, rev, taskid, m);
				pushUrsidsMessage(s, mac);
			} else {
				LOGGER.error("UnSupported Message type:" + type);
			}
		} catch (JMSException e) {
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
	}
	
	public void start(){
		zkWorker.start();
		mqWorker.start();
		ursidsWorker.start();
	}
	
	/**
	 * @param args
	 * @throws MalformedObjectNameException 
	 * @throws NotCompliantMBeanException 
	 * @throws MBeanRegistrationException 
	 * @throws InstanceAlreadyExistsException 
	 */
	public static void main(String[] args) throws Exception{
		
		JOrionConfig.loadConfig();
		
		JOrion jorion = new JOrion();
		
		MBeanServer server = MBeanServerFactory.createMBeanServer();
		
		ObjectName oname = new ObjectName("jorion:name=JOrion");
        server.registerMBean(jorion, oname);

        ObjectName adapterName = new ObjectName("jorionAgent:name=htmladapter,port=" + JOrionConfig.JMX_PORT);
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();
        server.registerMBean(adapter, adapterName);
        adapter.setPort(JOrionConfig.JMX_PORT);
        
        AuthInfo login = new AuthInfo();
        login.setLogin(JOrionConfig.JMX_USER);
        login.setPassword(JOrionConfig.JMX_PASS);
        adapter.addUserAuthenticationInfo(login);
        
		jorion.start();
        adapter.start();
		LOGGER.info("Server started!");
	}

}
