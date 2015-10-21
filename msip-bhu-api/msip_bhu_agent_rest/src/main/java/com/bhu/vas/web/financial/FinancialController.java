package com.bhu.vas.web.financial;

import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.api.vto.agent.AgentFinancialUploadVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;

/**
 * Created by bluesand on 10/21/15.
 */
@Controller
@RequestMapping("/financial")
public class FinancialController {


    private final static String URL_PREFIX = "http://192.168.66.7/img/" ;

    private final static int UPLOAD_TYPE_INVOICE = 1;
    private final static int UPLOAD_TYPE_RECEIPT = 2;

    private final static String INVOICE = "invoice";
    private final static String RECEIPT = "receipt";

    @Resource
    private IAgentRpcService agentRpcService;

    @ResponseBody()
    @RequestMapping(value="/post")
    public void postFinanical(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = true) Integer aid,
            @RequestParam(required = true) Double account,
            @RequestParam(required = true) String invoice,
            @RequestParam(required = true) String receipt,
            @RequestParam(required = false) String remark
    ) {
        try {
            boolean ret = agentRpcService.postAgentFinancialSettlement(uid, aid, account, invoice, receipt, remark);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
        }

    }


    @ResponseBody()
    @RequestMapping(value="/upload")
    public void uploadFinanical(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = true) Integer type,
            @RequestParam(required = true) Integer aid,
            @RequestParam("file") CommonsMultipartFile file

    ) {
        try {

            String dirPath = null;
            String uploadType = null;
            if (type == UPLOAD_TYPE_INVOICE) {
                dirPath = IAgentRpcService.PATH_INVOICE_PREFIX + File.separator + aid;
                uploadType = INVOICE;
            }

            if (type == UPLOAD_TYPE_RECEIPT) {
                dirPath = IAgentRpcService.PATH_RECEIPT_PREFIX + File.separator + aid;
                uploadType = RECEIPT;
            }

            //todo(bluesand): 方法提取
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

            int index = file.getOriginalFilename().lastIndexOf(".");
            String  ext = file.getOriginalFilename().substring(index);
            Date date = new Date();
            String filePath = dirPath + File.separator + date.getTime() + ext;
            File newFile = new File(filePath);
            file.transferTo(newFile);


            AgentFinancialUploadVTO vto = new AgentFinancialUploadVTO();
            vto.setUid(uid);
            vto.setAid(aid);
            vto.setUrl(URL_PREFIX + uploadType + File.separator + date.getTime() + ext);

            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);
        }

    }
}
