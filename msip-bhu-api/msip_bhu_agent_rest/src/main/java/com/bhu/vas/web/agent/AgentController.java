package com.bhu.vas.web.agent;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.agent.dto.AgentOutputDTO;
import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.api.rpc.agent.vto.AgentRevenueStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementPageVTO;
import com.bhu.vas.api.vto.agent.AgentBulltinBoardVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceClaimVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceImportLogVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
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
    @RequestMapping(value="/statistics", method={RequestMethod.POST})
    public void statistics(HttpServletRequest request,
                           HttpServletResponse response,
                           @RequestParam(required = true) Integer uid,
                           @RequestParam(required = false) String date){
    	try{
    		if(StringUtils.isEmpty(date)){
    			date = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(1),DateTimeHelper.FormatPattern5);
    		}
			RpcResponseDTO<AgentRevenueStatisticsVTO> rpcResult = agentRpcService.statistics(uid, date);
			if(!rpcResult.hasError())
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			else
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
    }
    
    @ResponseBody()
    @RequestMapping(value="/dailyrecords", method={RequestMethod.POST})
    public void dailyrecords(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = false) String date,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
    		){
    	try{
    		if(StringUtils.isEmpty(date)){
    			date = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(1),DateTimeHelper.FormatPattern5);
    		}
			RpcResponseDTO<TailPage<DailyRevenueRecordVTO>> rpcResult = agentRpcService.pageHistoryRecords(uid, date, pageNo, pageSize);
			if(!rpcResult.hasError())
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			else
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
    }
    
    @ResponseBody()
    @RequestMapping(value="/settlements", method={RequestMethod.POST})
    public void settlements_pages(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = false) String date,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
    		){
    	try{
    		if(StringUtils.isEmpty(date)){
    			date = DateTimeHelper.formatDate(DateTimeHelper.FormatPattern5);
    		}
			RpcResponseDTO<SettlementPageVTO> rpcResult = agentRpcService.pageSettlements(uid,date, pageNo, pageSize);
			if(!rpcResult.hasError())
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			else
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult.getErrorCode()));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
    }

    /**
     * 代理商设备列表
     * @param request
     * @param response
     * @param uid
     * @param status  0:离线 1:在线 2:所有
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value="/list", method={RequestMethod.POST})
    public void agentList(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(required = true) Integer uid,
                          @RequestParam(required = false, defaultValue="2") int status,
                          @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
                          @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize){

        try {
            AgentDeviceVTO dtos = agentRpcService.pageClaimedAgentDeviceByUid(uid, status, pageNo, pageSize);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dtos));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);

        }

    }

    /**
     * 仓储代理商列表
     * @param request
     * @param response
     * @param uid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value="/admin/list", method={RequestMethod.POST})
    public void agenAdmintList(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(required = true) Integer uid,
                               @RequestParam(required = false, defaultValue="2") int status,
                               @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
                               @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize){

        try {
            AgentDeviceVTO dtos = agentRpcService.pageClaimedAgentDevice(status, pageNo, pageSize);
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
    @RequestMapping(value="/admin/importlist", method={RequestMethod.POST})
    public void agentImportLogList(HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestParam(required = true) Integer uid,
                          @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
                          @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize){

        try {
            TailPage<AgentDeviceImportLogVTO> dtos = agentRpcService.pageAgentDeviceImportLog(pageNo, pageSize);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dtos));
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
            @RequestParam(required = true) Integer aid) {

        try {
            String inputDirPath = IAgentRpcService.PATH_INPUT_PREFIX + File.separator + aid;
            String outputDirPath = IAgentRpcService.PATH_OUTPUT_PREFIX + File.separator + aid;

            //todo(bluesand): 创建目录结构的时候方法有问题？
//            FileHelper.makeDirectory(inputDirPath);
//            FileHelper.makeDirectory(outputDirPath);

            File dirFile = new File(inputDirPath);
            if (dirFile.exists()) {
                if (!dirFile.isDirectory()) {
                    dirFile.delete();
                    dirFile = new File(inputDirPath);
                    dirFile.mkdirs();
                }
            } else {
                dirFile.mkdirs();
            }

            File outDirFile = new File(outputDirPath);
            if (outDirFile.exists()) {
                if (!outDirFile.isDirectory()) {
                    outDirFile.delete();
                    outDirFile = new File(outputDirPath);
                    outDirFile.mkdirs();
                }
            } else {
                outDirFile.mkdirs();
            }

            Date date = new Date();
            String inputPath = inputDirPath + File.separator + date.getTime() + ".xls";
            String outputPath = outputDirPath + File.separator + date.getTime() + ".xls";

            String originName = file.getOriginalFilename();

            File newFile = new File(inputPath);

            file.transferTo(newFile);

            agentRpcService.importAgentDeviceClaim(uid, aid, inputPath, outputPath, originName);

            SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.ERROR);
        }
    }


    /**
     * 下载导入记录
     * @param request
     * @param response
     * @param uid
     * @param bid
     * @return
     * @throws IOException
     */
    @ResponseBody()
    @RequestMapping(value="/download")
    public ResponseEntity<byte[]> downloadClaimAgentDevice (
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = true) Integer bid) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        AgentBulltinBoardVTO vto = agentRpcService.findAgentBulltinBoardById(bid);
        if (vto != null) {
            String content = vto.getM();
            AgentOutputDTO dto = JsonHelper.getDTO(content, AgentOutputDTO.class);
            String path = dto.getPath();
            if (path != null) {
                headers.setContentDispositionFormData("attachment", dto.getAid() + ".xls");
                File file = new File(path);
                return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
            }
        }
        
        return null;

    }


    /**
     * 获取公告列表
     * @param request
     * @param response
     * @param uid
     * @param pageNo
     * @param pageSize
     * @throws IOException
     */
    @ResponseBody()
    @RequestMapping(value="/bulltinlist")
    public void bulltinBoardList (
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) throws IOException {

        try {
            TailPage<AgentBulltinBoardVTO> vtos = agentRpcService.pageAgentBulltinBoardByUid(uid, pageNo, pageSize);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(vtos));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);

        }

    }



}
