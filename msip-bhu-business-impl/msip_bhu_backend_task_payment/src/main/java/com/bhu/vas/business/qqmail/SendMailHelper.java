package com.bhu.vas.business.qqmail;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SendMailHelper {
  
	public static String doSendMail(String msg){
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
	        String defaultStr = "2567517693@qq.com,961337821@qq.com,"
	        		+ "zhangpy@bhuwifi.com,zhaojiang@bhuwifi.com,"
	        		+ "gaokai@bhunetworks.com,yetao@bhunetworks.com,wanglin@bhuwifi.com,"
	        		+ "luozheng@bhuwifi.com,zongbh@bhunetworks.com,yuyx@bhunetworks.com,yangbin@bhunetworks.com,yangtao@bhunetworks.com";
	        StringBuffer sb = new StringBuffer();
	        sb.append(defaultStr);
	        mail.setRecipients(sb);
	        mail.setSubject("【预警：】打赏微信支付系统实时数据报告");
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
