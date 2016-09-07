package com.bhu.vas.business.ds.tag.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.HandsetDeviceDTO;
import com.bhu.vas.api.rpc.tag.dto.TagGroupHandsetDetailDTO;
import com.bhu.vas.api.rpc.tag.model.TagGroup;
import com.bhu.vas.api.rpc.tag.model.TagGroupHandsetDetail;
import com.bhu.vas.api.rpc.tag.model.TagGroupRelation;
import com.bhu.vas.business.bucache.redis.serviceimpl.handset.HandsetGroupPresentHashService;
import com.bhu.vas.business.ds.tag.service.TagGroupHandsetDetailService;
import com.bhu.vas.business.ds.tag.service.TagGroupRelationService;
import com.bhu.vas.business.ds.tag.service.TagGroupService;
import com.smartwork.msip.cores.helper.DateTimeHelper;
import com.smartwork.msip.cores.helper.StringHelper;
import com.smartwork.msip.cores.orm.support.criteria.ModelCriteria;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;

@Service
public class TagGroupFacadeService {
	private final Logger logger = LoggerFactory.getLogger(TagGroupFacadeService.class);
	
	@Resource
	private TagGroupService tagGroupService;
	
	@Resource
	private TagGroupRelationService tagGroupRelationService;
	
	@Resource
	private TagGroupHandsetDetailService tagGroupHandsetDetailService;
	
	public List<String> findGroupNamesByMacs(List<String> macs){
		if(macs == null || macs.isEmpty()) return Collections.emptyList();
		
		List<TagGroupRelation> tagGroupRelations = tagGroupRelationService.findByIds(macs, true, true);
		List<Integer> gids = new ArrayList<Integer>();
		for(TagGroupRelation tagGroupRelation : tagGroupRelations){
			if(tagGroupRelation != null){
				gids.add(tagGroupRelation.getGid());
			}else{
				gids.add(-1);
			}
		}
		
		List<TagGroup> tagGroups = tagGroupService.findByIds(gids, true, true);
		List<String> tagGroupNames = new ArrayList<String>();
		for(TagGroup tagGroup : tagGroups){
			if(tagGroup != null){
				tagGroupNames.add(tagGroup.getName());
			}else{
				tagGroupNames.add(TagGroup.DefaultGroupName);
			}
		}
		
		return tagGroupNames;
	}
	
	public  void handsetOnline(String ctx, HandsetDeviceDTO dto, String wifiId){
		if(dto == null) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(dto.getBssid()) || StringUtils.isEmpty(ctx))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		handsetComming(dto, wifiId);
	}

	public  void handsetOffline(String ctx, HandsetDeviceDTO dto, String wifiId){
		if(dto == null) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(dto.getBssid()) || StringUtils.isEmpty(ctx))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		handsetComming(dto, wifiId);
	}
	
	public void handsetDeviceSync(String ctx, List<HandsetDeviceDTO> dtos,String wifiId){
		if(StringUtils.isEmpty(wifiId) || StringUtils.isEmpty(ctx))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		if((dtos != null && !dtos.isEmpty()) && dtos.get(0).getMac() != null){
			for(HandsetDeviceDTO dto : dtos){
				handsetComming(dto, wifiId);
			}
		}
	}
	
	public  void handsetAuth(String ctx, HandsetDeviceDTO dto, String wifiId){
		if(dto == null) 
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		if(StringUtils.isEmpty(dto.getMac()) || StringUtils.isEmpty(dto.getBssid()) || StringUtils.isEmpty(ctx))
			throw new BusinessI18nCodeException(ResponseErrorCode.RPC_PARAMS_VALIDATE_EMPTY);
		TagGroupHandsetDetail detail = handsetComming(dto, wifiId);
		if(detail !=null){
			if (StringHelper.FALSE.equals(detail.getAuth())) {
				detail.setAuth(StringHelper.TRUE);
				tagGroupHandsetDetailService.update(detail);
			}
		}
	}
	
	
	private TagGroupHandsetDetail handsetComming(HandsetDeviceDTO dto, String wifiId){
		
		TagGroupHandsetDetailDTO handsetDto = null;
		
		TagGroupRelation tagGroupRelation = tagGroupRelationService.getById(wifiId);
		if(tagGroupRelation == null){
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_BUSINESS_ERROR);
		}
		String hdmac = dto.getMac();
		int gid = tagGroupRelation.getGid();
		String timestr = DateTimeHelper.getDateTime(DateTimeHelper.FormatPattern7);
		//1.判断当天是否该终端是否存在记录
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("hdmac", hdmac).andColumnEqualTo("gid", gid).andColumnEqualTo("timestr", timestr);
		List<TagGroupHandsetDetail> entitys = tagGroupHandsetDetailService.findModelByModelCriteria(mc);
		if (entitys == null || entitys.isEmpty()) {
			TagGroupHandsetDetail detail =new TagGroupHandsetDetail();
			detail.setAuth(dto.getAction());
			detail.setHdmac(hdmac);
			detail.setGid(gid);
			if(isNewHandset(hdmac, gid)){
				detail.setNewuser(true);
				HandsetGroupPresentHashService.getInstance().groupNewlyHandsetComming(gid);
			}
			tagGroupHandsetDetailService.insert(detail);
			HandsetGroupPresentHashService.getInstance().groupHandsetComming(gid);
		}
		return entitys.get(0);
	}
	
	private boolean isNewHandset(String hdmac,int gid){
		boolean flag = false;
		ModelCriteria mc = new ModelCriteria();
		mc.createCriteria().andColumnEqualTo("hdmac", hdmac).andColumnEqualTo("gid", gid);
		int count = tagGroupHandsetDetailService.countByModelCriteria(mc);
		if (count == 0) {
			flag = true;
		}
		return flag;
	}
}
