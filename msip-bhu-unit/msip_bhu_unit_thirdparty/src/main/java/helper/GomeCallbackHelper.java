package helper;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhu.vas.api.rpc.thirdparty.dto.GomeReportDTO;
import com.bhu.vas.api.rpc.thirdparty.dto.GomeResponseDTO;
import com.bhu.vas.api.rpc.thirdparty.helper.GomeParam;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.HttpHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.helper.encrypt.CryptoHelper;
import com.smartwork.msip.cores.helper.encrypt.NonceHelper;

public class GomeCallbackHelper {
	private final static Logger logger = LoggerFactory.getLogger(GomeCallbackHelper.class);

	public static Boolean notify(String mac, int online){
    	Map<String, String> pm = new HashMap<String, String>();
    	String tm = DateTimeHelper.formatDate(DateTimeHelper.FormatPattern16);
    	String nonce = NonceHelper.randomString(10);
    	pm.put(GomeParam.GomeRequestParam_Appid, BusinessRuntimeConfiguration.BhuToGomeAppId);
    	pm.put(GomeParam.GomeRequestParam_Timestamp, tm);
    	pm.put(GomeParam.GomeRequestParam_Nonce, nonce);
    	
    	GomeReportDTO dto = new GomeReportDTO();
    	dto.setGid(BusinessRuntimeConfiguration.BhuToGomeReportGid);
    	try{
    		dto.setDeviceId(CryptoHelper.aesEncryptToHex(mac, BusinessRuntimeConfiguration.BhuToGomeDataKey));
    	}catch(Exception e){
    		e.printStackTrace();
    		return Boolean.FALSE;
    	}
    	dto.setTime(DateTimeHelper.formatDate(DateTimeHelper.FormatPattern1));
    	dto.setOnline(String.valueOf(online));
    	String body = JsonHelper.getJSONString(dto);
    	
		String mysign = GomeParam.getSign(null, tm, nonce, BusinessRuntimeConfiguration.GomeToBhuAppKey, body);
    	pm.put(GomeParam.GomeRequestParam_Sign, mysign);
    	
    	logger.debug(String.format("tm[%s], nonce[%s], key[%s], body[%s]", tm, nonce, BusinessRuntimeConfiguration.GomeToBhuAppKey, body));
    	
    	String result = HttpHelper.postWithBody(BusinessRuntimeConfiguration.GomeApiUrl, pm, null, body);
    	if(StringUtils.isEmpty(result)){
    		return null;
    	}
    	logger.debug(String.format("response[%s]", result));
   		GomeResponseDTO response = JsonHelper.getDTO(result, GomeResponseDTO.class);
   		if(response.getCode() != 0)
   			return Boolean.FALSE;
    	return Boolean.TRUE;
	}
}
