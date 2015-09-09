package com.bhu.vas.web.agent;

import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.smartwork.msip.cores.helper.FileHelper;
import com.smartwork.msip.cores.helper.StringHelper;
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
    @RequestMapping(value="/upload",method={RequestMethod.POST})
    public void uploadClaimAgentDevice(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("file") CommonsMultipartFile file,
            @RequestParam(required = true) Integer uid ) {

        try {
            String path = IAgentRpcService.PATH_PREFIX + File.separator + uid + File.separator + new Date().getTime() + ".xls";

            String originName = file.getOriginalFilename();

            File newFile = new File(path);

            file.transferTo(newFile);

            agentRpcService.importAgentDeviceClaim(uid, path, originName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
