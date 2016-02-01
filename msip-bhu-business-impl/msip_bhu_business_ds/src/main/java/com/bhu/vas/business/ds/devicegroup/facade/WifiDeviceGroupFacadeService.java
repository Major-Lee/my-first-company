package com.bhu.vas.business.ds.devicegroup.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bhu.vas.api.rpc.devicegroup.model.WifiDeviceBackendTask;
import com.bhu.vas.api.rpc.devicegroup.model.WifiDeviceGroup;
import com.bhu.vas.api.rpc.devicegroup.model.WifiDeviceGroupSearchCondition;
import com.bhu.vas.api.rpc.user.dto.UserSearchConditionDTO;
import com.bhu.vas.api.vto.BackendTaskVTO;
import com.bhu.vas.api.vto.DeviceGroupDetailVTO;
import com.bhu.vas.api.vto.DeviceGroupVTO;
import com.bhu.vas.business.ds.devicegroup.service.WifiDeviceBackendTaskService;
import com.bhu.vas.business.ds.devicegroup.service.WifiDeviceGroupSearchConditionService;
import com.bhu.vas.business.ds.devicegroup.service.WifiDeviceGroupService;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.criteria.PerfectCriteria.Criteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * Created by bluesand on 8/4/15.
 *
 * 避免 wifiDeviceGroupService 与 wifiDeviceGroupRelationService 同级service相互调用
 */
@Service
public class WifiDeviceGroupFacadeService {

    /**
     * 定义<=10000的群组都为灰度群组
     */
/*    private final static Integer GRAY_GROUP_ID_PARENT = 10000;
    private final static Integer GRAY_GROUP_ID_ONE = 9999;
    private final static Integer GRAY_GROUP_ID_TWO = 9998;
    private final static Integer GRAY_GROUP_ID_THREE = 9997;*/

    @Resource
    private WifiDeviceGroupService wifiDeviceGroupService;
    
    @Resource
    private WifiDeviceGroupSearchConditionService wifiDeviceGroupSearchConditionService;
    
    @Resource
    private WifiDeviceBackendTaskService wifiDeviceBackendTaskService; 
    //@Resource
    //private WifiDeviceGroupRelationService wifiDeviceGroupRelationService;
    
