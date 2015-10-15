package com.bhu.vas.di.op.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.bhu.vas.business.ds.device.facade.DeviceFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;

public class I18nTest {
	public static void main(String[] argv){
		ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath*:com/bhu/vas/di/business/dataimport/dataImportCtx.xml");
		WifiDeviceService wifiDeviceService = (WifiDeviceService)ctx.getBean("wifiDeviceService");
		DeviceFacadeService deviceFacadeService = (DeviceFacadeService)ctx.getBean("deviceFacadeService");
		
		
		
		
		String jsonString = JsonHelper.getJSONString(
				ResponseError.embed(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL, new String[]{"a","b"}));
		System.out.println(jsonString);
		
		throw new BusinessI18nCodeException(ResponseErrorCode.TASK_PARAMS_VALIDATE_ILLEGAL);
		//throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_VERSION_TOO_HIGH);
		
		//System.out.println(String.format("执行结果：total[%s] create[%s] update[%s]", total,create,update));
	}
}
