package com.bhu.vas.web.operator;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.helper.NumberValidateHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.iservice.IChargingRpcService;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/ops/import")
public class OperatorImportController extends BaseController{

	private static final String DefaultSecretkey = "P45zdf2TFJSU6EBHG90dc21FcLew==";

	
    @Resource
    private IChargingRpcService chargingRpcService;

	
	private ResponseError validateSecretKey(String secretKey){
		if(!DefaultSecretkey.equals(secretKey)){
			return ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID);
		}
		return null;
	}

	
    @ResponseBody()
    @RequestMapping(value="/shipment",method={RequestMethod.POST})
    public void uploadShipmentDevice(
            HttpServletRequest request,
            HttpServletResponse response,
			@RequestParam(required = true,value="sk") String secretKey,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = false,value = "cc",defaultValue="86") int countrycode,
            @RequestParam(required = false,value = "mobileno") String mobileno_needbinded,
            @RequestParam(required = true,defaultValue = "-1") int distributor,
            @RequestParam(required = false) String sellor,
            @RequestParam(required = false) String partner,
            @RequestParam(required = true,value = "cbto",defaultValue="true") boolean canbeturnoff,
            @RequestParam(required = true,value = "percent") String owner_percent,
            @RequestParam(required = true,value = "percent_m") String manufacturer_percent,
            @RequestParam(required = true,value = "percent_d") String distributor_percent,
            @RequestParam(required = true,value = "sns") String sns,
            @RequestParam(required = true,value = "opsid") String opsid,
            @RequestParam(required = true,value = "chlv1") String channel_lv1,
            @RequestParam(required = true,value = "chlv2") String channel_lv2,
            @RequestParam(required = false) String remark
    ) {
		ResponseError validateError = validateSecretKey(secretKey);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}

        try{
        	if(StringUtils.isEmpty(sns) || StringUtils.isEmpty(opsid)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY);
        	}
    		if(StringUtils.isEmpty(owner_percent) || !NumberValidateHelper.isValidNumberCharacter(owner_percent)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{owner_percent});
			}
    		if(StringUtils.isEmpty(manufacturer_percent) || !NumberValidateHelper.isValidNumberCharacter(manufacturer_percent)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{manufacturer_percent});
			}
    		if(StringUtils.isEmpty(distributor_percent) || !NumberValidateHelper.isValidNumberCharacter(distributor_percent)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{distributor_percent});
			}
    		
    		double sum = ArithHelper.add(Double.valueOf(owner_percent), Double.valueOf(manufacturer_percent),Double.valueOf(distributor_percent));
    		if(sum < 1 || sum >1){
    			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{String.valueOf(sum)});
    		}

        	RpcResponseDTO<BatchImportVTO> rpcResult = chargingRpcService.doOpsInputDeviceRecord(uid, opsid, countrycode, mobileno_needbinded,distributor, 
        			sellor,
        			partner,
        			canbeturnoff,
        			owner_percent,manufacturer_percent,distributor_percent,
        			channel_lv1, channel_lv2,
        			remark);
			if(!rpcResult.hasError()){
				System.out.println("path:"+rpcResult.getPayload().toAbsoluteFileInputPath());
				File targetFile = new File(rpcResult.getPayload().toAbsoluteFileInputPath());
				targetFile.getParentFile().mkdirs();
				FileUtils.writeStringToFile(targetFile, sns);
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				//FileHelper.copyFileTo(file, rpcResult.getPayload().toAbsoluteFilePath());
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
    }
}