package com.bhu.vas.rpc.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.tag.dto.TagDTO;
import com.bhu.vas.api.rpc.tag.dto.TagItemsDTO;
import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.api.rpc.tag.model.TagName;
import com.bhu.vas.api.rpc.tag.vto.TagNameVTO;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.ds.tag.service.TagNameService;
import com.bhu.vas.business.search.service.increment.WifiDeviceStatusIndexIncrementService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.cores.orm.support.page.CommonPage;
import com.smartwork.msip.cores.orm.support.page.TailPage;

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

	private void addTag(String mac,TagDTO tag) {

		for (TagItemsDTO dto : tag.getItems()) {

			ModelCriteria mc = new ModelCriteria();
			mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("tag", dto.getTag());
			List<TagName> list = tagNameService.findModelByModelCriteria(mc);
			if (list.isEmpty()) {
				TagName tagName = new TagName();
				tagName.setTag(dto.getTag());
				tagName.setCreated_at(new Date());
				tagNameService.insert(tagName);
				String d_tags = fetchTag4ES(mac);
				wifiDeviceStatusIndexIncrementService.bindDTagsUpdIncrement(mac, d_tags);
			}
		}
	}

	public void bindTag(String mac, String tag) {

		TagDTO dto = JsonHelper.getDTO(tag, TagDTO.class);
		TagDevices tagDevices = tagDevicesService.getById(mac);
		addTag(mac,dto);
		
		if (tagDevices != null) {

			TagDTO old = JsonHelper.getDTO(tagDevices.getTag(), TagDTO.class);

			if (dto != null && dto.getItems() != null && !dto.getItems().isEmpty()) {
				for (TagItemsDTO item : dto.getItems()) {
					String name = item.getTag().trim();
					if (old != null && old.getItems() != null && !old.getItems().isEmpty()) {
						boolean flag = false;
						for (TagItemsDTO olditem : old.getItems()) {
							if (name.equals(olditem.getTag())) {
								flag = true;
								break;
							}
						}
						if (!flag) {
							old.getItems().add(item);
						}
						tagDevices.setTag(JsonHelper.getJSONString(old));
						tagDevices.setUpdate_at(new Date());
					}
				}
				tagDevicesService.update(tagDevices);
			}

		} else {
			TagDevices td = new TagDevices();
			td.setId(mac);
			td.setTag(tag);
			td.setCreated_at(new Date());
			tagDevicesService.insert(td);
		}
	}

	public TailPage<TagNameVTO> fetchTag(int pageNo, int pageSize) {

		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andSimpleCaulse("1=1");
		mc.setPageSize(pageSize);
		mc.setPageNumber(pageNo);

		TailPage<TagName> tailPages = tagNameService.findModelTailPageByModelCriteria(mc);

		List<TagNameVTO> result = new ArrayList<TagNameVTO>();
		for (TagName tagName : tailPages) {
			TagNameVTO vto = new TagNameVTO();
			vto.setTagName(tagName.getTag());
			result.add(vto);
		}

		return new CommonPage<TagNameVTO>(pageNo, pageSize, result.size(), result);
	}
	
	private String fetchTag4ES(String mac){
		TagDevices tagDevices = tagDevicesService.getById(mac);
		TagDTO dto = JsonHelper.getDTO(tagDevices.getTag(), TagDTO.class);
		
		StringBuilder sb = new StringBuilder();
		for (TagItemsDTO itemsDto : dto.getItems()) {
			sb.append(itemsDto.getTag()).append(" ");
		}
		return sb.toString();
	}
}
