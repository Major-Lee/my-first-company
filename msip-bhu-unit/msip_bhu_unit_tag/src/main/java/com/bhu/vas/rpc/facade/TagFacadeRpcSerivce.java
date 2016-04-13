package com.bhu.vas.rpc.facade;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bhu.vas.api.rpc.tag.dto.TagDTO;
import com.bhu.vas.api.rpc.tag.dto.TagItemsDTO;
import com.bhu.vas.api.rpc.tag.model.TagDevices;
import com.bhu.vas.api.rpc.tag.model.TagName;
import com.bhu.vas.api.rpc.tag.vto.TagItemsVTO;
import com.bhu.vas.business.ds.tag.service.TagDevicesService;
import com.bhu.vas.business.ds.tag.service.TagNameService;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;

/**
 * 
 * @author xiaowei
 *		by 16/04/12
 */
@Service
public class TagFacadeRpcSerivce {
	
	@Resource
	private TagNameService tagNameService;
	
	@Resource
	private TagDevicesService tagDevicesService;
	
	private void addTag(String tag){
        ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse("1=1").andColumnEqualTo("tag", tag);
        
        List<TagName> list = tagNameService.findModelByModelCriteria(mc);
        if (list.isEmpty()) {
        	TagName tagName = new TagName();
    		tagName.setTag(tag);
    		tagName.setCreated_at(new Date());
    		tagNameService.insert(tagName);
		}
	}
	
	public void bindTag(String mac,String tag){
		addTag(tag);
		
		TagDTO dto = JsonHelper.getDTO(tag, TagDTO.class);
		
		TagDevices tagDevices = tagDevicesService.getById(mac);
		
		if (tagDevices !=null) {
			
			TagDTO old = JsonHelper.getDTO(tagDevices.getTag(), TagDTO.class);
			
			if (dto != null && dto.getItems()!= null && !dto.getItems().isEmpty()) {
				for (TagItemsDTO item : dto.getItems()) {
					String name  = item.getTag().trim();
					if (old != null && old.getItems() != null && !old.getItems().isEmpty()) {
	                    boolean flag = false;
	                    for (TagItemsDTO olditem : old.getItems()) {
	                        if (name.equals(olditem.getTag())) {
	                        	flag = true;
	                            break;
	                        }
	                    }
	                    if(!flag){
	                    	old.getItems().add(item);
	                    }
	                    tagDevices.setTag(JsonHelper.getJSONString(old));
	                    tagDevices.setUpdate_at(new Date());
					}
				}
				tagDevicesService.update(tagDevices);
			}
			
		}else{
			TagDevices td = new TagDevices();
			td.setId(mac);
			td.setTag(JsonHelper.getJSONString(dto));
			td.setCreated_at(new Date());
			tagDevicesService.insert(td);
		}
	}
	
	public TagItemsVTO fetchTag(int pageNo,int pageSize){
		
		ModelCriteria mc = new ModelCriteria();
        mc.createCriteria().andSimpleCaulse("1=1");
        mc.setPageSize(pageSize);
        mc.setPageNumber(pageNo);
        List<TagName> list = tagNameService.findModelByModelCriteria(mc);
        TagItemsVTO vto = new TagItemsVTO();
        vto.setItems(list);

        return  vto; 
	}
}
