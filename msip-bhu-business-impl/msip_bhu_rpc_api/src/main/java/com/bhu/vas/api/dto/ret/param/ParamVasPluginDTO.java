package com.bhu.vas.api.dto.ret.param;

import com.smartwork.msip.business.runtimeconf.BusinessRuntimeConfiguration;
import com.smartwork.msip.cores.helper.JsonHelper;



/**
 * 设备配置信息的ad
 * 广告信息配置
 * @author edmond
 *
 */
public class ParamVasPluginDTO{
	//插件名称
	private String name;
	//是否生效
	private String enable;
	//嵌入执行启动指令
	private String start_cmd;
	//嵌入执行关闭指令
	private String stop_cmd;
	//下载地址
	private String download_path;
	//版本
	private String ver;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	public String getStart_cmd() {
		return start_cmd;
	}

	public void setStart_cmd(String start_cmd) {
		this.start_cmd = start_cmd;
	}

	public String getStop_cmd() {
		return stop_cmd;
	}

	public void setStop_cmd(String stop_cmd) {
		this.stop_cmd = stop_cmd;
	}

	public String getDownload_path() {
		return download_path;
	}

	public void setDownload_path(String download_path) {
		this.download_path = download_path;
	}
	
	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public Object[] builderProperties() {
		Object[] properties = new Object[6];
		properties[0] = name;
		properties[1] = enable;
		properties[2] = start_cmd;
		properties[3] = stop_cmd;
		properties[4] = download_path;
		properties[5] = ver;
		return properties;
	}
	
	public static ParamVasPluginDTO builderDefaultSambaPlugin(){
		ParamVasPluginDTO dto = new ParamVasPluginDTO();
		dto.setName(BusinessRuntimeConfiguration.Devices_Plugin_Samba_Name);
		dto.setEnable(BusinessRuntimeConfiguration.Devices_CMD_Enable);
		dto.setStart_cmd(BusinessRuntimeConfiguration.Devices_Plugin_Samba_StartCmd);
		dto.setStop_cmd(BusinessRuntimeConfiguration.Devices_Plugin_Samba_StopCmd);
		dto.setDownload_path(BusinessRuntimeConfiguration.Devices_Plugin_Samba_DownloadPath);
		dto.setVer(BusinessRuntimeConfiguration.Devices_Plugin_Samba_Ver);
		return dto;
	}
	
	public static void main(String[] argv){
		//http://7xpatx.dl1.z0.glb.clouddn.com/uRouter/smb.package.tar.gz
		ParamVasPluginDTO dto = new ParamVasPluginDTO();
		dto.setName("samba");
		dto.setEnable("enable");
		dto.setStart_cmd("./smbsrv.sh start");
		dto.setStop_cmd("./smbsrv.sh stop");
		dto.setDownload_path("http://7xpatx.dl1.z0.glb.clouddn.com/uRouter/smb.package.tar.gz");
		dto.setVer("0.01");
		System.out.println(JsonHelper.getJSONString(dto));
	}
}
