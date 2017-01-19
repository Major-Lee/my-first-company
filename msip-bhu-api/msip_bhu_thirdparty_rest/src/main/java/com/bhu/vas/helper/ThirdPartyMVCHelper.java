package com.bhu.vas.helper;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class ThirdPartyMVCHelper {
   
	public static String getRequestBody(HttpServletRequest request){
		StringBuffer sb = new StringBuffer();
		String inputLine = null;
		try{
			BufferedReader br = request.getReader();
			if(br == null)
				return null;
		     while ((inputLine = br.readLine()) != null)
		         sb.append(inputLine);
			br.close();
		}catch(IOException e){
		     System.out.println("IOException: " + e);
		     return null;
		}finally{
		}
		return sb.toString();
	}

}
