package com.bhu.vas.rpc.facade;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.vto.DeviceGroupDetailVTO;
import com.bhu.vas.api.vto.BackendTaskVTO;
import com.bhu.vas.api.vto.DeviceGroupVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.devicegroup.facade.WifiDeviceGroupFacadeService;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class DeviceGroupUnitFacadeRpcService{
	//private final Logger logger = LoggerFactory.getLogger(DeviceGroupUnitFacadeRpcService.class);
/*	@Resource
	private WifiDeviceGroupService wifiDeviceGroupService;

	@Resource
	private WifiDeviceGroupRelationService wifiDeviceGroupRelationService;

	@Resource
	private WifiDeviceService wifiDeviceService;*/

	@Resource
	private DeliverMessageService deliverMessageService;

	@Resource
	private WifiDeviceGroupFacadeService wifiDeviceGroupFacadeService;

	/**
	 * 通过pid取得pid=pid的节点
	 * @param uid
	 * @param pid
	 * @return
	 */
	public RpcResponseDTO<TailPage<DeviceGroupVTO>> birthTree(Integer creator, long pid, int pageNo, int pageSize) {
		try{
			TailPage<DeviceGroupVTO> birthTree = wifiDeviceGroupFacadeService.birthTree(creator, pid, pageNo, pageSize);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(birthTree);
		}catch(BusinessI18nCodeException i18nex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	
	public RpcResponseDTO<DeviceGroupVTO> deviceGroupSave(Integer creator, long gid,long pid, String name) {
		try{
			DeviceGroupVTO result = wifiDeviceGroupFacadeService.deviceGroupSave(creator, gid, pid, name);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result);
		}catch(BusinessI18nCodeException i18nex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<DeviceGroupDetailVTO> deviceGroupDetail(Integer creator, long gid) {
		try{
			DeviceGroupDetailVTO result = wifiDeviceGroupFacadeService.deviceGroupDetail(creator, gid);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(result);
		}catch(BusinessI18nCodeException i18nex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	public RpcResponseDTO<Boolean> deviceGroupCleanUpByIds(Integer creator, String gids) {
		try{
			wifiDeviceGroupFacadeService.deviceGroupCleanUpByIds(creator, gids);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException i18nex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
		
	public RpcResponseDTO<Boolean> assignUserSearchCondition4DeviceGroup(Integer assignor,Long gid, String message, String desc){
		try{
			wifiDeviceGroupFacadeService.assignUserSearchCondition4DeviceGroup(assignor, gid, message, desc);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}catch(BusinessI18nCodeException i18nex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
		
		
/*		Map<String,Object> payload = new HashMap<String,Object>();
		
		UserSearchConditionDTO dto = new UserSearchConditionDTO(message, desc);
		String dtojson = JsonHelper.getJSONString(dto);
		
		Double exist_ts = UserSearchConditionSortedSetService.getInstance().zscore(uid, dtojson);
		if(exist_ts != null && exist_ts > 0){
			payload.put("ts", exist_ts);
			payload.put("stored", false);
		}else{
			long ts = System.currentTimeMillis();
			Long ret = UserSearchConditionSortedSetService.getInstance().storeUserSearchCondition(uid, ts, dtojson);
			if(ret != null && ret > 0){
				payload.put("ts", ts);
				payload.put("stored", true);
			}
		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(payload);*/
	}
	/**
	 * 后台生成群组任务
	 * @param uid
	 * @param gid
	 * @param opt
	 * @param subopt
	 * @param extparams
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public RpcResponseDTO<BackendTaskVTO> generateBackendTask(int uid, long gid, String opt, String subopt, String extparams) {
		try{
			BackendTaskVTO backEndTask = wifiDeviceGroupFacadeService.generateBackendTask(uid,gid,opt,subopt,extparams);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(backEndTask);
		}catch(BusinessI18nCodeException i18nex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 分页查询现有任务
	 * @param uid
	 * @param state
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public RpcResponseDTO<TailPage<BackendTaskVTO>> fetch_backendtask(int uid, String state, int pageNo, int pageSize) {
		try{
			TailPage<BackendTaskVTO> backEndTask = wifiDeviceGroupFacadeService.fetch_backendtask(uid,state,pageNo,pageSize);
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(backEndTask);
		}catch(BusinessI18nCodeException i18nex){
			return RpcResponseDTOBuilder.builderErrorRpcResponse(i18nex.getErrorCode(),i18nex.getPayload());
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	/*public RpcResponseDTO<Boolean> grant(Integer uid, long gid, String wifi_ids) {
		if(StringUtils.isEmpty(wifi_ids)) {
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}
		String[] arrayresids = wifi_ids.split(StringHelper.COMMA_STRING_GAP);
		//绑定设备
		List<WifiDeviceGroupRelation> lists = new ArrayList<WifiDeviceGroupRelation>();

		if (arrayresids.length > 0) {
			for (String mac : arrayresids) {
				if (StringHelper.isValidMac(mac)) {
					WifiDeviceGroupRelation wifiDeviceGroupRelation = new WifiDeviceGroupRelation();
					wifiDeviceGroupRelation.setId(new WifiDeviceGroupRelationPK(gid, mac));
					wifiDeviceGroupRelation.setCreated_at(new Date());
					lists.add(wifiDeviceGroupRelation);
				} else {

				}
			}
			try {
				wifiDeviceGroupRelationService.insertAll(lists);
			}catch (Exception e) {
				logger.error("DeviceGroupUnitFacadeRpcService grant %s",e);
			}

			//批量建立索引
			deliverMessageService.sendDeviceGroupCreateIndexMessage(wifi_ids, gid,
					WifiDeviceGroupAsynCreateIndexDTO.GROUP_INDEX_GRANT);

		}
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	}
	
	public RpcResponseDTO<Boolean> ungrant(Integer uid, long gid, String wifi_ids) {
		if(StringUtils.isEmpty(wifi_ids)) {
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
		}
		String[] arrayresids = wifi_ids.split(StringHelper.COMMA_STRING_GAP);

		List<WifiDeviceGroupRelationPK> lists = new ArrayList<WifiDeviceGroupRelationPK>();
		if (arrayresids.length > 0) {
			for(String mac :arrayresids){
				lists.add(new WifiDeviceGroupRelationPK(gid, mac));
			}
			wifiDeviceGroupRelationService.deleteByIds(lists);
		}

		//批量建立索引
		deliverMessageService.sendDeviceGroupCreateIndexMessage(wifi_ids, gid,
				WifiDeviceGroupAsynCreateIndexDTO.GROUP_INDEX_UNGRANT);

		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	}*/




//	private DeviceGroupVTO fromWifiDeviceGroup(WifiDeviceGroup dgroup){
//		DeviceGroupVTO vto = new DeviceGroupVTO();
//		vto.setGid(dgroup.getId());
//		vto.setName(dgroup.getName());
//		vto.setPid(dgroup.getPid());
//		if(dgroup.getPid() == 0){
//			vto.setPname("根节点");
//		}else{
//			WifiDeviceGroup parent_group = wifiDeviceGroupService.getById(dgroup.getPid());
//			vto.setPname((parent_group != null) ? parent_group.getName() : null);
//		}
//		vto.setChildren(dgroup.getChildren());
//		//dto.setHaschild(dgroup.isHaschild());
//		vto.setPath(dgroup.getPath());
//		//vto.setDevices(dgroup.getInnerModels());
//
//
//
//		List<WifiDevice> entitys = wifiDeviceService.findByIds(dgroup.getInnerModels(), true, true);
//		List<WifiDeviceVTO> vtos = new ArrayList<WifiDeviceVTO>();
//		WifiDeviceVTO wifiDeviceVTO = null;
//		for(WifiDevice entity : entitys){
//			if(entity != null){
//				//todo(bluesand):此处以后会跟搜索结果合并？现在用于群组菜单业务。
//				wifiDeviceVTO = new WifiDeviceVTO();
//				wifiDeviceVTO.setWid(entity.getId());
//				wifiDeviceVTO.setOl(entity.isOnline()? 1: 0);
//				wifiDeviceVTO.setOm(org.apache.commons.lang.StringUtils.isEmpty(entity.getOem_model())
//						? entity.getOrig_model() : entity.getOem_model());
//				wifiDeviceVTO.setWm(entity.getWork_mode());
//				wifiDeviceVTO.setCfm(entity.getConfig_mode());
//				wifiDeviceVTO.setRts(entity.getLast_reged_at().getTime());
//				wifiDeviceVTO.setCts(entity.getCreated_at().getTime());
//				wifiDeviceVTO.setOvd(org.apache.commons.lang.StringUtils.isEmpty(entity.getOem_vendor())
//						? entity.getOrig_vendor() : entity.getOem_vendor());
//				wifiDeviceVTO.setOesv(entity.getOem_swver());
//				wifiDeviceVTO.setDof(org.apache.commons.lang.StringUtils.isEmpty(entity.getRx_bytes())
//						? 0 : Long.parseLong(entity.getRx_bytes()));
//				wifiDeviceVTO.setUof(org.apache.commons.lang.StringUtils.isEmpty(entity.getTx_bytes())
//						? 0 : Long.parseLong(entity.getTx_bytes()));
//				wifiDeviceVTO.setIpgen(entity.isIpgen());
//				//如果是离线 计算离线时间
//				if(wifiDeviceVTO.getOl() == 0){
//					long logout_ts = entity.getLast_logout_at().getTime();
//					wifiDeviceVTO.setOfts(logout_ts);
//					wifiDeviceVTO.setOftd(System.currentTimeMillis() - logout_ts);
//				}
//				vtos.add(wifiDeviceVTO);
//			}
//
//		}
//
//		vto.setDetail_devices(vtos);
//		return vto;
//	}



	/*private DeviceGroupVTO fromWifiDeviceGroupBirthTree(WifiDeviceGroup dgroup) {
		DeviceGroupVTO vto = new DeviceGroupVTO();
		vto.setGid(dgroup.getId());
		vto.setName(dgroup.getName());
		vto.setPid(dgroup.getPid());
		if(dgroup.getPid() == 0){
			vto.setPname("根节点");
		}else{
			WifiDeviceGroup parent_group = wifiDeviceGroupService.getById(dgroup.getPid());
			vto.setPname((parent_group != null) ? parent_group.getName() : null);
		}
		vto.setChildren(dgroup.getChildren());
		vto.setPath(dgroup.getPath());

		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("gid", dgroup.getId());

		int total = wifiDeviceGroupRelationService.countByCommonCriteria(mc);

		vto.setDevice_count(total);

		return vto;

	}
	private int countDevicesByGroupId(long gid) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("gid", gid);
		return  wifiDeviceGroupRelationService.countByCommonCriteria(mc);
	}*/
}
