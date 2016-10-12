package com.bhu.vas.api.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

public class OpsHttpHelper {
	private static final Logger logger = LoggerFactory.getLogger(OpsHttpHelper.class);
	private static Map<String,String> Common_Headers = new HashMap<String,String>();
	static{
		Common_Headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		Common_Headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36");
				/*headers.put("X-Requested-With", "XMLHttpRequest");
				headers.put("Accept-Encoding", "gzip, deflate");
				headers.put("Accept-Language", "zh-CN,zh;q=0.8");
				headers.put("Connection", "keep-alive");*/
	}
	
	public static Map<String, String> generateOpsImportParamMap(String opsid, String failed_sns){
		Map<String, String> api_params = new HashMap<String, String>();
		api_params.put("batch_no", opsid);
		api_params.put("failed_device_sn", failed_sns);
		api_params.put("token", BusinessRuntimeConfiguration.OpsImportCallbackToken);
		return api_params;
	}
	
	public static String doPost(String urlApi,Map<String, String> api_params){
		try{
			logger.info(String.format("Request  url[%s] params[%s]",urlApi, api_params));
			String response = HttpHelper.postUrlAsString(urlApi, api_params,Common_Headers);
			logger.info(String.format("Response url[%s] params[%s] response[%s]", urlApi, api_params, response));
			return response;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return null;
		}
	}
	
	private static ExecutorService exec_remote_portalexchange = ExecObserverManager.buildExecutorService(OpsHttpHelper.class,"Ops系统回调通知",10);
	
	public static void opsImportCallBackNotify(final String opsid, final String failed_sns){
		exec_remote_portalexchange.submit((new Runnable() {
			@Override
			public void run() {
				try{
					Map<String, String> api_params = OpsHttpHelper.generateOpsImportParamMap(opsid, failed_sns);
					logger.info(String.format("Ops Callback Api request url[%s] params[%s]", BusinessRuntimeConfiguration.OpsImportCallbackApi, api_params));
					String response = OpsHttpHelper.doPost(BusinessRuntimeConfiguration.OpsImportCallbackApi, api_params);
					logger.info(String.format("response[%s]", response));
				}catch(Exception ex){
					ex.printStackTrace(System.out);
				}
			}
		}));
	}
	
}
