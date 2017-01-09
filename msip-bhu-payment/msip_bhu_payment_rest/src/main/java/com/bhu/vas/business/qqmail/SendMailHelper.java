package com.bhu.vas.business.qqmail;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SendMailHelper {
  
	public static String doSendMail(int level,String msg){
		String result = "fail";
		try{
			Map<String,String> map= new HashMap<String,String>();
	        SendMail mail = new SendMail("2567517693@qq.com","vqxxlrxqlepceaie");
	        map.put("mail.smtp.host", "smtp.qq.com");

	        map.put("mail.smtp.auth", "true");
	        map.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	        map.put("mail.smtp.port", "465");
	        map.put("mail.smtp.socketFactory.port", "465");
	        mail.setPros(map);
	        mail.initMessage();
	        StringBuffer defaultStr = new StringBuffer();
	         defaultStr.append("2567517693@qq.com,961337821@qq.com");
    		
	        switch (level) {
			case 1:
				defaultStr.append(",zhangpy@bhuwifi.com,");
				defaultStr.append("gaokai@bhunetworks.com,yetao@bhunetworks.com,wanglin@bhuwifi.com,");
				defaultStr.append("luozheng@bhuwifi.com,zongbh@bhunetworks.com,yuyx@bhunetworks.com,");
				defaultStr.append("yangbin@bhunetworks.com,yangtao@bhunetworks.com");
				break;
			case 2:
				defaultStr.append(",zhangpy@bhuwifi.com,yetao@bhunetworks.com");
				break;
			case 3:
				defaultStr.append(",zhangpy@bhuwifi.com");
				break;

			default:
				break;
			}
	        StringBuffer sb = new StringBuffer();
	        sb.append(defaultStr);
	        mail.setRecipients(sb);
	        mail.setSubject("【预警：】支付系统状态报告："+msg);
	        mail.setDate(new Date());
	        mail.setFrom("BHU_service");
	        mail.setContent(msg, "text/html; charset=UTF-8");
	        System.out.println(msg);
	        result= mail.sendMessage();
		}catch(Exception e){
			System.out.println("send mail catch exception"+e.getCause()+e.getMessage());
		}
		return result;
	}
}
