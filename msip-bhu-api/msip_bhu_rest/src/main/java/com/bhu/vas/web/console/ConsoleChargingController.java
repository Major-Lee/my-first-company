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
            @RequestParam(required = false) boolean canturnoff,
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
        	RpcResponseDTO<BatchImportVTO> rpcResult = chargingRpcService.doInputDeviceRecord(uid, countrycode, mobileno, percent, remark);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				File targetFile = new File(rpcResult.getPayload().toAbsoluteFilePath());
				targetFile.mkdirs();
				file.transferTo(targetFile);
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
}
