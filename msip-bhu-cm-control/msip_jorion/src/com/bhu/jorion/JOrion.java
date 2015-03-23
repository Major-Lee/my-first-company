package com.bhu.jorion;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

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

public class JOrion {
    private final static Logger LOGGER = LoggerFactory.getLogger(JOrion.class);
    public static final CharsetDecoder charsetDecoder = (Charset.forName("UTF-8" /*"ISO-8859-1"*/)).newDecoder();
	private static final String SESSION_KEY = "ursids_id";
	private UrsidsWorker ursids;
	private MqWorker mq;
	private Map<String, UrsidsSession> ursidsSession;
	
	public UrsidsWorker getUrsids() {
		return ursids;
	}
	public void setUrsids(UrsidsWorker ursids) {
		this.ursids = ursids;
	}
	public MqWorker getMq() {
		return mq;
	}
	public void setMq(MqWorker mq) {
		this.mq = mq;
	}
	public JOrion(){
		ursids = new UrsidsWorker(this);
		mq = new MqWorker(this);
		ursidsSession = new ConcurrentHashMap<String, UrsidsSession>();
	}
	
	public void onUrsidsNotifyMsg(IoSession is, UrsidsMessage msg){
		String id = (String)is.getAttribute(SESSION_KEY);
		UrsidsSession s = ursidsSession.get(id);
		if(s == null){
			//TODO:no such session;
			return;
		}
		StringBuffer sb = new StringBuffer();
		IoBuffer ib = IoBuffer.wrap(msg.getBody());
		ib.rewind();
		int type = ib.getUnsignedShort();
		String mac = StringHelper.byteToHexString(ib, 6).toLowerCase();
		if(type == 1 || type == 3){
			sb.append((type == 1)?JOrionConfig.MSG_DEV_OFFLINE:JOrionConfig.MSG_DEV_NOTEXIST);
			sb.append(mac);
			mq.publishBusiness(id, sb.toString());
			s.removeDevice(mac);
		}
	}
	
