package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.bhu.vas.api.rpc.devices.model.WifiDevice;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGroupRelation;
import com.bhu.vas.api.rpc.devices.model.pk.WifiDeviceGroupRelationPK;
import com.bhu.vas.api.vto.DeviceGroupVTO;
import com.bhu.vas.api.vto.WifiDeviceVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.asyn.spring.builder.DeliverMessage;
import com.bhu.vas.business.ds.device.facade.WifiDeviceGroupFacadeService;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupRelationService;
import com.bhu.vas.business.ds.device.service.WifiDeviceService;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.Page;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.rpc.RpcResponseDTO;
import com.bhu.vas.api.rpc.RpcResponseDTOBuilder;
import com.bhu.vas.api.rpc.devices.model.WifiDeviceGroup;
import com.bhu.vas.business.ds.device.service.WifiDeviceGroupService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class DeviceGroupUnitFacadeRpcService{

	private final Logger logger = LoggerFactory.getLogger(DeviceGroupUnitFacadeRpcService.class);
	@Resource
	private WifiDeviceGroupService wifiDeviceGroupService;

	@Resource
	private WifiDeviceGroupRelationService wifiDeviceGroupRelationService;

	@Resource
	private WifiDeviceService wifiDeviceService;

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
	public TailPage<DeviceGroupVTO> birthTree(Integer uid, int pid, int pageNo, int pageSize) {
		//if(pid == null) pid = 0;
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("pid", pid);
		int total = wifiDeviceGroupService.countByCommonCriteria(mc);

		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		List<WifiDeviceGroup> groups = wifiDeviceGroupService.findModelByModelCriteria(mc);

    	List<DeviceGroupVTO> result = new ArrayList<DeviceGroupVTO>();
    	for(WifiDeviceGroup group:groups){
    		result.add(fromWifiDeviceGroupBirthTree(group));
    	}


		return new CommonPage<DeviceGroupVTO>(pageNo, pageSize, total,result);
	}

	
	public RpcResponseDTO<DeviceGroupVTO> save(Integer uid, int gid,int pid, String name) {
		//if(gid == null) gid = 0;
		//if(pid == null) pid = 0;
		WifiDeviceGroup dgroup= null;
		if(gid == 0){//新建一个组

			if (pid >0) {
				WifiDeviceGroup pgroup = wifiDeviceGroupService.getById(pid);
				if (pgroup == null) {
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST_PARENT);
				}

				String path = pgroup.getPath();
				if (path.split("/").length >= 3) {
					//节点已上限
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_TOO_LONG);
				}

				if (countDevicesByGroupId(pid) > 0) {
					//父节点上有数据，无法添加
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_EXIST_CHILDREN);
				}
			}

			dgroup = new WifiDeviceGroup();
			dgroup.setPid(pid);
			dgroup.setName(name);
			dgroup.setCreator(uid);
			dgroup.setUpdator(uid);
			dgroup = wifiDeviceGroupService.insert(dgroup);

		}else{
			dgroup = wifiDeviceGroupService.getById(gid);

			if (pid >0) {
				WifiDeviceGroup pgroup = wifiDeviceGroupService.getById(pid);
				if (pgroup == null) {
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST_PARENT);
				}

				String path = pgroup.getPath();
				if (path.split("/").length >= 3) {
					//节点已上限
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_TOO_LONG);
				}

				if (countDevicesByGroupId(pid) > 0) {
					//父节点上有数据，无法添加
					return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_EXIST_CHILDREN);
				}
			}

			int oldPid = dgroup.getPid();
			String oldPath = dgroup.getPath();
			if(oldPid != pid){
				//pid变化了 所有此gid的子节点全部迁移，并重新生成relationpath
				//第一步：获取此节点下的所有子节点，包括子节点的子节点
				List<WifiDeviceGroup> allByPath = wifiDeviceGroupService.fetchAllByPath(oldPath,false);
				dgroup.setPid(pid);
				dgroup.setName(name);
				dgroup.setPath(wifiDeviceGroupService.generateRelativePath(dgroup));
				dgroup.setUpdator(uid);
				for(WifiDeviceGroup child:allByPath){
					//String child_old_path = child.getPath();
					child.setPath(StringUtils.replace(child.getPath(), oldPath, dgroup.getPath()));
					//System.out.println(child_old_path+" "+ oldPath+" "+dgroup.getPath()+" "+child.getPath());
					child.setUpdator(uid);
					wifiDeviceGroupService.update(child);
				}
				dgroup = wifiDeviceGroupService.update(dgroup);
				/*System.out.println("~~~~~~~~~~"+allByPath.size());
				if(allByPath.isEmpty()){//没有子节点
					dgroup.setPid(pid);
					//dgroup.setHaschild(false);
					dgroup.setName(name);
					dgroup.setPath(wifiDeviceGroupService.generateRelativePath(dgroup));
					dgroup.setUpdator(uid);
					dgroup = wifiDeviceGroupService.update(dgroup);
				}else{
					dgroup.setPid(pid);
					//dgroup.setHaschild(true);
					dgroup.setName(name);
					dgroup.setPath(wifiDeviceGroupService.generateRelativePath(dgroup));
					dgroup.setUpdator(uid);
					for(WifiDeviceGroup child:allByPath){
						//String child_old_path = child.getPath();
						child.setPath(StringUtils.replace(child.getPath(), oldPath, dgroup.getPath()));
						//System.out.println(child_old_path+" "+ oldPath+" "+dgroup.getPath()+" "+child.getPath());
						child.setUpdator(uid);
						wifiDeviceGroupService.update(child);
					}
					dgroup = wifiDeviceGroupService.update(dgroup);
				}*/
				{//oldPid的节点需要判定hanchild是否为true
					if(oldPid > 0){
						WifiDeviceGroup parent_group = wifiDeviceGroupService.getById(oldPid);
						if(parent_group != null){
							parent_group.setChildren(parent_group.getChildren()-1);
							wifiDeviceGroupService.update(parent_group);
							/*int count = wifiDeviceGroupService.countAllByPath(parent_group.getPath(), false);
							if(count == 0 && parent_group.isHaschild()){
								parent_group.setHaschild(false);
								parent_group.setUpdator(uid);
								wifiDeviceGroupService.update(parent_group);
							}*/
						}
					}
				}
			}else{
				dgroup.setName(name);
				dgroup.setUpdator(uid);
				dgroup = wifiDeviceGroupService.update(dgroup);
			}
		}
		
		//其parent节点的haschild = true
		if(pid != 0){
			WifiDeviceGroup parent_group = wifiDeviceGroupService.getById(pid);
			if(parent_group != null){
				parent_group.setChildren(parent_group.getChildren()+1);
				wifiDeviceGroupService.update(parent_group);
			}
			
			/*if(parent_group != null && !parent_group.isHaschild()){
				parent_group.setHaschild(true);
				parent_group.setUpdator(uid);
				wifiDeviceGroupService.update(parent_group);
			}*/
		}
		
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(fromWifiDeviceGroupBirthTree(dgroup));
	}


	public RpcResponseDTO<DeviceGroupVTO> detail(int uid, int gid, int pageNo, int pageSize) {
		WifiDeviceGroup dgroup = wifiDeviceGroupService.getById(gid);
		if(dgroup != null){
			return RpcResponseDTOBuilder.builderSuccessRpcResponse(fromWifiDeviceGroup(dgroup, pageNo, pageSize));
		}else{
			return RpcResponseDTOBuilder.builderErrorRpcResponse(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST);
		}
	}

	public RpcResponseDTO<Boolean> remove(Integer uid, String gids) {

		wifiDeviceGroupFacadeService.cleanUpByIds(uid,gids);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	}
	
	public RpcResponseDTO<Boolean> grant(Integer uid, int gid, String wifi_ids, String group_ids) {
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
			deliverMessageService.sendDeviceGroupCreateIndexMessage(wifi_ids, group_ids);

		}



		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	}
	
	public RpcResponseDTO<Boolean> ungrant(Integer uid, int gid, String wifi_ids, String group_ids) {
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
		deliverMessageService.sendDeviceGroupCreateIndexMessage(wifi_ids, group_ids);

		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
	}

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



	private DeviceGroupVTO fromWifiDeviceGroupBirthTree(WifiDeviceGroup dgroup) {
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


	private DeviceGroupVTO fromWifiDeviceGroup(WifiDeviceGroup dgroup, int pageNo, int pageSize){
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

		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);

		List<WifiDeviceGroupRelationPK> ids = wifiDeviceGroupRelationService.findIdsByModelCriteria(mc);

		List<String> deviceIds = new ArrayList<String>();
		for (WifiDeviceGroupRelationPK pk : ids) {
			deviceIds.add(pk.getMac());
		}

		List<WifiDevice> entitys = wifiDeviceService.findByIds(deviceIds, true, true);
		List<WifiDeviceVTO> vtos = new ArrayList<WifiDeviceVTO>();
		WifiDeviceVTO wifiDeviceVTO = null;
		for(WifiDevice entity : entitys){
			if(entity != null){
				//todo(bluesand):此处以后会跟搜索结果合并？现在用于群组菜单业务。
				wifiDeviceVTO = new WifiDeviceVTO();
				wifiDeviceVTO.setWid(entity.getId());
				wifiDeviceVTO.setOl(entity.isOnline()? 1: 0);
				wifiDeviceVTO.setOm(org.apache.commons.lang.StringUtils.isEmpty(entity.getOem_model())
						? entity.getOrig_model() : entity.getOem_model());
				wifiDeviceVTO.setWm(entity.getWork_mode());
				wifiDeviceVTO.setCfm(entity.getConfig_mode());
				wifiDeviceVTO.setRts(entity.getLast_reged_at().getTime());
				wifiDeviceVTO.setCts(entity.getCreated_at().getTime());
				wifiDeviceVTO.setOvd(org.apache.commons.lang.StringUtils.isEmpty(entity.getOem_vendor())
						? entity.getOrig_vendor() : entity.getOem_vendor());
				wifiDeviceVTO.setOesv(entity.getOem_swver());
				wifiDeviceVTO.setDof(org.apache.commons.lang.StringUtils.isEmpty(entity.getRx_bytes())
						? 0 : Long.parseLong(entity.getRx_bytes()));
				wifiDeviceVTO.setUof(org.apache.commons.lang.StringUtils.isEmpty(entity.getTx_bytes())
						? 0 : Long.parseLong(entity.getTx_bytes()));
				wifiDeviceVTO.setIpgen(entity.isIpgen());
				//如果是离线 计算离线时间
				if(wifiDeviceVTO.getOl() == 0){
					long logout_ts = entity.getLast_logout_at().getTime();
					wifiDeviceVTO.setOfts(logout_ts);
					wifiDeviceVTO.setOftd(System.currentTimeMillis() - logout_ts);
				}
				vtos.add(wifiDeviceVTO);
			}

		}

		vto.setPage_devices(new CommonPage<WifiDeviceVTO>(pageNo, pageSize, total, vtos));

		return vto;
	}

	
	private int countDevicesByGroupId(int gid) {
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse(" 1=1 ").andColumnEqualTo("gid", gid);
		return  wifiDeviceGroupRelationService.countByCommonCriteria(mc);
	}
}
