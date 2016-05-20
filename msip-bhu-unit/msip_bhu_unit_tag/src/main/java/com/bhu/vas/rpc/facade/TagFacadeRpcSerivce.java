package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.api.rpc.tag.model.TagGroup;
import com.bhu.vas.api.rpc.tag.model.TagGroupRelation;
import com.bhu.vas.api.rpc.tag.model.TagName;
import com.bhu.vas.api.rpc.tag.vto.TagGroupVTO;
import com.bhu.vas.api.rpc.tag.vto.TagNameVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.tag.service.TagGroupService;
import com.bhu.vas.business.ds.tag.service.TagNameService;
import com.bhu.vas.business.search.service.increment.WifiDeviceStatusIndexIncrementService;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * 
 * @author xiaowei by 16/04/12
 */
@Service
public class TagFacadeRpcSerivce {

	@Resource
	private TagNameService tagNameService;

	@Resource
	private TagDevicesService tagDevicesService;

	@Resource
	private WifiDeviceStatusIndexIncrementService wifiDeviceStatusIndexIncrementService;

	@Resource
	private AsyncDeliverMessageService asyncDeliverMessageService;
	

	/*
	 * @Resource private DeliverMessageService deliverMessageService;
	 */

	@Resource
	private TagGroupService tagGroupService;

	@Resource
	private TagGroupRelationService tagGroupRelationService;

	private void addTag(int uid, String tag) {

		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("tag", tag.trim());
		int count = tagNameService.countByModelCriteria(mc);
		if (count == 0) {
			TagName tagName = new TagName();
			tagName.setTag(tag);
			tagName.setOperator(uid);
			tagNameService.insert(tagName);
		}
	}

	public void bindTag(int uid, String mac, String tag) throws Exception {
		boolean filter = StringFilter(tag);

		if (tag != null && filter) {

			TagDevices tagDevices = tagDevicesService.getOrCreateById(mac);

			tagDevices.setLast_operator(uid);

			String[] arrTemp = tag.split(",");

			for (String newTag : arrTemp) {
				addTag(uid, newTag);
			}
			tagDevices.replaceInnerModels(ArrayHelper.toSet(arrTemp));
			tagDevicesService.update(tagDevices);
			wifiDeviceStatusIndexIncrementService.bindDTagsUpdIncrement(mac, tagDevices.getTag2ES());
		} else {
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		}
	}

	public void delTag(int uid, String mac) throws Exception {
		TagDevices tagDevices = tagDevicesService.getById(mac);
		if (tagDevices != null) {
			wifiDeviceStatusIndexIncrementService.bindDTagsUpdIncrement(mac, "");
			tagDevicesService.deleteById(mac);
		} else {
			throw new Exception();
		}
	}

	public TailPage<TagNameVTO> fetchTag(int pageNo, int pageSize) {

		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse("1=1");
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);

		List<TagName> tailPages = tagNameService.findModelByCommonCriteria(mc);

		List<TagNameVTO> result = new ArrayList<TagNameVTO>();
		for (TagName tagName : tailPages) {
			TagNameVTO vto = new TagNameVTO();
			vto.setTagName(tagName.getTag());
			result.add(vto);
		}

