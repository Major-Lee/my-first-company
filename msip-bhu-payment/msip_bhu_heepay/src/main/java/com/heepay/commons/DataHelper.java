package com.heepay.commons;

import java.io.DataInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DataHelper {
	public final static String UTF8Encode="UTF-8";
	public final static String GBKEncode="GBK";
	
	public static String GetQueryString(Map<String, String> map)
	{
		Iterator<Entry<String, String>> iter = map.entrySet().iterator();
		StringBuilder sb = new StringBuilder();
		while (iter.hasNext()) {
			 Entry<String, String> entry = iter.next(); 
			 Object key = entry.getKey().toString();
			 Object val = entry.getValue().toString();
			 sb.append(key + "=" +val).append("&");
			}		
        if(sb.length()==0) return "";
		return sb.substring(0, sb.length()-1);
		
	}
	
	//对值进行转码 将本地编码的字符 转换为汇付宝的编码
	public  static void TranferCharsetEncode(Map<String, String> map) throws UnsupportedEncodingException
	{
		for (Entry<String, String> entry : map.entrySet()) {
		   if(entry.getValue()==null) continue;		
		   String utf8=URLEncoder.encode(entry.getValue(), DataHelper.UTF8Encode);
//		   String encodeValue1=new String(val1.getBytes("UTF-8"),"UTF-8");
		   entry.setValue(utf8);
		}
		     
	}
		
	
	public static String GetSortQueryToLowerString(Map<String, String> map)
	{
		List<Map.Entry<String, String>> keyValues =
			    new ArrayList<Map.Entry<String, String>>(map.entrySet());

		Collections.sort(keyValues, new Comparator<Map.Entry<String, String>>() {   
		    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
		        //return (o2.getValue() - o1.getValue()); 
		        return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		}); 
		
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<keyValues.size();i++) {	
			if(keyValues.get(i).getValue()==null)
			{
				sb.append(keyValues.get(i).getKey()+ "= " );
			}
			else
			{
				sb.append(keyValues.get(i).getKey()+ "=" + keyValues.get(i).getValue().toLowerCase());
			}
			sb.append("&");
		}
		
		return sb.substring(0, sb.length()-1);
		
	}
	
	public static String GetSortQueryString(Map<String, String> map)
	{
		List<Map.Entry<String, String>> keyValues =
			    new ArrayList<Map.Entry<String, String>>(map.entrySet());

		Collections.sort(keyValues, new Comparator<Map.Entry<String, String>>() {   
		    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
		        //return (o2.getValue() - o1.getValue()); 
		        return (o1.getKey()).toString().compareTo(o2.getKey());
		    }
		}); 
		
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<keyValues.size();i++) {			
			sb.append(keyValues.get(i).getKey()+ "=" + keyValues.get(i).getValue());
			sb.append("&");
		}
		
		return sb.substring(0, sb.length()-1);
		
	}
	
	
	public static String RequestGetUrl(String getUrl)
	{
		return GetPostUrl(null,getUrl,"GET");
	}
	
	public static String RequestPostUrl(String getUrl,String postData)
	{
		return GetPostUrl(postData,getUrl,"POST");
	}
	
	private static String  GetPostUrl(String postData,String postUrl,String submitMethod) {
		   URL url = null;
		   HttpURLConnection httpurlconnection = null;
		   try {
			    url = new URL(postUrl);
			    httpurlconnection = (HttpURLConnection) url.openConnection();
			    httpurlconnection.setRequestMethod(submitMethod.toUpperCase());
			    httpurlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			    httpurlconnection.setConnectTimeout(2000);  
			    httpurlconnection.setReadTimeout(3000);
			    httpurlconnection.setDoInput(true);
			    httpurlconnection.setDoOutput(true);    
			    if(submitMethod.equalsIgnoreCase("POST"))
			    {
				    httpurlconnection.getOutputStream().write(postData.getBytes(GBKEncode));
				    httpurlconnection.getOutputStream().flush();
				    httpurlconnection.getOutputStream().close();
			    }   
			    
			    int code = httpurlconnection.getResponseCode();
			    if (code == 200) 
			    {
			    	DataInputStream in = new DataInputStream(httpurlconnection.getInputStream());
			    	int len = in.available();
			    	byte[] by = new byte[len];
			    	in.readFully(by);
			    	String rev = new String(by,GBKEncode);	
			    	in.close();
			    	return rev;
			    }
			    else
			    {
			    	//http 请求返回非 200状态时处理
			    	
			    }
			    
		   }
		   catch (Exception e) 
		   {
			   e.printStackTrace();
		   } 
		   finally 
		   {
		    if (httpurlconnection != null) {
		     httpurlconnection.disconnect();
		    }
		   }
		   return null;
	}
}
