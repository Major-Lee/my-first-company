package com.smartwork.async.messagequeue.builder;

import com.smartwork.async.messagequeue.model.UserCreateDTO;
import com.smartwork.async.messagequeue.model.UserOfflineDTO;
import com.smartwork.async.messagequeue.model.UserOnlineDTO;

public class PayloadModelBuilder {
	public static PayloadDTO createUserCreateDTO(int uid,String name){
		UserCreateDTO dto = new UserCreateDTO();
		dto.setUid(uid);
		dto.setName(name);
		dto.setTs(System.currentTimeMillis());
		return dto;
	}
	public static PayloadDTO createUserOnlineDTO(int uid,String name,String area){
		UserOnlineDTO dto = new UserOnlineDTO();
		dto.setUid(uid);
		dto.setName(name);
		dto.setArea(area);
		dto.setTs(System.currentTimeMillis());
		return dto;
	}
	public static PayloadDTO createUserOfflineDTO(int uid,String name,boolean kick){
		UserOfflineDTO dto = new UserOfflineDTO();
		dto.setUid(uid);
		dto.setName(name);
		dto.setKick(kick);
		dto.setTs(System.currentTimeMillis());
		return dto;
	}
}
