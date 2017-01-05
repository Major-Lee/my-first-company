package com.bhu.vas.business.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.user.model.UserToken;
import com.bhu.vas.msip.cores.web.util.CookieUtils;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;


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
	
	public static void setCustomizeHeader(HttpServletResponse response, UserToken uToken){//,long currenttime) {
		if(uToken != null){
			/*System.out.println(uToken.getAccess_token());
			response.setHeader(RuntimeConfiguration.Param_ATokenHeader, uToken.getAccess_token());
			response.setHeader(RuntimeConfiguration.Param_RTokenHeader, uToken.getRefresh_token());*/
			setCustomizeHeader(response,uToken.getAccess_token(),uToken.getRefresh_token());
		}
	}
	
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
	
	// 各种时间格式
	public static Calendar getCalendar() {
		return Calendar.getInstance();
	}
	public static String formatDate() {
		return DateTimeHelper.shortDateFormat.format(getCalendar().getTime());
	}
	
	public static boolean isOpenWithdrawDate(){
		boolean flag = true;
		//Date currentTime = getCalendar().getTime();
		Calendar currentCal = getCalendar();
		//currentCal.add(Calendar.MONTH, -1);
		currentCal.set(Calendar.DATE,5);
		Date currentTime = currentCal.getTime();
		Calendar preCld = getCalendar();
		//preCld.add(Calendar.MONTH, 1);
		preCld.set(Calendar.DATE,6);
		preCld.set(Calendar.HOUR_OF_DAY, 0);
		preCld.set(Calendar.MINUTE, 0); 
		preCld.set(Calendar.SECOND,0);
		preCld.set(Calendar.MILLISECOND, 0); 
		Calendar sufCld = getCalendar();
		//sufCld.add(Calendar.MONTH, 1);
		sufCld.set(Calendar.DATE,26);
		sufCld.set(Calendar.HOUR_OF_DAY, 0);
		sufCld.set(Calendar.MINUTE, 0); 
		sufCld.set(Calendar.SECOND,0);
		sufCld.set(Calendar.MILLISECOND, 0);
//		System.out.println("currentTime:"+ DateTimeHelper.shortDateFormat.format(currentTime));
//		System.out.println("preCld:"+DateTimeHelper.shortDateFormat.format(preCld.getTime()));
//		System.out.println("sufCld:"+DateTimeHelper.shortDateFormat.format(sufCld.getTime()));
		if(preCld.getTime().getTime() < currentTime.getTime() && currentTime.getTime() < sufCld.getTime().getTime() ){
			flag = false;
		}
		return flag;
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
