package com.bhu.vas.msip.cores.web.mvc.spring;


import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.smartwork.msip.cores.helper.encrypt.Base64Helper;

/**
 * User: Edmond Lee
 * Date: 2010-1-12
 * Time: 12:00:07
 */
@Controller
public class StaticResultController{

    public static String getReturnUrl(String url) {
        if (!StringUtils.isBlank(url)) {
            return url;
        } else {
            return "/index.html";
        }
    }

    public static String getSuccessMessage(String message) {
        if (!StringUtils.isBlank(message))
            return message;
        else
            return "操作成功";
    }

    public static String getErrorMessage(String message) {
        if (!StringUtils.isBlank(message)){
        	/*try {
				message = new String(message.getBytes("ISO-8859-1"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}*/
            return message;
        }
        else
            return "抱歉，出错了";
    }

    public static Integer getLegacy(Integer legacy) {
        if (legacy != null) {
            return legacy;
        } else {
            return DEFAULT_LEGACY_SECOND;
        }
    }

    public static final String URL = "url";
    public static final String MESSAGE = "message";
    public static final String LEGACY = "legacy";
    public static final String RESPONSE = "response";
    public static final int DEFAULT_LEGACY_SECOND = 5;

/*    @RequestMapping("/success.html")
    public ModelAndView success(ModelAndView mv,
                                @RequestParam(required = false) String url,
                                @RequestParam(required = false) String message,
                                @RequestParam(required = false) Integer legacy) {
        mv = new ModelAndView("/success");
        mv.addObject(URL, getReturnUrl(url));
        mv.addObject(MESSAGE, getSuccessMessage(message));
        mv.addObject(LEGACY, getLegacy(legacy));
        return mv;
    }

    @RequestMapping("/error.html")
    public ModelAndView error(ModelAndView mv,
                              @RequestParam(required = false) String url,
                              @RequestParam(required = false) String message,
                              @RequestParam(required = false) Integer legacy) {
        mv = new ModelAndView("/error");
        mv.addObject(URL, getReturnUrl(url));
        mv.addObject(MESSAGE, getErrorMessage(message));
        mv.addObject(LEGACY, getLegacy(legacy));
        return mv;
    }*/

    public static void redirectSuccess(ModelAndView mv, String returnUrl) {
        redirectSuccess(mv, returnUrl, null, null);
    }

    public static void redirectSuccess(ModelAndView mv, String returnUrl, String message) {
        redirectSuccess(mv, returnUrl, message, null);
    }

    public static void redirectSuccess(ModelAndView mv, String returnUrl, String message, Integer legacy) {
        mv.addObject(URL, getReturnUrl(returnUrl));
        mv.addObject(MESSAGE, getSuccessMessage(message));
        mv.addObject(LEGACY, getLegacy(legacy));
        mv.setView(new RedirectView(BaseController.getContextPath()+"/success.html"));
    }

    public static void redirectError(ModelAndView mv, String returnUrl) {
        redirectError(mv, returnUrl, null, null);
    }

    public static void redirectError(ModelAndView mv, String returnUrl, String message) {
        redirectError(mv, returnUrl, null, message);
    }

    public static void redirectError(ModelAndView mv, String returnUrl, Integer legacy,String message) {
        mv.addObject(URL, getReturnUrl(returnUrl));
        mv.addObject(MESSAGE, new String(Base64Helper.encode(getErrorMessage(message).getBytes())));
        mv.addObject(LEGACY, getLegacy(legacy));
        mv.setView(new RedirectView(BaseController.getContextPath()+"/error"));//
    }
    public static void redirectURL(ModelAndView mv, String url, Integer legacy,String message) {
        /*mv.addObject(URL, getReturnUrl(returnUrl));
        mv.addObject(MESSAGE, new String(Base64Helper.encode(getErrorMessage(message).getBytes())));
        mv.addObject(LEGACY, getLegacy(legacy));*/
        mv.setView(new RedirectView(url));//
    }
}
