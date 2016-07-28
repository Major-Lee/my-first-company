/**
 *@title JSONArray.java 
 *@date 2013-5-7 
 *@version V1.0
*/
package com.bhu.vas.rpc.util;

import java.util.ArrayList;
import java.util.Map;

public class JSONArray extends ArrayList<Object>{

	private static final long serialVersionUID = 1L;

	public JSONArray(){
    super();
  }
  /**
   * @param size
   */
  public JSONArray(int size) {
    super(size);
  }

//  /**
//   * @param listjson
//   * @return
//   */
//  public static JSONArray fromObject(String listjson) {
//    return JsonUtils.fromObjectArray(listjson);
//  }

  /**
   * @param object
   * @return
   */
  public static JSONArray fromObject(Object object) {
	JSONArray arr = null;
	if(object!=null){
		if(object instanceof Iterable<?>){
			arr = new JSONArray();
			Iterable<?> list = (Iterable<?>)object;
			for(Object o : list){
				if(o instanceof Map<?, ?>){
					arr.add(JSONObject.fromObject(o));
				}
				else{
					arr.add(o);
				}
			}
		}
		else {
			arr = JsonUtils.fromObjectArray((String)object);
		}
	}
	return arr;
//    return JsonUtils.fromObjectArray(object);
  }
  public String toString(){
    return JsonUtils.toJson(this);
  }

  /**
   * @param i
   * @return
   */
  public JSONObject getJSONObject(int i) {
    return JSONObject.fromObject(super.get(i));
  }
  
  /**
   * @param i
   * @return
   */
  public String getString(int i) {
	  if(super.get(i) != null){
		  return String.valueOf(super.get(i));
	  }
	  return null;
  }
  
  /**
    * @param i
    * @return
    */
  public long getLong(int i) {
	  return Long.valueOf(String.valueOf(super.get(i)));
  }
  
  /**
    * @param i
    * @return
    */
  public int getInt(int i) {
	  return Integer.valueOf(String.valueOf(super.get(i)));
  }
  
  /**
    * @param i
    * @return
    */
  public double getDouble(int i) {
	  return Double.valueOf(String.valueOf(super.get(i)));
  }
}
