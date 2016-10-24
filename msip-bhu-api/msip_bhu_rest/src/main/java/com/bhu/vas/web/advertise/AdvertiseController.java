package com.bhu.vas.web.advertise;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.advertise.AdvertiseVTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.advertise.iservice.IAdvertiseRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

/**
 * 全城热播 controller
 * @author xiaowei
 *
 */
@Controller
@RequestMapping("/ad")
public class AdvertiseController extends BaseController{
	@Resource
	private IAdvertiseRpcService advertiseRpcService;

	@ResponseBody()
    @RequestMapping(value = "/createNewAdvertise", method = {RequestMethod.POST})
    public void createNewAdvertise(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String image,
            @RequestParam(required = true) String url,
            @RequestParam(required = true) String domain,
            @RequestParam(required = true) String province,
            @RequestParam(required = true) String city,
            @RequestParam(required = true) String district,
            @RequestParam(required = true) String description,
            @RequestParam(required = true) String title,
            @RequestParam(required = true) long start,
            @RequestParam(required = true) long end
            ) {
		try{
			RpcResponseDTO<Boolean> rpcResult = advertiseRpcService.createNewAdvertise
					    (uid, image, url,domain, province, city, district,description,title, start, end);
					if(!rpcResult.hasError()){
						SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
					}else{
						SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
					}
			}catch(BusinessI18nCodeException i18nex){
				SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
			}catch(Exception ex){
				ex.printStackTrace();
				SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
			}

    }
	
	@ResponseBody()
    @RequestMapping(value = "/updateAdvertise", method = {RequestMethod.POST})
    public void updateAdvertise(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) int advertiseId,
            @RequestParam(required = true) String image,
            @RequestParam(required = true) String url,
            @RequestParam(required = true) String domain,
            @RequestParam(required = true) String province,
            @RequestParam(required = true) String city,
            @RequestParam(required = true) String district,
            @RequestParam(required = true) String description,
            @RequestParam(required = true) String title,
            @RequestParam(required = true) long start,
            @RequestParam(required = true) long end
            ) {
		try{
			RpcResponseDTO<Boolean> rpcResult = advertiseRpcService.updateAdvertise
					    (uid,advertiseId, image, url,domain, province, city, district,description,title, start, end);
					if(!rpcResult.hasError()){
						SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
					}else{
						SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
					}
			}catch(BusinessI18nCodeException i18nex){
				SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
			}catch(Exception ex){
				ex.printStackTrace();
				SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
			}

    }
	
	@ResponseBody()
	@RequestMapping(value = "/verifyAdvertise", method = {RequestMethod.POST})
	public void updateAdvertise(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = true) int advertiseId,
			@RequestParam(required = true) String msg,
			@RequestParam(required = true) int state
			) {
		try{
			RpcResponseDTO<Boolean> rpcResult = advertiseRpcService.verifyAdvertise
					(uid,advertiseId, msg,state);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
		
	}
	@ResponseBody()
	@RequestMapping(value = "/queryAdvertiseList", method = {RequestMethod.POST})
	public void queryAdvertise(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = true) String image,
			@RequestParam(required = true) String url,
			@RequestParam(required = true) String domain,
			@RequestParam(required = true) String province,
			@RequestParam(required = true) String city,
			@RequestParam(required = true) String district,
			@RequestParam(required = true) String description,
			@RequestParam(required = true) String title,
			@RequestParam(required = true) long start,
			@RequestParam(required = true) long end
			) {
		try{
			RpcResponseDTO<Boolean> rpcResult = advertiseRpcService.createNewAdvertise
					(uid, image, url,domain, province, city, district,description,title, start, end);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
		
	}
	@ResponseBody()
	@RequestMapping(value = "/queryAdvertiseInfo", method = {RequestMethod.POST})
	public void queryAdvertise(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = true) int advertiseId
			) {
		try{
			RpcResponseDTO<AdvertiseVTO> rpcResult = advertiseRpcService.queryAdvertise
					(advertiseId);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
		
	}
	
	
	/**
	 * 查询设备地理位置分布
	 * @param request
	 * @param response
	 * @param uid
	 * @param province
	 * @param city
	 */
    @ResponseBody()
    @RequestMapping(value = "/fetch_device_position", method = {RequestMethod.POST})
    public void fetch_device_position_distribution(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city) {
		try{
	        RpcResponseDTO<List<String>> vtos = advertiseRpcService.fetchDevicePositionDistribution(province, city);
	        SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
    }
}
