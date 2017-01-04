package com.bhu.vas.web.operator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.DistributorType;
import com.bhu.vas.api.helper.NumberValidateHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.iservice.IChargingRpcService;
import com.bhu.vas.api.rpc.charging.vto.OpsBatchImportVTO;
import com.bhu.vas.api.vto.device.DeviceSharedealVTO;
import com.bhu.vas.business.helper.BusinessWebHelper;
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

	
	private ResponseError validateSecretKey(String secretKey, HttpServletRequest request){
		if(!DefaultSecretkey.equals(secretKey)){
			return ResponseError.embed(ResponseErrorCode.AUTH_TOKEN_INVALID, BusinessWebHelper.getLocale(request));
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
            @RequestParam(required = true,defaultValue = "-1") int distributor_l2,
            @RequestParam(required = true) String distributor_type,
            @RequestParam(required = false) String sellor,
            @RequestParam(required = false) String partner,
            @RequestParam(required = true,value = "cbto",defaultValue="true") boolean canbeturnoff,
            @RequestParam(required = false,value = "noapp",defaultValue="false") boolean noapp,
            @RequestParam(required = true,value = "percent") String owner_percent,
            @RequestParam(required = true,value = "percent_m") String manufacturer_percent,
            @RequestParam(required = true,value = "percent_d") String distributor_percent,
            @RequestParam(required = true,value = "percent_d2") String distributor_l2_percent,
            @RequestParam(required = true,value = "sns") String sns,
            @RequestParam(required = true,value = "opsid") String opsid,
            @RequestParam(required = true,value = "chlv1") String channel_lv1,
            @RequestParam(required = true,value = "chlv2") String channel_lv2,
            @RequestParam(required = false) String remark
    ) {
		ResponseError validateError = validateSecretKey(secretKey, request);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}

        try{
        	if(StringUtils.isEmpty(sns) || StringUtils.isEmpty(opsid)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY);
        	}
        	
        	if(!DistributorType.isValidType(distributor_type))
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, new String[]{"distributor_type"});
        		
    		if(StringUtils.isEmpty(owner_percent) || !NumberValidateHelper.isValidNumberCharacter(owner_percent)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{owner_percent});
			}
    		if(StringUtils.isEmpty(manufacturer_percent) || !NumberValidateHelper.isValidNumberCharacter(manufacturer_percent)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{manufacturer_percent});
			}
    		if(StringUtils.isEmpty(distributor_percent) || !NumberValidateHelper.isValidNumberCharacter(distributor_percent)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{distributor_percent});
			}
    		if(StringUtils.isEmpty(distributor_l2_percent) || !NumberValidateHelper.isValidNumberCharacter(distributor_l2_percent)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{distributor_l2_percent});
			}
    		
    		double sum = ArithHelper.add(Double.valueOf(owner_percent), Double.valueOf(manufacturer_percent),Double.valueOf(distributor_percent), Double.valueOf(distributor_l2_percent));
    		if(sum < 1 || sum >1){
    			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{String.valueOf(sum)});
    		}

        	RpcResponseDTO<OpsBatchImportVTO> rpcResult = chargingRpcService.doOpsInputDeviceRecord(uid, opsid, countrycode, mobileno_needbinded,distributor, distributor_l2, distributor_type,
        			sellor,
        			partner,
        			canbeturnoff, noapp,
        			owner_percent,manufacturer_percent,distributor_percent,distributor_l2_percent,
        			channel_lv1, channel_lv2,
        			sns,
        			remark);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				//FileHelper.copyFileTo(file, rpcResult.getPayload().toAbsoluteFilePath());
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }
    
    

    @ResponseBody()
    @RequestMapping(value="/sharedeal/modify",method={RequestMethod.POST})
    public void modifySharedeal(
            HttpServletRequest request,
            HttpServletResponse response,
			@RequestParam(required = true,value="sk") String secretKey,
            @RequestParam(required = true) Integer uid,
//            @RequestParam(required = true,value = "cbto",defaultValue="true") boolean canbeturnoff,
            @RequestParam(required = true,value = "percent") String owner_percent,
            @RequestParam(required = true,value = "percent_m") String manufacturer_percent,
            @RequestParam(required = true,value = "percent_d") String distributor_percent,
            @RequestParam(required = true,value = "percent_d2") String distributor_l2_percent,
            @RequestParam(required = true,value = "macs") String macs
    ) {
		ResponseError validateError = validateSecretKey(secretKey, request);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}

        try{
    		if(StringUtils.isEmpty(owner_percent) || !NumberValidateHelper.isValidNumberCharacter(owner_percent)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{owner_percent});
			}
    		if(StringUtils.isEmpty(manufacturer_percent) || !NumberValidateHelper.isValidNumberCharacter(manufacturer_percent)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{manufacturer_percent});
			}
    		if(StringUtils.isEmpty(distributor_percent) || !NumberValidateHelper.isValidNumberCharacter(distributor_percent)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{distributor_percent});
			}
    		if(StringUtils.isEmpty(distributor_l2_percent) || !NumberValidateHelper.isValidNumberCharacter(distributor_l2_percent)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{distributor_l2_percent});
			}
    		
    		double sum = ArithHelper.add(Double.valueOf(owner_percent), Double.valueOf(manufacturer_percent),Double.valueOf(distributor_percent), Double.valueOf(distributor_l2_percent));
    		if(sum < 1 || sum >1){
    			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_FLOAT_DECIMAL_PART_ERROR,new String[]{String.valueOf(sum)});
    		}
    		
           	RpcResponseDTO<Boolean> rpcResult = chargingRpcService.doBatchSharedealModify(uid, macs,
        			owner_percent,manufacturer_percent,distributor_percent,distributor_l2_percent);
    		if(!rpcResult.hasError()){
    			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
    		}else{
    			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
    		}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/sharedeal/detail", method = {RequestMethod.POST})
    public void detail(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) String mac
    		) {
    	try{
			RpcResponseDTO<DeviceSharedealVTO> rpcResult = chargingRpcService.sharedealDetail(mac.toLowerCase());
			if(!rpcResult.hasError())
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			else
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
	    }catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }

}