	public void sendXmlApply(UrsidsSession s, PendingTask t) throws JMSException{
		String xml = t.getMessage().getText().substring(42);
		String mac = t.getMessage().getText().substring(8, 20).toLowerCase();
		int mtype = Integer.parseInt(t.getMessage().getText().substring(30, 34));
		int stype = Integer.parseInt(t.getMessage().getText().substring(34, 42));
		IoBuffer ib = IoBuffer.allocate(xml.length() + 1 + DevHeader.DEV_HDR_LEN);
		DevHeader dev_hdr = new DevHeader();
		dev_hdr.setVer(0);
		dev_hdr.setMtype(mtype);
		dev_hdr.setStype(stype);
		dev_hdr.setSequence(DevHeader.getNextSendSequence());
		dev_hdr.setLength(xml.length() + 1);
		dev_hdr.store(ib);
		ib.put(xml.getBytes());
		ib.put((byte)0);
			
		UrsidsMessage join_rsp = UrsidsMessage.composeUrsidsMessage(1, 3, mac, t.getTaskid(), ib.array());
		try {
			t.setStatus(PendingTask.STATUS_SENDING);
			ursids.sendMessage(s.getSession(), join_rsp);
		} catch (Exception e) {
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
		
	}
	public void sendJoinReply(UrsidsSession s, String mac){
		//need to send join_rsp
		LOGGER.info("Sending join reply...");
		String devrsp = "<join_rsp><ITEM result=\"ok\" /></join_rsp>";
		IoBuffer ib = IoBuffer.allocate(devrsp.length() + 1 + DevHeader.DEV_HDR_LEN);
		DevHeader dev_hdr = new DevHeader();
		dev_hdr.setVer(0);
		dev_hdr.setMtype(0);
		dev_hdr.setStype(2);
		dev_hdr.setSequence(DevHeader.getNextSendSequence());
		dev_hdr.setLength(devrsp.length() + 1);
		dev_hdr.store(ib);
		ib.put(devrsp.getBytes());
		ib.put((byte)0);
			
		UrsidsMessage join_rsp = UrsidsMessage.composeUrsidsMessage(1, 3, mac, 0, ib.array());
		try {
			join_rsp.setMqMessage(false);
			ursids.sendMessage(s.getSession(), join_rsp);
		} catch (Exception e) {
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
	}
	
	public void onUrsidsForwardDevMsg(IoSession is, UrsidsMessage msg) throws UnsupportedEncodingException, CharacterCodingException{
		LOGGER.debug("forwarding msg to mq...");
		String id = (String)is.getAttribute(SESSION_KEY);
		UrsidsSession s = ursidsSession.get(id);
		if(s == null){
			LOGGER.error("no such ursids session:" + id);
			return;
		}
		StringBuffer sb = new StringBuffer();
		IoBuffer ib = IoBuffer.wrap(msg.getBody());
		ib.order(ByteOrder.BIG_ENDIAN);
		sb.append(JOrionConfig.MSG_DEV_MSG_FORWARD);
		ib.getUnsignedShort(); //reserved
		String mac = StringHelper.byteToHexString(ib, 6).toLowerCase();
		long taskid = ib.getUnsignedInt();
		if(taskid != 0){
			s.removeTask(mac, taskid);
		}
		LOGGER.debug("mac:" + mac + "  taskid:" + taskid);
		DevHeader header = new DevHeader(ib);
		if(header.getMtype() == 0 && header.getStype() == 1){
			sendJoinReply(s, mac);
		}
		sb.append(mac);
		sb.append(String.format("%1$010d", taskid));
		sb.append(String.format("%1$04d", header.getMtype()));
		sb.append(String.format("%1$08d", header.getStype()));
		sb.append(ib.getString(charsetDecoder));
		mq.publishBusiness(id, sb.toString());
		
		pushUrsidsMessage(s, mac);
	}
	
	
	public void onUrsidsJoin(IoSession is, UrsidsMessage msg) throws UnsupportedEncodingException {
		//ursids online
		String content = new String(msg.getBody(), "UTF-8");//"ISO-8859-1");
		Map m = (Map)(new JSONReader()).read(content);
		String id = (String)m.get("name") + "_" + (String)m.get("process_seq");
		is.setAttribute(SESSION_KEY, id);
		UrsidsSession s = ursidsSession.get(id);
		LOGGER.debug("Ursids joined:" + id);
		if(s == null){
			s = new UrsidsSession(is, id);
			ursidsSession.put(id, s);
		}
		String state = (String)m.get("state");
		Long last_frag = (Long)m.get("last_frag");
		s.setJoined_flag(last_frag.equals(1));
		if(state.equals("init") && s.getJoined_flag()){
			s.clearAllTasks();
		}
		
		if(mq.ursidsJoin(id)){
			StringBuffer sb = new StringBuffer();
			sb.append(JOrionConfig.MSG_URSIDS_ONLINE);
			sb.append(content);
			mq.publishManagementMessage(sb.toString());
		} else {
			LOGGER.error("Ursids join failed!");
			s.clearAllTasks();
			ursidsSession.remove(id);
			is.close(true);
			return;
		}
	}
	
	public void onUrsidsMessageSent(IoSession is, UrsidsMessage msg){
		if(!msg.isMqMessage()) //maybe it's join reply
			return;
		String id = (String)is.getAttribute(SESSION_KEY);
		if(id == null)
			return;
		UrsidsSession s = ursidsSession.get(id);
		if(s == null){
			return;
		}
		if(msg.getMac() == null || msg.getMac().isEmpty())
			return;
		//remove current message
		PendingTask p = s.getNextTask(msg.getMac());
		if(p == null)
			return;
		if(p.getTaskid() != 0)
			return;
		if(p.getStatus() != PendingTask.STATUS_SENDING){
			LOGGER.debug("the message in queue head has a wrong stauts,mac:" + msg.getMac());
			return;
		}
		s.removeTask(msg.getMac(), p.getTaskid());
		//push next message
		pushUrsidsMessage(s, msg.getMac());
	}
	
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
	
	public void onUrsidsSessionClose(IoSession session){
		String id = (String)session.getAttribute(SESSION_KEY);
		if(id == null)
			return;
		UrsidsSession s = ursidsSession.get(id);
		if(s == null)
			return;
		s.setSession(null);
		s.setJoined_flag(false);
		StringBuffer sb = new StringBuffer();
		JSONWriter writer = new JSONWriter();
		Map<String, String> m = new ConcurrentHashMap<String, String>();
		int pos = id.lastIndexOf("_");
		sb.append(JOrionConfig.MSG_URSIDS_OFFLINE);
		m.put("name", id.substring(0, pos));
		m.put("process_seq", id.substring(pos + 1));
		sb.append(writer.write(m));
		mq.publishManagementMessage(sb.toString());
	}

	public void pushUrsidsMessage(UrsidsSession s, String mac){
		PendingTask p = s.getNextTask(mac);
		if(p == null)
			return;
		LOGGER.debug("Msg in queue head, mac:" + mac + " taskid:"+ p.getTaskid());
		if(p.getStatus() != PendingTask.STATUS_PENDING){
			LOGGER.debug("previous message is waiting for response from dev");
			return;
		}
		try {
			sendXmlApply(s, p);
		} catch (JMSException e) {
			LOGGER.error(StringHelper.getStackTrace(e));
			e.printStackTrace();
		}
	}
	
	public void onMqMessage(String id, Message msg){
		TextMessage m = (TextMessage)msg;
		UrsidsSession s = ursidsSession.get(id);
		if(s == null){
			LOGGER.error("ursids session not found:" + id);
			return;
		}
		try {
			String text = m.getText();
			if(text.length() < 42){
				LOGGER.error("Wrong Message length, drop message");
				return;
			}
			String type = text.substring(0, 8);
			if(type.equals(JOrionConfig.MSG_DEV_XML_APPLY)){
				String mac = text.substring(8, 20).toLowerCase();
				long taskid = Long.parseLong(text.substring(20, 30));
				s.addTask(mac, taskid, m);
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
		mq.start();
		ursids.start();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		JOrion jorion = new JOrion();
		jorion.start();
		LOGGER.info("Server started!");
	}

}
