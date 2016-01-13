package com.bhu.vas.api.rpc.devices.dto;

import com.bhu.vas.api.helper.OperationCMD;
import com.bhu.vas.api.helper.OperationDS;


@SuppressWarnings("serial")
public class PersistenceCMDDetailDTO implements java.io.Serializable{
	private String opt;
	private String optname;
	private String subopt;
	private String suboptname;
	private String extparams;
	//指令下发后设备返回响应的成功值
	private boolean ds = false;
	public String getOpt() {
		return opt;
	}
	public void setOpt(String opt) {
		this.opt = opt;
	}
	public String getSubopt() {
		return subopt;
	}
	public void setSubopt(String subopt) {
		this.subopt = subopt;
	}
	public String getExtparams() {
		return extparams;
	}
	public void setExtparams(String extparams) {
		this.extparams = extparams;
	}
	
	
	
	public String getOptname() {
		return optname;
	}
	public void setOptname(String optname) {
		this.optname = optname;
	}
	public String getSuboptname() {
		return suboptname;
	}
	public void setSuboptname(String suboptname) {
		this.suboptname = suboptname;
	}
	public boolean isDs() {
		return ds;
	}
	public void setDs(boolean ds) {
		this.ds = ds;
	}
	public static PersistenceCMDDetailDTO from(PersistenceCMDDTO cmdDto){
		if(cmdDto == null) return null;
		
		OperationCMD opt_cmd = OperationCMD.getOperationCMDFromNo(cmdDto.getOpt());
		OperationDS ods_cmd = OperationDS.getOperationDSFromNo(cmdDto.getSubopt());
		PersistenceCMDDetailDTO result = new PersistenceCMDDetailDTO();
		if(opt_cmd != null){
			result.setOpt(opt_cmd.getNo());
			result.setOptname(opt_cmd.getDesc());
		}
		
		if(ods_cmd != null){
			result.setSubopt(ods_cmd.getNo());
			result.setSuboptname(ods_cmd.getDesc());
		}
		result.setDs(cmdDto.isDs());
		result.setExtparams(cmdDto.getExtparams());
		return result;
	}
}
