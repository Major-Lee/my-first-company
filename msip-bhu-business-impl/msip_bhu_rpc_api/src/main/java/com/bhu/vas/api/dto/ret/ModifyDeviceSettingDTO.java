package com.bhu.vas.api.dto.ret;

import java.io.Serializable;

import com.smartwork.msip.cores.helper.StringHelper;

/**
 * 修改设备配置的上报dto
 * <return>
        <ITEM result="ok" config_sequence="37" />
   </return>
   
   <?xml version="1.0" encoding="UTF-8"?>
	<error>
        <ITEM result="fail" reason="config sequence is missing" err="config sequence is missing" errno="-3" 
        path="bhutop" />
	</error>
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class ModifyDeviceSettingDTO implements Serializable{
	public static final String Result_Fail = "fail";
	public static final String Result_Success = "ok";
	//配置序列号不匹配的错误码
	public static final String Errno_Sequence_Not_Match = "-2";
	//配置序列号不匹配的路径
	public static final String Path_Sequence_Not_Match = "bhutop";
	//修改配置结果
	private String result;
	//错误原因
	private String reason;
	//错误编号
	private String errno;
	//错误细节
	private String err;
	//错误路径
	private String path;

	//返回配置当前的序列号
	private String config_sequence;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getErrno() {
		return errno;
	}
	public void setErrno(String errno) {
		this.errno = errno;
	}
	public String getErr() {
		return err;
	}
	public void setErr(String err) {
		this.err = err;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getConfig_sequence() {
		return config_sequence;
	}
	public void setConfig_sequence(String config_sequence) {
		this.config_sequence = config_sequence;
	}
	/**
	 * 是否是配置序列号不匹配错误
	 * @return
	 */
	public boolean isConfigSequenceMatchError(){
		if(StringHelper.isEmpty(path) || StringHelper.isEmpty(errno)){
			return false;
		}
		if(Errno_Sequence_Not_Match.equals(errno) && Path_Sequence_Not_Match.equals(path)){
			return true;
		}
		return false;
	}
	
}