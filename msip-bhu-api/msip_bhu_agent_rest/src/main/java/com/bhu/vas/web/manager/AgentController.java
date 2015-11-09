package com.bhu.vas.web.manager;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.agent.dto.AgentOutputDTO;
import com.bhu.vas.api.rpc.agent.iservice.IAgentRpcService;
import com.bhu.vas.api.rpc.agent.vto.AgentRevenueStatisticsVTO;
import com.bhu.vas.api.rpc.agent.vto.DailyRevenueRecordVTO;
import com.bhu.vas.api.rpc.agent.vto.SettlementPageVTO;
import com.bhu.vas.api.vto.agent.*;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
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
import org.springframework.util.StringUtils;
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
 * Created by bluesand on 9/9/15.
 */
@Controller
@RequestMapping("/agent")
public class AgentController {
    @Resource
    private IAgentRpcService agentRpcService;

    /**
     * 代理商首页面静态数据
     * @param request
     * @param response
     * @param uid
     * @param date
     */
    @ResponseBody()
    @RequestMapping(value="/statistics", method={RequestMethod.POST})
    public void statistics(HttpServletRequest request,
                           HttpServletResponse response,
                           @RequestParam(required = true) Integer uid,
                           @RequestParam(required = false) String date){
    	try{
    		if(StringUtils.isEmpty(date)){
    			//date = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(1),DateTimeHelper.FormatPattern5);
    			date = DateTimeHelper.formatDate(new Date(),DateTimeHelper.FormatPattern5);
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

    /**
     * 代理商首页面静态数据 每日收益流水
     * @param request
     * @param response
     * @param uid
     * @param date
     * @param pageNo
     * @param pageSize
     */
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
    			//date = DateTimeHelper.formatDate(DateTimeHelper.getDateDaysAgo(1),DateTimeHelper.FormatPattern5);
    			date = DateTimeHelper.formatDate(new Date(),DateTimeHelper.FormatPattern5);
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


    /**
     * 代理商结算列表
     * @param request
     * @param response
     * @param uid
     * @param status
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value="/settlements", method={RequestMethod.POST})
    public void settlements_pages(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = false, defaultValue="-1") int status,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize
    		){
    	try{
    		/*if(StringUtils.isEmpty(date)){
    			date = DateTimeHelper.formatDate(DateTimeHelper.FormatPattern5);
    		}*/
			RpcResponseDTO<SettlementPageVTO> rpcResult = agentRpcService.pageSettlements(uid, status, pageNo, pageSize);
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
     * 代理商设备列表
     * @param request
     * @param response
     * @param uid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value="/yet/list", method={RequestMethod.POST})
    public void agentYetList(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(required = true) Integer uid,
                          @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
                          @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize){

        try {
            AgentDeviceVTO dtos = agentRpcService.pageUnClaimAgentDeviceByUid(uid, pageNo, pageSize);
            SpringMVCHelper.renderJson(response, ResponseSuccess.embed(dtos));
        } catch (Exception e) {
            e.printStackTrace();
            SpringMVCHelper.renderJson(response, ResponseError.BUSINESS_ERROR);

        }

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
    @RequestMapping(value="/download", method={RequestMethod.GET})
    public ResponseEntity<byte[]> downloadClaimAgentDevice (
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) Integer uid,
            @RequestParam(required = true) Integer bid) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        AgentBulltinBoardVTO vto = agentRpcService.findAgentBulltinBoardById(uid, bid);
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




}
