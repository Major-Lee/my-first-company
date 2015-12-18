package com.bhu.vas.api.dto.ret.setting;

/**
 * 设备配置信息的插件信息
 * @author Edmond Lee
 *
 */
public class WifiDeviceSettingPluginDTO implements DeviceSettingBuilderDTO{
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
	public WifiDeviceSettingPluginDTO(){
		
	}

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

	@Override
	public boolean equals(Object o) {
		if(o==null)return false;
		if(o instanceof WifiDeviceSettingPluginDTO){
			WifiDeviceSettingPluginDTO oo = (WifiDeviceSettingPluginDTO)o;
			return this.getName().equals(oo.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}
	
	@Override
	public Object[] builderProperties() {
		Object[] properties = new Object[2];
		properties[0] = name;
		properties[1] = enable;
		return properties;
	}
	
	@Override
	public Object[] builderProperties(int type) {
		return builderProperties();
	}
	
	@Override
	public boolean beRemoved() {
		return false;
	}
	
	/*//http://7xpatx.dl1.z0.glb.clouddn.com/uRouter/smb.package.tar.gz
	public static WifiDeviceSettingPluginDTO buildSambaPlugin(String enable,String downlaod_url){
		WifiDeviceSettingPluginDTO dto = new WifiDeviceSettingPluginDTO();
		dto.setName("samba");
		dto.setEnable(enable);
		dto.setStart_cmd("./smbsrv.sh start");
		dto.setStop_cmd("./smbsrv.sh stop");
		dto.setDownload_path(downlaod_url);
		return dto;
	}*/
}
