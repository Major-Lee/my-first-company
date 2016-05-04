package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.api.rpc.tag.model.TagName;
import com.bhu.vas.api.rpc.tag.vto.TagNameVTO;
import com.bhu.vas.business.asyn.spring.activemq.service.DeliverMessageService;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.ds.tag.service.TagNameService;
import com.bhu.vas.business.search.service.increment.WifiDeviceStatusIndexIncrementService;
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
	private DeliverMessageService deliverMessageService;

	private void addTag(int uid, String tag) {

		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("tag", tag);
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

		if (filter) {

			TagDevices tagDevices = tagDevicesService.getOrCreateById(mac);

			tagDevices.setLast_operator(uid);

			String[] arrTemp = tag.split(",");
			tagDevices.addTag(null);
			for (String str : arrTemp) {
				tagDevices.addTag(str);
				addTag(uid, str);
			}

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

			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("extension_content",
					tagDevices.getExtension_content());
			int count = tagDevicesService.countByModelCriteria(mc);

			if (count == 0) {
				ModelCriteria tagMc = new ModelCriteria();
				tagMc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("tag",
						tagDevices.getExtension_content());
				tagNameService.deleteByModelCriteria(tagMc);
			}
			
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
			for(String newTag : arrTemp){
				addTag(uid, newTag);
			}
			deliverMessageService.sentDeviceBatchBindTagActionMessage(uid, message, tag);
			
		} else {
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
		}
	}
}
