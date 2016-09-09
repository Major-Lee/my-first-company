package com.bhu.vas.web.group;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.charging.vto.DeviceGroupPaymentStatisticsVTO;
import com.bhu.vas.api.rpc.devices.iservice.IDeviceRestRpcService;
import com.bhu.vas.api.rpc.tag.iservice.ITagRpcService;
import com.bhu.vas.api.rpc.tag.vto.GroupCountOnlineVTO;
import com.bhu.vas.api.rpc.tag.vto.GroupUsersStatisticsVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupHandsetDetailVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupRankUsersVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupVTO;
import com.bhu.vas.api.rpc.task.model.WifiDeviceDownTask;
import com.bhu.vas.msip.cores.web.mvc.spring.BaseController;
import com.bhu.vas.msip.cores.web.mvc.spring.helper.SpringMVCHelper;
import com.bhu.vas.validate.ValidateService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.jdo.ResponseError;
import com.smartwork.msip.jdo.ResponseSuccess;

@Controller
@RequestMapping("/group")
public class GroupController extends BaseController{
    @Resource
    private ITagRpcService tagRpcService;
    
    @Resource
    private IDeviceRestRpcService deviceRestRpcService;
    /**
     * 新建或修改分组
     * @param request
     * @param response
     * @param uid
     * @param gid
     * @param pid
     * @param name
     */
    @ResponseBody()
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    public void tag_Group_Save(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false,defaultValue = "0", value = "gid") int gid,
            @RequestParam(required = false, defaultValue = "0", value = "pid") int pid,
            @RequestParam(required = true) String name) {
    	RpcResponseDTO<TagGroupVTO> rpcResult = tagRpcService.saveTreeNode(uid, gid, pid, name);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    } 
    
    /**
     * 批量删除分组
     * @param request
     * @param response
     * @param uid
     * @param gids
     */
    @ResponseBody()
    @RequestMapping(value = "/del", method = {RequestMethod.POST})
    public void tag_Group_Del_Node(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String gids) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.delNode(uid, gids);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    /**
     * 分页查询
     * @param request
     * @param response
     * @param uid
     * @param pid
     * @param pageNo
     * @param pageSize
     */
    @ResponseBody()
    @RequestMapping(value = "/fetch", method = {RequestMethod.POST})
    public void tag_Group_Fetch(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false, defaultValue = "0", value = "pid") int pid,
    	    @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
    	    @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {
    	RpcResponseDTO<TailPage<TagGroupVTO>> rpcResult = tagRpcService.fetchChildGroup(uid, pid, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    /**
     * 添加分组的验证
     * @param request
     * @param response
     * @param uid
     * @param gid
     * @param pid
     * @param name
     */
    @ResponseBody()
    @RequestMapping(value = "/check", method = {RequestMethod.POST})
    public void tag_Group_check(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false,defaultValue = "0", value = "gid") int gid,
            @RequestParam(required = false, defaultValue = "0", value = "pid") int pid,
            @RequestParam(required = true) String name) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.CanSaveNode(uid, gid, pid, name);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    

    @ResponseBody()
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public void save_Devices_Group(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) int gid,
            @RequestParam(required = true) String path,
            @RequestParam(required = true) String macs) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.saveDevices2Group(uid, gid, path, macs);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    } 
    
    @ResponseBody()
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public void modify(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) int gid,
            @RequestParam(required = false,defaultValue= "0",value = "newGid") int newGid,
            @RequestParam(required = false,defaultValue= "0/",value = "newPath") String newPath,
            @RequestParam(required = true) String macs) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.modifyDeciceWithNode(uid, gid, newGid,newPath, macs);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    @ResponseBody()
    @RequestMapping(value = "/config", method = {RequestMethod.POST})
    public void cmd_generate(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) String message,
            @RequestParam(required = true) String opt,
            @RequestParam(required = false,defaultValue = "00") String subopt,
            @RequestParam(required = false) String extparams,
			@RequestParam(required = false, defaultValue=WifiDeviceDownTask.Task_LOCAL_CHANNEL) String channel,
			@RequestParam(required = false) String channel_taskid) {
    	RpcResponseDTO<Boolean> rpcResult = tagRpcService.batchGroupDownCmds(uid, message, opt, subopt, extparams,channel,channel_taskid);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    /**
	 * 修改用户共享网络配置并应用接口
	 * @param request
	 * @param response
	 * @param uid
	 * @param sharenetwork_type
	 * @param mac
	 */
	@ResponseBody()
	@RequestMapping(value="/snk/takeeffect",method={RequestMethod.POST})	
	public void takeeffect(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = true) Integer uid,
			@RequestParam(required = true) String message,
			@RequestParam(required = false,defaultValue= "SafeSecure",value="snk_type") String sharenetwork_type,
			@RequestParam(required = false,defaultValue= "0001",value="tpl") String template,
			@RequestParam(required = false,defaultValue = "true") boolean on) {
		ResponseError validateError = ValidateService.validateParamValueEmpty("message",message);
		if(validateError != null){
			SpringMVCHelper.renderJson(response, validateError);
			return;
		}
		RpcResponseDTO<Boolean> rpcResult = tagRpcService.batchGroupSnkTakeEffectNetworkConf(uid,message,on, sharenetwork_type,template);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
	}
	
    @ResponseBody()
    @RequestMapping(value = "/count/online", method = {RequestMethod.POST})
    public void count_online(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = false) String gids) {
    	
    	List<GroupCountOnlineVTO> list = tagRpcService.groupsStatsOnline(uid, gids);
    	SpringMVCHelper.renderJson(response, ResponseSuccess.embed(list));
    }
    
    @ResponseBody()
    @RequestMapping(value = "/gains/stats", method = {RequestMethod.POST})
    public void group_gains_stats(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
    	    @RequestParam(required = false) String gids,
    	    @RequestParam(required = false) String paths) {
    	RpcResponseDTO<List<DeviceGroupPaymentStatisticsVTO>> rpcResult = tagRpcService.groupsGainsStatistics(uid, gids, paths);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }

    /**
	 * 统计分组连接用户数
	 * @param gid 分组id
	 * @param timeStr 获取数据的时间 格式yyyyMMdd
	 */
    @ResponseBody()
    @RequestMapping(value = "/count/users", method = {RequestMethod.POST})
    public void group_count_users(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) int gid,
    	    @RequestParam(required = true) long time) {
    	RpcResponseDTO<GroupUsersStatisticsVTO> rpcResult = tagRpcService.groupUsersStatistics(gid, time);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    /**
	 * 统计分组连接用户数
	 * @param gid 分组id
	 * @param timeStr 获取数据的时间 格式yyyyMMdd
	 */
    @ResponseBody()
    @RequestMapping(value = "/users", method = {RequestMethod.POST})
    public void group_users_detail(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) int gid,
    	    @RequestParam(required = false) long beginTime,
    	    @RequestParam(required = false) long endTime,
    	    @RequestParam(required = false) boolean filter,
    	    @RequestParam(required = false) int count,
    	    @RequestParam(required = false) String mobileno,
    	    @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
    	    @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {
    	RpcResponseDTO<TailPage<TagGroupHandsetDetailVTO>> rpcResult = tagRpcService.groupUsersDetail(gid, beginTime, endTime,filter,count,mobileno, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    

    @ResponseBody()
    @RequestMapping(value = "/user/detail", method = {RequestMethod.POST})
    public void group_user_detail(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true) int uid,
            @RequestParam(required = true) int gid,
    	    @RequestParam(required = true) String hdmac,
    	    @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
    	    @RequestParam(required = false, defaultValue = "20", value = "ps") int pageSize) {
    	RpcResponseDTO<List<Date>> rpcResult = tagRpcService.groupUserDetail(gid,hdmac, pageNo, pageSize);
		if(!rpcResult.hasError()){
			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
		}else{
			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
		}
    }
    
    /**
  	 * 统计分组连接排名
  	 * @param gid 分组id
  	 * @param timeStr 获取数据的时间 格式yyyyMMdd
  	 */
      @ResponseBody()
      @RequestMapping(value = "/rank/users", method = {RequestMethod.POST})
      public void group_rank_users(
              HttpServletRequest request,
              HttpServletResponse response,
              @RequestParam(required = true) int uid,
              @RequestParam(required = true) int gid,
              @RequestParam(required = false, defaultValue = "1", value = "pn") int pageNo,
              @RequestParam(required = false, defaultValue = "5", value = "ps") int pageSize) {
      	RpcResponseDTO<TailPage<TagGroupRankUsersVTO>> rpcResult = tagRpcService.groupRankUsers(uid,gid, pageNo,pageSize);
  		if(!rpcResult.hasError()){
  			SpringMVCHelper.renderJson(response, ResponseSuccess.embed(rpcResult.getPayload()));
  		}else{
  			SpringMVCHelper.renderJson(response, ResponseError.embed(rpcResult));
  		}
      }
}
