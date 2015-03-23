package com.bhu.jorion.message;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;

public class DevHeader {
	public final static int DEV_HDR_LEN = 8;
	public static int send_sequence = 1;
	private int ver;
	private int mtype;
	private int stype;
	private int sequence;
	private long length;
	
	public DevHeader(IoBuffer buf){
		prase(buf);
	}
	
	public DevHeader() {
		// TODO Auto-generated constructor stub
	}

	public static int getNextSendSequence(){
		DevHeader.send_sequence ++;
		if(DevHeader.send_sequence > 65535)
			DevHeader.send_sequence = 1;
		return DevHeader.send_sequence;
	}
	
	private void prase(IoBuffer buf){
		buf.order(ByteOrder.BIG_ENDIAN);
		byte t = buf.get();
		ver = t & 0xF0;
		mtype = t & 0x0F;
		stype = buf.getUnsigned();
		sequence = buf.getUnsignedShort();
		length = buf.getUnsignedInt();
	}
	
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getVer() {
		return ver;
	}
	public void setVer(int ver) {
		this.ver = ver;
	}
	public int getMtype() {
		return mtype;
	}
	public void setMtype(int mtype) {
		this.mtype = mtype;
	}
	public int getStype() {
		return stype;
	}
	public void setStype(int stype) {
		this.stype = stype;
	}
	public int getSequence() {
		return sequence;
	}
	public void setReserved(int sequence) {
		this.sequence = sequence;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}

	public void store(IoBuffer ib){
//		byte t = (byte)(ver & mtype);
		//TODO:ignore ver
		byte t = (byte)mtype;
		ib.order(ByteOrder.BIG_ENDIAN);
		ib.put(t);
		ib.putUnsigned(stype);
		ib.putUnsignedShort(sequence);
		ib.putUnsignedInt(length);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(" ver:" + ver);
		sb.append(" mtype:" + mtype);
		sb.append(" stype:" + stype);
		sb.append(" sequence:" + sequence);
		sb.append(" length:" + length);
		return sb.toString();
	}
}
