package com.bhu.vas.api.dto.ret.param;



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
	public static void main(String[] argv){
		/*WifiDeviceSettingVapHttp404DTO dto = new WifiDeviceSettingVapHttp404DTO();
		//dto.setAd_interface(ad_interface);
		//dto.setAd_url(ad_url);
		dto.setUrl("http://auth.wi2o.cn/ad/ad.js");
		dto.setEnable("enable");
		System.out.println(JsonHelper.getJSONString(dto));*/
	}
}
