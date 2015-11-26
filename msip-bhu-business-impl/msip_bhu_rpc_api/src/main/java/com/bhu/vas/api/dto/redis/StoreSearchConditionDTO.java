package com.bhu.vas.api.dto.redis;

/**
 * 用于用户保存的搜索条件的dto
 * @author lwaliet
 *
 */
@SuppressWarnings("serial")
public class StoreSearchConditionDTO implements java.io.Serializable{
	//搜索条件的message
	private String message;
	//索索条件的描述信息 客户端维护
	private String desc;
	
	public StoreSearchConditionDTO() {
	}
	public StoreSearchConditionDTO(String message, String desc) {
		this.message = message;
		this.desc = desc;
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
}
