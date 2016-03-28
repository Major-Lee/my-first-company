package com.bhu.vas.api.dto.ret.setting;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.bhu.vas.api.dto.ret.setting.DeviceSettingBuilderDTO;
import com.smartwork.msip.cores.helper.StringHelper;
/**
 * 通过设备操作绑定dto
 * @author tangzichao
 *
 */
@SuppressWarnings("serial")
public class WifiDeviceSettingSyskeyDTO implements Serializable, DeviceSettingBuilderDTO{
	/**
	 *  1.	key值存在，并且绑定成功。返回status = 3；
		2.	key值存在，绑定不成功。返回status=2；
		3.	key值不存在。返回status=1；
	 */
	public static final String KEY_STATUS_SUCCESSED = "3";
	public static final String KEY_STATUS_FAILED = "2";
	public static final String KEY_STATUS_VALIDATE_FAILED = "1";
	public static final String KEY_STATUS_NOBIND = "-1";
	//uid
	private String keynum;
	//status
	private String keystatus;
	//industry
	private String industry;
	
	public WifiDeviceSettingSyskeyDTO(){
		
	}
	
	public WifiDeviceSettingSyskeyDTO(String keynum, String keystatus, String industry){
		this.keynum = keynum;
		this.keystatus = keystatus;
		this.industry = industry;
	}
	
	public String getKeynum() {
		return keynum;
	}
	public void setKeynum(String keynum) {
		this.keynum = keynum;
	}
	public String getKeystatus() {
		return keystatus;
	}
	public void setKeystatus(String keystatus) {
		this.keystatus = keystatus;
	}
	
	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}
	
	public boolean isSuccessedStatus(){
		if(KEY_STATUS_SUCCESSED.equals(keystatus)) return true;
		return false;
	}

	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[3];
		properties[0] = keynum;
		properties[1] = keystatus;
		if(StringUtils.isNotEmpty(industry)){
			properties[2] = industry;
		}else{
			properties[2] = StringHelper.EMPTY_STRING_GAP;
		}
		return properties;
	}
	
	@Override
	public Object[] builderProperties(int type) {
		return null;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o==null)return false;
		if(o instanceof WifiDeviceSettingSyskeyDTO){
			WifiDeviceSettingSyskeyDTO oo = (WifiDeviceSettingSyskeyDTO)o;
			if(StringUtils.isEmpty(this.getKeynum()) && StringUtils.isEmpty(oo.getKeynum())){
				return true;
			}
			if(StringUtils.isNotEmpty(this.getKeynum())){
				if(this.getKeynum().equals(oo.getKeynum())){
					return true;
				}
			}
			//return this.getKeynum().equals(oo.getKeynum()) && this.getIndustry().equals(oo.getIndustry());
		}
		return false;
	}

//	@Override
//	public int hashCode() {
//		tr
//		return this.getMac().toString().hashCode();
//	}
	
	@Override
	public boolean beRemoved() {
		return false;
	}
}