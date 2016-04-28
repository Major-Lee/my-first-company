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
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/charging")
public class ConsoleChargingController extends BaseController {

    @Resource
    private IChargingRpcService chargingRpcService;
    
    @ResponseBody()
    @RequestMapping(value="/shipment/upload",method={RequestMethod.POST})
    public void uploadClaimAgentDevice(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("file") CommonsMultipartFile file,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = false,value="cc",defaultValue="86") int countrycode,
            @RequestParam(required = false) String mobileno,
            @RequestParam(required = false,defaultValue="true") boolean canbeturnoff,
            @RequestParam(required = false) double percent,
            @RequestParam(required = false) String remark
    ) {
    	String originName = file.getOriginalFilename();
        String ext = originName.substring(originName.lastIndexOf("."));
        System.out.println("ext===" + ext);
        if (!".xls".equals(ext) && !".xlsx".equals(ext)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.UPLOAD_FILE_FORMAT_INVALID));
            return ;
        }
        try{
        	RpcResponseDTO<BatchImportVTO> rpcResult = chargingRpcService.doInputDeviceRecord(uid, countrycode, mobileno, percent,canbeturnoff, remark);
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
            @RequestParam(required = true) String batchno
    ) {
    	try{
        	RpcResponseDTO<BatchImportVTO> rpcResult = chargingRpcService.doConfirmDeviceRecord(uid, batchno);//(uid, countrycode, mobileno, percent, remark);
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
