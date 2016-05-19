package com.bhu.statistics.util.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bhu.statistics.util.JSONObject;
import com.bhu.statistics.util.Logger;


public class ByteStringUtil {
	
	public static Logger log = new Logger(ByteStringUtil.class);
	
	public static byte[] toBytes(Object value) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bo);
			oos.writeObject(value);
			oos.close();
			byte[] ubyte = bo.toByteArray();
			return ubyte;
		} catch (IOException e) {
			log.error("toBytes:",e);
		} finally {

		}
		return null;
	}
	
	public static String toString(Object value) {
		byte[] bytes = toBytes(value);
		if (bytes==null) return null;
		
		try {
			return new String(bytes,"iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			log.error("toString:",e);
		}
		return null;
	}

	public static Object toObject(byte[] value) {
		try {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(value));
		Object ret = ois.readObject();
		ois.close();
		return ret;
		} catch (UnsupportedEncodingException e) {
			log.error("toObject:",e);
		} catch (IOException e) {
			log.error("toObject:",e);
		} catch (ClassNotFoundException e) {
			log.error("toObject:",e);
		} finally {

		}
		return null;
	}
	
	public static Object toObject(String value) {
		if (value==null) return null;
		try {
			byte[] bytes = value.getBytes("iso-8859-1");
			return toObject(bytes);
		} catch (UnsupportedEncodingException e) {
			log.error("toObject:",e);
		}
		return null;
	}
	
	public static void main(String[] args) {
		byte[] bytes = toBytes(new Date());
		System.out.println(bytes);
		System.out.println(toObject(bytes));
		
		String str = toString("passport");
		System.out.println(str);
		System.out.println(toObject(str));
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "333");
		map.put("2", "2020");
		str = toString(map);
		System.out.println(str);
		System.out.println(toObject(str));
		
		JSONObject json = new JSONObject();
		json.put("1", "333");
		json.put("2", "2222");
		System.out.println(json);
	}
}
