package com.bhu.vas.web.console;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bhu.vas.api.helper.NumberValidateHelper;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.iservice.IChargingRpcService;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.api.rpc.charging.vto.SharedealDefaultVTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.bhu.vas.api.rpc.commdity.vto.QualityGoodsSharedealVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.cores.helper.ArithHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/charging")
public class ConsoleChargingController extends BaseController {

    @Resource
    private IChargingRpcService chargingRpcService;
	@Resource
	private IOrderRpcService orderRpcService;

    @ResponseBody()
    @RequestMapping(value = "/sharedeal/default", method = {RequestMethod.POST})
    public void sharedeal_default(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid
            ) {
    	RpcResponseDTO<SharedealDefaultVTO> rpcResult = chargingRpcService.doFetchDefaultSharedeal(uid);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    /**
     * 
     * @param request
     * @param response
     * @param uid
     * @param message
     * @param canbeturnoff
     * @param enterpriselevel
     * @param customized false 使用默认值 true 使用定制的值
     * @param owner_percent
     * @param range_cash_mobile
     * @param range_cash_pc
     * @param access_internet_time
     */
    @ResponseBody()
    @RequestMapping(value = "/sharedeal/batch/modify", method = {RequestMethod.POST})
    public void deviceBatch_sharedeal_modify(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String message,
            @RequestParam(required = false,value = "cbto") Boolean canbeturnoff,
            @RequestParam(required = false,value = "el") Boolean enterpriselevel,
            @RequestParam(required = false,defaultValue="false") boolean customized,
            @RequestParam(required = false,value = "percent") String owner_percent,
            @RequestParam(required = false,value = "percent_m") String manufacturer_percent,
            @RequestParam(required = false,value = "percent_d") String distributor_percent,
            @RequestParam(required = false,value = "percent_d2") String distributor_l2_percent,
            @RequestParam(required = false,value = "rcm") String range_cash_mobile,
            @RequestParam(required = false,value = "rcp") String range_cash_pc,
            @RequestParam(required = false,value = "ait") String access_internet_time,
            @RequestParam(required = false,value = "fait") String free_access_internet_time
//            @RequestParam(required = false,value = "chlv1") String channel_lv1,
//            @RequestParam(required = false,value = "chlv2") String channel_lv2
            ) {
    	try{
    		if(!customized){
        		owner_percent = StringHelper.MINUS_STRING_GAP;
        		manufacturer_percent = StringHelper.MINUS_STRING_GAP;
        		distributor_percent = StringHelper.MINUS_STRING_GAP;
        		distributor_l2_percent = StringHelper.MINUS_STRING_GAP;
        		range_cash_mobile = StringHelper.MINUS_STRING_GAP;
        		range_cash_pc = StringHelper.MINUS_STRING_GAP;
        		access_internet_time = StringHelper.MINUS_STRING_GAP;
        		free_access_internet_time = StringHelper.MINUS_STRING_GAP;
        	}else{
        		ValidateService.validAmountRange(range_cash_mobile,NumberValidateHelper.Range_Amount_Min,NumberValidateHelper.Range_Amount_Max);
        		ValidateService.validAmountRange(range_cash_pc,NumberValidateHelper.Range_Amount_Min,NumberValidateHelper.Range_Amount_Max);
        		ValidateService.validAitRange(access_internet_time,NumberValidateHelper.Range_Ait_Min,NumberValidateHelper.Range_Ait_Max);
        		if(StringUtils.isNotEmpty(free_access_internet_time))
            		ValidateService.validFreeAitRange(free_access_internet_time,NumberValidateHelper.Range_Ait_Min,NumberValidateHelper.Range_Ait_Max);
        			
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
        	}
        	
        	RpcResponseDTO<Boolean> rpcResult = chargingRpcService.doBatchSharedealModify(uid, message, 
        			canbeturnoff,enterpriselevel,
        			customized,
        			owner_percent,manufacturer_percent,distributor_percent,distributor_l2_percent,
        			range_cash_mobile,range_cash_pc,access_internet_time, StringUtils.isEmpty(free_access_internet_time)?access_internet_time:free_access_internet_time, /* channel_lv1, channel_lv2, */false);
    		if(!rpcResult.hasError()){
    			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
    		}else{
    			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    		}
    	}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
    	
    }  
    
    /**
     * 
     * @param request
     * @param response
     * @param file
     * @param uid
     * @param countrycode
     * @param mobileno_needbinded
     * @param sellor
     * @param partner
     * @param canbeturnoff
     * @param enterpriselevel
     * @param customized false 使用默认值 true 使用定制的值
     * @param owner_percent
     * @param range_cash_mobile
     * @param range_cash_pc
     * @param access_internet_time
     * @param remark
     */
    @ResponseBody()
    @RequestMapping(value="/shipment/upload",method={RequestMethod.POST})
    public void uploadShipmentDevice(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("file") CommonsMultipartFile file,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = false,value = "cc",defaultValue="86") int countrycode,
            @RequestParam(required = false,value = "mobileno") String mobileno_needbinded,
            @RequestParam(required = false,defaultValue = "-1") int distributor,
            @RequestParam(required = false) String sellor,
            @RequestParam(required = false) String partner,
            @RequestParam(required = false,value = "cbto",defaultValue="true") boolean canbeturnoff,
            @RequestParam(required = false,value = "el",defaultValue="false") boolean enterpriselevel,
            @RequestParam(required = false,defaultValue="false") boolean customized,
            @RequestParam(required = false,value = "percent") String owner_percent,
            @RequestParam(required = false,value = "percent_m") String manufacturer_percent,
            @RequestParam(required = false,value = "percent_d") String distributor_percent,
            @RequestParam(required = false,value = "rcm") String range_cash_mobile,
            @RequestParam(required = false,value = "rcp") String range_cash_pc,
            @RequestParam(required = false,value = "ait") String access_internet_time,
            @RequestParam(required = true,value = "chlv1") String channel_lv1,
            @RequestParam(required = true,value = "chlv2") String channel_lv2,
            @RequestParam(required = false) String remark
    ) {
    	if(file == null){
    		SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.UPLOAD_FILE_UNKNOW_ERROR));
            return ;	
    	}
    	String originName = file.getOriginalFilename();
        String ext = originName.substring(originName.lastIndexOf("."));
        System.out.println("ext===" + ext);
        if (!".xls".equals(ext) && !".xlsx".equals(ext)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.UPLOAD_FILE_FORMAT_INVALID));
            return ;
        }
        try{
        	if(!customized){
        		owner_percent = StringHelper.MINUS_STRING_GAP;
        		manufacturer_percent = StringHelper.MINUS_STRING_GAP;
        		distributor_percent = StringHelper.MINUS_STRING_GAP;
        		range_cash_mobile = StringHelper.MINUS_STRING_GAP;
        		range_cash_pc = StringHelper.MINUS_STRING_GAP;
        		access_internet_time = StringHelper.MINUS_STRING_GAP;
        	}else{
        		ValidateService.validAmountRange(range_cash_mobile,NumberValidateHelper.Range_Amount_Min,NumberValidateHelper.Range_Amount_Max);
        		ValidateService.validAmountRange(range_cash_pc,NumberValidateHelper.Range_Amount_Min,NumberValidateHelper.Range_Amount_Max);
        		ValidateService.validAitRange(access_internet_time,NumberValidateHelper.Range_Ait_Min,NumberValidateHelper.Range_Ait_Max);

        		
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
        	}
        	RpcResponseDTO<BatchImportVTO> rpcResult = chargingRpcService.doInputDeviceRecord(uid, countrycode, mobileno_needbinded,distributor, 
        			sellor,
        			partner,
        			canbeturnoff,
        			enterpriselevel,
        			customized,
        			owner_percent,manufacturer_percent,distributor_percent,
        			range_cash_mobile,range_cash_pc, access_internet_time,
        			channel_lv1, channel_lv2,
        			remark);
			if(!rpcResult.hasError()){
				System.out.println("path:"+rpcResult.getPayload().toAbsoluteFileInputPath());
				File targetFile = new File(rpcResult.getPayload().toAbsoluteFileInputPath());
				targetFile.getParentFile().mkdirs();
				file.transferTo(targetFile);
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
        //RpcResponseDTO<BatchImportVTO> rpcResult = chargingRpcService.doInputDeviceRecord(uid, countrycode, mobileno, percent, remark);
        /*String inputDirPath = BusinessRuntimeConfiguration.BatchImport_Manufacturer
        		.concat(BusinessRuntimeConfiguration.BatchImport_Shipment)
        		.concat(BusinessRuntimeConfiguration.BatchImport_Sub_Input_Dir);*/
    }
    
    @ResponseBody()
    @RequestMapping(value="/shipment/confirm",method={RequestMethod.POST})
    public void confirmShipment(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = true) String batchno,
            @RequestParam(required = true,defaultValue="false") boolean confirm
    ) {
    	try{
        	RpcResponseDTO<BatchImportVTO> rpcResult = null;//
        	if(confirm)
        		rpcResult = chargingRpcService.doConfirmDeviceRecord(uid, batchno);//(uid, countrycode, mobileno, percent, remark);
        	else{
        		rpcResult = chargingRpcService.doCancelDeviceRecord(uid, batchno);
        	}
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
    }
    
    
	@ResponseBody()
	@RequestMapping(value = "/shipment/pages", method = { RequestMethod.POST })
	public void pagesdv(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = true,defaultValue = "0") int status,
			@RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {
		
		ResponseError validateError = ValidateService.validatePageSize(pageSize);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		try{
			RpcResponseDTO<TailPage<BatchImportVTO>> rpcResult = chargingRpcService.doPages(uid, status, pageNo, pageSize);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	
	/**
	 * 分页列表
	 * @param request
	 * @param response
	 * @param uid
	 * @param status
	 * @param upact 手势上滑或下拉 true上滑 false下拉 
	 * @param lastrowid
	 * @param start
	 * @param size
	 */
	@ResponseBody()
	@RequestMapping(value = "/shipment/_pages", method = { RequestMethod.POST })
	public void _pagesdv(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = true,defaultValue = "0") int status,
			@RequestParam(required = false, defaultValue = "true") boolean upact,
			@RequestParam(required = false, defaultValue = "0", value = "lri") int lastrowid,
			@RequestParam(required = false, defaultValue = "0", value = "st") int start,
			@RequestParam(required = false, defaultValue = "10", value = "ps") int size) {
		
		ResponseError validateError = ValidateService.validatePageSize(size);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		try{
			RpcResponseDTO<TailPage<BatchImportVTO>> rpcResult = chargingRpcService.doStatRowPages(uid, status, upact, lastrowid, start, size);//(uid, status, pageNo, pageSize);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	
	
	/**
	 * 列出最近15天的必虎良品订单
	 * @param request
	 * @param response
	 * @param uid
	 * @param pageNo
	 * @param pageSize
	 */
	@ResponseBody()
	@RequestMapping(value = "/sharedeal_order/pages", method = { RequestMethod.POST })
	public void sharedealOrderPages(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {
		
		ResponseError validateError = ValidateService.validatePageSize(pageSize);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		try{
			RpcResponseDTO<QualityGoodsSharedealVTO> rpcResult = orderRpcService.qualityGoodsSharedealPages(uid, pageNo, pageSize);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	

	@ResponseBody()
	@RequestMapping(value = "/sharedeal_order/cancel", method = { RequestMethod.POST })
	public void sharedealOrderPages(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = true) String orderid,
			@RequestParam(required = true) String remark){		
        try{
    		if(StringUtils.isEmpty(orderid)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY,new String[]{"orderid"});
			}
    		if(StringUtils.isEmpty(remark)){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY,new String[]{"remark"});
			}
    		if(remark.length() > 255){
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR,new String[]{"remark"});
    		}
        	RpcResponseDTO<Boolean> rpcResult = orderRpcService.doOrderSharedealCancel(uid, orderid, remark);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}	}

}
