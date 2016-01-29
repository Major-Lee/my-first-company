package com.bhu.vas.di.op.userreg;

import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.bucache.redis.serviceimpl.BusinessKeyDefine;
import com.bhu.vas.business.bucache.redis.serviceimpl.unique.impl.mobleno.UniqueMobilenoHashService;


/**
 * 数据库中不存在的手机号数据
 * redis中通过keypattern方式获取所有mobileno及其uid，如果数据库中不存在此uid，则移除redis中的mobileno
 * @author Edmond
 *
 */
public class UserMobileRedisDataClearOp {
	public static void main(String[] argv){
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:/com/whisper/di/business/dataimport/dataImportCtx.xml");
		
		//UserService userService = (UserService)ctx.getBean("userService");

		Set<String> set = UniqueMobilenoHashService.getInstance().keySet();
		for(String key:set){
			//System.out.println(key);
			String mobileno = key.substring(BusinessKeyDefine.Unique.MobilenoCheck.length()+1);
			System.out.println(String.format("key[%s] mobileno [%s]",key, mobileno));
			if(mobileno.indexOf(' ') == -1){
				System.out.println(String.format("mobile[%s] maybe removed!", mobileno));
				System.out.println(UniqueMobilenoHashService.getInstance().remove(0,mobileno));
			}
			/*Map<String,String> map = UniqueMobilenoHashService.getInstance().fetchAll(0,mobileno);
			//System.out.println(map);
			String uidstr = map.get(BusinessFieldDefine.UidFiled);
			if(StringUtils.isNotEmpty(uidstr)){
				User user = userService.getById(Integer.parseInt(uidstr));
				if(user == null){
					//System.out.println(String.format("uid[%s] mobile[%s] maybe removed!", uidstr,mobileno));
					//System.out.println(UniqueMobilenoHashService.getInstance().remove(0,mobileno));
				}
			}*/
		}
	}
}
