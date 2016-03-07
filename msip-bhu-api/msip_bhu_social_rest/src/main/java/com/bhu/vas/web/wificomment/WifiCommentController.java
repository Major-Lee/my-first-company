package com.bhu.vas.web.wificomment;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.social.iservice.ISocialRpcService;
import com.bhu.vas.api.rpc.social.vto.CommentedWifiVTO;
import com.bhu.vas.api.rpc.social.vto.WifiCommentVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

public class WifiCommentController extends BaseController {
	
	@Resource
	private ISocialRpcService socialRpcService;
	
	
	/**
	 * 评论wifi
	 * @param response
	 * @param bssid
	 * @param uid
	 * @param message
	 */
	@ResponseBody()
	@RequestMapping(value="/app/wifi/comment",method={RequestMethod.POST})
	public void comment(
			HttpServletResponse response,
			 @RequestParam(required = true, value = "bssid") String bssid,
             @RequestParam(required = true, value = "uid") long uid,
             @RequestParam(required = false, value = "hd_mac") String hd_mac,
             @RequestParam(required = true, value = "message") String message) {
		try {
			if (socialRpcService.comment(uid, bssid, hd_mac, message)) {
				SpringMVCHelper.renderJson(response, ResponseSuccess.SUCCESS);
			} else {
				SpringMVCHelper.renderJson(response, ResponseError.ERROR);
			}

		} catch (Exception ex) {
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * wifi下评论列表
	 * @param response
	 * @param bssid
	 * @param uid
	 * @param message
	 */
	@ResponseBody()
	@RequestMapping(value = "/app/wifi/comments", method = { RequestMethod.POST })
	public void pageWifiCommentVTO(HttpServletResponse response,
			@RequestParam(required = true, value = "bssid") String bssid,
			@RequestParam(required = true, value = "uid") int uid,
			@RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {
		try {
			RpcResponseDTO<TailPage<WifiCommentVTO>> rpcResult = socialRpcService.pageWifiCommentVTO(uid, bssid, pageNo,
					pageSize);
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		} catch (Exception ex) {
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
	
	/**
	 * 用户评论过的wifi列表
	 * @param response
	 * @param bssid
	 * @param uid
	 * @param message
	 */
	@ResponseBody()
	@RequestMapping(value = "/app/wifi/list", method = { RequestMethod.POST })
	public void fetchUserCommentWifiList(HttpServletResponse response,
			@RequestParam(required = true, value = "uid") String uid,
			@RequestParam(required = false, value = "hd_mac") String hd_mac) {
		try {
			RpcResponseDTO<List<CommentedWifiVTO>> rpcResult = socialRpcService.fetchUserCommentWifiList(uid, hd_mac);
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		} catch (Exception ex) {
			SpringMVCHelper.renderJson(response, ResponseError.SYSTEM_ERROR);
		}
	}
}