		return new CommonPage<TagNameVTO>(pageNo, pageSize, result.size(), result);
	}

	public boolean StringFilter(String str) {
		String regex = "^[a-zA-Z0-9,_\u4e00-\u9fa5]+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(str);
		boolean flag = match.matches();
		return flag;
	}

	public void deviceBatchBindTag(int uid, String message, String tag) {
		boolean filter = StringFilter(tag);
		if (message != null && tag != null && filter) {

			String[] arrTemp = tag.split(",");
			for (String newTag : arrTemp) {
				addTag(uid, newTag);
			}
			asyncDeliverMessageService.sentDeviceBatchBindTagActionMessage(uid, message, tag);

		} else {
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		}
	}

	public void deviceBatchDelTag(int uid, String message) {
		if (message != null) {
			asyncDeliverMessageService.sentDeviceBatchDelTagActionMessage(uid, message);
		} else {
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		}
	}

	/**
	 * 保存节点信息
	 * 
	 * @param uid
	 * @param gid
	 *            当前节点
	 * @param pid
	 *            父节点
	 * @param name
	 *            节点名称
	 * @return
	 */
	public TagGroupVTO saveTreeNode(int uid, int gid, int pid, String name) {

		// 验证是否能新建分组
		CanSaveNode(uid, gid, pid, name);

		TagGroup tagGroup = null;
		boolean needParentChildrenInr = false;
		if (gid == 0) {
			tagGroup = new TagGroup();
			tagGroup.setCreator(uid);
			tagGroup.setName(name);
			tagGroup.setUpdator(uid);
			tagGroup.setPid(pid);
			tagGroupService.insert(tagGroup);
			needParentChildrenInr = true;
		} else {
			tagGroup = tagGroupService.getById(gid);
			tagGroup.setName(name);
			tagGroup.setUpdator(uid);
			tagGroupService.update(tagGroup);
		}

		// 更新父节点child参数，其parent节点的haschild = true
		if (pid > 0 && needParentChildrenInr) {
			TagGroup parent_group = tagGroupService.getById(pid);
			if (parent_group != null) {
				parent_group.setChildren(parent_group.getChildren() + 1);
				tagGroupService.update(parent_group);
			}
		}
		return TagGroupDetail(tagGroup);
	}

	/**
	 * 分组添加设备
	 * 
	 * @param uid
	 * @param gid
	 * @param macs
	 * @return
	 */
	public void saveDevices2Group(int uid, int gid, String path, String macs) {

		String[] macsTemp = macs.split(StringHelper.COMMA_STRING_GAP);

		List<String> macsList = ArrayHelper.toList(macsTemp);

		// 验证是否能添加设备
		CanAddDevices2Group(uid, gid, macsTemp.length);

		List<TagGroupRelation> entities = new ArrayList<TagGroupRelation>();

		for (String mac : macsTemp) {

			TagGroupRelation tagGroupRelation = new TagGroupRelation();
			tagGroupRelation.setId(mac);
			tagGroupRelation.setGid(gid);
			tagGroupRelation.setUid(uid);
			tagGroupRelation.setPath(path);

			entities.add(tagGroupRelation);
		}

		tagGroupRelationService.insertAll(entities);

		changeDevicesCount(gid, macsTemp.length);

		String paths = tagGroupService.getById(gid).getPath2ES();

		wifiDeviceStatusIndexIncrementService.ucExtensionMultiUpdIncrement(macsList, paths);

	}

	/**
	 * 修改设备分组信息
	 */
	public void modifyDeciceWithNode(int uid, int gid, int newGid, String macs) {

		String[] macTemp = macs.split(StringHelper.COMMA_STRING_GAP);

		List<String> macsList = ArrayHelper.toList(macTemp);

		List<TagGroupRelation> entities = tagGroupRelationService.findByIds(macsList);

		if (newGid == 0) {
			tagGroupRelationService.deleteAll(entities);
			wifiDeviceStatusIndexIncrementService.ucExtensionMultiUpdIncrement(macsList, null);
		} else {
			for (TagGroupRelation tagGroupRelation : entities) {
				tagGroupRelation.setGid(newGid);
			}
			tagGroupRelationService.updateAll(entities);

			String paths = tagGroupService.getById(newGid).getPath2ES();
			wifiDeviceStatusIndexIncrementService.ucExtensionMultiUpdIncrement(macsList, paths);
		}

		changeDevicesCount(gid, -macTemp.length);
	}

	/**
	 * 删除节点
	 * 
	 * @param uid
	 * @param gids
	 * @return
	 */
	public void delNode(int uid, String gids) {
		String[] gidsTemp = gids.split(StringHelper.COMMA_STRING_GAP);

		for (String gidString : gidsTemp) {

			int gid = Integer.parseInt(gidString);

			TagGroup group = tagGroupService.getById(gid);
			if (group != null && group.getCreator() == uid) {
				int pid = group.getPid();

				// 先删除设备和索引，再删除节点
				delChildNodeDevices(uid, group.getPath());

				tagGroupService.removeAllByPath(group.getPath(), true);

				if (pid != 0) {
					TagGroup parent_group = tagGroupService.getById(pid);
					parent_group.setChildren(parent_group.getChildren() - 1);
					tagGroupService.update(parent_group);
				}
			}else{
				throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_USER_PRIVILEGE_ERROR);
			}
		}
	}

	/**
	 * 能否添加节点的可行性验证
	 * 
	 * false 为可以创建节点
	 * 
	 * @return
	 */
	public boolean CanSaveNode(int uid, int gid, int pid, String name) {

		boolean flag = StringFilter(name);

		// 所有节点不可重名
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("creator", uid).andColumnEqualTo("name", name);
		int count = tagGroupService.countByModelCriteria(mc);

		if (flag && count != 0) {
			flag = false;
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_NAME_ERROR);
		}

		if (flag && pid > 0) {
			// 父节点creator是否与当前用户一致
			TagGroup tagParentGroup = tagGroupService.getById(pid);
			if (tagParentGroup.getCreator() != uid) {
				flag = false;
				throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_USER_PRIVILEGE_ERROR);
			}
			// 父节点最多只有100个子节点
			if (flag && tagParentGroup.getChildren() > 99) {
				flag = false;
				throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_COUNT_MAX_ERROR);
			}

			// 目前限制最多三级
			count = countSubString(tagParentGroup.getPath(), "/");
			if (flag && count >= 3) {
				// 节点已上限
				throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_GROUP_TOO_LONG);
			}

		}

		// 最多100个同级节点
		// mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("pid",
		// pid).andColumnEqualTo("creator", uid);
		// List<TagGroup> tagSamePidGroups =
		// tagGroupService.findModelByModelCriteria(mc);
		// if (flag && tagSamePidGroups != null && tagSamePidGroups.size() >=
		// 100) {
		// return false;
		// }

		return flag;
	}

	/**
	 * 当前节点能否添加设备的可行性验证
	 * 
	 * @return
	 */
	private boolean CanAddDevices2Group(int uid, int gid, int tempSize) {

		boolean flag = true;

		TagGroup tagGroup = tagGroupService.getById(gid);

		// 当前节点是否存在
		if (tagGroup == null) {
			flag = false;
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_INEXISTENCE);
		} else {

			// // 当前节点是否为叶子节点
			// if (flag && tagGroup.getChildren() != 0) {
			// flag = false;
			// }

			// 当前节点添加设备是否超过100台
			if (flag && (tagGroup.getDevice_count() > 99 || (tagGroup.getDevice_count() + tempSize > 99))) {
				flag = false;
				throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_DEVICE_COUNT_MAX);
			}
		}
		return flag;
	}

	private static int countSubString(String origin, String sub) {
		Pattern p = Pattern.compile(sub, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(origin);
		int count = 0;
		while (m.find()) {
			count++;
		}
		return count;
	}

	/**
	 * 递归修改当前节点和上层节点绑定设备数
	 * 
	 * @param gid
	 * @param num
	 */
	private void changeDevicesCount(int gid, int num) {
		TagGroup tagGroup = tagGroupService.getById(gid);
		tagGroup.setDevice_count(tagGroup.getDevice_count() + num);
		tagGroupService.update(tagGroup);
		if (tagGroup.getPid() != 0) {
			changeDevicesCount(tagGroup.getPid(), num);
		}
	}

	/**
	 * 删除当前节点和子节点设备关系 同时清除ES索引
	 * 
	 * @param uid
	 * @param tagGroup
	 */
	private void delChildNodeDevices(int uid, String path) {

		// 模糊搜索
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid).andColumnLike("path", path + "%");
		List<TagGroupRelation> entities = tagGroupRelationService.findModelByModelCriteria(mc);

		List<String> macsList = new ArrayList<String>();
		for (TagGroupRelation tagGroupRelation : entities) {
			macsList.add(tagGroupRelation.getId());
		}
		// 清除所有索引信息
		wifiDeviceStatusIndexIncrementService.ucExtensionMultiUpdIncrement(macsList, null);

		tagGroupRelationService.deleteByModelCriteria(mc);

		// if (tagGroup.getChildren() != 0) {
		// ModelCriteria newMc = new ModelCriteria();
		// newMc.createCriteria().andColumnEqualTo("pid", tagGroup.getId());
		// // TODO ? 通过冗余path去模糊删除
		// List<TagGroup> tagchildGroups =
		// tagGroupService.findModelByModelCriteria(newMc);
		// for (TagGroup tagChildGroup : tagchildGroups) {
		// delChildNodeDevices(tagChildGroup);
		// }
		// }
	}

	/**
	 * 生成当前节点信息，为客户端提供gid，pid参数
	 * 
	 * @param tagGroup
	 * @return
	 */
	private TagGroupVTO TagGroupDetail(TagGroup tagGroup) {
		TagGroupVTO vto = new TagGroupVTO();
		vto.setGid(tagGroup.getId());
		vto.setName(tagGroup.getName());
		vto.setPid(tagGroup.getPid());
		vto.setCreator(tagGroup.getCreator());
		if (tagGroup.getPid() == 0) {
			vto.setPname("根节点");
		} else {
			TagGroup parent_group = tagGroupService.getById(tagGroup.getPid());
			vto.setPname((parent_group != null) ? parent_group.getName() : null);
		}
		vto.setDevice_count(tagGroup.getDevice_count());
		vto.setParent(tagGroup.getChildren() > 0);
		vto.setPath(tagGroup.getPath());
		return vto;
	}

	/**
	 * 分页获取当前节点下一级子节点数据
	 * 
	 * @param uid
	 * @param gid
	 * @param pageNo
	 * @param pageSize
	 */
	public TailPage<TagGroupVTO> fetchChildGroup(int uid, int pid, int pageNo, int pageSize) {
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("creator", uid).andColumnEqualTo("pid", pid);
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		mc.setOrderByClause(" created_at desc");
		TailPage<TagGroup> pages = tagGroupService.findModelTailPageByModelCriteria(mc);
		List<TagGroupVTO> result = new ArrayList<TagGroupVTO>();
		for (TagGroup tagGroup : pages) {
			TagGroupVTO vto = TagGroupDetail(tagGroup);
			result.add(vto);
		}

		return new CommonPage<TagGroupVTO>(pages.getPageNumber(), pages.getPageSize(), pages.getTotalItemsCount(), result);
	}

	public TagGroupVTO currentGroupDetail(int uid, int gid) {

		TagGroup tagGroup = tagGroupService.getById(gid);

		if (tagGroup != null) {
			return TagGroupDetail(tagGroup);
		} else {
			throw new BusinessI18nCodeException(ResponseErrorCode.WIFIDEVICE_GROUP_NOTEXIST,
					new String[] { String.valueOf(gid) });
		}
	}

	/**
	 * 分组下发指令
	 * 
	 * @param uid
	 * @param message
	 * @param cmds
	 */
	public void batchGroupDownCmds(int uid, String message, String opt, String subopt,String extparams) {
		if (message !=null && opt != null) {
			OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(opt);
			if (opt_cmd.equals(OperationCMD.ModifyDeviceSetting)) {
				asyncDeliverMessageService.sentBatchGroupCmdsActionMessage(uid, message, opt,subopt,extparams);
			}else{	
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			}
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}
	
	public boolean batchGroupSnkTakeEffectNetworkConf(int uid, String message, boolean on, String snk_type,String template){
		try{
			asyncDeliverMessageService.sendBatchGroupDeviceSnkApplyActionMessage(uid,message, snk_type, template,on?IDTO.ACT_UPDATE:IDTO.ACT_DELETE);
			return true;
		}catch(Exception ex){
			ex.printStackTrace(System.out);
			return false;
		}
	}
}
