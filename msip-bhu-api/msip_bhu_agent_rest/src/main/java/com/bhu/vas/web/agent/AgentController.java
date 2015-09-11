package com.bhu.vas.web.agent;

import java.io.File;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bhu.vas.api.rpc.agent.dto.AgentDeviceClaimDTO;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseSuccess;

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
                                       @RequestParam(required = true) Integer uid){
        System.out.println("hello !!!!!");
        SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
    }


    @ResponseBody()
    @RequestMapping(value="/list_agent", method={RequestMethod.POST})
    public void agentList(HttpServletRequest request,
                      HttpServletResponse response,
                      @RequestParam(required = true) Integer uid,
                          @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
                          @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize){

        try {
            TailPage<AgentDeviceClaimDTO> dtos = agentRpcService.pageClaimedAgentDevice(uid, pageNo, pageSize);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dtos));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);

        }

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
