package com.bhu.vas.web.console;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.iservice.IChargingRpcService;
import com.bhu.vas.api.rpc.charging.vto.BatchImportVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/charging")
public class ConsoleChargingController extends BaseController {

    @Resource
    private IChargingRpcService chargingRpcService;
    
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
    public void deviceBatch_Bind_Tag(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String message,
            @RequestParam(required = false,value = "cbto") Boolean canbeturnoff,
            @RequestParam(required = false,value = "el") Boolean enterpriselevel,
            @RequestParam(required = false,defaultValue="false") boolean customized,
            @RequestParam(required = false,value = "percent") String owner_percent,
            @RequestParam(required = false,value = "rcm") String range_cash_mobile,
            @RequestParam(required = false,value = "rcp") String range_cash_pc,
            @RequestParam(required = false,value = "ait") String access_internet_time
            ) {
    	if(!customized){
    		owner_percent = StringHelper.MINUS_STRING_GAP;
    		range_cash_mobile = StringHelper.MINUS_STRING_GAP;
    		range_cash_pc = StringHelper.MINUS_STRING_GAP;
    		access_internet_time = StringHelper.MINUS_STRING_GAP;
    	}
    	
    	RpcResponseDTO<Boolean> rpcResult = chargingRpcService.doBatchSharedealModify(uid, message, 
    			canbeturnoff,enterpriselevel,
    			customized,
    			owner_percent,
    			range_cash_mobile,range_cash_pc,access_internet_time);
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
    public void uploadClaimAgentDevice(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("file") CommonsMultipartFile file,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = false,value = "cc",defaultValue="86") int countrycode,
            @RequestParam(required = false,value = "mobileno") String mobileno_needbinded,
            @RequestParam(required = false) String sellor,
            @RequestParam(required = false) String partner,
            @RequestParam(required = false,value = "cbto",defaultValue="true") boolean canbeturnoff,
            @RequestParam(required = false,value = "el",defaultValue="false") boolean enterpriselevel,
            @RequestParam(required = false,defaultValue="false") boolean customized,
            @RequestParam(required = false,value = "percent") String owner_percent,
            @RequestParam(required = false,value = "rcm") String range_cash_mobile,
            @RequestParam(required = false,value = "rcp") String range_cash_pc,
            @RequestParam(required = false,value = "ait") String access_internet_time,
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
        		range_cash_mobile = StringHelper.MINUS_STRING_GAP;
        		range_cash_pc = StringHelper.MINUS_STRING_GAP;
        		access_internet_time = StringHelper.MINUS_STRING_GAP;
        	}
        	RpcResponseDTO<BatchImportVTO> rpcResult = chargingRpcService.doInputDeviceRecord(uid, countrycode, mobileno_needbinded, 
        			sellor,
        			partner,
        			canbeturnoff,
        			enterpriselevel,
        			customized,
        			owner_percent,
        			range_cash_mobile,range_cash_pc, access_internet_time,
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
    public void uploadClaimAgentDevice(
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
}
