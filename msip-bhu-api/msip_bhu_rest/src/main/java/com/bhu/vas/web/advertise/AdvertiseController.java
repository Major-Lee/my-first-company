package com.bhu.vas.web.advertise;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.UserType;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.charging.vto.SharedealDefaultVTO;
import com.bhu.vas.api.rpc.user.model.DeviceEnum;
import com.bhu.vas.business.helper.BusinessWebHelper;
import com.bhu.vas.msip.cores.web.mvc.WebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.business.token.UserTokenDTO;
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

	@ResponseBody()
    @RequestMapping(value = "/sharedeal/default", method = {RequestMethod.POST})
    public void sharedeal_default(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String image,
            @RequestParam(required = true) String url,
            @RequestParam(required = true) String province,
            @RequestParam(required = true) String city,
            @RequestParam(required = true) String district,
            @RequestParam(required = true) String start,
            @RequestParam(required = true) String end
            ) {
		try{
			
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			ex.printStackTrace();
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}finally{
			
		}
    }
}
