package com.bhu.vas.business.commdity.dto;

@SuppressWarnings("serial")
public class CommdityPorcDTO implements java.io.Serializable{
	private Integer id;
	private Integer error = -1;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getError() {
		return error;
	}

	public void setError(Integer error) {
		this.error = error;
	}

	public String getProc_call(){
		return "{call commdity_test(#{id,jdbcType=INTEGER,mode=IN},#{error,jdbcType=INTEGER,mode=OUT})}";
		//return "{call commdity_test(5)}";
	}
}
