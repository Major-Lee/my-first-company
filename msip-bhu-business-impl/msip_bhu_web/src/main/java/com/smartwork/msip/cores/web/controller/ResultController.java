package com.smartwork.msip.cores.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.smartwork.msip.cores.helper.encrypt.Base64Helper;
import com.smartwork.msip.cores.web.mvc.spring.StaticResultController;

/**
 * User: Edmond Lee
 * Date: 2010-1-12
 * Time: 12:00:07
 */
@Controller
public class ResultController{

    @RequestMapping("/success.html")
    public ModelAndView success(ModelAndView mv,
                                @RequestParam(required = false) String url,
                                @RequestParam(required = false) String message,
                                @RequestParam(required = false) Integer legacy) {
        mv = new ModelAndView("/success");
        //mv = new ModelAndView("/success");
        mv.addObject(StaticResultController.URL, StaticResultController.getReturnUrl(url));
        mv.addObject(StaticResultController.MESSAGE, StaticResultController.getSuccessMessage(message));
        mv.addObject(StaticResultController.LEGACY, StaticResultController.getLegacy(legacy));
        return mv;
    }

    @RequestMapping("/error.html")
    public ModelAndView error(ModelAndView mv,
                              @RequestParam(required = false) String url,
                              @RequestParam(required = false) String message,
                              @RequestParam(required = false) Integer legacy) {
        mv = new ModelAndView("/error");
        mv.addObject(StaticResultController.URL, StaticResultController.getReturnUrl(url));
        if(message == null){
        	mv.addObject(StaticResultController.MESSAGE, StaticResultController.getErrorMessage(message));
        }else
        	mv.addObject(StaticResultController.MESSAGE, new String(Base64Helper.decode(message)));
        mv.addObject(StaticResultController.LEGACY, StaticResultController.getLegacy(legacy));
        return mv;
    }
}
