package com.bhu.vas.api.rpc.user.dto;

public class WithdrawRemoteResponseDTO implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//remoteCode
	private String rc;
	//code 描述
	private String desc;
	//发生时间
	private long ts;
	public String getRc() {
		return rc;
	}
	public void setRc(String rc) {
		this.rc = rc;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	
	public static WithdrawRemoteResponseDTO build(String rc,String desc){
		WithdrawRemoteResponseDTO dto = new WithdrawRemoteResponseDTO();
		dto.setRc(rc);
		dto.setDesc(desc);
		dto.setTs(System.currentTimeMillis());
		return dto;
	}
}
