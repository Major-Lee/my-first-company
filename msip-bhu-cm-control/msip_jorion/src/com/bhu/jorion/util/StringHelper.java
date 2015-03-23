package com.bhu.jorion.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(StringHelper.class);

	public static String byteToHexString(IoBuffer ib, int len){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < len; i ++){
			sb.append(String.format("%02x", ib.get()));
		}
		return sb.toString();
	}
	
	public static void hexStringToByte(String s, IoBuffer ib){
		byte b = 0;
		int off = 0;
		for(int i = 0; i < (s.length() + 1)/2; i ++){
			b = 0;
			off = i * 2 + 2;
			if(off > s.length())
				off = s.length();
			try{
				b = (byte)Integer.parseInt(s.substring(i * 2, off), 16);
			}catch(NumberFormatException e){
				LOGGER.error(StringHelper.getStackTrace(e));
				e.printStackTrace();
			}
			ib.put(b);
		}
	}

	public static String byteToHexString(byte[] b){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < b.length; i ++){
			sb.append(String.format("%02x", b[i]));
		}
		return sb.toString();
	}
	
	public static byte[] hexStringToByte(String s){
		byte[] b = new byte[(s.length() + 1) / 2];
		for(int i = 0; i < (s.length() + 1)/2; i ++){
			b[i] = 0;
			try{
				b[i] = (byte)Integer.parseInt(s.substring(i * 2, (i +1)*2), 16);
			}catch(NumberFormatException e){
				LOGGER.error(StringHelper.getStackTrace(e));
				e.printStackTrace();
			}
		}
		return b;
	}
	
	
	public static String getStackTrace(Throwable t)  
	{  
	    StringWriter sw = new StringWriter();  
	    PrintWriter pw = new PrintWriter(sw);  
	  
	    try  
	    {  
	        t.printStackTrace(pw);  
	        return sw.toString();  
	    }  
	    finally  
	    {  
	        pw.close();  
	    }  
	}  

}
