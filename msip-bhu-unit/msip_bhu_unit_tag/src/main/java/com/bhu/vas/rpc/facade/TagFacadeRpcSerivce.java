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
			boolean flag = tagDevices.getTag2ES().equals(tag);
			
			if (!flag) {
				tagDevices.setLast_operator(uid);
				tagDevices.setExtension_content(tag);
				tagDevicesService.update(tagDevices);
				wifiDeviceStatusIndexIncrementService.bindDTagsUpdIncrement(mac, tag.trim());
				addTag(uid, tag);
			}else{
				throw new Exception();
			}
			
		}else{
			throw  new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_ILLEGAL);
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
	
    public boolean StringFilter(String str){
		String regex="^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher match=pattern.matcher(str);
		boolean flag=match.matches();
		return flag;
    }
}
