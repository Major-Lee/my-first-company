package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.vas.api.dto.commdity.OrderSMSPromotionDTO;
import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.WifiDeviceDocumentEnumType;
import com.bhu.vas.api.rpc.charging.vto.DeviceGroupPaymentStatisticsVTO;
import com.bhu.vas.api.rpc.tag.dto.TagGroupHandsetDetailDTO;
import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.api.rpc.tag.model.TagGroup;
import com.bhu.vas.api.rpc.tag.model.TagGroupHandsetDetail;
import com.bhu.vas.api.rpc.tag.model.TagGroupRelation;
import com.bhu.vas.api.rpc.tag.model.TagGroupSortMessage;
import com.bhu.vas.api.rpc.tag.model.TagName;
import com.bhu.vas.api.rpc.tag.vto.GroupCountOnlineVTO;
import com.bhu.vas.api.rpc.tag.vto.GroupStatDetailVTO;
import com.bhu.vas.api.rpc.tag.vto.GroupUsersStatisticsVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupHandsetDetailVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupRankUsersVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupSendSortMessageVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupSortMessageVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupUserConnectDataVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupUserStatisticsConnectVTO;
import com.bhu.vas.api.rpc.tag.vto.TagGroupVTO;
import com.bhu.vas.api.rpc.tag.vto.TagNameVTO;
import com.bhu.vas.api.rpc.user.model.UserWallet;
import com.bhu.vas.business.asyn.spring.activemq.service.async.AsyncDeliverMessageService;
import com.bhu.vas.business.asyn.spring.model.IDTO;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetGroupPresentHashService;
import com.bhu.vas.business.ds.builder.BusinessTagModelBuilder;
import com.bhu.vas.business.ds.charging.facade.ChargingStatisticsFacadeService;
import com.bhu.vas.business.ds.commdity.facade.OrderFacadeService;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.ds.tag.service.TagGroupHandsetDetailService;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.tag.service.TagGroupService;
import com.bhu.vas.business.ds.tag.service.TagGroupSortMessageService;
import com.bhu.vas.business.ds.tag.service.TagNameService;
import com.bhu.vas.business.ds.user.facade.UserValidateServiceHelper;
import com.bhu.vas.business.ds.user.facade.UserWifiDeviceFacadeService;
import com.bhu.vas.business.ds.user.service.UserIdentityAuthService;
import com.bhu.vas.business.ds.user.service.UserWalletService;
import com.bhu.vas.business.search.service.WifiDeviceDataSearchService;
import com.bhu.vas.business.search.service.increment.WifiDeviceStatusIndexIncrementService;
import com.smartwork.msip.cores.helper.ArrayHelper;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.helper.phone.PhoneHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

/**
 * @author xiaowei by 16/04/12
 */
@Service
public class TagFacadeRpcSerivce {
	private final Logger logger = LoggerFactory.getLogger(TagFacadeRpcSerivce.class);
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

	@Resource
	private ChargingStatisticsFacadeService chargingStatisticsFacadeService;

	@Resource
	private WifiDeviceDataSearchService wifiDeviceDataSearchService;

//	@Resource
//	private UserDeviceService userDeviceService;
	
	@Resource
	private UserWifiDeviceFacadeService userWifiDeviceFacadeService;
	
	@Resource
	private TagGroupHandsetDetailService tagGroupHandsetDetailService;
	
	@Resource
	private UserIdentityAuthService userIdentityAuthService;
	
	@Resource
	private UserWalletService userWalletService;
	
	@Resource
	private TagGroupSortMessageService tagGroupSortMessageService;
	
	@Resource OrderFacadeService orderFacadeService;
	
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
		long t0 = System.currentTimeMillis();
		System.out.println("saveTreeNode start----");
		// 验证是否能新建分组
		CanSaveNode(uid, gid, pid, name);
		long t1 = System.currentTimeMillis();
		System.out.println("saveTreeNode start----step1 cost:"+(t1-t0));
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
		long t2 = System.currentTimeMillis();
		System.out.println("saveTreeNode start----step2 cost:"+(t2-t1));
		// 更新父节点child参数，其parent节点的haschild = true
		if (pid > 0 && needParentChildrenInr) {
			TagGroup parent_group = tagGroupService.getById(pid);
			if (parent_group != null) {
				parent_group.setChildren(parent_group.getChildren() + 1);
				tagGroupService.update(parent_group);
			}
		}
		long t3 = System.currentTimeMillis();
		System.out.println("saveTreeNode start----step2 cost:"+(t3-t2));
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

