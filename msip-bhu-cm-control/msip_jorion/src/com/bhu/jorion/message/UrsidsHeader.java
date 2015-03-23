package com.bhu.jorion.message;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.jorion.ursids.UrsidsDecoder;

public class UrsidsHeader {
    private final static Logger LOGGER = LoggerFactory.getLogger(UrsidsHeader.class);
	public final static int URSIDS_HDR_LEN = 8;
	private int ver;
	private int mtype;
	private int stype;
	private byte[] reserved;
	private long length;
	
	public UrsidsHeader(){
		reserved = new byte[2];
		reserved[0] = 0;
		reserved[1] = 0;
	}
	
	public UrsidsHeader(IoBuffer buf){
		reserved = new byte[2];
		prase(buf);
	}
	
	private void prase(IoBuffer buf){
        buf.order(ByteOrder.BIG_ENDIAN);
		byte t = buf.get();
		ver = t & 0xF0;
		mtype = t & 0x0F;
		stype = buf.getUnsigned();
		reserved[0] = buf.get();
		reserved[1] = buf.get();
		length = buf.getUnsignedInt();
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
	public byte[] getReserved() {
		return reserved;
	}
	public void setReserved(byte[] reserved) {
		this.reserved = reserved;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	
	public void store(IoBuffer ib){
		ib.order(ByteOrder.BIG_ENDIAN);
//		byte t = (byte)(ver & mtype);
		//TODO:ignore ver
		byte t = (byte)mtype;
		ib.put(t);
		ib.putUnsigned(stype);
		ib.put((byte)0);
		ib.put((byte)0);
		ib.putUnsignedInt(length);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(" ver:" + ver);
		sb.append(" mtype:" + mtype);
		sb.append(" stype:" + stype);
		sb.append(" length:" + length);
		return sb.toString();
	}
}
