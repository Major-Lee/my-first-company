package com.bhu.vas.rule;

import org.apache.commons.lang.math.RandomUtils;

import com.smartwork.msip.cores.fspublishrule.PublishType;
import com.smartwork.msip.cores.fsstorerule.image.ThumbType;
import com.smartwork.msip.cores.fsstorerule.image.ruleimpl.CommonImageRule;

public class CustomerThreadImageRule extends CommonImageRule{
	private static String BusinessType = "/threads";
	@Override
	public String ID2URL(String id, ThumbType thumb) {
    	StringBuilder sb = new StringBuilder();
    	PublishType publishType = getPubishType();
    	try{
	       	switch(thumb){
	       		case NORMALORIGINAL: 
	       			sb.append("http://").append(PublishType.randNumberWithKey(publishType.getName())).append(BusinessType).append(super.ID2URL(id,thumb));
	       			break;
	       		case NORMALMIDDLE:
	       			sb.append("http://").append(PublishType.randNumberWithKey(publishType.getName())).append(BusinessType).append(super.ID2URL(id,thumb));
	       			break;
	       		case NORMALLARGE:
	       			sb.append("http://").append(PublishType.randNumberWithKey(publishType.getName())).append(BusinessType).append(super.ID2URL(id,thumb));
	       			break;
	       		case NORMALSMALL:
	       			sb.append("http://").append(PublishType.randNumberWithKey(publishType.getName())).append(BusinessType).append(super.ID2URL(id,thumb));
	       			break;
	       		default:
	       			sb.append("http://").append(PublishType.randNumberWithKey(publishType.getName())).append(BusinessType).append(super.ID2URL(id,thumb));
	       			break;
	       	}
	       	sb.append("?"+RandomUtils.nextInt());
	       	return sb.toString();
    	}catch(java.lang.StringIndexOutOfBoundsException siobe){
    		return "";
    	}
    	
    }
	public PublishType getPubishType(){
		return PublishType.IMAGE;
	}
}
