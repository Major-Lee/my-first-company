package com.bhu.vas.web.commdity;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.commdity.CommdityAmountDTO;
import com.bhu.vas.api.dto.commdity.CommdityDTO;
import com.bhu.vas.api.dto.commdity.CommdityPhysicalDTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.ICommdityRpcService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.cores.web.mvc.spring.BaseController;
import com.smartwork.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/commdity")
public class CommdityController extends BaseController{
	
	@Resource
	private ICommdityRpcService commdityRpcService;
	
	/**
	 * 针对商品的区间价格 生成随机金额
	 * @param commdityid 商品id
	 * @param appid 应用id
	 * @param mac 设备mac
	 * @param umac 用户mac
	 * @return
	 */
	@ResponseBody()
	@RequestMapping(value="/interval/amount",method={RequestMethod.GET,RequestMethod.POST})
	public void amount(
			HttpServletRequest request,
			HttpServletResponse response,
			//@RequestParam(required = true) Integer appid,
			@RequestParam(required = true) String mac,
			@RequestParam(required = true) String umac,
			@RequestParam(required = true) Integer commdityid,
			@RequestParam(required = false, defaultValue = "2") Integer umactype) {
		String mac_lower = mac.toLowerCase();
		String umac_lower = umac.toLowerCase();
		RpcResponseDTO<CommdityAmountDTO> rpcResult = commdityRpcService.rewardIntervalAMount(commdityid, mac_lower, umac_lower, umactype);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	
	/**
	 * 获取商品列表
	 * @param request
	 * @param response
	 * @param status 商品状态
	 * @param pageNo 页码
	 * @param pageSize 每页数量
	 */
	@ResponseBody()
	@RequestMapping(value="/query/pages",method={RequestMethod.GET,RequestMethod.POST})
	public void query_pages(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false, defaultValue = "1") Integer status,
			@RequestParam(required = true) Integer category,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {

		RpcResponseDTO<TailPage<CommdityDTO>> rpcResult = commdityRpcService.commdityPages(status, category, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/physical/get_address",method={RequestMethod.GET,RequestMethod.POST})
	public void physical_get_address(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String umac) {
		String umac_lower = umac.toLowerCase();
		RpcResponseDTO<CommdityPhysicalDTO> rpcResult = commdityRpcService.physical_get_address(umac_lower);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
	@ResponseBody()
	@RequestMapping(value="/physical/set_address",method={RequestMethod.GET,RequestMethod.POST})
	public void physical_set_address(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) String umac,
			@RequestParam(required = true) String uname,
			@RequestParam(required = true) String acc,
			@RequestParam(required = true) String address) {
		String umac_lower = umac.toLowerCase();
		RpcResponseDTO<CommdityPhysicalDTO> rpcResult = commdityRpcService.physical_set_address(umac_lower,uname,acc,address);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
}
