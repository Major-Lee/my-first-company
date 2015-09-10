package com.bhu.vas.web.agent;

import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.FileHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;

/**
 * Created by bluesand on 9/9/15.
 */
@Controller
@RequestMapping("/agent")
public class AgentController {
    @Resource
    private IAgentRpcService agentRpcService;


    @ResponseBody()
    @RequestMapping(value="/hello", method={RequestMethod.POST})
    public void hello(HttpServletRequest request,
                                       HttpServletResponse response,
                                       @RequestParam(required = true) Integer uid){d
        System.out.println("hello !!!!!");
        SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
    }

    @ResponseBody()
    @RequestMapping(value="/upload",method={RequestMethod.POST})
    public void uploadClaimAgentDevice(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("file") CommonsMultipartFile file,
            @RequestParam(required = true) Integer uid ) {

        try {
            String dirPath = IAgentRpcService.PATH_PREFIX + File.separator + uid;

            File dirFile = new File(dirPath);
            if (dirFile.exists()) {
                if (!dirFile.isDirectory()) {
                    dirFile.delete();
                    dirFile = new File(dirPath);
                    dirFile.mkdirs();
                }
            } else {
                dirFile.mkdirs();
            }

            String path = dirPath + File.separator + new Date().getTime() + ".xls";

            String originName = file.getOriginalFilename();

            File newFile = new File(path);

            newFile.mkdirs();

            file.transferTo(newFile);

            agentRpcService.importAgentDeviceClaim(uid, path, originName);

            SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
