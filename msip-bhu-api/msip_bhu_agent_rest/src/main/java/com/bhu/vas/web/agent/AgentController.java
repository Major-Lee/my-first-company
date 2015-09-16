package com.bhu.vas.web.agent;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.agent.dto.AgentOutputDTO;
import com.bhu.vas.api.vto.agent.AgentBulltinBoardVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceImportLogVTO;

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

import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.api.rpc.agent.vto.AgentUserDetailVTO;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.StatisticsVTO;
import com.bhu.vas.api.vto.agent.AgentDeviceClaimVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
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
			RpcResponseDTO<StatisticsVTO> rpcResult = agentRpcService.statistics(uid, date);
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
    @RequestMapping(value="/hello", method={RequestMethod.POST})
    public void hello(HttpServletRequest request,
                                       HttpServletResponse response,
                                       @RequestParam(required = true) Integer uid){
        System.out.println("hello !!!!!");
        SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
    }


    @ResponseBody()
    @RequestMapping(value="/list", method={RequestMethod.POST})
    public void agentList(HttpServletRequest request,
                      HttpServletResponse response,
                      @RequestParam(required = true) Integer uid,
                          @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
                          @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize){

        try {
            TailPage<AgentDeviceClaimVTO> dtos = agentRpcService.pageClaimedAgentDevice(uid, pageNo, pageSize);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dtos));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);

        }

    }


    @ResponseBody()
    @RequestMapping(value="/log_list", method={RequestMethod.POST})
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
            String content = vto.getContent();
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




}