	/**
	 * 通过pid取得pid=pid的节点
	 * @param creator 可以为空
	 * @param pid
	 * @return
	 */
	public TailPage<DeviceGroupVTO> birthTree(Integer creator, long pid, int pageNo, int pageSize) {
		//if(pid == null) pid = 0;
		ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		createCriteria.andSimpleCaulse(" 1=1 ").andColumnEqualTo("pid", pid);
		/*if(creator != null){
			createCriteria.andColumnEqualTo("creator", creator);
		}*/
		//int total = wifiDeviceGroupService.countByCommonCriteria(mc);
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		TailPage<WifiDeviceGroup> tailPages = wifiDeviceGroupService.findModelTailPageByModelCriteria(mc);
    	List<DeviceGroupVTO> result = new ArrayList<DeviceGroupVTO>();
    	for(WifiDeviceGroup group:tailPages){
    		result.add(fromWifiDeviceGroupBirthTree(group));
    	}
		return new CommonPage<DeviceGroupVTO>(pageNo, pageSize, tailPages.getTotalItemsCount(),result);
	}
	
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
		//vto.setChildren(dgroup.getChildren());
		vto.setParent(dgroup.getChildren()>0);
		vto.setPath(dgroup.getPath());
		//ModelCriteria mc = new ModelCriteria();
		//mc.createCriteria().andColumnEqualTo("gid", dgroup.getId());
		//int total = wifiDeviceGroupRelationService.countByCommonCriteria(mc);
		//vto.setDevice_count(total);
		return vto;
	}
	
	public static int countSubString(String origin,String sub){
		Pattern p = Pattern.compile(sub,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(origin);
        int count = 0;
        while(m.find()){
              count ++;
        }
        return count;
	}
	
	/**
	 * 设备群组新增和更新
	 * 需要验证树层级关系，目前不能超过3级
	 * @param creator
	 * @param gid
	 * @param pid
	 * @param name
	 * @return
	 */
	public DeviceGroupVTO deviceGroupSave(Integer creator, long gid,long pid, String name){
		if(pid < 0){
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST_PARENT,new String[]{String.valueOf(pid)});
		}
		if(pid > 0 ){//如果pid = 0 代表根节点下的一级子节点，无需验证树层级约束
			WifiDeviceGroup pgroup = wifiDeviceGroupService.getById(pid);
			if (pgroup == null) {
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST_PARENT,new String[]{String.valueOf(pid)});
			}
			int count = countSubString(pgroup.getPath(),"/");
			if (count >= 3) {
				//节点已上限
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_GROUP_TOO_LONG);
			}
		}
		//WifiDeviceGroup pgroup = wifiDeviceGroupService.getById(pid);
		WifiDeviceGroup dgroup= null;
		if(gid == 0){//新建一个组
			dgroup = new WifiDeviceGroup();
			dgroup.setPid(pid);
			dgroup.setName(name);
			dgroup.setCreator(creator);
			dgroup.setUpdator(creator);
			dgroup = wifiDeviceGroupService.insert(dgroup);
		}else{
			dgroup = wifiDeviceGroupService.getById(gid);
			if (dgroup == null) {
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST,new String[]{String.valueOf(gid)});
			}
			long oldPid = dgroup.getPid();
			String oldPath = dgroup.getPath();
			if(oldPid != pid){//父节点变更了
				//pid变化了 所有此gid的子节点全部迁移，并重新生成relationpath
				//第一步：获取此节点下的所有子节点，包括子节点的子节点
				List<WifiDeviceGroup> allByPath = wifiDeviceGroupService.fetchAllByPath(oldPath,false);
				dgroup.setPid(pid);
				dgroup.setName(name);
				dgroup.setPath(wifiDeviceGroupService.generateRelativePath(dgroup));
				dgroup.setUpdator(creator);
				for(WifiDeviceGroup child:allByPath){
					//String child_old_path = child.getPath();
					child.setPath(StringUtils.replace(child.getPath(), oldPath, dgroup.getPath()));
					//System.out.println(child_old_path+" "+ oldPath+" "+dgroup.getPath()+" "+child.getPath());
					child.setUpdator(creator);
					wifiDeviceGroupService.update(child);
				}
				dgroup = wifiDeviceGroupService.update(dgroup);
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
				dgroup.setUpdator(creator);
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
		}
		return fromWifiDeviceGroupBirthTree(dgroup);
	}

    /**
     * 还需要删除gid及其所有的子节点
     * @param uid
     * @param gids
     */
    public void deviceGroupCleanUpByIds(Integer uid, String gids){
        String[] arrayresids = gids.split(StringHelper.COMMA_STRING_GAP);
        for(String residstr : arrayresids){
            Long resid = new Long(residstr);

            WifiDeviceGroup group = wifiDeviceGroupService.getById(resid);
            if(group != null){
                //int gid = group.getId().intValue();
                long pid = group.getPid();
                wifiDeviceGroupService.removeAllByPath(group.getPath(), true);
                //removeAllByPathStepByStep(group.getPath(),true);
                //判定每个gid的parentid是否为hanchild
                if(pid != 0){
					/*try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
                    WifiDeviceGroup parent_group = wifiDeviceGroupService.getById(pid);
                    parent_group.setChildren(parent_group.getChildren()-1);
                    wifiDeviceGroupService.update(parent_group);
					/*if(parent_group.getChildren() > 1){

					}*/
					/*int count  = countAllByPath(parent_group.getPath(),false);
					System.out.println("~~~~~~~~~~~~count:"+count);
					if(count == 0 && parent_group.getChildren() > 0){
						parent_group.setChildren(0);
						this.update(parent_group);
					}*/

                }else{//pid == 0 本身是根节点，被删除后，无需动作
                    ;
                }
                //删除绑定的设备
                /*ModelCriteria mc = new ModelCriteria();
                mc.createCriteria().andColumnEqualTo("gid", resid);
                wifiDeviceGroupRelationService.deleteByCommonCriteria(mc);*/
            }
        }
    }

    public DeviceGroupDetailVTO deviceGroupDetail(int uid, long gid) {
    	WifiDeviceGroup dgroup = wifiDeviceGroupService.getById(gid);
		if(dgroup != null){
			return (toDeviceGroupDetailVTO(dgroup));
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST,new String[]{String.valueOf(gid)});
		}
    }
    
    public UserSearchConditionDTO assignUserSearchCondition4DeviceGroup(int assignor,long gid,String message,String desc){
    	if (gid <= 0 ) {
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST,new String[]{String.valueOf(gid)});
		}
    	WifiDeviceGroupSearchCondition searchCondition = wifiDeviceGroupSearchConditionService.getById(gid);
    	boolean newed = false;
		if(searchCondition == null){
			searchCondition = new WifiDeviceGroupSearchCondition();
			searchCondition.setId(gid);
			newed = true;
		}
		UserSearchConditionDTO dto = new UserSearchConditionDTO(message, desc);
		dto.setTs(System.currentTimeMillis());
		searchCondition.replaceInnerModel(dto);
		if(newed){
			wifiDeviceGroupSearchConditionService.insert(searchCondition);
		}else{
			wifiDeviceGroupSearchConditionService.update(searchCondition);
		}
		return dto;
    }
    
    public List<WifiDeviceBackendTask> fetchRecentPendingBackendTask(int pageNo,int pageSize){
    	return Collections.emptyList();
    }

    /*public Boolean remove(Integer uid, String gids) {
    	this.cleanUpByIds(uid,gids);
		return RpcResponseDTOBuilder.builderSuccessRpcResponse(Boolean.TRUE);
    }*/
    
    public Boolean grant(Integer uid, long gid) {
    	return null;
    }
    
    private DeviceGroupDetailVTO toDeviceGroupDetailVTO(WifiDeviceGroup dgroup){
    	DeviceGroupDetailVTO vto = new DeviceGroupDetailVTO();
		vto.setGid(dgroup.getId());
		vto.setName(dgroup.getName());
		vto.setPid(dgroup.getPid());
		if(dgroup.getPid() == 0){
			vto.setPname("根节点");
		}else{
			WifiDeviceGroup parent_group = wifiDeviceGroupService.getById(dgroup.getPid());
			vto.setPname((parent_group != null) ? parent_group.getName() : null);
		}
		//vto.setChildren(dgroup.getChildren());
		vto.setParent(dgroup.getChildren()>0);
		vto.setPath(dgroup.getPath());
		WifiDeviceGroupSearchCondition conditon = wifiDeviceGroupSearchConditionService.getById(dgroup.getId());
		if(conditon != null){
			vto.setCondition(conditon.getExtension_content());
		}
		return vto;
	}
    
    private DeviceGroupVTO fromWifiDeviceGroup(WifiDeviceGroup dgroup){
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
		//vto.setChildren(dgroup.getChildren());
		vto.setParent(dgroup.getChildren()>0);
		vto.setPath(dgroup.getPath());

		/*ModelCriteria mc = new ModelCriteria();
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

		}*/
		//vto.setPage_devices(new CommonPage<WifiDeviceVTO>(pageNo, pageSize, total, vtos));
		return vto;
	}
    
	public TailPage<BackendTaskVTO> fetch_backendtask(int uid, String state, int pageNo, int pageSize) {
				
		ModelCriteria mc = new ModelCriteria();
		Criteria createCriteria = mc.createCriteria();
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		TailPage<WifiDeviceBackendTask> tailPages = null;
		
		if (state.equals("ALL")) {
			createCriteria.andSimpleCaulse(" 1=1");
			tailPages = wifiDeviceBackendTaskService.findModelTailPageByModelCriteria(mc);
		}else{
			createCriteria.andColumnEqualTo("state", state);
			tailPages = wifiDeviceBackendTaskService.findModelTailPageByModelCriteria(mc);
		}

		List<BackendTaskVTO> result = new ArrayList<BackendTaskVTO>();
    	for(WifiDeviceBackendTask group:tailPages){
    		result.add(fromBackendTask(group));
    	}
		return new CommonPage<BackendTaskVTO>(pageNo, pageSize, tailPages.getTotalItemsCount(),result);	
	}
    
	private BackendTaskVTO fromBackendTask(WifiDeviceBackendTask dgroup) {
		BackendTaskVTO vto = new BackendTaskVTO();
		vto.setId(dgroup.getId());
		vto.setGid(dgroup.getGid());
		vto.setUid(dgroup.getUid());
		vto.setTotal(dgroup.getTotal());
		vto.setState(dgroup.getState());
		vto.setDescription(dgroup.getDescription());
		
		return vto;
	}
	
	public BackendTaskVTO generateBackendTask(int uid, long gid, String opt, String subopt, String extparams) {
		WifiDeviceBackendTask entity = new WifiDeviceBackendTask();
		
		entity.setUid(uid);
		entity.setGid(gid);
		entity.setOpt(opt);
		entity.setSubopt(subopt);
		entity.setDescription(extparams);
		wifiDeviceBackendTaskService.insert(entity);
		
		return fromBackendTask(entity);
	}
	
    public static void main(String[] argv){
    	System.out.println(WifiDeviceGroupFacadeService.countSubString("afa/1sfsfd/gdgsdfasd/fa/1/s/fd","/"));
    }




}
