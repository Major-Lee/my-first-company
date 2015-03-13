package com.whisper.business.helper;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.smartwork.msip.business.runtimeconf.RuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.whisper.api.user.model.UserToken;

public class BusinessWebHelper {
	
	/*public static void setCustomizeHeader(HttpServletResponse response, UserToken uToken) {
		setCustomizeHeader(response,uToken);//,System.currentTimeMillis());
	}*/
	
	
	public static void setCustomizeHeader(HttpServletResponse response, UserToken uToken){//,long currenttime) {
		if(uToken != null){
			System.out.println(uToken.getAccess_token());
			response.setHeader(RuntimeConfiguration.Param_ATokenHeader, uToken.getAccess_token());
			response.setHeader(RuntimeConfiguration.Param_RTokenHeader, uToken.getRefresh_token());
		}
		String[] times = currentTimeFormat();
		response.setHeader(RuntimeConfiguration.Param_STimeHeader, times[0]);
		response.setHeader(RuntimeConfiguration.Param_SFTimeHeader, times[1]);
		//response.setHeader(RuntimeConfiguration.JingSFTimeHeader, times[1]);
	}
	private static String[] currentTimeFormat(){
		String[] result = new String[2];
		try{
			Date now =  new Date();
			String dformat = DateTimeHelper.formatDate(now, DateTimeHelper.FormatPattern13);// DateTimeHelper.FormatPattern13
			result[0] = String.valueOf(now.getTime());
			result[1] = dformat;
		}catch(Exception ex){
			result[0] = String.valueOf(System.currentTimeMillis());
			result[1] = DateTimeHelper.formatDate(DateTimeHelper.FormatPattern13);
		}
		return result;
	}
}
