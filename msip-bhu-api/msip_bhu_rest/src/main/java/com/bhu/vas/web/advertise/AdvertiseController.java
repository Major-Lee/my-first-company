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

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.advertise.iservice.IAdvertiseRpcService;
import com.bhu.vas.api.vto.advertise.AdDevicePositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseListVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
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
            @RequestParam(required = true) String advertiseId,
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
	@RequestMapping(value = "/queryAdvertiseList", method = {RequestMethod.POST})
	public void queryAdvertise(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = false) String province,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String district,
			@RequestParam(required = false) String publishStartTime,
			@RequestParam(required = false) String publishEndTime,
			@RequestParam(required = false,defaultValue="0") int type,
			@RequestParam(required = false) String  createStartTime,
			@RequestParam(required = false) String createEndTime,
			@RequestParam(required = false) String mobileNo,
			@RequestParam(required = false,defaultValue="-1") int state,
    	    @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
    	    @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
			) {
		try{
			RpcResponseDTO<AdvertiseListVTO> rpcResult = advertiseRpcService.queryAdvertiseList
					(uid, province, city,district, publishStartTime, publishEndTime, type,createStartTime,createEndTime,mobileNo,state,pageNo,pageSize);
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
			@RequestParam(required = true) String advertiseId
			) {
		try{
			RpcResponseDTO<AdvertiseVTO> rpcResult = advertiseRpcService.queryAdvertise
					(uid,advertiseId);
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
	@RequestMapping(value = "/escapeAdvertise", method = {RequestMethod.POST})
	public void escapeAdvertise(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = true) String advertiseId
			) {
		try{
			RpcResponseDTO<Boolean> rpcResult = advertiseRpcService.escapeAdvertise
					(uid,advertiseId);
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
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district) {
	        RpcResponseDTO<AdDevicePositionVTO> rpcResult = advertiseRpcService.fetchDevicePositionDistribution(province, city, district);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
    }
}
