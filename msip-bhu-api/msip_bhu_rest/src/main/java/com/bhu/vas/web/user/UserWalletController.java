package com.bhu.vas.web.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.dto.commdity.RewardQueryExportRecordVTO;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.commdity.iservice.IOrderRpcService;
import com.bhu.vas.api.rpc.unifyStatistics.vto.UcloudMacStatisticsVTO;
import com.bhu.vas.api.rpc.user.dto.ShareDealWalletSummaryProcedureVTO;
import com.bhu.vas.api.rpc.user.iservice.IUserWalletRpcService;
import com.bhu.vas.api.vto.statistics.RankingListVTO;
import com.bhu.vas.api.vto.wallet.UserWalletDetailVTO;
import com.bhu.vas.api.vto.wallet.UserWalletLogVTO;
import com.bhu.vas.api.vto.wallet.UserWithdrawApplyVTO;
import com.bhu.vas.business.yun.iservice.IYunUploadService;
import com.bhu.vas.msip.cores.web.mvc.WebHelper;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping(value = "/account")
public class UserWalletController extends BaseController{
	
	@Resource
	private IUserWalletRpcService userWalletRpcService;
	@Resource
	private IOrderRpcService orderRpcService;
	@Resource
	private IYunUploadService yunOperateService;

	@ResponseBody()
	@RequestMapping(value="/wallet/withdraw", method={RequestMethod.GET,RequestMethod.POST})
	public void walletWithdraw(
			HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam(required = true) int uid,
			@RequestParam(required = false,defaultValue = "1001") int appid,
			@RequestParam(required = false) String payment_type,
			@RequestParam(required = false) String pwd,
			@RequestParam(required = true) double cash
			){
		try{
			if(cash <= 0){
				SpringMVCHelper.renderJson(response, ResponseError.embed(ResponseErrorCode.COMMON_DATA_PARAM_RANGE_ERROR,
						new String[]{"cash:".concat(String.valueOf(cash)),
						String.valueOf(BusinessRuntimeConfiguration.User_WalletWithdraw_Default_Withdraw_MinLimit),
						""}));
				return;
			}
			//ValidateService.validAmountRange(String.valueOf(cash),NumberValidateHelper.Range_Amount_Min,NumberValidateHelper.Range_Amount_Max);
			String remoteIp = WebHelper.getRemoteAddr(request);
			RpcResponseDTO<UserWithdrawApplyVTO> rpcResult = userWalletRpcService.withdrawOper(appid,payment_type,uid, pwd, cash, remoteIp);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(BusinessI18nCodeException i18nex){
			SpringMVCHelper.renderJson(response, ResponseError.embed(i18nex));
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/wallet/detail", method={RequestMethod.GET,RequestMethod.POST})
	public void walletDetail(
			HttpServletResponse response, 
			@RequestParam(required=true) Integer uid){
		try{
			RpcResponseDTO<UserWalletDetailVTO> rpcResult = userWalletRpcService.walletDetail(uid);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	
	@ResponseBody()
	@RequestMapping(value="/wallet/pwdset", method={RequestMethod.GET,RequestMethod.POST})
	public void withdrawPwdSet(
			HttpServletResponse response, 
			@RequestParam(required=true) Integer uid,
			@RequestParam(required=true) String captcha,
			@RequestParam(required=true) String pwd){
		try{
			RpcResponseDTO<Boolean> rpcResult = userWalletRpcService.withdrawPwdSet(uid,captcha, pwd);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/*@ResponseBody()
	@RequestMapping(value="/wallet/pwdupd", method={RequestMethod.GET,RequestMethod.POST})
	public void withdrawPwdSet(
			HttpServletResponse response, 
			@RequestParam(required=true) Integer uid,
			@RequestParam(required=true) String pwd,
			@RequestParam(required=true) String npwd){
		try{
			RpcResponseDTO<Boolean> rpcResult = userWalletRpcService.withdrawPwdUpd(uid, pwd, npwd);
			if(!rpcResult.hasError()){
				SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
			}else{
				SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
			}
		}catch(Exception ex){
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}*/
	
	/**
	 * 分页提取钱包流水日志记录
	 * @param request
	 * @param response
	 * @param uid uid
	 * @param transmode 交易方式
	 * @param transtype 交易类别
	 * @param pageNo 分页no
	 * @param pageSize 分页size
	 */
    @ResponseBody()
    @RequestMapping(value = "/wallet/fetch_logs", method = {RequestMethod.POST})
    public void fetch_logs(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false,defaultValue = "") String transmode,
            @RequestParam(required = false,defaultValue = "") String transtype,
            @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize
    		) {
    	ResponseError validateError = ValidateService.validatePageSize(pageSize);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<TailPage<UserWalletLogVTO>> rpcResult = userWalletRpcService.pageUserWalletlogs(uid, transmode, transtype, pageNo, pageSize);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
    
    
    @ResponseBody()
    @RequestMapping(value = "/wallet/fetch_logstatistics", method = {RequestMethod.POST})
    public void fetch_logs(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid
    		) {
		RpcResponseDTO<ShareDealWalletSummaryProcedureVTO> rpcResult = userWalletRpcService.walletLogStatistics(uid);
		if(!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    }
    
    @ResponseBody()
    @RequestMapping(value="/wallet/rankingList", method={RequestMethod.GET,RequestMethod.POST})
    public void rankingList(HttpServletResponse response, @RequestParam(required = true) Integer uid){
    	
    	RpcResponseDTO<RankingListVTO> rpcResult = userWalletRpcService.rankingList(uid);
    	if(!rpcResult.hasError()){
    		SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
    	}else{
    		SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    	}
    }
    
    @ResponseBody()
    @RequestMapping(value="/richStatistics", method={RequestMethod.GET,RequestMethod.POST})
    public void richStatistics( HttpServletResponse response, 
    		@RequestParam(required = true) int uid){
    	try{
    		RpcResponseDTO<UcloudMacStatisticsVTO> rpcResult = userWalletRpcService.richStatistics(uid);
    		if(!rpcResult.hasError()){
    			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
    		}else{
    			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    		}
    	}catch(Exception ex){
    		SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
    	}
    }
    
    /**
     * 根据筛选条件导出数据
     * @param request
     * @param response
     * @param uid 用户id
     * @param mac 设备mac
     * @param umac 支付订单的用户mac
     * @param status 订单状态 默认发货完成
     * @param start_created_ts 起始时间戳
     * @param end_created_ts 结束时间戳
     * @param pageNo 页码
     * @param pageSize 每页数量
     * 
     */
    @ResponseBody()
    @RequestMapping(value="/reward/query/exportrecord",method={RequestMethod.GET,RequestMethod.POST})
    public void reward_query_export_record(
    		HttpServletRequest request,
    		HttpServletResponse response,
    		@RequestParam(required = true) Integer uid,
    		@RequestParam(required = false) String mac,
    		@RequestParam(required = false) String umac,
    		@RequestParam(required = false, defaultValue = "10") Integer status,
    		@RequestParam(required = false) String dut,
    		@RequestParam(required = false, defaultValue = "0") long start_created_ts,
    		@RequestParam(required = false, defaultValue = "0") long end_created_ts,
    		@RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
            @RequestParam(required = false, defaultValue = "50", value = "ps") int pageSize
    		) {
    	
    		RpcResponseDTO<RewardQueryExportRecordVTO> rpcResult = orderRpcService.rewardQueryExportRecord(uid, mac, umac, 
    				status, dut, start_created_ts, end_created_ts,pageNo,pageSize);
    		if(!rpcResult.hasError()){
    			byte[] bs = rpcResult.getPayload().getBs();
    			String filename = rpcResult.getPayload().getFilename();
    			yunOperateService.uploadYun(bs, filename);
    			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
    		}else{
    			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
    		}
    	}
}
