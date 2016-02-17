package com.bhu.vas.api.rpc.user.model;

import com.smartwork.msip.cores.orm.model.extjson.KeyDtoMapJsonExtIntModel;

/**
 * 存储
 * 	开通的服务系统
 *  分配的简单角色
 *  sns帐号
 *  weichat
 *  qq
 *  weibo	
 * @author Edmond
 *
 */
@SuppressWarnings("serial")
public class UserExt extends KeyDtoMapJsonExtIntModel<String>{
	private int hasServiceLines;
	private int hasRoles;
	//虚拟货币
	//private int currency;
	public UserExt() {
		super();
	}

	public UserExt(Integer userid){
		this.setId(userid);
	}
	
	public Integer getUserid(){
		return this.id;
	}
	
	public void setUserid(Integer userid){
		this.setId(userid);
	}
	
	static final String Weichat = "weichat";
	static final String QQ = "qq";
	
	/*
	public Set<UserRoleDTO> distillRoles(){
		return this.get(Roles,true);
	}
	public Set<Integer> distillRoleIds(){
		return PermissionDTOHelper.getDTOIntSetIds(this.distillRoles());
	}
	public Set<String> distillRoleNames(){
		return PermissionDTOHelper.getDTOStringSetNames(this.distillRoles());
	}
	
	public void putToRoles(UserRoleDTO... dtos){
		Set<UserRoleDTO> roles = this.distillRoles();
		for(UserRoleDTO dto:dtos){
			roles.add(dto);
		}
	}
	
	public void replaceRoles(Set<UserRoleDTO> dtos){
		this.put(Roles,dtos);
	}
	
	public boolean removeFromRoles(UserRoleDTO dto){
		Set<UserRoleDTO> roles = this.distillRoles();
		return roles.remove(dto);
	}*/
	public int getHasServiceLines() {
		return hasServiceLines;
	}

	public void setHasServiceLines(int hasServiceLines) {
		this.hasServiceLines = hasServiceLines;
	}

	public int getHasRoles() {
		return hasRoles;
	}

	public void setHasRoles(int hasRoles) {
		this.hasRoles = hasRoles;
	}

	@Override
	public Class<String> getJsonParserModel() {
		return String.class;
	}	
}
