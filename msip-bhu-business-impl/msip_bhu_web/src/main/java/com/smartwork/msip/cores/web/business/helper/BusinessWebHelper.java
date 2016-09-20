package com.smartwork.msip.cores.web.business.helper;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

//import com.bhu.vas.api.rpc.user.model.UserToken;
import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;


public class BusinessWebHelper {
	
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
}
