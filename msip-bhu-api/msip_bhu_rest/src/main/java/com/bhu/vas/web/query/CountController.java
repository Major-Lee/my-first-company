package com.bhu.vas.web.query;

import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bluesand on 5/26/15.
 */
@Controller
@RequestMapping("/noauth/vap")
public class CountController extends BaseController {

    @ResponseBody()
    @RequestMapping(value="/urlview",method={RequestMethod.POST,RequestMethod.GET})
    public void url404(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) String key,
            @RequestParam(required = true) String subkey
            ) {

    }
}
