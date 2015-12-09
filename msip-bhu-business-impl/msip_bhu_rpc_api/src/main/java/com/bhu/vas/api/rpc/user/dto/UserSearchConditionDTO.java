package com.bhu.vas.api.rpc.user.dto;

/**
 * 用于用户保存的搜索条件的dto
 * @author lwaliet
 *
 */
@SuppressWarnings("serial")
public class UserSearchConditionDTO implements java.io.Serializable{
	//搜索条件保存的时间戳
	private long ts;
	//搜索条件的message
	private String message;
	//索索条件的描述信息 客户端维护
	private String desc;
	//是否保存着
	private boolean stored = true;
	
	public UserSearchConditionDTO() {
	}
	
	public UserSearchConditionDTO(long ts) {
		this.ts = ts;
	}
	
	public UserSearchConditionDTO(String message, String desc) {
		this.message = message;
		this.desc = desc;
	}
	
	public UserSearchConditionDTO(long ts, String message, String desc) {
		this.ts = ts;
		this.message = message;
		this.desc = desc;
	}
	
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public boolean isStored() {
		return stored;
	}
	public void setStored(boolean stored) {
		this.stored = stored;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof UserSearchConditionDTO){
			UserSearchConditionDTO dto = (UserSearchConditionDTO)obj;
			if(dto.getTs() <= 0) return false;
			if(dto.getTs() == this.getTs()) return true;
			else return false;
		}else return false;
	}
	
	@Override
	public int hashCode() {
		return (int)this.getTs();
	}
	
}