		UserValidateServiceHelper.validateUserDevices(uid, macsList, userWifiDeviceFacadeService);
		
		if(CanAddDevices2Group(uid, gid, macsList)){
			List<TagGroupRelation> entities = new ArrayList<TagGroupRelation>();

			for (String mac : macsTemp) {
				if (mac != null) {
					TagGroupRelation tagGroupRelation = new TagGroupRelation();
					tagGroupRelation.setId(mac);
					tagGroupRelation.setGid(gid);
					tagGroupRelation.setUid(uid);
					tagGroupRelation.setPath(path);
					entities.add(tagGroupRelation);
				}
			}

			tagGroupRelationService.insertAll(entities);

//			changeDevicesCount(gid, macsTemp.length);

			String paths = tagGroupService.getById(gid).getPath2ES();
			
			wifiDeviceStatusIndexIncrementService.ucExtensionMultiUpdIncrement(macsList, paths);
		}
	}

	/**
	 * 修改设备分组信息
	 */
	public void modifyDeciceWithNode(int uid, int gid, int newGid,String newPath, String macs) {

		String[] macTemp = macs.split(StringHelper.COMMA_STRING_GAP);

		List<String> macsList = ArrayHelper.toList(macTemp);
		
		if (macsList.isEmpty() || gid == 0) {
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_PARAM_ERROR);
		}
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnIn("id", macsList).andColumnEqualTo("gid", gid);	
		List<TagGroupRelation> entities = tagGroupRelationService.findModelByModelCriteria(mc);
		
		// mac绑定uid检测
		UserValidateServiceHelper.validateUserDevices(uid, macsList, userWifiDeviceFacadeService);
		
		if (newGid == 0) {
			tagGroupRelationService.deleteAll(entities);
			wifiDeviceStatusIndexIncrementService.ucExtensionMultiUpdIncrement(macsList, null);
		} else {
			if (CanAddDevices2Group(uid, newGid, macsList)) {
				
				for (TagGroupRelation tagGroupRelation : entities) {
					if (tagGroupRelation == null || tagGroupRelation.getGid() != gid) {
						throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUPREL_DEVICE_NOEXIST);
					}	
					tagGroupRelation.setGid(newGid);
					tagGroupRelation.setPath(newPath);
				}
				tagGroupRelationService.updateAll(entities);
				
				String paths = tagGroupService.getById(newGid).getPath2ES();
				wifiDeviceStatusIndexIncrementService.ucExtensionMultiUpdIncrement(macsList, paths);
//				changeDevicesCount(newGid, macTemp.length);
			}
		}
