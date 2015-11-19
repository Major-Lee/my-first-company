package com.bhu.jorion.message;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.UUID;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.jorion.util.StringHelper;



public class UrsidsMessage {
    private final static Logger LOGGER = LoggerFactory.getLogger(UrsidsMessage.class);
	private UrsidsHeader header;
	private byte[] body;
	private String mac;
	private boolean isMqMessage;
	
	public UrsidsMessage(UrsidsHeader hdr, byte[] bd, String mac){
		header = hdr;
		body = bd;
		this.mac = mac;
		this.isMqMessage = false;
	}
	
	public UrsidsMessage(UrsidsHeader hdr, byte[] bd){
		header = hdr;
		body = bd;
		this.mac = null;
		this.isMqMessage = true;
	}

	public static UrsidsMessage composeUrsidsMessage(int mtype, int stype, byte[] content){
		UrsidsHeader hdr = new UrsidsHeader();	
		hdr.setVer(0);
		hdr.setMtype(mtype);
		hdr.setStype(stype);
		hdr.setLength(content.length);
		
		return new UrsidsMessage(hdr, content);
	}
	
	public static UrsidsMessage composeUrsidsMessage(int mtype, int stype, String mac, int rev, long taskid, byte[] content){
		UrsidsHeader hdr = new UrsidsHeader();
		byte[] body = new byte[content.length + 2 + 6 + 4];//2reserved + 6 mac + 4 taskid
		hdr.setVer(0);
		hdr.setMtype(mtype);
		hdr.setStype(stype);
		hdr.setLength(body.length);
		
		IoBuffer ib = IoBuffer.wrap(body);
		ib.order(ByteOrder.BIG_ENDIAN);
		//reserved,now used to extend taskid from mq.
		ib.putUnsignedShort(rev);
		//mac
		StringHelper.hexStringToByte(mac, ib);
		ib.putUnsignedInt(taskid);
		ib.put(content);
		return new UrsidsMessage(hdr, body, mac);
	}
	
	public boolean isMqMessage() {
		return isMqMessage;
	}

	public void setMqMessage(boolean isMqMessage) {
		this.isMqMessage = isMqMessage;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public UrsidsHeader getHeader() {
		return header;
	}
	public void setHeader(UrsidsHeader header) {
		this.header = header;
	}
	public byte[] getBody() {
		return body;
	}
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public int getTotalLength(){
		return (int)header.getLength() + UrsidsHeader.URSIDS_HDR_LEN;
	}
	
	public void store(IoBuffer ib){
		header.store(ib);
		ib.put(body);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		if(header != null){
			sb.append(header.toString());
		}
		if(body != null && body.length > 0){
			try {
				sb.append("    ");
				sb.append(new String(body, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				LOGGER.error(StringHelper.getStackTrace(e));
			}
		}
		return sb.toString();
	}
}
