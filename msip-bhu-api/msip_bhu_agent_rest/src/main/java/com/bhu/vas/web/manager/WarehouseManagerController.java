package com.bhu.vas.web.manager;

import com.bhu.vas.api.rpc.agent.dto.AgentOutputDTO;
import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.api.vto.agent.*;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.io.IOException;
import java.util.Date;

/**
 * Created by bluesand on 11/2/15.
 * 仓储管理员
 */
@Controller
@RequestMapping("/whm")
public class WarehouseManagerController extends BaseController{

    @Resource
    private IAgentRpcService agentRpcService;

    /**
     * 仓储代理商列表
     * @param request
     * @param response
     * @param uid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value="/list", method={RequestMethod.POST})
    public void agenAdmintList(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(required = true) Integer uid,
                               @RequestParam(required = false, defaultValue="2") int status,
                               @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
                               @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize){

        try {
            AgentDeviceVTO dtos = agentRpcService.pageClaimedAgentDevice(uid,status, pageNo, pageSize);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dtos));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);

        }

    }

    /**
     * 仓储导入记录列表
     * @param request
     * @param response
     * @param uid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value="/log/list", method={RequestMethod.POST})
    public void agentImportLogList(HttpServletRequest request,
                                   HttpServletResponse response,
                                   @RequestParam(required = true) Integer uid,
                                   @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
                                   @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize){

        try {
            TailPage<AgentDeviceImportLogVTO> dtos = agentRpcService.pageAgentDeviceImportLog(uid, pageNo, pageSize);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dtos));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);

        }

    }


    /**
     * 确认导入结果
     * @param request
     * @param response
     * @param uid
     * @param logid
     */
    @ResponseBody()
    @RequestMapping(value="/log/update")
    public void updateImport(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = true) Integer logid
    ) {

        try {
            boolean ret  = agentRpcService.updateAgentImportImport(uid, logid);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(ret));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);

        }
    }


    /**
     * 上传代理商Excel
     * @param request
     * @param response
     * @param file
     * @param uid
     * @param aid
     */
    @ResponseBody()
    @RequestMapping(value="/upload",method={RequestMethod.POST})
    public void uploadClaimAgentDevice(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("file") CommonsMultipartFile file,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = true) Integer aid,
            @RequestParam(required = true) Integer wid,
            @RequestParam(required = false) String remark
    ) {

        AgentUploadVTO vto = new AgentUploadVTO();
        String originName = file.getOriginalFilename();
        vto.setFilename(originName);

        String ext = originName.substring(originName.lastIndexOf("."));

        System.out.println("ext===" + ext);
        if (!".xls".equals(ext) && !".xlsx".equals(ext)) {
            SpringMVCHelper.renderJson(response, new AgentUploadResponseError(false,"not excel",vto));
            return ;
        }
        try {
            String inputDirPath = IAgentRpcService.PATH_INPUT_PREFIX + File.separator + aid;
            String outputDirPath = IAgentRpcService.PATH_OUTPUT_PREFIX + File.separator + aid;

            //todo(bluesand): 创建目录结构的时候方法有问题？
//            FileHelper.makeDirectory(inputDirPath);
//            FileHelper.makeDirectory(outputDirPath);

            makeDirs(inputDirPath);
            makeDirs(outputDirPath);

            Date date = new Date();
            String inputPath = inputDirPath + File.separator + date.getTime() + ".xls";
            String outputPath = outputDirPath + File.separator + date.getTime() + ".xls";

            File newFile = new File(inputPath);
            file.transferTo(newFile);

            vto.setUid(uid);
            vto.setAid(aid);

            AgentDeviceImportLogVTO log = agentRpcService.importAgentDeviceClaim(uid, aid, wid, inputPath,
                    outputPath, originName, remark);

            vto.setLog(log);

            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vto));

        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, new AgentUploadResponseError(false,"error",vto));

        }
    }



    private void makeDirs(String path) {
        File dirFile = new File(path);
        if (dirFile.exists()) {
            if (!dirFile.isDirectory()) {
                dirFile.delete();
                dirFile = new File(path);
                dirFile.mkdirs();
            }
        } else {
            dirFile.mkdirs();
        }
    }


    /**
     * 下载导入记录
     * @param request
     * @param response
     * @param uid
     * @param logid
     * @return
     * @throws IOException
     */
    @ResponseBody()
    @RequestMapping(value="/download")
    public ResponseEntity<byte[]> downloadClaimAgentDevice (
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
//            @RequestParam(required = true) Integer bid) throws IOException {
            @RequestParam(required = true) Integer logid) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);



        AgentDeviceImportLogVTO vto = agentRpcService.findAgentDeviceImportLogById(uid, logid);
        if (vto != null) {
            String content = vto.getContent();
            AgentOutputDTO dto = JsonHelper.getDTO(content, AgentOutputDTO.class);
            String path = dto.getPath();
            if (path != null) {
                headers.setContentDispositionFormData("attachment", dto.getName() + ".xls");
                File file = new File(path);
                return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
            }
        }

        return null;

    }

    /**
     * 还未认领的设备
     * @param request
     * @param response
     * @param uid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value="/yet/list", method={RequestMethod.POST})
    public void agentDeviceYetList(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(required = true) Integer uid,
                               @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
                               @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize){

        try {
            AgentDeviceVTO  dto = agentRpcService.pageUnClaimAgentDevice(pageNo, pageSize);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dto));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);

        }

    }



}
