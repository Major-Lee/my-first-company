package com.bhu.vas.web.console;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.vap.iservice.IVapRpcService;
import com.bhu.vas.api.vto.device.CurrentGrayUsageVTO;
import com.bhu.vas.api.vto.device.DeviceUnitTypeVTO;
import com.bhu.vas.api.vto.device.GrayUsageVTO;
import com.bhu.vas.api.vto.device.VersionVTO;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.qiniu.common.QiniuException;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/console/ver")
public class ConsoleVersionController extends BaseController {

	private ExecutorService exec = Executors.newFixedThreadPool(5);
	@Resource
	private IVapRpcService vapRpcService;
	@Resource
	private YunOperateService yunOperateService;

	/**
	 * 获取设备定义的类型
	 *
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value = "/fetch_duts", method = { RequestMethod.POST })
	public void fetch_duts(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) int uid) {
		RpcResponseDTO<List<DeviceUnitTypeVTO>> rpcResult = vapRpcService.deviceUnitTypes(uid);
		if (!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
	}

	/**
	 * 
	 * 获取设备类型的灰度列表
	 * 
	 * @param request
	 * @param response
	 * @param uid
	 * @param dut
	 */
	@ResponseBody()
	@RequestMapping(value = "/fetch_grays", method = { RequestMethod.POST })
	public void fetch_grays(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) int uid, @RequestParam(required = true) String dut) {
		RpcResponseDTO<CurrentGrayUsageVTO> rpcResult = vapRpcService.currentGrays(uid, dut);
		if (!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
	}

	/**
	 * 获取指定设备型号的固件版本分页列表或者增值模块版本分页列表
	 * 
	 * @param request
	 * @param response
	 */
	@ResponseBody()
	@RequestMapping(value = "/pagesdv", method = { RequestMethod.POST })
	public void pagesdv(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) int uid, @RequestParam(required = true) String dut,
			@RequestParam(required = true) boolean fw,
			@RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
			@RequestParam(required = false, defaultValue = "10", value = "ps") int pageSize) {
		RpcResponseDTO<TailPage<VersionVTO>> rpcResult = vapRpcService.pagesDeviceVersions(uid, dut, fw, pageNo,
				pageSize);
		if (!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
	}

	/**
	 * 变更指定产品类型的灰度关联的固件版本号和增值组件版本号
	 * 
	 * @param request
	 * @param response
	 * @param uid
	 * @param dut
	 * @param gl
	 * @param fwid
	 * @param omid
	 */
	@ResponseBody()
	@RequestMapping(value = "/modifyrv4gv", method = { RequestMethod.POST })
	public void modifyrv4gv(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) int uid, @RequestParam(required = true) String dut,
			@RequestParam(required = true) int gl, @RequestParam(required = true) String fwid,
			@RequestParam(required = true) String omid) {
		RpcResponseDTO<GrayUsageVTO> rpcResult = vapRpcService.modifyRelatedVersion4GrayVersion(uid, dut, gl, fwid,
				omid);
		if (!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
	}

	/**
	 * 增加固件版本或者增值组件版本信息
	 * 
	 * @param request
	 * @param response
	 * @param uid
	 * @param dut
	 * @param fw
	 * @param versionid
	 * @param upgrade_url
	 */
	@ResponseBody()
	@RequestMapping(value = "/adddv", method = { RequestMethod.POST })
	public void adddv(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("file") CommonsMultipartFile file,
			@RequestParam(required = true) int uid,
			@RequestParam(required = true) String dut, 
			@RequestParam(required = true) boolean fw) {

		byte[] bs = new byte[1000];
		bs = file.getBytes();
		String fileName = file.getOriginalFilename();
		uploadYun(bs, dut, fw, fileName);
		String QNurl = null;
		String ALurl = null;
		if (fw) {
			 QNurl = yunOperateService.QN_BUCKET_URL_FW + "/" + dut + "/build/" + fileName;
			 ALurl = yunOperateService.AL_BUCKET_NAME_FW + "." + yunOperateService.AL_END_POINT + "/" + dut
					+ "/build/" + fileName;
		}
		if (!fw) {
			 QNurl = yunOperateService.QN_BUCKET_URL_OM + "/" + yunOperateService.getRemoteName(fileName) + "/" + fileName;
			 ALurl = yunOperateService.AL_BUCKET_NAME_OM + "." + yunOperateService.AL_END_POINT + "/" + dut
					+ "/build/" + fileName;
		}
		System.out.println(QNurl + "::::::::::::::::" + ALurl);
		RpcResponseDTO<VersionVTO> rpcResult = vapRpcService.addDeviceVersion(uid, dut, fw, fileName, QNurl, ALurl);
		if (!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));

	}

	// 异步的上传至阿里云、七牛云
	private void uploadYun(final byte[] bs, final String dut, final boolean fw, final String fileName) {

		exec.submit((new Runnable() {
			@Override
			public void run() {
				try {
						String JsFilePath = null;
						if(!fw){
							JsFilePath = yunOperateService.getFilePath(fileName);
						}
						// 七牛云
						yunOperateService.uploadFile2QN(bs,dut,fileName,fw,JsFilePath);
						// 阿里云
						yunOperateService.uploadFile2AL(bs,dut,fileName,fw,JsFilePath);
				}  catch (Exception e) {
					e.printStackTrace();
				}
			}
		}));
	}

	/**
	 * 删除固件版本或者增值组件版本信息
	 * 
	 * @param request
	 * @param response
	 * @param uid
	 * @param dut
	 * @param fw
	 * @param fileName
	 */
	@ResponseBody()
	@RequestMapping(value = "/removedv", method = { RequestMethod.POST })
	public void removedv(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = true) int uid, @RequestParam(required = true) String dut,
			@RequestParam(required = true) boolean fw, @RequestParam(required = true) String fileName) {

		System.out.println("deleteFile:begin");
		yunOperateService.deleteFile(fileName,dut,fw);
		System.out.println("deleteFile:finish");
		RpcResponseDTO<VersionVTO> rpcResult = vapRpcService.removeDeviceVersion(uid, dut, fw, fileName);
		if (!rpcResult.hasError())
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		else
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
	}

}
