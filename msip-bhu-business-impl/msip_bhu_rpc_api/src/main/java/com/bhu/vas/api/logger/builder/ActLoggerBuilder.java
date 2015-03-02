package com.bhu.vas.api.logger.builder;

import com.bhu.vas.api.logger.dto.ActLoggerClickDTO;
import com.bhu.vas.api.logger.dto.ActLoggerDTO;
import com.bhu.vas.api.logger.dto.ActLoggerShareDTO;
import com.smartwork.msip.cores.helper.JsonHelper;

public class ActLoggerBuilder {
	public static ActLoggerDTO buidSubjectClickActLogger(int uid, int sid, String act,String from,long incr){
		ActLoggerDTO dto = new ActLoggerDTO(uid,ActLoggerDTO.Type_Subject_UpAndDown);
		ActLoggerClickDTO clickdto = new ActLoggerClickDTO(sid,act,from,incr,System.currentTimeMillis());
		dto.setPayload(JsonHelper.getJSONString(clickdto));
		return dto;
	}
	
	public static ActLoggerDTO buidSubjectAbstractClickActLogger(int uid, int aid, String act,String from,long incr){
		ActLoggerDTO dto = new ActLoggerDTO(uid,ActLoggerDTO.Type_SubjectAbstract_UpAndDown);
		ActLoggerClickDTO clickdto = new ActLoggerClickDTO(aid,act,from,incr,System.currentTimeMillis());
		dto.setPayload(JsonHelper.getJSONString(clickdto));
		return dto;
	}
	
	public static ActLoggerDTO buidShareActLogger(int uid, int sid){
		ActLoggerDTO dto = new ActLoggerDTO(uid,ActLoggerDTO.Type_Share);
		ActLoggerShareDTO sharedto = new ActLoggerShareDTO(sid,System.currentTimeMillis());
		dto.setPayload(JsonHelper.getJSONString(sharedto));
		return dto;
	}
}
