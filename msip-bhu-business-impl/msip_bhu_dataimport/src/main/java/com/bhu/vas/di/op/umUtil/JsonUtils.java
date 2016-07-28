package com.bhu.vas.di.op.umUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @title JsonUtils.java
 * @date 2013-5-3
 * @version V1.0
 */
public class JsonUtils {
	final static ObjectMapper objectMapper;
	final static JsonFactory factory = new JsonFactory();

	static boolean isPretty = true;

	static {
		objectMapper = new ObjectMapper(factory);
	}

	public static ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public static <T> T json2GenericObject(String jsonString, TypeReference<T> tr) {

		if (jsonString == null || "".equals(jsonString)) {
			return null;
		} else {
			try {
				return (T) objectMapper.readValue(jsonString, tr);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static String toJson(Object object) {
		String jsonString = null;
		try {
			jsonString = objectMapper.writeValueAsString(object);
		} catch (Exception e) {
		}
		return jsonString;
	}

	public static Object json2Object(String jsonString, Class<?> c) {

		if (jsonString == null || "".equals(jsonString)) {
			return null;
		} else {
			try {
				return objectMapper.readValue(jsonString, c);
			} catch (Exception e) {
			}

		}
		return null;
	}
	public static <T> T json2T(String jsonString, Class<?> c) {
		
		if (jsonString == null || "".equals(jsonString)) {
			return null;
		} else {
			try {
				return (T) objectMapper.readValue(jsonString, c);
			} catch (Exception e) {
			}
			
		}
		return null;
	}

	/**
	 * @param arrayList
	 * @return
	 */
	public static JSONArray toJSONArray(ArrayList arrayList) {
		JSONArray jsonArray = new JSONArray(arrayList.size());
		for (Object o : arrayList) {
			if (o instanceof Map) {
				JSONObject j = new JSONObject();
				Map mo = (Map) o;
				for (Object e : mo.keySet()) {
					j.put(e, mo.get(e));
				}
				jsonArray.add(j);
			}
		}
		return jsonArray;
	}

	/**
	 * @param souceInfo
	 * @return
	 */
	public static JSONObject fromObject(Object json) {
		try {
			if (json instanceof String) {
				return (JSONObject) objectMapper.readValue(json.toString(), JSONObject.class);
			} else if (json instanceof Map) {
				Map map = (Map) json;
				JSONObject jsonObject = new JSONObject();
				jsonObject.putAll(map);
				// for(Object k : map.keySet()){
				// jsonObject.put(k, map.get(k));
				// }
				return jsonObject;
			} else {
				return (JSONObject) objectMapper.readValue(toJson(json), JSONObject.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray fromObjectArray(String json) {
		try {
			return objectMapper.readValue(json, new TypeReference<JSONArray>() {
			});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		// Map<String, Object> userInMap = new HashMap<String, Object>();
		// userInMap.put("name", "mkyong");
		// userInMap.put("age", 29);
		// JSONObject json = JSONObject.fromObject(userInMap);
		// System.out.println(json);
		// userInMap.put("age", 30);
		// System.out.println(json);
		System.out.println(Long.valueOf("15"));
		// net.sf.json.JSONObject jsono = new net.sf.json.JSONObject();
		// System.out.println(jsono.getJSONArray("aa"));
		// JSONObject o = new JSONObject();
		// o.put("name", "johnbin");
		// o.put("age", 29);
		//
		// List<JSONObject> list = new ArrayList<JSONObject>();
		// list.add(o);
		// list.add(o);
		// list.add(o);
		// String listjson = net.sf.json.JSONArray.fromObject(list).toString();
		// userInMap.put("messages", list);
		//
		// String jString = JsonUtils.toJson(userInMap);
		// System.out.println(jString);
		//
		// JSONObject jsonObject1 = JsonUtils.fromObject(jString);
		// ArrayList<JSONObject> arrayList = (ArrayList<JSONObject>)
		// jsonObject1.get("messages");
		// JSONArray list2 = JsonUtils.toJSONArray(arrayList);
		// System.out.println(list2.toString());
		//
		// // String j = "{\"name\":\"ab\",\"age\":5}";
		// System.out.println(listjson);
		// long t1 = System.currentTimeMillis();
		// for (int i = 0; i < 1000000; i++) {
		// JSONArray jsonObject = JsonUtils.fromObjectArray(listjson);
		// }
		// long t2 = System.currentTimeMillis();
		// System.out.println(t2-t1);
		// t1 = System.currentTimeMillis();
		// for (int i = 0; i < 1000000; i++) {
		// net.sf.json.JSONArray jsonArray =
		// net.sf.json.JSONArray.fromObject(listjson);
		// }
		// t2 = System.currentTimeMillis();
		// System.out.println(t2-t1);
	}
}