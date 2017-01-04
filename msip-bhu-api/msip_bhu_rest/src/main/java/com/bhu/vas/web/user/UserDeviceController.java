package com.bhu.vas.web.user;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.helper.VapEnumType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.rpc.user.dto.UserDeviceCheckUpdateDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceDTO;
import com.bhu.vas.api.rpc.user.dto.UserDeviceStatusDTO;
import com.bhu.vas.api.rpc.user.iservice.IUserDeviceRpcService;
import com.bhu.vas.api.vto.WifiDeviceIndustryVTO;
import com.bhu.vas.api.vto.device.UserDeviceTCPageVTO;
import com.bhu.vas.api.vto.device.UserDeviceVTO;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

/**
 * 用户和设备直接操作,绑定，解绑。
 */
@Controller
@RequestMapping("/user/device")
public class UserDeviceController extends BaseController {

    @Resource
    private IUserDeviceRpcService userDeviceRpcService;
    
    @Resource
    private IDeviceRestRpcService deviceRestRpcService;


    /**
     * 用户绑定设备
     * @param response
     * @param mac
     * @param uid
     * @throws Exception
     */
    @ResponseBody()
    @RequestMapping(value="/bind",method={RequestMethod.POST})
    public void bindDevice(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(required = true, value = "mac") String mac,
                           @RequestParam(required = true, value = "uid") int uid) throws Exception{
    	mac = mac.toLowerCase();
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, BusinessWebHelper.getLocale(request)));
            return;
        }

        RpcResponseDTO<UserDeviceDTO> userDeviceResult = userDeviceRpcService.bindDevice(mac, uid);
        if (!userDeviceResult.hasError()) {
        	SpringMVCHelper.renderJson(response, ResponseSuccess.embed(userDeviceResult.getPayload()));
        } else {
        	SpringMVCHelper.renderJson(response, ResponseError.embed(userDeviceResult, BusinessWebHelper.getLocale(request)));
        }

    }

    /**
     * 设备不在线可以解绑
     * @param response
     * @param mac
     * @param uid
     */
    @ResponseBody()
    @RequestMapping(value="/unbind",method={RequestMethod.POST})
    public void unBindDevice(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam(required = true, value = "mac") String mac,
                             @RequestParam(required = true, value = "uid") int uid
    ) {
    	mac = mac.toLowerCase();
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, BusinessWebHelper.getLocale(request)));
            return ;
        }
        /*int deviceStatus = userDeviceRpcService.validateDeviceStatusIsOnlineAndBinded(mac);
        logger.debug("devicestatus==" + deviceStatus);
        if (deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_NOT_EXIST ) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.DEVICE_DATA_NOT_EXIST));
        } else if (deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_NOT_UROOTER) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.DEVICE_NOT_UROOTER));
        }  else if (deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_BINDED
                || deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_NOT_ONLINE) {
            RpcResponseDTO<Boolean> userDeviceResult = userDeviceRpcService.unBindDevice(mac, uid);
            if (userDeviceResult.getPayload()) {
                SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
            } else {
                SpringMVCHelper.renderJson(response, ResponseError.embed(userDeviceResult.getErrorCode()));
            }

        } else if (deviceStatus == IUserDeviceRpcService.WIFI_DEVICE_STATUS_UNBINDED) {
            //TODO(bluesand):未绑定过装备的时候，取消绑定
            SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
        }*/
        RpcResponseDTO<Boolean> rpcResult = userDeviceRpcService.unBindDevice(mac, uid);
        if (!rpcResult.hasError()) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
        }
    }

    @ResponseBody()
    @RequestMapping(value="/validate",method={RequestMethod.POST})
    public void validateDevice(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(required = true, value = "mac") String mac) {
    	mac = mac.toLowerCase();
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, BusinessWebHelper.getLocale(request)));
            return ;
        }
        RpcResponseDTO<UserDeviceStatusDTO> rpcResult = userDeviceRpcService.validateDeviceStatus(mac);
        if (!rpcResult.hasError()) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
        }
        //SpringMVCHelper.renderJson(response,ResponseSuccess.embed(userDeviceRpcService.validateDeviceStatus(mac).getPayload()));
    }


    /**
     * 获取用户绑定列表,默认为urouter
     * @param response
     * @param uid
     * @param dut
     */
    @ResponseBody()
    @RequestMapping(value="/fetchbinded",method={RequestMethod.POST})
    public void listBindDevice(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(required = true, value = "uid") int uid,
                @RequestParam(required = false, defaultValue = VapEnumType.DUT_uRouter, value = "dut") String dut,
        		@RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
        		@RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize){
    	RpcResponseDTO<List<UserDeviceDTO>> rpcResult = userDeviceRpcService.fetchBindDevices(uid, dut, pageNo, pageSize);
        if (!rpcResult.hasError()) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
        }
    }

    /**
     * 获取用户绑定分页列表,默认为urouter
     * @param response
     * @param uid
     * @param dut
     */
    @ResponseBody()
    @RequestMapping(value="/fetchbinded_pages",method={RequestMethod.POST})
    public void pageBindDevice(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(required = true, value = "uid") int uid,
                @RequestParam(required = false, defaultValue = VapEnumType.DUT_uRouter, value = "dut") String dut,
        		@RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
        		@RequestParam(required = false, defaultValue = "50", value = "ps") int pageSize){
    	RpcResponseDTO<TailPage<UserDeviceDTO>> rpcResult = userDeviceRpcService.fetchPageBindDevices(uid, dut, pageNo, pageSize);
        if (!rpcResult.hasError()) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
        }
    }

    
    /**
     * app定位后，上传地址更新数据库记录
     * @param response
     * @param uid
	 * @param ct
     * @param dut
     */
    @ResponseBody()
    @RequestMapping(value="/update_location",method={RequestMethod.POST})
    public void updateDeviceLocation(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(required = true, value = "uid") int uid,
                @RequestParam(required = false, value = "mac") String mac,
                @RequestParam(required = false, value = "country") String country,
                @RequestParam(required = false, value = "province") String province,
                @RequestParam(required = false, value = "city") String city,
                @RequestParam(required = false, value = "district") String district,
                @RequestParam(required = false, value = "street") String street,
                @RequestParam(required = false, value = "faddress") String faddress,
                @RequestParam(required = false, value = "lon") String lon,
                @RequestParam(required = false, value = "lat") String lat){
    	
    	if(StringUtils.isEmpty(lon) || StringUtils.isEmpty(lat)){
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_EMPTY, BusinessWebHelper.getLocale(request)));
            return;
    	}

    	RpcResponseDTO<Boolean> rpcResult = userDeviceRpcService.updateDeviceLocation(uid, mac, country, province, city, district, street, faddress, lon, lat);
        if (!rpcResult.hasError()) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
        }
    }

    
	/**
	 * 修改设备行业信息和商户信息
	 * @param request
	 * @param response
	 * @param uid
	 * @param org
	 * @param sk
	 */
	@ResponseBody()
	@RequestMapping(value="/update_industry",method={RequestMethod.POST})
	public void deviceInfoUpdate(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String macs,
			@RequestParam(required = true) String industry,
			@RequestParam(required = false) String merchant_name){

		try{
	    	if(StringUtils.isEmpty(industry))
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, new String[]{"industry"});
//    		if(StringUtils.isEmpty(merchant_name))
//				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, new String[]{"merchant_name"});
	    	if(StringUtils.isEmpty(macs))
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, new String[]{"macs"});
	
	    	String[] macarry = macs.toLowerCase().split(StringHelper.COMMA_STRING_GAP);
			
			RpcResponseDTO<Boolean> rpcResult = deviceRestRpcService.deviceInfoUpdate(uid, Arrays.asList(macarry), industry, merchant_name);
			
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
	@RequestMapping(value="/fetch_industry_list",method={RequestMethod.POST})
	public void fetchIndustryInfo(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid){
		
		RpcResponseDTO<List<WifiDeviceIndustryVTO>> rpcResult = deviceRestRpcService.fetchIndustyList();
		
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
		}
	}

    
    /**
     * 新增云平台接口
     * @param response
     * @param uid
     * @param u_id
     * @param d_online
     * @param s_content
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value="/pagebinded_custom",method={RequestMethod.POST})
    public void pageBindedDeviceCustom(HttpServletRequest request, HttpServletResponse response,
                               	 @RequestParam(required = true) Integer uid,
                               	 @RequestParam(required = false) Integer u_id,
                                 @RequestParam(required = false) String d_online,
                                 @RequestParam(required = false) String s_content,
                                 @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
                                 @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize

                                 ) {
    	ResponseError validateError = ValidateService.validatePageSize(pageSize, request);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
        UserDeviceTCPageVTO rpcResult = userDeviceRpcService.pageBindDevicesCustom(uid, u_id, d_online,
        		s_content, pageNo, pageSize);
//        System.out.println("ret===" + rpcResult.isEmpty());
        if (rpcResult != null) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.ERROR);
        }
    }


    /**
     * 通用获取设备列表接口
     * @param response
     * @param uid
     * @param u_id
     * @param d_online
     * @param s_content
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value="/pagebinded",method={RequestMethod.POST})
    public void pageBindedDevice(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(required = true) Integer uid,
                                 @RequestParam(required = false) Integer u_id,
                                 @RequestParam(required = false) String d_online,
                                 @RequestParam(required = false) String s_content,
                                 @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
                                 @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize

    ) {
    	ResponseError validateError = ValidateService.validatePageSize(pageSize, request);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
        List<UserDeviceVTO>rpcResult = userDeviceRpcService.pageBindDevices(uid, u_id, d_online,
                s_content, pageNo, pageSize);
//        System.out.println("ret===" + rpcResult.isEmpty());
        if (rpcResult != null) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult));
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.ERROR);
        }
    }


    /**
     * 修改设备昵称
     * @param request
     * @param response
     * @param uid
     * @param mac
     * @param deviceName
     * @throws Exception
     */
    @ResponseBody()
    @RequestMapping(value="/modify/device_name",method={RequestMethod.POST})
    public void modifyDeviceName(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @RequestParam(required = true) Integer uid,
                                 @RequestParam(required = true) String mac,
                                 @RequestParam(required = true, value = "device_name") String deviceName) throws Exception{
    	mac = mac.toLowerCase();
        if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, BusinessWebHelper.getLocale(request)));
            return;
        }

        if (!validateDeviceName(deviceName)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_VALIDATE_LENGTH_ILEGAL, BusinessWebHelper.getLocale(request)));
            return;
        }

        if (userDeviceRpcService.modifyDeviceName(mac, uid, deviceName)) {
            SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
        } else {
            SpringMVCHelper.renderJson(response, ResponseError.ERROR);
        }
    }

    
    @ResponseBody()
    @RequestMapping(value="/check_upgrade",method={RequestMethod.POST})
    public void check_upgrade(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(required = true, value = "uid") int uid,
                               @RequestParam(required = true) String mac,
                               @RequestParam(required = true) String appver
                               ) {
    	mac = mac.toLowerCase();
    	if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, BusinessWebHelper.getLocale(request)));
            return;
        }
        RpcResponseDTO<UserDeviceCheckUpdateDTO> rpcResult = userDeviceRpcService.checkDeviceUpdate(uid, mac, appver);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			return;
		}
		SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult, BusinessWebHelper.getLocale(request)));
    }
    
    @ResponseBody()
    @RequestMapping(value="/force_upgrade",method={RequestMethod.POST})
    public void force_upgrade(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(required = true, value = "uid") int uid,
                               @RequestParam(required = true) String mac
                               ) {
    	mac = mac.toLowerCase();
    	if (!StringHelper.isValidMac(mac)) {
            SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_ERROR, BusinessWebHelper.getLocale(request)));
            return;
        }
        RpcResponseDTO<Boolean> resp = userDeviceRpcService.forceDeviceUpdate(uid, mac);
		if(!resp.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(resp.getPayload()));
			return;
		}
		SpringMVCHelper.renderJson(response, ResponseError.embed(resp, BusinessWebHelper.getLocale(request)));
    }
    
    private boolean validateDeviceName(String deviceName) throws  Exception {
        if (deviceName.getBytes("utf-8").length < 48) {
            return true;
        }
        return false;
    }

}
