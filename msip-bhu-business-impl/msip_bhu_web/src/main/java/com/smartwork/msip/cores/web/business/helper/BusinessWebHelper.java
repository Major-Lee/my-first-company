package com.smartwork.msip.cores.web.business.helper;

import java.util.Date;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

//import com.bhu.vas.api.rpc.user.model.UserToken;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.web.util.CookieUtils;


public class BusinessWebHelper {

	private final static String LANGUAGE_KEY = "A-Lang"; 
	
	/*public static void setCustomizeHeader(HttpServletResponse response, UserToken uToken) {
		setCustomizeHeader(response,uToken);//,System.currentTimeMillis());
	}*/
	
	public static void setCustomizeHeader(HttpServletResponse response, String atoken,String rtoken){//,long currenttime) {
		response.setHeader(RuntimeConfiguration.Param_ATokenHeader, atoken);
		response.setHeader(RuntimeConfiguration.Param_RTokenHeader, rtoken);
		String[] times = currentTimeFormat();
		response.setHeader(RuntimeConfiguration.Param_STimeHeader, times[0]);
		response.setHeader(RuntimeConfiguration.Param_SFTimeHeader, times[1]);
		//response.setHeader(RuntimeConfiguration.JingSFTimeHeader, times[1]);
	}
	
	/*public static void setCustomizeHeader(HttpServletResponse response, UserToken uToken){//,long currenttime) {
		if(uToken != null){
			setCustomizeHeader(response,uToken.getAccess_token(),uToken.getRefresh_token());
		}
	}*/
	
	private static String[] currentTimeFormat(){
		String[] result = new String[2];
		try{
			Date now =  new Date();
			String dformat = DateTimeHelper.formatDate(now, DateTimeHelper.FormatPattern16);// DateTimeHelper.FormatPattern13
			result[0] = String.valueOf(now.getTime());
			result[1] = dformat;
		}catch(Exception ex){
			result[0] = String.valueOf(System.currentTimeMillis());
			result[1] = DateTimeHelper.formatDate(DateTimeHelper.FormatPattern16);
		}
		return result;
	}
	
	public static Locale getLocale(HttpServletRequest request){
		String key = request.getHeader(LANGUAGE_KEY);
		if(StringUtils.isEmpty(key)){
			Cookie ck = CookieUtils.getCookie(request, LANGUAGE_KEY);
			if(ck != null)
				key = ck.getValue();
		}
		
		if(StringUtils.isEmpty(key))
			return Locale.CHINA;
		
		try{
			String[] arr = key.split(StringHelper.MINUS_STRING_GAP);
			if(arr.length >= 2)
				return new Locale(arr[0], arr[1]);
			return new Locale(arr[0]);
		}catch(Exception e){
			return Locale.CHINA;
		}
	}

}
