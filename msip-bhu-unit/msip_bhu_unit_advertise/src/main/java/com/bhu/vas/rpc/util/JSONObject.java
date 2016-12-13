package com.bhu.vas.rpc.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @title JSONObject.java
 * @date 2013-5-6
 * @version V1.0
 */
public class JSONObject implements Map {

	private Map properties = new HashMap();

	@SuppressWarnings("unchecked")
	public static JSONObject fromObject(Object json) {
		try {
			return json == null ? null : JsonUtils.fromObject(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String toString() {
		return JsonUtils.toJson(this.properties);
	}

	public JSONObject() {
		// this.properties = new HashMap();
	}

	public Object get(String key) {
		return this.properties.get(key);
	}

	public String getString(String key) {
		if (get(key) == null) {
			return null;
		}
		return this.properties.get(key).toString();
	}

	public JSONObject getJSONObject(String key) {
		Object o = get(key);
		if (o instanceof Map) {
			JSONObject json = new JSONObject();
			json.setProperties((Map) o);
			return json;
		}
		return fromObject(get(key));
	}

	public static String toJsonString(Object object) {
		return object == null ? null : JsonUtils.toJson(object);
	}

	public long getLong(String key) {
		return Long.valueOf(String.valueOf(this.properties.get(key)));
	}

	// public void put(String key,Object value){
	// this.properties.put(key, value);
	// }
	/**
	 * @param string
	 * @return
	 */
	public int getInt(String key) {
		return Integer.valueOf(String.valueOf(this.properties.get(key)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#size()
	 */
	@Override
	public int size() {

		return this.properties.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#isEmpty()
	 */
	@Override
	public boolean isEmpty() {

		return this.properties.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {

		return this.properties.containsKey(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {

		return this.properties.containsValue(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key) {

		return this.properties.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object put(Object key, Object value) {
		if (value == null) {
			this.properties.remove(key);
			return this.properties;
		}
		return this.properties.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public Object remove(Object key) {

		return this.properties.remove(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map m) {
		this.properties.putAll(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {

		this.properties.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#keySet()
	 */
	@Override
	public Set keySet() {

		return this.properties.keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection values() {

		return this.properties.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#entrySet()
	 */
	@Override
	public Set entrySet() {

		return this.properties.entrySet();
	}

	/**
	 * @param string
	 * @return
	 */
	public double getDouble(String string) {

		return Double.valueOf(String.valueOf(get(string)));
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean has(String key) {
		return this.containsKey(key);
	}

	/**
	 * @param string
	 * @return
	 */
	public JSONArray getJSONArray(String key) {
		Object o = this.get(key);
		// if (o instanceof Colle) {
		return JSONArray.fromObject(o);
		// return JsonUtils.fromObjectArray(o);
		// }else {
		// return null;
		// }
	}

	/**
	 * @param string
	 * @return
	 */
	public Boolean getBoolean(String key) {
		return Boolean.valueOf(String.valueOf(this.get(key)));
	}

	/**
	 * @param jsonObject
	 * @param clazz
	 * @return
	 */
	public static Object toBean(JSONObject jsonObject, Class clazz) {
		return JsonUtils.json2Object(jsonObject.toString(), clazz);
	}

	public JSONObject accumulateAll(JSONObject jsonObject) {
		if (jsonObject != null) {
			for (Object key : jsonObject.keySet()) {
				if (key == null) {
					continue;
				}
				Object value = jsonObject.get(key);
				if (value == null) {
					continue;
				}
				this.put(key, value);
			}
		}
		return this;
	}

	public Map getProperties() {
		return properties;
	}

	public void setProperties(Map properties) {
		this.properties = properties;
	}
}
