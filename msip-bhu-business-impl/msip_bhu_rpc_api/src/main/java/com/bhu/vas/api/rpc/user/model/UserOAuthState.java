package com.bhu.vas.api.rpc.user.model;

import com.bhu.vas.api.rpc.user.dto.UserOAuthStateDTO;
import com.bhu.vas.api.rpc.user.model.pk.UserOAuthStatePK;
import com.smartwork.msip.cores.orm.model.extjson.SimpleJsonExtPKModel;
@SuppressWarnings("serial")
public class UserOAuthState extends SimpleJsonExtPKModel<UserOAuthStateDTO,UserOAuthStatePK>{
	private String auid;
	
	public UserOAuthState(){
	}
	
	public UserOAuthState(UserOAuthStatePK pk){
		super.setId(pk);
	}
	
	public String getAuid() {
		return auid;
	}

	public void setAuid(String auid) {
		this.auid = auid;
	}
	
	public int getUid(){
		if(this.getId() == null) return 0;
		return this.getId().getUid();
	}
	
	public void setUid(int uid){
		if(this.getId() == null) this.setId(new UserOAuthStatePK());
		this.getId().setUid(uid);
	}
	
	public String getIdentify(){
		if(this.getId() == null) return null;
		return this.getId().getIdentify();
	}
	
	public void setIdentify(String identify){
		if(this.getId() == null) this.setId(new UserOAuthStatePK());
		this.getId().setIdentify(identify);
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o instanceof UserOAuthState){
			UserOAuthState oo = (UserOAuthState)o;
			return this.id.equals(oo.id);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.id.toString().hashCode();
	}
	
	@Override
	protected Class<UserOAuthStatePK> getPKClass() {
		return UserOAuthStatePK.class;
	}

	@Override
	public Class<UserOAuthStateDTO> getJsonParserModel() {
		return UserOAuthStateDTO.class;
	}
}
