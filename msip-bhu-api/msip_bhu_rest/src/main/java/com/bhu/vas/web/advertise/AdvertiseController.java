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
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.advertise.iservice.IAdvertiseRpcService;
import com.bhu.vas.api.vto.advertise.AdCommentsVTO;
import com.bhu.vas.api.vto.advertise.AdDevicePositionVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseListVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseReportVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseResponseVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseUserDetailVTO;
import com.bhu.vas.api.vto.advertise.AdvertiseVTO;
import com.bhu.vas.api.vto.device.DeviceGEOPointCountVTO;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
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
            @RequestParam(required = false) Integer vuid,
            @RequestParam(required = true) String adid,
            @RequestParam(required = false,defaultValue = "2") int tag,
            @RequestParam(required = false,defaultValue = "0") int type,
            @RequestParam(required = false) String image,
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = true) String adcode,
            @RequestParam(required = false,defaultValue = "0.0") double lat,
            @RequestParam(required = false,defaultValue = "0.0") double lon,
            @RequestParam(required = false) String distance,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String title,
            @RequestParam(required = true) long start,
            @RequestParam(required = true) long end,
            @RequestParam(required = false,defaultValue = "false") boolean isTop,
            @RequestParam(required = false) String extparams
            ) {
		try{
			RpcResponseDTO<AdvertiseVTO> rpcResult = advertiseRpcService.createNewAdvertise
					    (uid,vuid,adid,tag,type,image, url,domain, province, city, district,adcode,lat,lon,distance,description,title, start, end,isTop,extparams);
					if(!rpcResult.hasError()){
						SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
					}else{
						SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
					}
			}catch(BusinessI18nCodeException i18nex){
				SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
			}catch(Exception ex){
				ex.printStackTrace();
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
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
						SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
					}
			}catch(BusinessI18nCodeException i18nex){
				SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
			}catch(Exception ex){
				ex.printStackTrace();
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
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
			@RequestParam(required = false) Integer type,
			@RequestParam(required = false) String  createStartTime,
			@RequestParam(required = false) String createEndTime,
			@RequestParam(required = false) String mobileNo,
			@RequestParam(required = false,defaultValue="-1") int state,
    	    @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
    	    @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
			) {
		try{
			RpcResponseDTO<AdvertiseListVTO> rpcResult = advertiseRpcService.queryAdvertiseList
					(uid, province, city,district, publishStartTime, publishEndTime, type,createStartTime,createEndTime,mobileNo,state,pageNo,pageSize,false);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
	}
	@ResponseBody()
	@RequestMapping(value = "/queryAdvertiseInfo", method = {RequestMethod.POST})
	public void queryAdvertise(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false) Integer uid,
			@RequestParam(required = true) String advertiseId
			) {
		try{
			RpcResponseDTO<AdvertiseVTO> rpcResult = advertiseRpcService.queryAdvertise
					(uid,advertiseId);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
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
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex,BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
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
            @RequestParam(required = false,defaultValue = "0") int  type,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district) {
	        RpcResponseDTO<AdDevicePositionVTO> rpcResult = advertiseRpcService.fetchDevicePositionDistribution(uid,type,province, city, district);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
    }
    
	@ResponseBody()
	@RequestMapping(value = "/report", method = {RequestMethod.POST})
	public void fetchAdvertiseReport(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = true) String advertiseId
			) {
		try{
			RpcResponseDTO<AdvertiseReportVTO> rpcResult = advertiseRpcService.fetchAdvertiseReport
					(uid,advertiseId);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
		
	}
	
	@ResponseBody()
	@RequestMapping(value = "/fetch_device_geopoint", method = {RequestMethod.POST})
	public void countDeviceCountByGEOPoint(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) int uid,
			@RequestParam(required = false) String province,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String district,
			@RequestParam(required = true) double lat,
			@RequestParam(required = true) double lon,
			@RequestParam(required = true) String distances
			) {
		try{
			RpcResponseDTO<List<DeviceGEOPointCountVTO>> rpcResult = advertiseRpcService.countDeviceCountByGEOPoint
					(uid, province, city, district, lat, lon,  distances);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex, BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
	}
	
    @ResponseBody()
    @RequestMapping(value = "/fetch_by_condition_message", method = {RequestMethod.POST})
    public void fetch_by_condition_message(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) Integer uid,
            @RequestParam(required = false) String message,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {

        RpcResponseDTO<List<TailPage<AdvertiseVTO>>> rpcResult = advertiseRpcService.fetchBySearchConditionMessages(null,null,
        		pageNo, pageSize,false, message);
		if(!rpcResult.hasError()){
			//兼容老的界面和接口
			List<TailPage<AdvertiseVTO>> rpcResultPayload = rpcResult.getPayload();
			if(rpcResultPayload != null && !rpcResultPayload.isEmpty()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(RpcResponseDTOBuilder.
						builderSuccessRpcResponse(rpcResultPayload.get(0))));
			}else{
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(null));
			}
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/customize/fetch_by_condition_message", method = {RequestMethod.POST})
    public void customize_fetch_by_condition_message(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) Integer uid,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String mac,
            @RequestParam(required = false) String umac,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {

        RpcResponseDTO<List<TailPage<AdvertiseVTO>>> rpcResult = advertiseRpcService.fetchBySearchConditionMessages(
        	mac,umac,pageNo, pageSize,true, message);
		if(!rpcResult.hasError()){
			//兼容老的界面和接口
			List<TailPage<AdvertiseVTO>> rpcResultPayload = rpcResult.getPayload();
			if(rpcResultPayload != null && !rpcResultPayload.isEmpty()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(RpcResponseDTOBuilder.
						builderSuccessRpcResponse(rpcResultPayload.get(0))));
			}else{
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(null));
			}
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/oper", method = {RequestMethod.POST})
    public void advertiseOperation(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String adid,
            @RequestParam(required = false, defaultValue = "false") boolean istop,
            @RequestParam(required = false, defaultValue = "false") boolean isrefresh) {

		try{
	        RpcResponseDTO<Boolean> rpcResult = advertiseRpcService.advertiseOperation(
	        		uid,adid, istop, isrefresh);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex,BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/comment", method = {RequestMethod.POST})
    public void advertiseComment(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false) Integer vuid,
            @RequestParam(required = true) String adid,
            @RequestParam(required = true) String message,
            @RequestParam(required = false, defaultValue = "0") int type,
            @RequestParam(required = false) Double score) {

		try{
	        RpcResponseDTO<Boolean> rpcResult = advertiseRpcService.advertiseComment(
	        		uid, vuid,adid, message, type,score);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex,BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }
    
    
    @ResponseBody()
    @RequestMapping(value = "/comment/detail", method = {RequestMethod.POST})
    public void fetchCommentDetail(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) String[] adids,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {
		try{
	        RpcResponseDTO<List<AdCommentsVTO>> rpcResult = advertiseRpcService.fetchCommentDetail(adids,pageNo,pageSize);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex,BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/tips", method = {RequestMethod.POST})
    public void userAdvertiseDetail(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int  uid) {
		try{
	        RpcResponseDTO<AdvertiseUserDetailVTO> rpcResult = advertiseRpcService.userAdvertiseDetail(uid);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex,BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/random", method = {RequestMethod.POST})
    public void queryRandomAdvertiseDetails(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) String mac,
            @RequestParam(required = false) String umac) {
		try{
	        RpcResponseDTO<List<AdvertiseVTO>> rpcResult = advertiseRpcService.queryRandomAdvertiseDetails(mac,umac);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex,BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/confirmPay", method = {RequestMethod.POST})
    public void confirmPay(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int  uid,
            @RequestParam(required = true) String  adid) {
		try{
	        RpcResponseDTO<Boolean> rpcResult = advertiseRpcService.confirmPay(uid,adid);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex,BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/destorytips", method = {RequestMethod.POST})
    public void destoryTips(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int  uid,
            @RequestParam(required = true) String  adid) {
		try{
	        RpcResponseDTO<Boolean> rpcResult = advertiseRpcService.destoryTips(uid,adid);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex,BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/cpm_notify", method = {RequestMethod.POST})
    public void advertiseCPMNotify(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) String[]  adids,
            @RequestParam(required = true) String  userid,
            @RequestParam(required = true) String  sourcetype,
            @RequestParam(required = true) String  systype) {
		try{
	        RpcResponseDTO<Boolean> rpcResult = advertiseRpcService.advertiseCPMNotify(adids,userid,sourcetype,systype);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex,BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/fetch_adlist_portal", method = {RequestMethod.POST})
    public void fetchAdListByPortal(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) String  mac,
            @RequestParam(required = true) String  umac,
            @RequestParam(required = true) String  sourcetype,
            @RequestParam(required = true) String  systype,           
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {
		try{
	        RpcResponseDTO<AdvertiseResponseVTO> rpcResult = advertiseRpcService.fetchAdListByPortal(mac , umac , sourcetype , systype,  pageSize ,  pageNo);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex,BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/fetch_adlist_app", method = {RequestMethod.POST})
    public void fetchAdListByAPP(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) double  lat,
            @RequestParam(required = true) double  lon,
            @RequestParam(required = true) String  adcode,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {
		try{
	        RpcResponseDTO<List<AdvertiseVTO>> rpcResult = advertiseRpcService.fetchAdListByAPP(lat,lon,adcode,pageSize,pageNo);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
				
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex,BusinessWebHelper.getLocale(request)));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR, BusinessWebHelper.getLocale(request)));
		}
    }
}