//		changeDevicesCount(gid, -entities.size());
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
				int count = delChildNodeDevices(uid, group.getPath());
				tagGroupService.removeAllByPath(group.getPath(), true);
				
				if (pid != 0) {
					TagGroup parent_group = tagGroupService.getById(pid);
					parent_group.setChildren(parent_group.getChildren() - 1);
					parent_group.setDevice_count(parent_group.getDevice_count() - count);
					tagGroupService.update(parent_group);
				}
			} else {
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

		if (name.isEmpty()) {
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_NAME_EMPTY);
		}
		
		if (name.equals(TagGroup.DefaultGroupName)) {
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_NAME_ERROR);
		}
		
		boolean flag = StringFilter(name);

		if (!flag) {
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_NAME_FORMAT_ERROR);
		}
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

		return flag;
	}

	/**
	 * 当前节点能否添加设备的可行性验证
	 * 
	 * @param userDeviceService2
	 * 
	 * @return
	 */
	private boolean CanAddDevices2Group(int uid, int gid, List<String> macList) {

		boolean flag = true;

		TagGroup tagGroup = tagGroupService.getById(gid);

		// 当前节点是否存在
		if (tagGroup == null) {
			flag = false;
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_INEXISTENCE);
		} else {
			
			if (tagGroup.getCreator() != uid) {
				flag = false;
				throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_USER_PRIVILEGE_ERROR);
			}
			// 当前节点添加设备是否超过100台
			if (flag && (tagGroup.getDevice_count() > 99 || (tagGroup.getDevice_count() + macList.size() > 99))) {
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

//	/**
//	 * 递归修改当前节点和上层节点绑定设备数
//	 * 
//	 * @param gid
//	 * @param num
//	 */
//	private void changeDevicesCount(int gid, int num) {
//		TagGroup tagGroup = tagGroupService.getById(gid);
//		tagGroup.setDevice_count(tagGroup.getDevice_count() + num);
//		tagGroupService.update(tagGroup);
//		if (tagGroup.getPid() != 0) {
//			changeDevicesCount(tagGroup.getPid(), num);
//		}
//	}

	/**
	 * 删除当前节点和子节点设备关系 同时清除ES索引
	 * 
	 * @param uid
	 * @param tagGroup
	 */
	private int delChildNodeDevices(int uid, String path) {

		// 模糊搜索
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid).andColumnLike("path", path + "%");
		List<TagGroupRelation> entities = tagGroupRelationService.findModelByModelCriteria(mc);
		
		List<String> macsList = new ArrayList<String>();
		for (TagGroupRelation tagGroupRelation : entities) {
			macsList.add(tagGroupRelation.getId());
			System.out.println(tagGroupRelation.getId());
		}
		// 清除所有索引信息
		wifiDeviceStatusIndexIncrementService.ucExtensionMultiUpdIncrement(macsList, null);

		int count = tagGroupRelationService.deleteByModelCriteria(mc);
		
		return count;
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
//		vto.setDevice_count(tagGroup.getDevice_count());
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
		
		int total = tagGroupService.countByCommonCriteria(mc);
		
		List<TagGroup> pages = tagGroupService.findModelByCommonCriteria(mc);
		List<TagGroupVTO> result = new ArrayList<TagGroupVTO>();

		if (pageNo == 1 && pid == 0) {
			TagGroupVTO vto = new TagGroupVTO();
			vto.setName(TagGroup.DefaultGroupName);
//			vto.setDevice_count((int) wifiDeviceDataSearchService.searchCountByUserGroup(uid, null, null));
			result.add(vto);
		}

		for (TagGroup tagGroup : pages) {
			TagGroupVTO vto = TagGroupDetail(tagGroup);
			result.add(vto);
		}
		return new CommonPage<TagGroupVTO>(pageNo, pageSize, total,result);
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
	 * @param channel_taskid
	 * @param channel
	 * @param cmds
	 */
	public void batchGroupDownCmds(int uid, String message, String opt, String subopt, String extparams, String channel,
			String channel_taskid) {
		if (message != null && opt != null) {
			OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(opt);
			if (opt_cmd.equals(OperationCMD.ModifyDeviceSetting)) {
				asyncDeliverMessageService.sentBatchGroupCmdsActionMessage(uid, message, opt, subopt, extparams,
						channel, channel_taskid);
			} else {
				throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
			}
		} else {
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
	}

	/**
	 * 分组批量设置安全共享模板
	 * 
	 * @param uid
	 * @param message
	 * @param on
	 * @param snk_type
	 * @param template
	 * @return
	 */
	public boolean batchGroupSnkTakeEffectNetworkConf(int uid, String message, boolean on, String snk_type,
			String template) {
		try {
			asyncDeliverMessageService.sendBatchGroupDeviceSnkApplyActionMessage(uid, message, snk_type, template,
					on ? IDTO.ACT_UPDATE : IDTO.ACT_DELETE);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return false;
		}
	}

	/**
	 * 获得分组收益统计
	 * 
	 * @param uid
	 * @param gids
	 * @param paths
	 * @return
	 */
	public List<DeviceGroupPaymentStatisticsVTO> groupsGainsStatistics(int uid, String gids, String paths) {

		String[] gidArr = gids.split(StringHelper.COMMA_STRING_GAP);
		String[] pathsArr = paths.split(StringHelper.COMMA_STRING_GAP);
		List<DeviceGroupPaymentStatisticsVTO> list = new ArrayList<DeviceGroupPaymentStatisticsVTO>();
		if (gidArr.length == pathsArr.length) {
			for (int i = 0; i < pathsArr.length; i++) {
				DeviceGroupPaymentStatisticsVTO vto = chargingStatisticsFacadeService
						.fetchDeviceGroupPaymentStatistics(uid, gidArr[i].equals("0") ? null :gidArr[i], pathsArr[i]);
				list.add(vto);
			}
		}
		return list;
	}

	/**
	 * 获得分组在线数
	 * 
	 * @param uid
	 * @param gids
	 * @return
	 */
	public List<GroupCountOnlineVTO> groupsStatsOnline(int uid, String gids) {
		String[] arr = gids.split(StringHelper.COMMA_STRING_GAP);
		List<GroupCountOnlineVTO> list = new ArrayList<GroupCountOnlineVTO>();
		for (String gid : arr) {
			GroupCountOnlineVTO vto = new GroupCountOnlineVTO();
			vto.setGid(gid);
			if (gid.isEmpty()|| gid.equals("0")) {
				vto.setOnline(wifiDeviceDataSearchService.searchCountByUserGroup(uid, null,
						WifiDeviceDocumentEnumType.OnlineEnum.Online.getType()));
				vto.setCount(wifiDeviceDataSearchService.searchCountByUserGroup(uid, null,null));
			} else {
				vto.setOnline(wifiDeviceDataSearchService.searchCountByUserGroup(uid, "g_" + gid,
						WifiDeviceDocumentEnumType.OnlineEnum.Online.getType()));
				vto.setCount(wifiDeviceDataSearchService.searchCountByUserGroup(uid, "g_" + gid,null));
			}
			vto.setOffline(vto.getCount()-vto.getOnline());
			list.add(vto);
		}
		return list;
	}
	/**
	 * 分组用户连接情况
	 * @param gid
	 * @param timeStr
	 * @return
	 */
	public GroupUsersStatisticsVTO groupUsersStatistics(int gid, String timeStr) {
		GroupUsersStatisticsVTO vto = new GroupUsersStatisticsVTO();
		Date date = DateTimeHelper.fromDateStr(timeStr);
		Date dateDaysAgo = DateTimeHelper.getDateDaysAgo(date, 1);
		String today = DateTimeHelper.formatDate(date, DateTimeHelper.FormatPattern7);
		String yesterday = DateTimeHelper.formatDate(dateDaysAgo, DateTimeHelper.FormatPattern7);
		logger.info(String.format("groupUsersStatistics timeStr[%s] date[%s] today[%s] yesterday[%s]",
				timeStr,date,today,yesterday));
		Map<String, String> todayMap = HandsetGroupPresentHashService.getInstance().fetchGroupConnDetail(gid, today);
		Map<String, String> yesterdayMap = HandsetGroupPresentHashService.getInstance().fetchGroupConnDetail(gid, yesterday);
		vto.setToday_newly(todayMap.get("newly"));
		vto.setToday_total(todayMap.get("total"));
		vto.setYesterday_newly(yesterdayMap.get("newly"));
		vto.setYesterday_total(yesterdayMap.get("total"));
		vto.setCount(HandsetGroupPresentHashService.getInstance().fetchGroupConnTotal(gid));
		return vto;
	}
	
	/**
	 * 分组用户详情
	 * @param gid
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public TailPage<TagGroupHandsetDetailVTO> groupUsersDetail(int uid,int gid,String beginTime,String endTime,boolean filter,String match,int count,String mobileno,int pageNo,int pageSize){
		
		boolean isGroup = tagGroupService.checkGroup(gid, uid);
		if(!isGroup){
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_NOT_EXIST_OR_USER_NO_MATCH);
		}
		
		List<Map<String, Object>> handsetMap = tagGroupHandsetDetailService.selectHandsetDetail(gid, beginTime, endTime,pageNo,pageSize);
		List<TagGroupHandsetDetailVTO> vtos = new ArrayList<TagGroupHandsetDetailVTO>();
		for(Map<String, Object> map : handsetMap){
			vtos.add(BusinessTagModelBuilder.builderGroupUserDetailVTO(map));
		}
		
		{// 根据连接次数和手机号过滤
			if (filter) {
				Iterator<TagGroupHandsetDetailVTO> iter = vtos.iterator();
				while (iter.hasNext()) {
					TagGroupHandsetDetailVTO rv = iter.next();
					if (rv.isFilter(match,count,mobileno))
						iter.remove();
				}
			}
			if(mobileno ==null || mobileno.isEmpty()){
				for(TagGroupHandsetDetailVTO vto : vtos){
					if(vto.getMobileno() != null){
						StringBuilder sb = new StringBuilder(vto.getMobileno());
						sb.replace(3,7, "****");
						vto.setMobileno(sb.toString());
					}
				}
			}
		}
		
		return new CommonPage<TagGroupHandsetDetailVTO>(pageNo, pageSize, vtos.size(), vtos);
	}
	
	/**
	 * 分组用户详情
	 * @param gid
	 * @param beginTime
	 * @param endTime
	 * @return 
	 * @return
	 */
	public List<Date> groupUserDetail(int uid ,int gid,String hdmac,int pageNo,int pageSize){
		
		boolean isGroup = tagGroupService.checkGroup(gid, uid);
		if(!isGroup){
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_NOT_EXIST_OR_USER_NO_MATCH);
		}
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("gid", gid).andColumnEqualTo("hdmac", hdmac);
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		mc.setOrderByClause(" created_at desc");
		List<TagGroupHandsetDetail> list = tagGroupHandsetDetailService.findModelByModelCriteria(mc);
		
		List<Date> resultList = new ArrayList<Date>();
		if(!list.isEmpty()){
			for(TagGroupHandsetDetail detail : list){
				resultList.add(detail.getCreated_at());
			}
		}
		return resultList;
	}
	
	/**
	 * 分组用户排名
	 * @param uid
	 * @param gid
	 * @param startTime
	 * @param endTime
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	private TailPage<TagGroupRankUsersVTO> groupRankUsers(int uid, int gid, String startTime, 
			String endTime ,int pageNo, int pageSize) {
		
		boolean isGroup = tagGroupService.checkGroup(gid, uid);
		if(!isGroup){
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_NOT_EXIST_OR_USER_NO_MATCH);
		}
		
		List<Map<String, String>> handsetMap = tagGroupHandsetDetailService.selectGroupUsersRank(gid, 
				startTime, endTime, pageNo, pageSize);
		List<TagGroupRankUsersVTO> vtos = new ArrayList<TagGroupRankUsersVTO>();
		for(Map<String, String> map : handsetMap){
			logger.info(String.format("groupRankUsers count[%s] date[%s]", map.get("count"), map.get("date")));
			vtos.add(BusinessTagModelBuilder.builderGroupRankUsers(map));
		}
		return new CommonPage<TagGroupRankUsersVTO>(pageNo, pageSize, vtos.size(), vtos);
	}
	
	private List<TagGroupUserConnectDataVTO> groupUserConnectData(int uid, int gid, String startTime, 
			String endTime) {
		
		boolean isGroup = tagGroupService.checkGroup(gid, uid);
		if(!isGroup){
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_NOT_EXIST_OR_USER_NO_MATCH);
		}
		
		long count = DateTimeHelper.getTwoDateDifferentDay(endTime, startTime, DateTimeHelper.FormatPattern5);
		Date date = null;
		Date dateDaysAgo = null;
		List<TagGroupUserConnectDataVTO> vtos = new ArrayList<TagGroupUserConnectDataVTO>();
		for (int i = 0; i <= count;i++){
			date = DateTimeHelper.fromDateStr(startTime);
			dateDaysAgo = DateTimeHelper.getDateDaysAfter(date, i);
			TagGroupUserConnectDataVTO vto = new TagGroupUserConnectDataVTO();
			String today = DateTimeHelper.formatDate(dateDaysAgo, DateTimeHelper.FormatPattern7);
			String today_date = DateTimeHelper.formatDate(dateDaysAgo, DateTimeHelper.FormatPattern5);
			Map<String, String> detail = HandsetGroupPresentHashService.getInstance().fetchGroupConnDetail(gid, today);
			vto.setDate(today_date);
			vto.setNewly(detail.get("newly"));
			vto.setTotal(detail.get("total"));
			vtos.add(vto);
		}
		return vtos;
	}
	
	public TagGroupUserStatisticsConnectVTO groupUserStatisticsConnect(int uid, int gid, long startTime, long endTime,
			int pageNo, int pageSize) {
		
		boolean isGroup = tagGroupService.checkGroup(gid, uid);
		if(!isGroup){
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_NOT_EXIST_OR_USER_NO_MATCH);
		}
		
		String startTimeStr = DateTimeHelper.formatDate(new Date(startTime), DateTimeHelper.FormatPattern5);
		String endTimeStr = DateTimeHelper.formatDate(new Date(endTime), DateTimeHelper.FormatPattern5);
		TagGroupUserStatisticsConnectVTO vto = new TagGroupUserStatisticsConnectVTO();
		vto.setRankList(groupRankUsers(uid,gid,startTimeStr,endTimeStr,pageNo,pageSize));
		vto.setUserConnectData(groupUserConnectData(uid,gid,startTimeStr,endTimeStr));
		return vto;
	}
	public GroupStatDetailVTO groupUsersCount(int uid ,int gid,String beginTime,String endTime){
		
		boolean isGroup = tagGroupService.checkGroup(gid, uid);
		if(!isGroup){
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_NOT_EXIST_OR_USER_NO_MATCH);
		}
		
		GroupStatDetailVTO vto = new GroupStatDetailVTO();

		int userTotal = tagGroupHandsetDetailService.countGroupUsers(gid, beginTime, endTime);
		
		ModelCriteria connTotalMC = new ModelCriteria();
		connTotalMC.createCriteria().andColumnEqualTo("gid", gid).andColumnBetween("timestr", beginTime, endTime);
		int connTotal = tagGroupHandsetDetailService.countByModelCriteria(connTotalMC);
		
		ModelCriteria authTotalMC = new ModelCriteria();
		authTotalMC.createCriteria().andColumnEqualTo("gid", gid).andColumnEqualTo("auth",StringHelper.TRUE).andColumnBetween("timestr", beginTime, endTime);
		int authTotal = tagGroupHandsetDetailService.countByModelCriteria(authTotalMC);
		
		vto.setAuthTotal(authTotal);
		vto.setConnTotal(connTotal);
		vto.setUserTotal(userTotal);
		
		return vto;
	}
	/**
	 * 创建发送短信任务，实现生成好需要发送的信息
	 * @param uid
	 * @param gid
	 * @param count
	 * @param context
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public TagGroupSendSortMessageVTO generateGroupSendSMSTask(int uid ,int gid ,int count,String context,String startTime, String endTime){
		
		boolean isGroup = tagGroupService.checkGroup(gid, uid);
		if(!isGroup){
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_NOT_EXIST_OR_USER_NO_MATCH);
		}
		
		if(context == null || context.isEmpty() || context.length() >TagGroupSortMessage.msgLength){
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_MSG_TOO_LONG_OR_NULL);
		}
		
		List<Map<String, Object>> handsetMap = tagGroupHandsetDetailService.selectHandsetDetail(gid, startTime, endTime,0,0);
		List<TagGroupHandsetDetailDTO> dtos = new ArrayList<TagGroupHandsetDetailDTO>();
		for(Map<String, Object> map : handsetMap){
			TagGroupHandsetDetailDTO dto = BusinessTagModelBuilder.builderGroupUserDetailFilterVTO(map,count);
			if(dto == null)
				continue;
			dtos.add(dto);
		}
		TagGroupSendSortMessageVTO vto = new TagGroupSendSortMessageVTO();	
		UserWallet uwallet = userWalletService.getById(uid);
		long total_vcurrency = (uwallet.getVcurrency()+uwallet.getVcurrency_bing());
		
		if(!dtos.isEmpty()){
			boolean flag = false;
			int sm_count = dtos.size();
			long vcurrency_cost = orderFacadeService.getSMSPromotionSpendvcurrency(uid,sm_count);
			vto.setTotal_vcurrency(total_vcurrency);
			vto.setSm_count(sm_count);
			vto.setVcurrency_cost(vcurrency_cost);
			if(total_vcurrency >= vcurrency_cost){
				vto.setMessage(String.format("当前虎钻余额%s颗", total_vcurrency));
				flag = true;
			}else{
				vto.setMessage(String.format("当前虎钻余额%s颗,余额不足,请充值", total_vcurrency));
			}
			
			if(flag){
				List<String> mobilenoList = new ArrayList<String>();
				for(TagGroupHandsetDetailDTO detailDto: dtos){
					mobilenoList.add(detailDto.getMobileno());
				}
				TagGroupSortMessage tagGroupSortMessage = new TagGroupSortMessage();
				tagGroupSortMessage.setUid(uid);
				tagGroupSortMessage.setGid(gid);
				tagGroupSortMessage.setContext(context);
				tagGroupSortMessage.setStart(startTime);
				tagGroupSortMessage.setEnd(endTime);
				tagGroupSortMessage.setConnect(count);
				tagGroupSortMessage.replaceInnerModels(mobilenoList);
				tagGroupSortMessage.setSmtotal(sm_count);
				TagGroupSortMessage resultEntity =  tagGroupSortMessageService.insert(tagGroupSortMessage);
				vto.setTaskid(resultEntity.getId());
			}
		}
		return vto;
	}
	
	public boolean executeSendTask(int uid ,int taskid){
		TagGroupSortMessage entity =tagGroupSortMessageService.getById(taskid);

		if(entity !=null && entity.getUid() == uid){
			if(!entity.getState().equals(TagGroupSortMessage.deafult)){
				throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_TASK_AlREADY_DONE);
			}
			OrderSMSPromotionDTO dto = orderFacadeService.vcurrencyFromUserWalletForSMSPromotion(uid, TagGroupSortMessage.commdityId, entity.getSmtotal(), TagGroupSortMessage.commdityDesc);
			asyncDeliverMessageService.sentGroupSmsActionMessage(uid, taskid, dto.getId());
		}else{
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_TASK_NOT_EXIST);
		}
		return true;
	}
	
	public TailPage<TagGroupSortMessageVTO> sendMessageDetail(int uid ,int gid,int pageNo,int pageSize){
		
		boolean isGroup = tagGroupService.checkGroup(gid, uid);
		if(!isGroup){
			throw new BusinessI18nCodeException(ResponseErrorCode.TAG_GROUP_NOT_EXIST_OR_USER_NO_MATCH);
		}
		
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("uid", uid).andColumnEqualTo("gid", gid);
		mc.setPageNumber(pageNo);
		mc.setPageSize(pageSize);
		List<TagGroupSortMessage> list = tagGroupSortMessageService.findModelByModelCriteria(mc);
		List<TagGroupSortMessageVTO> vtos = new ArrayList<TagGroupSortMessageVTO>();
		for(TagGroupSortMessage entity : list){
			vtos.add(builderSendMEssageVTO(entity));
		}
		return new CommonPage<TagGroupSortMessageVTO>(pageNo, pageSize, vtos.size(), vtos);
	}
	
	private TagGroupSortMessageVTO builderSendMEssageVTO(TagGroupSortMessage entity){
		TagGroupSortMessageVTO vto = new TagGroupSortMessageVTO();
		TagGroup groupEntity = tagGroupService.getById(entity.getGid());
		vto.setGroupName(groupEntity.getName());
		vto.setContext(entity.getContext());
		vto.setCount(entity.getConnect());
		vto.setSendCount(entity.getSmtotal());
		vto.setSendTime(entity.getCreated_at());
		vto.setStart(entity.getStart());
		vto.setEnd(entity.getEnd());
		vto.setState(entity.getState());
		return vto;
	}
}